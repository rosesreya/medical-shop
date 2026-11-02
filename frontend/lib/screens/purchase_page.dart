
import 'package:flutter/material.dart';

import '../models/product.dart';
import '../models/supplier.dart';
import '../services/api_service.dart';

class PurchasePage extends StatefulWidget {
  const PurchasePage({super.key});

  @override
  State<PurchasePage> createState() => _PurchasePageState();
}

class _PurchasePageState extends State<PurchasePage> {
  final ApiService _apiService = ApiService();
  final _formKey = GlobalKey<FormState>();

  List<Supplier> _suppliers = [];
  List<Product> _products = [];

  Supplier? _selectedSupplier;
  DateTime _purchaseDate = DateTime.now();

  final TextEditingController _invoiceController =
      TextEditingController();

  final List<_PurchaseLine> _items = [_PurchaseLine()];

  bool _isLoading = true;
  bool _isSubmitting = false;
  String? _loadError;

  @override
  void initState() {
    super.initState();
    _loadInitialData();
  }

  @override
  void dispose() {
    _invoiceController.dispose();

    for (final item in _items) {
      item.dispose();
    }

    super.dispose();
  }

  Future<void> _loadInitialData() async {
    setState(() {
      _isLoading = true;
      _loadError = null;
    });

    try {
      final results = await Future.wait([
        _apiService.getActiveSuppliers(),
        _apiService.getProducts(),
      ]);

      final suppliers = results[0] as List<Supplier>;
      final products = results[1] as List<Product>;

      if (!mounted) return;

      setState(() {
        _suppliers = suppliers;
        _products = products.where((product) => product.active).toList();
        _isLoading = false;
      });
    } catch (error) {
      if (!mounted) return;

      setState(() {
        _loadError = error.toString();
        _isLoading = false;
      });
    }
  }

  String _formatDate(DateTime date) {
    final month = date.month.toString().padLeft(2, '0');
    final day = date.day.toString().padLeft(2, '0');

    return '${date.year}-$month-$day';
  }

  Future<DateTime?> _pickDate({
    required DateTime? initialDate,
    required DateTime firstDate,
    required DateTime lastDate,
  }) {
    return showDatePicker(
      context: context,
      initialDate: initialDate ?? DateTime.now(),
      firstDate: firstDate,
      lastDate: lastDate,
    );
  }

  Future<void> _selectPurchaseDate() async {
    final picked = await _pickDate(
      initialDate: _purchaseDate,
      firstDate: DateTime(2000),
      lastDate: DateTime(2100),
    );

    if (picked != null && mounted) {
      setState(() {
        _purchaseDate = picked;
      });
    }
  }

  Future<void> _selectItemDate(
    _PurchaseLine item, {
    required bool isManufacturingDate,
  }) async {
    final currentDate = isManufacturingDate
        ? item.manufacturingDate
        : item.expiryDate;

    final picked = await _pickDate(
      initialDate: currentDate,
      firstDate: DateTime(2000),
      lastDate: DateTime(2100),
    );

    if (picked == null || !mounted) return;

    setState(() {
      if (isManufacturingDate) {
        item.manufacturingDate = picked;
      } else {
        item.expiryDate = picked;
      }
    });
  }

  void _addItem() {
    setState(() {
      _items.add(_PurchaseLine());
    });
  }

  void _removeItem(int index) {
    if (_items.length == 1) {
      _showMessage('At least one purchase item is required.');
      return;
    }

    setState(() {
      _items[index].dispose();
      _items.removeAt(index);
    });
  }

  double _lineTotal(_PurchaseLine item) {
    final quantity = int.tryParse(item.quantityController.text) ?? 0;
    final cost = double.tryParse(item.purchaseCostController.text) ?? 0;

    return quantity * cost;
  }

  double get _grandTotal {
    return _items.fold<double>(
      0,
      (total, item) => total + _lineTotal(item),
    );
  }

  String _money(double amount) => '₹${amount.toStringAsFixed(2)}';

  Widget _sectionTitle(String title, IconData icon) {
    return Row(
      children: [
        Icon(icon, size: 21, color: Theme.of(context).colorScheme.primary),
        const SizedBox(width: 8),
        Text(
          title,
          style: Theme.of(context).textTheme.titleMedium?.copyWith(
                fontWeight: FontWeight.bold,
              ),
        ),
      ],
    );
  }

  Widget _buildDateField({
    required String label,
    required DateTime? date,
    required VoidCallback onTap,
  }) {
    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(8),
      child: InputDecorator(
        decoration: InputDecoration(
          labelText: label,
          border: const OutlineInputBorder(),
          suffixIcon: const Icon(Icons.calendar_month_outlined),
        ),
        child: Text(
          date == null ? 'Select date' : _formatDate(date),
        ),
      ),
    );
  }

  Widget _buildTextField({
    required String label,
    required TextEditingController controller,
    required String? Function(String?) validator,
    TextInputType keyboardType = TextInputType.text,
    void Function(String)? onChanged,
  }) {
    return TextFormField(
      controller: controller,
      decoration: InputDecoration(
        labelText: label,
        border: const OutlineInputBorder(),
      ),
      keyboardType: keyboardType,
      validator: validator,
      onChanged: onChanged,
    );
  }

  String? _requiredValidator(String? value) {
    if (value == null || value.trim().isEmpty) {
      return 'Required';
    }

    return null;
  }

  String? _positiveIntegerValidator(String? value) {
    if (value == null || value.trim().isEmpty) {
      return 'Required';
    }

    final quantity = int.tryParse(value.trim());

    if (quantity == null || quantity <= 0) {
      return 'Enter a whole number above zero';
    }

    return null;
  }

  String? _nonNegativeNumberValidator(String? value) {
    if (value == null || value.trim().isEmpty) {
      return 'Required';
    }

    final number = double.tryParse(value.trim());

    if (number == null || !number.isFinite || number < 0) {
      return 'Enter a valid non-negative amount';
    }

    return null;
  }

  Widget _buildPurchaseItem(_PurchaseLine item, int index) {
    return Card(
      margin: const EdgeInsets.only(top: 12),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Expanded(
                  child: Text(
                    'Item ${index + 1}',
                    style: Theme.of(context).textTheme.titleSmall?.copyWith(
                          fontWeight: FontWeight.bold,
                        ),
                  ),
                ),
                IconButton(
                  tooltip: 'Remove item',
                  onPressed: _isSubmitting
                      ? null
                      : () => _removeItem(index),
                  icon: const Icon(Icons.delete_outline),
                ),
              ],
            ),
            const SizedBox(height: 12),
            DropdownButtonFormField<int>(
              initialValue: item.productId,
              isExpanded: true,
              decoration: const InputDecoration(
                labelText: 'Medicine / Product *',
                border: OutlineInputBorder(),
              ),
              items: _products
                  .map(
                    (product) => DropdownMenuItem<int>(
                      value: product.id,
                      child: Text(
                        product.name,
                        overflow: TextOverflow.ellipsis,
                      ),
                    ),
                  )
                  .toList(),
              onChanged: _isSubmitting
                  ? null
                  : (id) {
                      setState(() {
                        item.productId = id;
                      });
                    },
              validator: (_) {
                if (item.productId == null) {
                  return 'Select a product';
                }

                return null;
              },
            ),
            const SizedBox(height: 12),
            _buildTextField(
              label: 'Batch number *',
              controller: item.batchNumberController,
              validator: _requiredValidator,
            ),
            const SizedBox(height: 12),
            LayoutBuilder(
              builder: (context, constraints) {
                final stacked = constraints.maxWidth < 480;

                final manufacturingField = _buildDateField(
                  label: 'Manufacturing date *',
                  date: item.manufacturingDate,
                  onTap: () => _selectItemDate(
                    item,
                    isManufacturingDate: true,
                  ),
                );

                final expiryField = _buildDateField(
                  label: 'Expiry date *',
                  date: item.expiryDate,
                  onTap: () => _selectItemDate(
                    item,
                    isManufacturingDate: false,
                  ),
                );

                if (stacked) {
                  return Column(
                    children: [
                      manufacturingField,
                      const SizedBox(height: 12),
                      expiryField,
                    ],
                  );
                }

                return Row(
                  children: [
                    Expanded(child: manufacturingField),
                    const SizedBox(width: 12),
                    Expanded(child: expiryField),
                  ],
                );
              },
            ),
            const SizedBox(height: 12),
            LayoutBuilder(
              builder: (context, constraints) {
                final stacked = constraints.maxWidth < 480;

                final quantityField = _buildTextField(
                  label: 'Quantity *',
                  controller: item.quantityController,
                  keyboardType: TextInputType.number,
                  validator: _positiveIntegerValidator,
                  onChanged: (_) => setState(() {}),
                );

                final costField = _buildTextField(
                  label: 'Purchase cost / unit *',
                  controller: item.purchaseCostController,
                  keyboardType:
                      const TextInputType.numberWithOptions(decimal: true),
                  validator: _nonNegativeNumberValidator,
                  onChanged: (_) => setState(() {}),
                );

                final mrpField = _buildTextField(
                  label: 'MRP / unit *',
                  controller: item.mrpController,
                  keyboardType:
                      const TextInputType.numberWithOptions(decimal: true),
                  validator: _nonNegativeNumberValidator,
                );

                if (stacked) {
                  return Column(
                    children: [
                      quantityField,
                      const SizedBox(height: 12),
                      costField,
                      const SizedBox(height: 12),
                      mrpField,
                    ],
                  );
                }

                return Row(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Expanded(child: quantityField),
                    const SizedBox(width: 12),
                    Expanded(child: costField),
                    const SizedBox(width: 12),
                    Expanded(child: mrpField),
                  ],
                );
              },
            ),
            const SizedBox(height: 12),
            Align(
              alignment: Alignment.centerRight,
              child: Text(
                'Line total: ${_money(_lineTotal(item))}',
                style: Theme.of(context).textTheme.titleSmall?.copyWith(
                      fontWeight: FontWeight.bold,
                    ),
              ),
            ),
            if (item.manufacturingDate != null &&
                item.expiryDate != null &&
                !item.expiryDate!.isAfter(item.manufacturingDate!))
              const Align(
                alignment: Alignment.centerLeft,
                child: Padding(
                  padding: EdgeInsets.only(top: 8),
                  child: Text(
                    'Expiry date must be after manufacturing date.',
                    style: TextStyle(color: Colors.red),
                  ),
                ),
              ),
          ],
        ),
      ),
    );
  }

  Future<void> _showMessage(String message, {String title = 'Notice'}) async {
    await showDialog<void>(
      context: context,
      builder: (dialogContext) => AlertDialog(
        title: Text(title),
        content: Text(message),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(dialogContext).pop(),
            child: const Text('OK'),
          ),
        ],
      ),
    );
  }

  Future<void> _submitPurchase() async {
    FocusScope.of(context).unfocus();

    final formIsValid = _formKey.currentState?.validate() ?? false;

    if (!formIsValid) return;

    if (_selectedSupplier == null) {
      await _showMessage('Please select a supplier.');
      return;
    }

    for (var i = 0; i < _items.length; i++) {
      final item = _items[i];

      if (item.manufacturingDate == null || item.expiryDate == null) {
        await _showMessage(
          'Select manufacturing and expiry dates for item ${i + 1}.',
        );
        return;
      }

      if (!item.expiryDate!.isAfter(item.manufacturingDate!)) {
        await _showMessage(
          'Expiry date must be after manufacturing date for item ${i + 1}.',
        );
        return;
      }

      if (!item.expiryDate!.isAfter(_purchaseDate)) {
        await _showMessage(
          'Expiry date must be after the purchase date for item ${i + 1}.',
        );
        return;
      }

      if (item.manufacturingDate!.isAfter(_purchaseDate)) {
        await _showMessage(
          'Manufacturing date cannot be after the purchase date '
          'for item ${i + 1}.',
        );
        return;
      }

      final cost = double.parse(item.purchaseCostController.text.trim());
      final mrp = double.parse(item.mrpController.text.trim());

      if (mrp < cost) {
        await _showMessage(
          'MRP cannot be lower than purchase cost for item ${i + 1}.',
        );
        return;
      }
    }

    final seenBatches = <String>{};

    for (final item in _items) {
      final key =
          '${item.productId}:${item.batchNumberController.text.trim().toLowerCase()}';

      if (!seenBatches.add(key)) {
        await _showMessage(
          'The same product and batch number cannot be entered twice '
          'in one purchase.',
        );
        return;
      }
    }

    setState(() {
      _isSubmitting = true;
    });

    try {
      final purchaseData = <String, dynamic>{
        'supplierId': _selectedSupplier!.id,
        'invoiceNumber': _invoiceController.text.trim(),
        'purchaseDate': _formatDate(_purchaseDate),
        'items': _items.map((item) {
          return <String, dynamic>{
            'productId': item.productId,
            'batchNumber': item.batchNumberController.text.trim(),
            'manufacturingDate': _formatDate(item.manufacturingDate!),
            'expiryDate': _formatDate(item.expiryDate!),
            'quantity': int.parse(item.quantityController.text.trim()),
            'purchaseCost':
                double.parse(item.purchaseCostController.text.trim()),
            'mrp': double.parse(item.mrpController.text.trim()),
          };
        }).toList(),
      };

      final response = await _apiService.createPurchase(purchaseData);

      if (!mounted) return;

      await _showMessage(
        'Purchase recorded successfully.\n'
        'Invoice: ${response['invoiceNumber'] ?? _invoiceController.text.trim()}\n'
        'Total: ${_money(_grandTotal)}',
        title: 'Purchase successful',
      );

      if (!mounted) return;

      setState(() {
        _invoiceController.clear();
        _purchaseDate = DateTime.now();
        _selectedSupplier = null;

        for (final item in _items) {
          item.dispose();
        }

        _items
          ..clear()
          ..add(_PurchaseLine());
      });
    } catch (error) {
      if (!mounted) return;

      await _showMessage(
        error.toString().replaceFirst('Exception: ', ''),
        title: 'Purchase failed',
      );
    } finally {
      if (mounted) {
        setState(() {
          _isSubmitting = false;
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Purchase Entry'),
        actions: [
          IconButton(
            tooltip: 'Reload suppliers and products',
            onPressed: _isLoading || _isSubmitting ? null : _loadInitialData,
            icon: const Icon(Icons.refresh),
          ),
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _loadError != null
              ? Center(
                  child: Padding(
                    padding: const EdgeInsets.all(24),
                    child: Column(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        const Icon(Icons.error_outline, size: 42),
                        const SizedBox(height: 12),
                        const Text('Failed to load purchase data.'),
                        const SizedBox(height: 8),
                        Text(
                          _loadError!,
                          textAlign: TextAlign.center,
                        ),
                        const SizedBox(height: 16),
                        FilledButton.icon(
                          onPressed: _loadInitialData,
                          icon: const Icon(Icons.refresh),
                          label: const Text('Retry'),
                        ),
                      ],
                    ),
                  ),
                )
              : _suppliers.isEmpty || _products.isEmpty
                  ? Center(
                      child: Padding(
                        padding: const EdgeInsets.all(24),
                        child: Column(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            const Icon(Icons.inventory_2_outlined, size: 42),
                            const SizedBox(height: 12),
                            Text(
                              _suppliers.isEmpty
                                  ? 'No active suppliers available.'
                                  : 'No active products available.',
                              textAlign: TextAlign.center,
                            ),
                            const SizedBox(height: 16),
                            OutlinedButton.icon(
                              onPressed: _loadInitialData,
                              icon: const Icon(Icons.refresh),
                              label: const Text('Reload'),
                            ),
                          ],
                        ),
                      ),
                    )
                  : Form(
                      key: _formKey,
                      child: Column(
                        children: [
                          Expanded(
                            child: SingleChildScrollView(
                              padding: const EdgeInsets.all(20),
                              child: Center(
                                child: ConstrainedBox(
                                  constraints:
                                      const BoxConstraints(maxWidth: 1000),
                                  child: Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.stretch,
                                    children: [
                                      Text(
                                        'Record Supplier Purchase',
                                        style: Theme.of(context)
                                            .textTheme
                                            .headlineSmall
                                            ?.copyWith(
                                              fontWeight: FontWeight.bold,
                                            ),
                                      ),
                                      const SizedBox(height: 4),
                                      Text(
                                        'Enter the supplier invoice and '
                                        'the batches received.',
                                        style: Theme.of(context)
                                            .textTheme
                                            .bodyMedium,
                                      ),
                                      const SizedBox(height: 20),
                                      Card(
                                        child: Padding(
                                          padding: const EdgeInsets.all(16),
                                          child: Column(
                                            crossAxisAlignment:
                                                CrossAxisAlignment.start,
                                            children: [
                                              _sectionTitle(
                                                'Purchase details',
                                                Icons.receipt_long_outlined,
                                              ),
                                              const SizedBox(height: 16),
                                              DropdownButtonFormField<int>(
                                                initialValue:
                                                    _selectedSupplier?.id,
                                                isExpanded: true,
                                                decoration:
                                                    const InputDecoration(
                                                  labelText: 'Supplier *',
                                                  border: OutlineInputBorder(),
                                                ),
                                                items: _suppliers
                                                    .map(
                                                      (supplier) =>
                                                          DropdownMenuItem<int>(
                                                        value: supplier.id,
                                                        child: Text(
                                                          supplier.name,
                                                          overflow: TextOverflow
                                                              .ellipsis,
                                                        ),
                                                      ),
                                                    )
                                                    .toList(),
                                                onChanged: _isSubmitting
                                                    ? null
                                                    : (id) {
                                                        setState(() {
                                                          _selectedSupplier =
                                                              _suppliers
                                                                  .where(
                                                                    (supplier) =>
                                                                        supplier
                                                                            .id ==
                                                                        id,
                                                                  )
                                                                  .firstOrNull;
                                                        });
                                                      },
                                                validator: (_) =>
                                                    _selectedSupplier == null
                                                        ? 'Select a supplier'
                                                        : null,
                                              ),
                                              const SizedBox(height: 12),
                                              _buildTextField(
                                                label: 'Supplier invoice number *',
                                                controller: _invoiceController,
                                                validator: _requiredValidator,
                                              ),
                                              const SizedBox(height: 12),
                                              _buildDateField(
                                                label: 'Purchase date *',
                                                date: _purchaseDate,
                                                onTap: _isSubmitting
                                                    ? () {}
                                                    : _selectPurchaseDate,
                                              ),
                                            ],
                                          ),
                                        ),
                                      ),
                                      const SizedBox(height: 12),
                                      Card(
                                        child: Padding(
                                          padding: const EdgeInsets.all(16),
                                          child: Column(
                                            crossAxisAlignment:
                                                CrossAxisAlignment.start,
                                            children: [
                                              Row(
                                                children: [
                                                  Expanded(
                                                    child: _sectionTitle(
                                                      'Purchase items',
                                                      Icons.medication_outlined,
                                                    ),
                                                  ),
                                                  FilledButton.tonalIcon(
                                                    onPressed: _isSubmitting
                                                        ? null
                                                        : _addItem,
                                                    icon: const Icon(
                                                      Icons.add,
                                                    ),
                                                    label: const Text(
                                                      'Add item',
                                                    ),
                                                  ),
                                                ],
                                              ),
                                              const SizedBox(height: 4),
                                              Text(
                                                'Each item represents one '
                                                'product and batch.',
                                                style: Theme.of(context)
                                                    .textTheme
                                                    .bodySmall,
                                              ),
                                              ..._items.asMap().entries.map(
                                                    (entry) =>
                                                        _buildPurchaseItem(
                                                      entry.value,
                                                      entry.key,
                                                    ),
                                                  ),
                                            ],
                                          ),
                                        ),
                                      ),
                                    ],
                                  ),
                                ),
                              ),
                            ),
                          ),
                          Material(
                            elevation: 8,
                            color: Theme.of(context)
                                .colorScheme
                                .surfaceContainer,
                            child: Center(
                              child: ConstrainedBox(
                                constraints:
                                    const BoxConstraints(maxWidth: 1000),
                                child: Padding(
                                  padding: const EdgeInsets.all(16),
                                  child: Row(
                                    children: [
                                      Expanded(
                                        child: Column(
                                          crossAxisAlignment:
                                              CrossAxisAlignment.start,
                                          mainAxisSize: MainAxisSize.min,
                                          children: [
                                            const Text('Purchase total'),
                                            Text(
                                              _money(_grandTotal),
                                              style: Theme.of(context)
                                                  .textTheme
                                                  .titleLarge
                                                  ?.copyWith(
                                                    fontWeight: FontWeight.bold,
                                                  ),
                                            ),
                                          ],
                                        ),
                                      ),
                                      FilledButton.icon(
                                        onPressed: _isSubmitting
                                            ? null
                                            : _submitPurchase,
                                        icon: _isSubmitting
                                            ? const SizedBox(
                                                width: 18,
                                                height: 18,
                                                child:
                                                    CircularProgressIndicator(
                                                  strokeWidth: 2,
                                                ),
                                              )
                                            : const Icon(Icons.save_outlined),
                                        label: Text(
                                          _isSubmitting
                                              ? 'Saving...'
                                              : 'Save purchase',
                                        ),
                                      ),
                                    ],
                                  ),
                                ),
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),
    );
  }
}

class _PurchaseLine {
  int? productId;

  final TextEditingController batchNumberController =
      TextEditingController();
  final TextEditingController quantityController =
      TextEditingController();
  final TextEditingController purchaseCostController =
      TextEditingController();
  final TextEditingController mrpController = TextEditingController();

  DateTime? manufacturingDate;
  DateTime? expiryDate;

  void dispose() {
    batchNumberController.dispose();
    quantityController.dispose();
    purchaseCostController.dispose();
    mrpController.dispose();
  }
}
