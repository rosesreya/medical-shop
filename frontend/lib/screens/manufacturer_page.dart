
import 'package:flutter/material.dart';

import '../models/manufacturer.dart';
import '../services/api_service.dart';

class ManufacturerPage extends StatefulWidget {
  const ManufacturerPage({super.key});

  @override
  State<ManufacturerPage> createState() => _ManufacturerPageState();
}

class _ManufacturerPageState extends State<ManufacturerPage> {
  final ApiService apiService = ApiService();

  final TextEditingController nameController = TextEditingController();
  final TextEditingController addressController = TextEditingController();
  final TextEditingController contactController = TextEditingController();
  final TextEditingController emailController = TextEditingController();

  final TextEditingController editNameController = TextEditingController();
  final TextEditingController editAddressController = TextEditingController();
  final TextEditingController editContactController = TextEditingController();
  final TextEditingController editEmailController = TextEditingController();

  List<Manufacturer> manufacturers = [];

  bool isLoading = true;
  bool isSaving = false;

  int currentPage = 0;
  final int pageSize = 10;

  @override
  void initState() {
    super.initState();
    loadManufacturers();
  }

  @override
  void dispose() {
    nameController.dispose();
    addressController.dispose();
    contactController.dispose();
    emailController.dispose();

    editNameController.dispose();
    editAddressController.dispose();
    editContactController.dispose();
    editEmailController.dispose();

    super.dispose();
  }

  List<Manufacturer> get paginatedManufacturers {
    final startIndex = currentPage * pageSize;

    if (startIndex >= manufacturers.length) {
      return [];
    }

    final endIndex =
        (startIndex + pageSize).clamp(0, manufacturers.length).toInt();

    return manufacturers.sublist(startIndex, endIndex);
  }

  int get totalPages => (manufacturers.length / pageSize).ceil();

  Future<void> loadManufacturers() async {
    try {
      final result = await apiService.getManufacturers();

      if (!mounted) return;

      setState(() {
        manufacturers = result;

        final pages = (result.length / pageSize).ceil();

        currentPage = pages == 0
            ? 0
            : currentPage.clamp(0, pages - 1).toInt();

        isLoading = false;
      });
    } catch (e) {
      if (!mounted) return;

      setState(() {
        isLoading = false;
      });

      showMessage('Failed to load manufacturers');
    }
  }

  Future<void> addManufacturer() async {
    if (isSaving) return;

    final name = nameController.text.trim();

    if (name.isEmpty) {
      showMessage('Please enter a manufacturer name');
      return;
    }

    setState(() {
      isSaving = true;
    });

    try {
      await apiService.createManufacturer(
        name: name,
        address: addressController.text.trim().isEmpty
            ? null
            : addressController.text.trim(),
        contactNumber: contactController.text.trim().isEmpty
            ? null
            : contactController.text.trim(),
        email: emailController.text.trim().isEmpty
            ? null
            : emailController.text.trim(),
      );

      if (!mounted) return;

      nameController.clear();
      addressController.clear();
      contactController.clear();
      emailController.clear();

      setState(() {
        currentPage = 0;
      });

      await loadManufacturers();

      if (!mounted) return;

      showMessage('Manufacturer added successfully');
    } catch (e) {
      if (!mounted) return;
      showMessage(cleanError(e));
    } finally {
      if (mounted) {
        setState(() {
          isSaving = false;
        });
      }
    }
  }

  Future<void> editManufacturer(Manufacturer manufacturer) async {
    if (isSaving) return;

    editNameController.text = manufacturer.name;
    editAddressController.text = manufacturer.address ?? '';
    editContactController.text = manufacturer.contactNumber ?? '';
    editEmailController.text = manufacturer.email ?? '';

    final editFormKey = GlobalKey<FormState>();

    final shouldUpdate = await showDialog<bool>(
      context: context,
      builder: (dialogContext) {
        return AlertDialog(
          title: const Text('Edit Manufacturer'),
          content: SizedBox(
            width: 450,
            child: SingleChildScrollView(
              child: Form(
                key: editFormKey,
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    TextFormField(
                      controller: editNameController,
                      autofocus: true,
                      decoration: const InputDecoration(
                        labelText: 'Manufacturer Name',
                        border: OutlineInputBorder(),
                      ),
                      validator: (value) {
                        if (value == null || value.trim().isEmpty) {
                          return 'Please enter a manufacturer name';
                        }
                        return null;
                      },
                    ),
                    const SizedBox(height: 12),
                    TextFormField(
                      controller: editAddressController,
                      maxLines: 2,
                      decoration: const InputDecoration(
                        labelText: 'Address',
                        border: OutlineInputBorder(),
                      ),
                    ),
                    const SizedBox(height: 12),
                    TextFormField(
                      controller: editContactController,
                      keyboardType: TextInputType.phone,
                      decoration: const InputDecoration(
                        labelText: 'Contact Number',
                        border: OutlineInputBorder(),
                      ),
                    ),
                    const SizedBox(height: 12),
                    TextFormField(
                      controller: editEmailController,
                      keyboardType: TextInputType.emailAddress,
                      decoration: const InputDecoration(
                        labelText: 'Email',
                        border: OutlineInputBorder(),
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(dialogContext, false),
              child: const Text('Cancel'),
            ),
            ElevatedButton(
              onPressed: () {
                if (editFormKey.currentState!.validate()) {
                  Navigator.pop(dialogContext, true);
                }
              },
              child: const Text('Save'),
            ),
          ],
        );
      },
    );

    if (shouldUpdate != true || !mounted) return;

    setState(() {
      isSaving = true;
    });

    try {
      final updates = <String, dynamic>{
        'name': editNameController.text.trim(),
        'address': editAddressController.text.trim().isEmpty
            ? null
            : editAddressController.text.trim(),
        'contactNumber': editContactController.text.trim().isEmpty
            ? null
            : editContactController.text.trim(),
        'email': editEmailController.text.trim().isEmpty
            ? null
            : editEmailController.text.trim(),
      };

      await apiService.updateManufacturer(manufacturer.id, updates);

      if (!mounted) return;

      await loadManufacturers();

      if (!mounted) return;

      showMessage('Manufacturer updated successfully');
    } catch (e) {
      if (!mounted) return;
      showMessage(cleanError(e));
    } finally {
      if (mounted) {
        setState(() {
          isSaving = false;
        });
      }
    }
  }

  Future<void> changeManufacturerStatus(
    Manufacturer manufacturer,
    bool activate,
  ) async {
    if (isSaving) return;

    setState(() {
      isSaving = true;
    });

    try {
      if (activate) {
        await apiService.activateManufacturer(manufacturer.id);
      } else {
        await apiService.deactivateManufacturer(manufacturer.id);
      }

      if (!mounted) return;

      await loadManufacturers();

      if (!mounted) return;

      showMessage(
        activate
            ? 'Manufacturer activated successfully'
            : 'Manufacturer deactivated successfully',
      );
    } catch (e) {
      if (!mounted) return;
      showMessage(cleanError(e));
    } finally {
      if (mounted) {
        setState(() {
          isSaving = false;
        });
      }
    }
  }

  String cleanError(Object error) {
    return error.toString().replaceFirst('Exception: ', '');
  }

  void showMessage(String message) {
    if (!mounted) return;

    showDialog<void>(
      context: context,
      builder: (dialogContext) {
        return AlertDialog(
          content: Text(message),
          actions: [
            TextButton(
              onPressed: () {
                Navigator.of(dialogContext).pop();
              },
              child: const Text('OK'),
            ),
          ],
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    final activeCount =
        manufacturers.where((manufacturer) => manufacturer.active).length;

    final inactiveCount = manufacturers.length - activeCount;

    return Padding(
      padding: const EdgeInsets.all(24),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            'Manufacturer Management',
            style: TextStyle(
              fontSize: 24,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 8),
          const Text(
            'Create, edit and manage manufacturer status.',
          ),
          const SizedBox(height: 24),

          Wrap(
            spacing: 16,
            runSpacing: 12,
            crossAxisAlignment: WrapCrossAlignment.center,
            children: [
              SizedBox(
                width: 280,
                child: TextField(
                  controller: nameController,
                  enabled: !isSaving,
                  onSubmitted: (_) => addManufacturer(),
                  decoration: const InputDecoration(
                    labelText: 'Manufacturer Name',
                    border: OutlineInputBorder(),
                  ),
                ),
              ),
              SizedBox(
                width: 220,
                child: TextField(
                  controller: addressController,
                  enabled: !isSaving,
                  decoration: const InputDecoration(
                    labelText: 'Address',
                    border: OutlineInputBorder(),
                  ),
                ),
              ),
              SizedBox(
                width: 180,
                child: TextField(
                  controller: contactController,
                  enabled: !isSaving,
                  keyboardType: TextInputType.phone,
                  decoration: const InputDecoration(
                    labelText: 'Contact Number',
                    border: OutlineInputBorder(),
                  ),
                ),
              ),
              SizedBox(
                width: 220,
                child: TextField(
                  controller: emailController,
                  enabled: !isSaving,
                  keyboardType: TextInputType.emailAddress,
                  decoration: const InputDecoration(
                    labelText: 'Email',
                    border: OutlineInputBorder(),
                  ),
                ),
              ),
              ElevatedButton.icon(
                onPressed: isSaving ? null : addManufacturer,
                icon: const Icon(Icons.add),
                label: const Text('Add Manufacturer'),
              ),
            ],
          ),

          const SizedBox(height: 24),

          Wrap(
            spacing: 24,
            runSpacing: 8,
            children: [
              Text('Total: ${manufacturers.length}'),
              Text('Active: $activeCount'),
              Text('Inactive: $inactiveCount'),
            ],
          ),

          const SizedBox(height: 16),

          Expanded(
            child: Column(
              children: [
                Expanded(
                  child: isLoading
                      ? const Center(
                          child: CircularProgressIndicator(),
                        )
                      : manufacturers.isEmpty
                          ? const Center(
                              child: Text('No manufacturers found'),
                            )
                          : LayoutBuilder(
                              builder: (context, constraints) {
                                return SingleChildScrollView(
                                  child: SingleChildScrollView(
                                    scrollDirection: Axis.horizontal,
                                    child: ConstrainedBox(
                                      constraints: BoxConstraints(
                                        minWidth: constraints.maxWidth,
                                      ),
                                      child: DataTable(
                                        headingRowColor:
                                            WidgetStateProperty.all(
                                          Theme.of(context)
                                              .colorScheme
                                              .surfaceContainerHighest,
                                        ),
                                        columns: const [
                                          DataColumn(
                                            label: Text('Sl. No.'),
                                          ),
                                          DataColumn(
                                            label: Text('Manufacturer Name'),
                                          ),
                                          DataColumn(
                                            label: Text('Address'),
                                          ),
                                          DataColumn(
                                            label: Text('Contact Number'),
                                          ),
                                          DataColumn(
                                            label: Text('Email'),
                                          ),
                                          DataColumn(
                                            label: Text('Status'),
                                          ),
                                          DataColumn(
                                            label: Text('Edit'),
                                          ),
                                          DataColumn(
                                            label: Text('Change Status'),
                                          ),
                                        ],
                                        rows: List.generate(
                                          paginatedManufacturers.length,
                                          (index) {
                                            final manufacturer =
                                                paginatedManufacturers[index];

                                            final serialNumber =
                                                currentPage * pageSize +
                                                    index +
                                                    1;

                                            return DataRow(
                                              cells: [
                                                DataCell(
                                                  Text('$serialNumber'),
                                                ),
                                                DataCell(
                                                  Text(manufacturer.name),
                                                ),
                                                DataCell(
                                                  Text(
                                                    manufacturer.address ?? '-',
                                                  ),
                                                ),
                                                DataCell(
                                                  Text(
                                                    manufacturer.contactNumber ??
                                                        '-',
                                                  ),
                                                ),
                                                DataCell(
                                                  Text(
                                                    manufacturer.email ?? '-',
                                                  ),
                                                ),
                                                DataCell(
                                                  Chip(
                                                    label: Text(
                                                      manufacturer.active
                                                          ? 'Active'
                                                          : 'Inactive',
                                                    ),
                                                    backgroundColor:
                                                        manufacturer.active
                                                            ? Colors
                                                                .green.shade100
                                                            : Colors
                                                                .grey.shade300,
                                                  ),
                                                ),
                                                DataCell(
                                                  IconButton(
                                                    tooltip:
                                                        'Edit manufacturer',
                                                    onPressed: isSaving
                                                        ? null
                                                        : () =>
                                                            editManufacturer(
                                                              manufacturer,
                                                            ),
                                                    icon: const Icon(
                                                      Icons.edit_outlined,
                                                    ),
                                                  ),
                                                ),
                                                DataCell(
                                                  Switch(
                                                    value:
                                                        manufacturer.active,
                                                    onChanged: isSaving
                                                        ? null
                                                        : (value) =>
                                                            changeManufacturerStatus(
                                                              manufacturer,
                                                              value,
                                                            ),
                                                  ),
                                                ),
                                              ],
                                            );
                                          },
                                        ),
                                      ),
                                    ),
                                  ),
                                );
                              },
                            ),
                ),

                const SizedBox(height: 12),

                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      manufacturers.isEmpty
                          ? 'Showing 0 manufacturers'
                          : 'Showing ${currentPage * pageSize + 1}'
                              '-${((currentPage + 1) * pageSize).clamp(0, manufacturers.length)}'
                              ' of ${manufacturers.length}',
                    ),
                    Row(
                      children: [
                        TextButton(
                          onPressed: currentPage > 0
                              ? () {
                                  setState(() {
                                    currentPage--;
                                  });
                                }
                              : null,
                          child: const Text('Previous'),
                        ),
                        const SizedBox(width: 8),
                        Text(
                          totalPages == 0
                              ? '0 / 0'
                              : '${currentPage + 1} / $totalPages',
                        ),
                        const SizedBox(width: 8),
                        TextButton(
                          onPressed: currentPage < totalPages - 1
                              ? () {
                                  setState(() {
                                    currentPage++;
                                  });
                                }
                              : null,
                          child: const Text('Next'),
                        ),
                      ],
                    ),
                  ],
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
