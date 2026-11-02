
import 'package:flutter/material.dart';

import '../services/api_service.dart';

class PurchaseHistoryPage extends StatefulWidget {
  const PurchaseHistoryPage({super.key});

  @override
  State<PurchaseHistoryPage> createState() =>
      _PurchaseHistoryPageState();
}

class _PurchaseHistoryPageState extends State<PurchaseHistoryPage> {
  final ApiService _apiService = ApiService();

  List<Map<String, dynamic>> _purchases = [];
  bool _isLoading = true;
  String? _error;

  @override
  void initState() {
    super.initState();
    _loadPurchases();
  }

  Future<void> _loadPurchases() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });

    try {
      final purchases = await _apiService.getPurchases();

      if (!mounted) return;

      setState(() {
        _purchases = purchases;
        _isLoading = false;
      });
    } catch (error) {
      if (!mounted) return;

      setState(() {
        _error = error.toString().replaceFirst('Exception: ', '');
        _isLoading = false;
      });
    }
  }

  String _formatAmount(dynamic value) {
    final amount = double.tryParse(value?.toString() ?? '');

    if (amount == null) return '—';

    return '₹${amount.toStringAsFixed(2)}';
  }

  String _displayDate(dynamic value) {
    if (value == null) return '—';

    final date = DateTime.tryParse(value.toString());

    if (date == null) return value.toString();

    return '${date.day.toString().padLeft(2, '0')}/'
        '${date.month.toString().padLeft(2, '0')}/'
        '${date.year}';
  }

  List<Map<String, dynamic>> _getItems(
    Map<String, dynamic> purchase,
  ) {
    final rawItems = purchase['items'];

    if (rawItems is! List) return [];

    return rawItems
        .whereType<Map<String, dynamic>>()
        .toList();
  }

  Widget _buildDetailRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 145,
            child: Text(
              label,
              style: TextStyle(
                color: Theme.of(context).colorScheme.onSurfaceVariant,
              ),
            ),
          ),
          Expanded(
            child: Text(
              value,
              style: const TextStyle(fontWeight: FontWeight.w500),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildPurchaseItem(Map<String, dynamic> item) {
    final productName =
        item['productName']?.toString() ??
        'Product #${item['productId'] ?? '—'}';

    final batchNumber =
        item['batchNumber']?.toString() ?? '—';

    final quantity = item['quantity']?.toString() ?? '—';

    final manufacturingDate =
        _displayDate(item['manufacturingDate']);

    final expiryDate = _displayDate(item['expiryDate']);

    final purchaseCost = _formatAmount(item['purchaseCost']);

    final mrp = _formatAmount(item['mrp']);

    final lineTotal = _formatAmount(item['lineTotal']);

    return Container(
      width: double.infinity,
      margin: const EdgeInsets.only(top: 10),
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
        border: Border.all(
          color: Theme.of(context).colorScheme.outlineVariant,
        ),
        borderRadius: BorderRadius.circular(10),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Icon(
                Icons.medication_outlined,
                color: Theme.of(context).colorScheme.primary,
              ),
              const SizedBox(width: 10),
              Expanded(
                child: Text(
                  productName,
                  style: Theme.of(context).textTheme.titleSmall?.copyWith(
                        fontWeight: FontWeight.bold,
                      ),
                ),
              ),
              Text(
                lineTotal,
                style: Theme.of(context).textTheme.titleSmall?.copyWith(
                      fontWeight: FontWeight.bold,
                    ),
              ),
            ],
          ),
          const SizedBox(height: 12),
          _buildDetailRow('Batch number', batchNumber),
          _buildDetailRow('Quantity', quantity),
          _buildDetailRow('Purchase cost / unit', purchaseCost),
          _buildDetailRow('MRP / unit', mrp),
          _buildDetailRow('Manufacturing date', manufacturingDate),
          _buildDetailRow('Expiry date', expiryDate),
        ],
      ),
    );
  }

  Widget _buildPurchaseCard(Map<String, dynamic> purchase) {
    final invoiceNumber =
        purchase['invoiceNumber']?.toString() ?? '—';

    final supplierName =
        purchase['supplierName']?.toString() ??
        'Supplier #${purchase['supplierId'] ?? '—'}';

    final purchaseDate = _displayDate(purchase['purchaseDate']);

    final total = _formatAmount(purchase['totalAmount']);

    final items = _getItems(purchase);

    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      clipBehavior: Clip.antiAlias,
      child: ExpansionTile(
        tilePadding: const EdgeInsets.symmetric(
          horizontal: 16,
          vertical: 8,
        ),
        childrenPadding: const EdgeInsets.fromLTRB(16, 0, 16, 16),
        leading: CircleAvatar(
          backgroundColor:
              Theme.of(context).colorScheme.secondaryContainer,
          child: Icon(
            Icons.receipt_long_outlined,
            color: Theme.of(context).colorScheme.onSecondaryContainer,
          ),
        ),
        title: Text(
          invoiceNumber,
          style: const TextStyle(fontWeight: FontWeight.w600),
        ),
        subtitle: Padding(
          padding: const EdgeInsets.only(top: 6),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(supplierName),
              const SizedBox(height: 4),
              Text('Date: $purchaseDate'),
            ],
          ),
        ),
        trailing: SizedBox(
          width: 112,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.end,
            children: [
              Text(
                total,
                textAlign: TextAlign.end,
                style: const TextStyle(fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 4),
              Text(
                '${items.length} ${items.length == 1 ? 'item' : 'items'}',
                style: Theme.of(context).textTheme.bodySmall,
              ),
            ],
          ),
        ),
        children: [
          const Divider(),
          Align(
            alignment: Alignment.centerLeft,
            child: Text(
              'Purchase details',
              style: Theme.of(context).textTheme.titleSmall?.copyWith(
                    fontWeight: FontWeight.bold,
                  ),
            ),
          ),
          const SizedBox(height: 8),
          _buildDetailRow('Invoice number', invoiceNumber),
          _buildDetailRow('Supplier', supplierName),
          _buildDetailRow('Purchase date', purchaseDate),
          _buildDetailRow('Total amount', total),
          const SizedBox(height: 12),
          Align(
            alignment: Alignment.centerLeft,
            child: Text(
              'Purchased items',
              style: Theme.of(context).textTheme.titleSmall?.copyWith(
                    fontWeight: FontWeight.bold,
                  ),
            ),
          ),
          if (items.isEmpty)
            const Padding(
              padding: EdgeInsets.symmetric(vertical: 16),
              child: Text('No item details available.'),
            )
          else
            ...items.map(_buildPurchaseItem),
          const SizedBox(height: 8),
          Align(
            alignment: Alignment.centerRight,
            child: Text(
              'Purchase total: $total',
              style: Theme.of(context).textTheme.titleMedium?.copyWith(
                    fontWeight: FontWeight.bold,
                  ),
            ),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Padding(
        padding: const EdgeInsets.all(20),
        child: Center(
          child: ConstrainedBox(
            constraints: const BoxConstraints(maxWidth: 1000),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Row(
                  children: [
                    Expanded(
                      child: Text(
                        'Purchase History',
                        style: Theme.of(context)
                            .textTheme
                            .headlineSmall
                            ?.copyWith(fontWeight: FontWeight.bold),
                      ),
                    ),
                    IconButton(
                      tooltip: 'Refresh purchases',
                      onPressed: _isLoading ? null : _loadPurchases,
                      icon: const Icon(Icons.refresh),
                    ),
                  ],
                ),
                const SizedBox(height: 4),
                Text(
                  'View supplier invoices and purchased batch details.',
                  style: Theme.of(context).textTheme.bodyMedium,
                ),
                const SizedBox(height: 16),
                Expanded(
                  child: _isLoading
                      ? const Center(
                          child: CircularProgressIndicator(),
                        )
                      : _error != null
                          ? Center(
                              child: Padding(
                                padding: const EdgeInsets.all(20),
                                child: Column(
                                  mainAxisSize: MainAxisSize.min,
                                  children: [
                                    const Icon(
                                      Icons.error_outline,
                                      size: 42,
                                    ),
                                    const SizedBox(height: 12),
                                    const Text(
                                      'Failed to load purchase history.',
                                      style: TextStyle(
                                        fontWeight: FontWeight.bold,
                                      ),
                                    ),
                                    const SizedBox(height: 8),
                                    Text(
                                      _error!,
                                      textAlign: TextAlign.center,
                                    ),
                                    const SizedBox(height: 16),
                                    FilledButton.icon(
                                      onPressed: _loadPurchases,
                                      icon: const Icon(Icons.refresh),
                                      label: const Text('Retry'),
                                    ),
                                  ],
                                ),
                              ),
                            )
                          : _purchases.isEmpty
                              ? RefreshIndicator(
                                  onRefresh: _loadPurchases,
                                  child: ListView(
                                    physics:
                                        const AlwaysScrollableScrollPhysics(),
                                    children: const [
                                      SizedBox(height: 140),
                                      Icon(
                                        Icons.receipt_long_outlined,
                                        size: 48,
                                      ),
                                      SizedBox(height: 12),
                                      Center(
                                        child: Text('No purchases found.'),
                                      ),
                                    ],
                                  ),
                                )
                              : RefreshIndicator(
                                  onRefresh: _loadPurchases,
                                  child: ListView.builder(
                                    physics:
                                        const AlwaysScrollableScrollPhysics(),
                                    itemCount: _purchases.length,
                                    itemBuilder: (context, index) {
                                      return _buildPurchaseCard(
                                        _purchases[index],
                                      );
                                    },
                                  ),
                                ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
