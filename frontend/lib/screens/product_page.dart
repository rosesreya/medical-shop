
import 'package:flutter/material.dart';

import '../models/product.dart';
import '../models/category.dart';
import '../models/manufacturer.dart';
import '../models/generic_name.dart';
import '../services/api_service.dart';

class ProductPage extends StatefulWidget {
  const ProductPage({super.key});

  @override
  State<ProductPage> createState() => _ProductPageState();
}

class _ProductPageState extends State<ProductPage> {
  final ApiService _apiService = ApiService();

  List<Product> _products = [];
  List<Category> _categories = [];
  List<Manufacturer> _manufacturers = [];
  List<GenericName> _genericNames = [];

  bool _isLoading = true;
  String? _loadError;
  String _searchQuery = '';

  int _currentPage = 0;
  final int _pageSize = 10;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    setState(() {
      _isLoading = true;
      _loadError = null;
    });

    try {
      final results = await Future.wait([
        _apiService.getProducts(),
        _apiService.getCategories(),
        _apiService.getManufacturers(),
        _apiService.getGenericNames(),
      ]);

      if (!mounted) return;

      setState(() {
        _products = results[0] as List<Product>;
        _categories = results[1] as List<Category>;
        _manufacturers = results[2] as List<Manufacturer>;
        _genericNames = results[3] as List<GenericName>;
        _isLoading = false;
      });
    } catch (e) {
      if (!mounted) return;

      setState(() {
        _isLoading = false;
        _loadError = e.toString();
      });
    }
  }

  List<Product> get _filteredProducts {
    final query = _searchQuery.trim().toLowerCase();

    if (query.isEmpty) return _products;

    return _products.where((product) {
      return product.name.toLowerCase().contains(query) ||
          (product.brandName ?? '').toLowerCase().contains(query) ||
          _categoryName(product.categoryId).toLowerCase().contains(query) ||
          _manufacturerName(product.manufacturerId)
              .toLowerCase()
              .contains(query) ||
          _genericName(product.genericNameId)
              .toLowerCase()
              .contains(query);
    }).toList();
  }

  List<Product> get _currentPageProducts {
    final products = _filteredProducts;
    final start = _currentPage * _pageSize;

    if (start >= products.length) return [];

    final end = (start + _pageSize).clamp(0, products.length);
    return products.sublist(start, end);
  }

  String _categoryName(int id) {
    for (final item in _categories) {
      if (item.id == id) return item.name;
    }
    return 'Unknown category';
  }

  String _manufacturerName(int id) {
    for (final item in _manufacturers) {
      if (item.id == id) return item.name;
    }
    return 'Unknown manufacturer';
  }

  String _genericName(int id) {
    for (final item in _genericNames) {
      if (item.id == id) return item.name;
    }
    return 'Unknown generic name';
  }

  Future<void> _showMessage(String message) async {
    if (!mounted) return;

    await showDialog<void>(
      context: context,
      builder: (dialogContext) => AlertDialog(
        content: Text(message),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(dialogContext),
            child: const Text('OK'),
          ),
        ],
      ),
    );
  }

  Future<void> _openProductDialog({Product? product}) async {
    final formKey = GlobalKey<FormState>();

    final nameController = TextEditingController(
      text: product?.name ?? '',
    );
    final brandController = TextEditingController(
      text: product?.brandName ?? '',
    );
    final strengthController = TextEditingController(
      text: product?.strength ?? '',
    );
    final dosageFormController = TextEditingController(
      text: product?.dosageForm ?? '',
    );
    final descriptionController = TextEditingController(
      text: product?.description ?? '',
    );

    // Unit and GST configuration
    final baseUnitController = TextEditingController(
      text: product?.baseUnit ?? 'UNIT',
    );
    final packUnitController = TextEditingController(
      text: product?.packUnit ?? 'UNIT',
    );
    final unitsPerPackController = TextEditingController(
      text: (product?.unitsPerPack ?? 1).toString(),
    );
    final cgstRateController = TextEditingController(
      text: (product?.cgstRate ?? 0.0).toString(),
    );
    final sgstRateController = TextEditingController(
      text: (product?.sgstRate ?? 0.0).toString(),
    );

    int? categoryId = product?.categoryId;
    int? manufacturerId = product?.manufacturerId;
    int? genericNameId = product?.genericNameId;

    bool isSaving = false;

    final dialogCategories =
        _categories.where((x) => x.active || x.id == categoryId).toList();
    final dialogManufacturers = _manufacturers
        .where((x) => x.active || x.id == manufacturerId)
        .toList();
    final dialogGenericNames = _genericNames
        .where((x) => x.active || x.id == genericNameId)
        .toList();

    try {
      await showDialog<void>(
        context: context,
        barrierDismissible: false,
        builder: (dialogContext) {
          return StatefulBuilder(
            builder: (context, setDialogState) {
              return AlertDialog(
                title: Text(
                  product == null ? 'Add Product' : 'Edit Product',
                ),
                content: SizedBox(
                  width: 500,
                  child: SingleChildScrollView(
                    child: Form(
                      key: formKey,
                      child: Column(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          TextFormField(
                            controller: nameController,
                            decoration: const InputDecoration(
                              labelText: 'Product Name *',
                              border: OutlineInputBorder(),
                            ),
                            validator: (value) {
                              if (value == null ||
                                  value.trim().isEmpty) {
                                return 'Please enter a product name';
                              }
                              return null;
                            },
                          ),
                          const SizedBox(height: 14),
                          TextFormField(
                            controller: brandController,
                            decoration: const InputDecoration(
                              labelText: 'Brand Name',
                              border: OutlineInputBorder(),
                            ),
                          ),
                          const SizedBox(height: 14),
                          DropdownButtonFormField<int>(
                            value: categoryId,
                            isExpanded: true,
                            decoration: const InputDecoration(
                              labelText: 'Category *',
                              border: OutlineInputBorder(),
                            ),
                            items: dialogCategories.map((item) {
                              return DropdownMenuItem<int>(
                                value: item.id,
                                child: Text(
                                  '${item.name}${item.active ? '' : ' (Inactive)'}',
                                ),
                              );
                            }).toList(),
                            onChanged: (value) =>
                                setDialogState(() => categoryId = value),
                            validator: (value) => value == null
                                ? 'Select a category'
                                : null,
                          ),
                          const SizedBox(height: 14),
                          DropdownButtonFormField<int>(
                            value: manufacturerId,
                            isExpanded: true,
                            decoration: const InputDecoration(
                              labelText: 'Manufacturer *',
                              border: OutlineInputBorder(),
                            ),
                            items: dialogManufacturers.map((item) {
                              return DropdownMenuItem<int>(
                                value: item.id,
                                child: Text(
                                  '${item.name}${item.active ? '' : ' (Inactive)'}',
                                ),
                              );
                            }).toList(),
                            onChanged: (value) {
                              setDialogState(
                                () => manufacturerId = value,
                              );
                            },
                            validator: (value) => value == null
                                ? 'Select a manufacturer'
                                : null,
                          ),
                          const SizedBox(height: 14),
                          DropdownButtonFormField<int>(
                            value: genericNameId,
                            isExpanded: true,
                            decoration: const InputDecoration(
                              labelText: 'Generic Name *',
                              border: OutlineInputBorder(),
                            ),
                            items: dialogGenericNames.map((item) {
                              return DropdownMenuItem<int>(
                                value: item.id,
                                child: Text(
                                  '${item.name}${item.active ? '' : ' (Inactive)'}',
                                ),
                              );
                            }).toList(),
                            onChanged: (value) {
                              setDialogState(
                                () => genericNameId = value,
                              );
                            },
                            validator: (value) => value == null
                                ? 'Select a generic name'
                                : null,
                          ),
                          const SizedBox(height: 14),
                          TextFormField(
                            controller: strengthController,
                            decoration: const InputDecoration(
                              labelText: 'Strength',
                              hintText: 'e.g. 500 mg',
                              border: OutlineInputBorder(),
                            ),
                          ),
                          const SizedBox(height: 14),
                          TextFormField(
                            controller: dosageFormController,
                            decoration: const InputDecoration(
                              labelText: 'Dosage Form',
                              hintText: 'e.g. Tablet, Syrup, Capsule',
                              border: OutlineInputBorder(),
                            ),
                          ),
                          const SizedBox(height: 14),
                          TextFormField(
                            controller: descriptionController,
                            maxLines: 3,
                            decoration: const InputDecoration(
                              labelText: 'Description',
                              border: OutlineInputBorder(),
                            ),
                          ),

                          // Unit configuration
                          const SizedBox(height: 20),
                          const Align(
                            alignment: Alignment.centerLeft,
                            child: Text(
                              'Unit Configuration',
                              style: TextStyle(
                                fontSize: 16,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                          ),
                          const SizedBox(height: 12),
                          TextFormField(
                            controller: baseUnitController,
                            decoration: const InputDecoration(
                              labelText: 'Base Unit *',
                              hintText: 'e.g. TABLET, CAPSULE, ML',
                              border: OutlineInputBorder(),
                            ),
                            validator: (value) {
                              if (value == null ||
                                  value.trim().isEmpty) {
                                return 'Please enter a base unit';
                              }
                              return null;
                            },
                          ),
                          const SizedBox(height: 14),
                          TextFormField(
                            controller: packUnitController,
                            decoration: const InputDecoration(
                              labelText: 'Pack Unit *',
                              hintText: 'e.g. STRIP, BOTTLE, BOX',
                              border: OutlineInputBorder(),
                            ),
                            validator: (value) {
                              if (value == null ||
                                  value.trim().isEmpty) {
                                return 'Please enter a pack unit';
                              }
                              return null;
                            },
                          ),
                          const SizedBox(height: 14),
                          TextFormField(
                            controller: unitsPerPackController,
                            keyboardType: TextInputType.number,
                            decoration: const InputDecoration(
                              labelText: 'Units Per Pack *',
                              hintText: 'e.g. 10 tablets per strip',
                              border: OutlineInputBorder(),
                            ),
                            validator: (value) {
                              final units = int.tryParse(
                                value?.trim() ?? '',
                              );
                              if (units == null || units < 1) {
                                return 'Enter a whole number of at least 1';
                              }
                              return null;
                            },
                          ),

                          // GST configuration
                          const SizedBox(height: 20),
                          const Align(
                            alignment: Alignment.centerLeft,
                            child: Text(
                              'GST Configuration',
                              style: TextStyle(
                                fontSize: 16,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                          ),
                          const SizedBox(height: 12),
                          TextFormField(
                            controller: cgstRateController,
                            keyboardType:
                                const TextInputType.numberWithOptions(
                              decimal: true,
                            ),
                            decoration: const InputDecoration(
                              labelText: 'CGST Rate (%) *',
                              border: OutlineInputBorder(),
                            ),
                            validator: (value) {
                              final rate = double.tryParse(
                                value?.trim() ?? '',
                              );
                              if (rate == null ||
                                  !rate.isFinite ||
                                  rate < 0 ||
                                  rate > 100) {
                                return 'Enter a rate between 0 and 100';
                              }
                              return null;
                            },
                          ),
                          const SizedBox(height: 14),
                          TextFormField(
                            controller: sgstRateController,
                            keyboardType:
                                const TextInputType.numberWithOptions(
                              decimal: true,
                            ),
                            decoration: const InputDecoration(
                              labelText: 'SGST Rate (%) *',
                              border: OutlineInputBorder(),
                            ),
                            validator: (value) {
                              final rate = double.tryParse(
                                value?.trim() ?? '',
                              );
                              if (rate == null ||
                                  !rate.isFinite ||
                                  rate < 0 ||
                                  rate > 100) {
                                return 'Enter a rate between 0 and 100';
                              }
                              return null;
                            },
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
                actions: [
                  TextButton(
                    onPressed: isSaving
                        ? null
                        : () => Navigator.pop(dialogContext),
                    child: const Text('Cancel'),
                  ),
                  FilledButton(
                    onPressed: isSaving
                        ? null
                        : () async {
                            if (!formKey.currentState!.validate()) {
                              return;
                            }

                            setDialogState(() => isSaving = true);

                            final data = <String, dynamic>{
                              'name': nameController.text.trim(),
                              'brandName':
                                  brandController.text.trim().isEmpty
                                      ? null
                                      : brandController.text.trim(),
                              'categoryId': categoryId,
                              'manufacturerId': manufacturerId,
                              'genericNameId': genericNameId,
                              'strength':
                                  strengthController.text.trim().isEmpty
                                      ? null
                                      : strengthController.text.trim(),
                              'dosageForm':
                                  dosageFormController.text.trim().isEmpty
                                      ? null
                                      : dosageFormController.text.trim(),
                              'description':
                                  descriptionController.text.trim().isEmpty
                                      ? null
                                      : descriptionController.text.trim(),
                              'baseUnit': baseUnitController.text
                                  .trim()
                                  .toUpperCase(),
                              'packUnit': packUnitController.text
                                  .trim()
                                  .toUpperCase(),
                              'unitsPerPack': int.parse(
                                unitsPerPackController.text.trim(),
                              ),
                              'cgstRate': double.parse(
                                cgstRateController.text.trim(),
                              ),
                              'sgstRate': double.parse(
                                sgstRateController.text.trim(),
                              ),
                            };

                            try {
                              if (product == null) {
                                await _apiService.createProduct(data);
                              } else {
                                await _apiService.updateProduct(
                                  product.id,
                                  data,
                                );
                              }

                              if (!mounted) return;

                              Navigator.pop(dialogContext);
                              await _loadData();

                              await _showMessage(
                                product == null
                                    ? 'Product added successfully'
                                    : 'Product updated successfully',
                              );
                            } catch (e) {
                              setDialogState(() => isSaving = false);
                              await _showMessage(
                                'Failed to save product: $e',
                              );
                            }
                          },
                    child: isSaving
                        ? const SizedBox(
                            height: 18,
                            width: 18,
                            child: CircularProgressIndicator(
                              strokeWidth: 2,
                            ),
                          )
                        : const Text('Save'),
                  ),
                ],
              );
            },
          );
        },
      );
    } finally {
      nameController.dispose();
      brandController.dispose();
      strengthController.dispose();
      dosageFormController.dispose();
      descriptionController.dispose();
      baseUnitController.dispose();
      packUnitController.dispose();
      unitsPerPackController.dispose();
      cgstRateController.dispose();
      sgstRateController.dispose();
    }
  }

  Future<void> _changeStatus(Product product) async {
    final action = product.active ? 'deactivate' : 'activate';

    final confirmed = await showDialog<bool>(
      context: context,
      builder: (dialogContext) => AlertDialog(
        title: Text(
          product.active ? 'Deactivate Product' : 'Activate Product',
        ),
        content: Text('Do you want to $action "${product.name}"?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(dialogContext, false),
            child: const Text('Cancel'),
          ),
          FilledButton(
            onPressed: () => Navigator.pop(dialogContext, true),
            child: Text(product.active ? 'Deactivate' : 'Activate'),
          ),
        ],
      ),
    );

    if (confirmed != true) return;

    try {
      if (product.active) {
        await _apiService.deactivateProduct(product.id);
      } else {
        await _apiService.activateProduct(product.id);
      }

      await _loadData();

      await _showMessage(
        product.active
            ? 'Product deactivated successfully'
            : 'Product activated successfully',
      );
    } catch (e) {
      await _showMessage('Failed to change product status: $e');
    }
  }

  Widget _summaryChip(String label, int count) {
    return Chip(
      label: Text('$label: $count'),
      side: BorderSide(
        color: Theme.of(context).colorScheme.outlineVariant,
      ),
      backgroundColor: Colors.transparent,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(10),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final totalPages = (_filteredProducts.length / _pageSize).ceil();

    return Padding(
      padding: const EdgeInsets.all(24),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              const Expanded(
                child: Text(
                  'Product Management',
                  style: TextStyle(
                    fontSize: 24,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
              FilledButton.tonalIcon(
                onPressed: _isLoading ||
                        !_categories.any((x) => x.active) ||
                        !_manufacturers.any((x) => x.active) ||
                        !_genericNames.any((x) => x.active)
                    ? null
                    : () => _openProductDialog(),
                icon: const Icon(Icons.add),
                label: const Text('Add Product'),
              ),
            ],
          ),
          const SizedBox(height: 16),
          if (_isLoading)
            const Expanded(
              child: Center(child: CircularProgressIndicator()),
            )
          else if (_loadError != null)
            Expanded(
              child: Center(
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const Text(
                      'Failed to load products and reference data.',
                    ),
                    const SizedBox(height: 8),
                    Text(_loadError!),
                    const SizedBox(height: 12),
                    FilledButton(
                      onPressed: _loadData,
                      child: const Text('Retry'),
                    ),
                  ],
                ),
              ),
            )
          else ...[
            Wrap(
              spacing: 10,
              runSpacing: 4,
              children: [
                _summaryChip('Total', _products.length),
                _summaryChip(
                  'Active',
                  _products.where((x) => x.active).length,
                ),
                _summaryChip(
                  'Inactive',
                  _products.where((x) => !x.active).length,
                ),
              ],
            ),
            const SizedBox(height: 12),
            TextField(
              decoration: const InputDecoration(
                hintText: 'Search products',
                prefixIcon: Icon(Icons.search),
                border: OutlineInputBorder(),
                isDense: true,
              ),
              onChanged: (value) {
                setState(() {
                  _searchQuery = value;
                  _currentPage = 0;
                });
              },
            ),
            const SizedBox(height: 16),
            Expanded(
              child: _filteredProducts.isEmpty
                  ? Center(
                      child: Text(
                        _products.isEmpty
                            ? 'No products found'
                            : 'No products match your search',
                      ),
                    )
                  : SingleChildScrollView(
                      child: SingleChildScrollView(
                        scrollDirection: Axis.horizontal,
                        child: DataTable(
                          headingRowColor: WidgetStateProperty.all(
                            Theme.of(context)
                                .colorScheme
                                .surfaceContainerHighest,
                          ),
                          columns: const [
                            DataColumn(label: Text('Sl. No.')),
                            DataColumn(label: Text('Product Name')),
                            DataColumn(label: Text('Brand')),
                            DataColumn(label: Text('Category')),
                            DataColumn(label: Text('Manufacturer')),
                            DataColumn(label: Text('Generic Name')),
                            DataColumn(label: Text('Strength')),
                            DataColumn(label: Text('Dosage Form')),
                            DataColumn(label: Text('Base Unit')),
                            DataColumn(label: Text('Pack Unit')),
                            DataColumn(label: Text('Units/Pack')),
                            DataColumn(label: Text('CGST %')),
                            DataColumn(label: Text('SGST %')),
                            DataColumn(label: Text('Status')),
                            DataColumn(label: Text('Edit')),
                            DataColumn(label: Text('Change Status')),
                          ],
                          rows: _currentPageProducts
                              .asMap()
                              .entries
                              .map((entry) {
                            final index = entry.key;
                            final product = entry.value;

                            return DataRow(
                              cells: [
                                DataCell(
                                  Text(
                                    '${_currentPage * _pageSize + index + 1}',
                                  ),
                                ),
                                DataCell(Text(product.name)),
                                DataCell(Text(product.brandName ?? '-')),
                                DataCell(
                                  Text(_categoryName(product.categoryId)),
                                ),
                                DataCell(
                                  Text(
                                    _manufacturerName(
                                      product.manufacturerId,
                                    ),
                                  ),
                                ),
                                DataCell(
                                  Text(_genericName(product.genericNameId)),
                                ),
                                DataCell(Text(product.strength ?? '-')),
                                DataCell(Text(product.dosageForm ?? '-')),
                                DataCell(Text(product.baseUnit)),
                                DataCell(Text(product.packUnit)),
                                DataCell(
                                  Text(product.unitsPerPack.toString()),
                                ),
                                DataCell(
                                  Text(product.cgstRate.toStringAsFixed(2)),
                                ),
                                DataCell(
                                  Text(product.sgstRate.toStringAsFixed(2)),
                                ),
                                DataCell(
                                  Chip(
                                    label: Text(
                                      product.active ? 'Active' : 'Inactive',
                                    ),
                                    backgroundColor: product.active
                                        ? Colors.green.shade100
                                        : Colors.grey.shade300,
                                  ),
                                ),
                                DataCell(
                                  IconButton(
                                    tooltip: 'Edit product',
                                    icon: const Icon(Icons.edit_outlined),
                                    onPressed: () =>
                                        _openProductDialog(product: product),
                                  ),
                                ),
                                DataCell(
                                  Switch(
                                    value: product.active,
                                    onChanged: (_) => _changeStatus(product),
                                  ),
                                ),
                              ],
                            );
                          }).toList(),
                        ),
                      ),
                    ),
            ),
            const SizedBox(height: 8),
            Row(
              mainAxisAlignment: MainAxisAlignment.end,
              children: [
                Text(
                  'Showing '
                  '${_filteredProducts.isEmpty ? 0 : _currentPage * _pageSize + 1}'
                  '-${_currentPage * _pageSize + _currentPageProducts.length}'
                  ' of ${_filteredProducts.length} records',
                ),
                const SizedBox(width: 12),
                IconButton(
                  tooltip: 'Previous page',
                  onPressed: _currentPage > 0
                      ? () => setState(() => _currentPage--)
                      : null,
                  icon: const Icon(Icons.chevron_left),
                ),
                Text(
                  totalPages == 0
                      ? 'Page 0 of 0'
                      : 'Page ${_currentPage + 1} of $totalPages',
                ),
                IconButton(
                  tooltip: 'Next page',
                  onPressed: _currentPage + 1 < totalPages
                      ? () => setState(() => _currentPage++)
                      : null,
                  icon: const Icon(Icons.chevron_right),
                ),
              ],
            ),
          ],
        ],
      ),
    );
  }
}
