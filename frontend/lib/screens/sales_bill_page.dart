
import 'package:flutter/material.dart';

import '../models/product.dart';
import '../services/api_service.dart';

class SalesBillPage extends StatefulWidget {
  const SalesBillPage({super.key});

  @override
  State<SalesBillPage> createState() => _SalesBillPageState();
}

class _SalesBillPageState extends State<SalesBillPage> {
  final ApiService _apiService = ApiService();

  final _customerNameController = TextEditingController();
  final _mobileController = TextEditingController();
  final _cashController = TextEditingController();

  final List<_SalesItem> _items = [];

  bool _loading = false;
  bool _submitting = false;

  String _paymentMethod = 'CASH';
  String? _lastBillNumber;
  String? _lastPaymentStatus;

  DateTime get _today => DateTime.now();

  String get _formattedDate {
    final date = _today;
    return '${date.day.toString().padLeft(2, '0')}/'
        '${date.month.toString().padLeft(2, '0')}/'
        '${date.year}';
  }

  double get _subtotal => _items.fold(
        0,
        (sum, item) => sum + item.grossAmount,
      );

  double get _discountTotal => _items.fold(
        0,
        (sum, item) => sum + item.discountAmount,
      );

  double get _grandTotal => _subtotal - _discountTotal;

  // These values are a live preview. The backend remains authoritative
  // and returns the persisted GST totals after the bill is created.
  double get _taxableTotal => _items.fold(
        0.0,
        (sum, item) => sum + item.taxableAmount,
      );

  double get _cgstTotal => _items.fold(
        0.0,
        (sum, item) => sum + item.cgstAmount,
      );

  double get _sgstTotal => _items.fold(
        0.0,
        (sum, item) => sum + item.sgstAmount,
      );

  double get _cashReceived =>
      double.tryParse(_cashController.text.trim()) ?? 0;

  double get _change =>
      _paymentMethod == 'CASH' && _cashReceived > _grandTotal
          ? _cashReceived - _grandTotal
          : 0;

  String _money(double amount) => '₹${amount.toStringAsFixed(2)}';

  @override
  void dispose() {
    _customerNameController.dispose();
    _mobileController.dispose();
    _cashController.dispose();

    for (final item in _items) {
      item.dispose();
    }

    super.dispose();
  }

  void _message(String message) {
    if (!mounted) return;

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message)),
    );
  }

  Future<void> _addMedicine() async {
    setState(() => _loading = true);

    try {
      final products = (await _apiService.getProducts())
          .where((product) => product.active)
          .toList();

      if (!mounted) return;

      final selected = await showDialog<Product>(
        context: context,
        builder: (context) => _MedicinePickerDialog(
          products: products,
        ),
      );

      if (selected == null || !mounted) return;

      // A product already in the cart can be increased.
      final existing = _items.where(
        (item) => item.product.id == selected.id,
      );

      if (existing.isNotEmpty) {
        _changeQuantity(existing.first, 1);
        return;
      }

      final batches =
          await _apiService.getBatchesByProduct(selected.id);

      final now = DateTime.now();
      final today = DateTime(now.year, now.month, now.day);

      final eligible = batches.where((batch) {
        final quantity =
            (batch['quantity'] as num?)?.toInt() ?? 0;

        final expiry = DateTime.tryParse(
          batch['expiryDate']?.toString() ?? '',
        );

        if (expiry == null || quantity <= 0) return false;

        return !DateTime(
          expiry.year,
          expiry.month,
          expiry.day,
        ).isBefore(today);
      }).toList();

      eligible.sort((a, b) {
        final dateA = DateTime.parse(a['expiryDate'].toString());
        final dateB = DateTime.parse(b['expiryDate'].toString());

        final result = dateA.compareTo(dateB);

        if (result != 0) return result;

        final idA = (a['id'] as num?)?.toInt() ?? 0;
        final idB = (b['id'] as num?)?.toInt() ?? 0;

        return idA.compareTo(idB);
      });

      if (eligible.isEmpty) {
        _message('No unexpired stock is available for ${selected.name}.');
        return;
      }

      final availableStock = eligible.fold<int>(
        0,
        (sum, batch) =>
            sum + ((batch['quantity'] as num?)?.toInt() ?? 0),
      );

      final mrp = (eligible.first['mrp'] as num?)?.toDouble();

      if (mrp == null || mrp < 0) {
        _message('A valid MRP is not available for this medicine.');
        return;
      }

      if (!mounted) return;

      setState(() {
        _items.add(
          _SalesItem(
            product: selected,
            availableStock: availableStock,
            sellingRate: mrp,
          ),
        );
      });
    } catch (e) {
      if (mounted) {
        _message(
          'Could not add medicine: '
          '${e.toString().replaceFirst('Exception: ', '')}',
        );
      }
    } finally {
      if (mounted) setState(() => _loading = false);
    }
  }

  void _changeQuantity(_SalesItem item, int difference) {
    final next = item.quantity + difference;

    if (next < 1) {
      _removeItem(item);
      return;
    }

    if (next > item.availableStock) {
      _message('Quantity exceeds available stock.');
      return;
    }

    setState(() => item.quantity = next);
  }

  void _removeItem(_SalesItem item) {
    setState(() => _items.remove(item));
    item.dispose();
  }

  void _clearBill() {
    if (_items.isEmpty &&
        _customerNameController.text.isEmpty &&
        _mobileController.text.isEmpty) {
      return;
    }

    showDialog<void>(
      context: context,
      builder: (dialogContext) => AlertDialog(
        title: const Text('Clear current bill?'),
        content: const Text(
          'All medicines and customer details in this unsaved bill '
          'will be cleared.',
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(dialogContext),
            child: const Text('Cancel'),
          ),
          FilledButton(
            onPressed: () {
              Navigator.pop(dialogContext);

              setState(() {
                for (final item in _items) {
                  item.dispose();
                }
                _items.clear();
                _customerNameController.clear();
                _mobileController.clear();
                _cashController.clear();
                _paymentMethod = 'CASH';
              });
            },
            child: const Text('Clear bill'),
          ),
        ],
      ),
    );
  }

  Future<void> _saveBill() async {
    if (_items.isEmpty) {
      _message('Add at least one medicine.');
      return;
    }

    if (_paymentMethod == 'CASH' && _cashReceived < _grandTotal) {
      _message('Cash received must cover the bill total.');
      return;
    }

    final mobile = _mobileController.text.trim();

    if (mobile.isNotEmpty &&
        !RegExp(r'^[0-9]{10}$').hasMatch(mobile)) {
      _message('Enter a valid 10-digit mobile number.');
      return;
    }

    setState(() => _submitting = true);

    try {
      final request = <String, dynamic>{
        'customerName': _customerNameController.text.trim().isEmpty
            ? null
            : _customerNameController.text.trim(),
        'customerMobile': mobile.isEmpty ? null : mobile,
        'paymentMethod': _paymentMethod,
        'items': _items.map((item) {
          return <String, dynamic>{
            'productId': item.product.id,
            'quantity': item.quantity,
            'sellingRate': item.sellingRate,
            'discountAmount': item.discountAmount,
          };
        }).toList(),
      };

      if (_paymentMethod == 'CASH') {
        request['amountReceived'] = _cashReceived;
      }

      final response = await _apiService.createBill(request);

      if (!mounted) return;

      final billNumber =
          response['billNumber']?.toString() ?? '—';
      final paymentStatus =
          response['paymentStatus']?.toString() ?? 'UNKNOWN';
      final total =
          (response['grandTotal'] as num?)?.toDouble() ?? _grandTotal;
      final change =
          (response['changeAmount'] as num?)?.toDouble() ?? 0;
      final taxableTotal =
          (response['totalTaxableAmount'] as num?)?.toDouble() ??
              _taxableTotal;
      final cgstTotal =
          (response['totalCgstAmount'] as num?)?.toDouble() ?? _cgstTotal;
      final sgstTotal =
          (response['totalSgstAmount'] as num?)?.toDouble() ?? _sgstTotal;

      setState(() {
        _lastBillNumber = billNumber;
        _lastPaymentStatus = paymentStatus;
      });

      await showDialog<void>(
        context: context,
        barrierDismissible: false,
        builder: (dialogContext) => AlertDialog(
          icon: const Icon(
            Icons.check_circle_outline,
            color: Colors.green,
            size: 42,
          ),
          title: const Text('Sales bill created'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text('Bill number: $billNumber'),
              const SizedBox(height: 8),
              Text('Taxable amount: ${_money(taxableTotal)}'),
              Text('CGST: ${_money(cgstTotal)}'),
              Text('SGST: ${_money(sgstTotal)}'),
              const Divider(height: 20),
              Text('Grand total: ${_money(total)}'),
              Text('Payment method: $_paymentMethod'),
              Text('Payment status: $paymentStatus'),
              if (_paymentMethod == 'CASH')
                Text('Change: ${_money(change)}'),
            ],
          ),
          actions: [
            FilledButton(
              onPressed: () => Navigator.pop(dialogContext),
              child: const Text('Done'),
            ),
          ],
        ),
      );

      if (!mounted) return;

      setState(() {
        for (final item in _items) {
          item.dispose();
        }
        _items.clear();
        _customerNameController.clear();
        _mobileController.clear();
        _cashController.clear();
        _paymentMethod = 'CASH';
      });
    } catch (e) {
      if (mounted) {
        _message(
          'Failed to create bill: '
          '${e.toString().replaceFirst('Exception: ', '')}',
        );
      }
    } finally {
      if (mounted) setState(() => _submitting = false);
    }
  }

  Widget _sectionTitle(
    BuildContext context,
    String title, {
    Widget? trailing,
  }) {
    return Row(
      children: [
        Expanded(
          child: Text(
            title,
            style: Theme.of(context).textTheme.titleMedium?.copyWith(
                  fontWeight: FontWeight.w700,
                ),
          ),
        ),
        if (trailing != null) trailing,
      ],
    );
  }

  Widget _summaryValue(
    BuildContext context,
    String label,
    double value, {
    bool prominent = false,
  }) {
    final style = prominent
        ? Theme.of(context).textTheme.titleLarge?.copyWith(
              color: Theme.of(context).colorScheme.primary,
              fontWeight: FontWeight.w800,
            )
        : Theme.of(context).textTheme.bodyMedium;

    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 5),
      child: Row(
        children: [
          Expanded(child: Text(label)),
          Text(_money(value), style: style),
        ],
      ),
    );
  }

  Widget _buildMedicineTable(BuildContext context) {
    final theme = Theme.of(context);

    if (_items.isEmpty) {
      return Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(
              Icons.medication_outlined,
              size: 42,
              color: theme.colorScheme.onSurfaceVariant,
            ),
            const SizedBox(height: 8),
            const Text('No medicines added to this bill'),
            const SizedBox(height: 4),
            Text(
              'Select Add Medicine to start a sale.',
              style: theme.textTheme.bodySmall,
            ),
          ],
        ),
      );
    }

    return SingleChildScrollView(
      scrollDirection: Axis.horizontal,
      child: ConstrainedBox(
        constraints: const BoxConstraints(minWidth: 1280),
        child: SingleChildScrollView(
          child: DataTable(
            headingRowColor: WidgetStatePropertyAll(
              theme.colorScheme.surfaceContainerHighest,
            ),
            columnSpacing: 20,
            horizontalMargin: 16,
            columns: const [
              DataColumn(label: Text('#')),
              DataColumn(label: Text('Medicine')),
              DataColumn(label: Text('Stock')),
              DataColumn(label: Text('Qty')),
              DataColumn(label: Text('MRP / Rate')),
              DataColumn(label: Text('Discount')),
              DataColumn(label: Text('Taxable')),
              DataColumn(label: Text('CGST')),
              DataColumn(label: Text('SGST')),
              DataColumn(label: Text('Amount')),
              DataColumn(label: Text('')),
            ],
            rows: List.generate(_items.length, (index) {
              final item = _items[index];

              return DataRow(
                cells: [
                  DataCell(Text('${index + 1}')),
                  DataCell(
                    SizedBox(
                      width: 190,
                      child: Text(
                        item.product.name,
                        overflow: TextOverflow.ellipsis,
                      ),
                    ),
                  ),
                  DataCell(Text('${item.availableStock}')),
                  DataCell(
                    SizedBox(
                      width: 100,
                      child: Row(
                        children: [
                          IconButton(
                            tooltip: 'Decrease quantity',
                            visualDensity: VisualDensity.compact,
                            onPressed: _submitting
                                ? null
                                : () => _changeQuantity(item, -1),
                            icon: const Icon(Icons.remove, size: 18),
                          ),
                          Text('${item.quantity}'),
                          IconButton(
                            tooltip: 'Increase quantity',
                            visualDensity: VisualDensity.compact,
                            onPressed: _submitting
                                ? null
                                : () => _changeQuantity(item, 1),
                            icon: const Icon(Icons.add, size: 18),
                          ),
                        ],
                      ),
                    ),
                  ),
                  DataCell(Text(_money(item.sellingRate))),
                  DataCell(
                    SizedBox(
                      width: 95,
                      child: TextField(
                        controller: item.discountController,
                        enabled: !_submitting,
                        keyboardType:
                            const TextInputType.numberWithOptions(
                          decimal: true,
                        ),
                        decoration: const InputDecoration(
                          prefixText: '₹ ',
                          isDense: true,
                          contentPadding: EdgeInsets.symmetric(
                            horizontal: 8,
                            vertical: 10,
                          ),
                        ),
                        onChanged: (value) {
                          final discount =
                              double.tryParse(value) ?? 0;

                          if (discount < 0 ||
                              discount > item.grossAmount) {
                            return;
                          }

                          setState(() => item.discountAmount = discount);
                        },
                      ),
                    ),
                  ),
                  DataCell(Text(_money(item.taxableAmount))),
                  DataCell(
                    Text('${item.product.cgstRate.toStringAsFixed(2)}%  ${_money(item.cgstAmount)}'),
                  ),
                  DataCell(
                    Text('${item.product.sgstRate.toStringAsFixed(2)}%  ${_money(item.sgstAmount)}'),
                  ),
                  DataCell(
                    Text(
                      _money(item.lineTotal),
                      style: const TextStyle(
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                  DataCell(
                    IconButton(
                      tooltip: 'Remove medicine',
                      onPressed: _submitting
                          ? null
                          : () => _removeItem(item),
                      icon: Icon(
                        Icons.delete_outline,
                        color: theme.colorScheme.error,
                      ),
                    ),
                  ),
                ],
              );
            }),
          ),
        ),
      ),
    );
  }

  Widget _buildFooter(BuildContext context) {
    final theme = Theme.of(context);
    final wide = MediaQuery.sizeOf(context).width >= 850;

    final customerSection = Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        _sectionTitle(context, 'Customer details'),
        const SizedBox(height: 12),
        TextField(
          controller: _customerNameController,
          enabled: !_submitting,
          decoration: const InputDecoration(
            labelText: 'Customer name (optional)',
            prefixIcon: Icon(Icons.person_outline),
          ),
        ),
        const SizedBox(height: 10),
        TextField(
          controller: _mobileController,
          enabled: !_submitting,
          keyboardType: TextInputType.phone,
          decoration: const InputDecoration(
            labelText: 'Mobile number (optional)',
            prefixIcon: Icon(Icons.phone_outlined),
          ),
        ),
      ],
    );

    final paymentSection = Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        _sectionTitle(context, 'Payment'),
        const SizedBox(height: 12),
        DropdownButtonFormField<String>(
          value: _paymentMethod,
          decoration: const InputDecoration(
            labelText: 'Payment method',
            prefixIcon: Icon(Icons.account_balance_wallet_outlined),
          ),
          items: const [
            DropdownMenuItem(value: 'CASH', child: Text('Cash')),
            DropdownMenuItem(value: 'UPI', child: Text('UPI')),
            DropdownMenuItem(value: 'CARD', child: Text('Card')),
          ],
          onChanged: _submitting
              ? null
              : (value) {
                  if (value == null) return;
                  setState(() => _paymentMethod = value);
                },
        ),
        if (_paymentMethod == 'CASH') ...[
          const SizedBox(height: 10),
          TextField(
            controller: _cashController,
            enabled: !_submitting,
            keyboardType: const TextInputType.numberWithOptions(
              decimal: true,
            ),
            decoration: const InputDecoration(
              labelText: 'Cash received',
              prefixText: '₹ ',
            ),
            onChanged: (_) => setState(() {}),
          ),
          const SizedBox(height: 8),
          _summaryValue(context, 'Change', _change),
        ] else ...[
          const SizedBox(height: 8),
          Text(
            'Payment confirmation is required before this payment '
            'can be marked as paid.',
            style: theme.textTheme.bodySmall?.copyWith(
              color: theme.colorScheme.onSurfaceVariant,
            ),
          ),
        ],
      ],
    );

    final totalsSection = Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        _sectionTitle(context, 'Bill summary'),
        const SizedBox(height: 8),
        _summaryValue(context, 'Subtotal (incl. GST)', _subtotal),
        _summaryValue(context, 'Item discounts', _discountTotal),
        _summaryValue(context, 'Taxable amount', _taxableTotal),
        _summaryValue(context, 'CGST', _cgstTotal),
        _summaryValue(context, 'SGST', _sgstTotal),
        const Divider(height: 20),
        _summaryValue(
          context,
          'Grand total',
          _grandTotal,
          prominent: true,
        ),
      ],
    );

    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        if (wide)
          Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(child: customerSection),
              const SizedBox(width: 20),
              Expanded(child: paymentSection),
              const SizedBox(width: 20),
              Expanded(child: totalsSection),
            ],
          )
        else
          Column(
            children: [
              customerSection,
              const SizedBox(height: 20),
              paymentSection,
              const SizedBox(height: 20),
              totalsSection,
            ],
          ),
        const SizedBox(height: 20),
        Wrap(
          alignment: WrapAlignment.end,
          spacing: 10,
          runSpacing: 10,
          children: [
            OutlinedButton.icon(
              onPressed: _submitting ? null : _clearBill,
              icon: const Icon(Icons.clear_all),
              label: const Text('Clear'),
            ),
            FilledButton.icon(
              onPressed:
                  _submitting || _items.isEmpty ? null : _saveBill,
              icon: _submitting
                  ? const SizedBox(
                      height: 18,
                      width: 18,
                      child: CircularProgressIndicator(strokeWidth: 2),
                    )
                  : const Icon(Icons.save_outlined),
              label: Text(_submitting ? 'Saving...' : 'Save Bill'),
            ),
          ],
        ),
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Scaffold(
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(20),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Row(
                children: [
                  Icon(
                    Icons.point_of_sale_outlined,
                    size: 30,
                    color: theme.colorScheme.primary,
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          'Sales Bill',
                          style: theme.textTheme.headlineSmall?.copyWith(
                            fontWeight: FontWeight.w800,
                          ),
                        ),
                        Text(
                          _lastBillNumber == null
                              ? 'Create a new medicine sale'
                              : 'Last bill: $_lastBillNumber'
                                  ' • ${_lastPaymentStatus ?? ''}',
                          style: theme.textTheme.bodySmall?.copyWith(
                            color: theme.colorScheme.onSurfaceVariant,
                          ),
                        ),
                      ],
                    ),
                  ),
                  OutlinedButton.icon(
                    onPressed: _loading || _submitting
                        ? null
                        : _addMedicine,
                    icon: const Icon(Icons.add),
                    label: const Text('Add Medicine'),
                  ),
                ],
              ),
              const SizedBox(height: 16),
              Card(
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Wrap(
                    spacing: 24,
                    runSpacing: 12,
                    crossAxisAlignment: WrapCrossAlignment.center,
                    children: [
                      _headerField(
                        'Bill number',
                        _lastBillNumber ?? 'Generated on save',
                      ),
                      _headerField('Bill date', _formattedDate),
                      _headerField(
                        'Items',
                        '${_items.fold<int>(0, (sum, item) => sum + item.quantity)}',
                      ),
                      _headerField(
                        'Payment status',
                        _lastPaymentStatus ?? 'Not saved',
                      ),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 16),
              Expanded(
                child: Card(
                  clipBehavior: Clip.antiAlias,
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      Padding(
                        padding: const EdgeInsets.fromLTRB(16, 16, 16, 8),
                        child: _sectionTitle(
                          context,
                          'Medicine items',
                          trailing: Text(
                            '${_items.length} lines',
                            style: theme.textTheme.bodySmall,
                          ),
                        ),
                      ),
                      const Divider(height: 1),
                      Expanded(child: _buildMedicineTable(context)),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 16),
              // Keep the footer compact so the medicine table has
              // enough vertical space. The footer itself scrolls when
              // its contents do not fit on shorter screens.
              SizedBox(
                height: MediaQuery.sizeOf(context).height * 0.30,
                child: Card(
                  clipBehavior: Clip.antiAlias,
                  child: Padding(
                    padding: const EdgeInsets.all(12),
                    child: SingleChildScrollView(
                      child: _buildFooter(context),
                    ),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _headerField(String label, String value) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          label,
          style: Theme.of(context).textTheme.bodySmall?.copyWith(
                color: Theme.of(context).colorScheme.onSurfaceVariant,
              ),
        ),
        const SizedBox(height: 3),
        Text(
          value,
          style: Theme.of(context).textTheme.titleSmall?.copyWith(
                fontWeight: FontWeight.w700,
              ),
        ),
      ],
    );
  }
}

class _SalesItem {
  final Product product;
  final int availableStock;
  final double sellingRate;
  int quantity;
  double discountAmount = 0;

  final TextEditingController discountController =
      TextEditingController(text: '0');

  _SalesItem({
    required this.product,
    required this.availableStock,
    required this.sellingRate,
  }) : quantity = 1;

  double get grossAmount => quantity * sellingRate;

  double get lineTotal => grossAmount - discountAmount;

  double get taxableAmount {
    final totalRate = product.cgstRate + product.sgstRate;
    if (totalRate <= 0) return _roundMoney(lineTotal);
    return _roundMoney(lineTotal / (1 + totalRate / 100));
  }

  double get cgstAmount =>
      _roundMoney(taxableAmount * product.cgstRate / 100);

  double get sgstAmount =>
      _roundMoney(taxableAmount * product.sgstRate / 100);

  double _roundMoney(double value) =>
      (value * 100).roundToDouble() / 100;

  void dispose() {
    discountController.dispose();
  }
}

class _MedicinePickerDialog extends StatefulWidget {
  final List<Product> products;

  const _MedicinePickerDialog({
    required this.products,
  });

  @override
  State<_MedicinePickerDialog> createState() =>
      _MedicinePickerDialogState();
}

class _MedicinePickerDialogState
    extends State<_MedicinePickerDialog> {
  String _query = '';

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    final filtered = widget.products.where((product) {
      return product.name.toLowerCase().contains(
            _query.trim().toLowerCase(),
          );
    }).toList();

    return AlertDialog(
      title: const Text('Select medicine'),
      content: SizedBox(
        width: 500,
        height: 450,
        child: Column(
          children: [
            TextField(
              autofocus: true,
              decoration: const InputDecoration(
                labelText: 'Search medicine',
                prefixIcon: Icon(Icons.search),
              ),
              onChanged: (value) => setState(() => _query = value),
            ),
            const SizedBox(height: 12),
            Expanded(
              child: filtered.isEmpty
                  ? const Center(child: Text('No matching medicines.'))
                  : ListView.separated(
                      itemCount: filtered.length,
                      separatorBuilder: (_, __) =>
                          const Divider(height: 1),
                      itemBuilder: (context, index) {
                        final product = filtered[index];

                        return ListTile(
                          leading: Icon(
                            Icons.medication_outlined,
                            color: theme.colorScheme.primary,
                          ),
                          title: Text(product.name),
                          trailing: const Icon(Icons.add_circle_outline),
                          onTap: () => Navigator.pop(context, product),
                        );
                      },
                    ),
            ),
          ],
        ),
      ),
      actions: [
        TextButton(
          onPressed: () => Navigator.pop(context),
          child: const Text('Cancel'),
        ),
      ],
    );
  }
}
