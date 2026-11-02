
import 'package:flutter/material.dart';
import '../models/generic_name.dart';
import '../services/api_service.dart';

class GenericNamePage extends StatefulWidget {
  const GenericNamePage({super.key});

  @override
  State<GenericNamePage> createState() => _GenericNamePageState();
}

class _GenericNamePageState extends State<GenericNamePage> {
  final ApiService apiService = ApiService();
  final TextEditingController nameController = TextEditingController();

  List<GenericName> genericNames = [];

  bool isLoading = true;
  bool isSaving = false;

  int currentPage = 0;
  final int pageSize = 10;

  int get totalPages =>
      genericNames.isEmpty ? 1 : (genericNames.length / pageSize).ceil();

  List<GenericName> get paginatedGenericNames {
    final start = currentPage * pageSize;
    final end = (start + pageSize).clamp(0, genericNames.length);

    return genericNames.sublist(start, end);
  }

  int get activeCount =>
      genericNames.where((item) => item.active).length;

  int get inactiveCount =>
      genericNames.where((item) => !item.active).length;

  @override
  void initState() {
    super.initState();
    loadGenericNames();
  }

  @override
  void dispose() {
    nameController.dispose();
    super.dispose();
  }

  Future<void> loadGenericNames() async {
    try {
      final result = await apiService.getGenericNames();

      if (!mounted) return;

      setState(() {
        genericNames = result;
        currentPage = currentPage.clamp(0, totalPages - 1);
        isLoading = false;
      });
    } catch (e) {
      if (!mounted) return;

      setState(() => isLoading = false);
      showMessage(cleanError(e));
    }
  }

  String cleanError(Object error) {
    return error.toString().replaceFirst('Exception: ', '');
  }

  void showMessage(String message) {
    if (!mounted) return;

    showDialog<void>(
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

  Future<void> addGenericName() async {
    final name = nameController.text.trim();

    if (name.isEmpty) {
      showMessage('Please enter a generic name');
      return;
    }

    setState(() => isSaving = true);

    try {
      await apiService.createGenericName(name: name);

      if (!mounted) return;

      nameController.clear();

      final result = await apiService.getGenericNames();

      if (!mounted) return;

      setState(() {
        genericNames = result;
        currentPage = (genericNames.length - 1)
            .clamp(0, genericNames.length)
            ~/ pageSize;
        isSaving = false;
      });

      showMessage('Generic name added successfully');
    } catch (e) {
      if (!mounted) return;

      setState(() => isSaving = false);
      showMessage(cleanError(e));
    }
  }

  Future<void> editGenericName(GenericName item) async {
    final controller = TextEditingController(text: item.name);

    final newName = await showDialog<String>(
      context: context,
      builder: (dialogContext) {
        return AlertDialog(
          title: const Text('Edit Generic Name'),
          content: TextField(
            controller: controller,
            autofocus: true,
            decoration: const InputDecoration(
              labelText: 'Generic Name',
              border: OutlineInputBorder(),
            ),
            onSubmitted: (value) {
              Navigator.pop(dialogContext, value.trim());
            },
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(dialogContext),
              child: const Text('Cancel'),
            ),
            ElevatedButton(
              onPressed: () {
                Navigator.pop(dialogContext, controller.text.trim());
              },
              child: const Text('Save'),
            ),
          ],
        );
      },
    );

    controller.dispose();

    if (newName == null) return;

    if (newName.isEmpty) {
      showMessage('Please enter a generic name');
      return;
    }

    setState(() => isSaving = true);

    try {
      await apiService.updateGenericName(item.id, newName);

      if (!mounted) return;

      await refreshGenericNames();
      if (!mounted) return;

      showMessage('Generic name updated successfully');
    } catch (e) {
      if (!mounted) return;

      showMessage(cleanError(e));
    } finally {
      if (mounted) {
        setState(() => isSaving = false);
      }
    }
  }

  Future<void> changeGenericNameStatus(
    GenericName item,
    bool active,
  ) async {
    setState(() => isSaving = true);

    try {
      if (active) {
        await apiService.activateGenericName(item.id);
      } else {
        await apiService.deactivateGenericName(item.id);
      }

      if (!mounted) return;

      await refreshGenericNames();
      if (!mounted) return;

      showMessage(
        active
            ? 'Generic name activated successfully'
            : 'Generic name deactivated successfully',
      );
    } catch (e) {
      if (!mounted) return;
      showMessage(cleanError(e));
    } finally {
      if (mounted) {
        setState(() => isSaving = false);
      }
    }
  }

  Future<void> refreshGenericNames() async {
    final result = await apiService.getGenericNames();

    if (!mounted) return;

    setState(() {
      genericNames = result;
      currentPage = currentPage.clamp(0, totalPages - 1);
    });
  }

  Widget buildSummary(String label, int count) {
    return Chip(
      label: Text('$label: $count'),
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
    );
  }

  @override
  Widget build(BuildContext context) {
    final rows = paginatedGenericNames;

    return Stack(
      children: [
        Padding(
          padding: const EdgeInsets.all(24),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text(
                'Generic Name Management',
                style: TextStyle(
                  fontSize: 24,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 8),
              Text(
                'Manage generic names and their availability.',
                style: TextStyle(
                  color: Theme.of(context).colorScheme.onSurfaceVariant,
                ),
              ),
              const SizedBox(height: 24),

              Wrap(
                spacing: 12,
                runSpacing: 12,
                crossAxisAlignment: WrapCrossAlignment.center,
                children: [
                  SizedBox(
                    width: 350,
                    child: TextField(
                      controller: nameController,
                      decoration: const InputDecoration(
                        labelText: 'Generic Name',
                        hintText: 'Enter generic name',
                        border: OutlineInputBorder(),
                      ),
                      onSubmitted: (_) {
                        if (!isSaving) addGenericName();
                      },
                    ),
                  ),
                  ElevatedButton.icon(
                    onPressed: isSaving ? null : addGenericName,
                    icon: const Icon(Icons.add),
                    label: const Text('Add Generic Name'),
                  ),
                ],
              ),

              const SizedBox(height: 20),

              Wrap(
                spacing: 12,
                runSpacing: 8,
                children: [
                  buildSummary('Total', genericNames.length),
                  buildSummary('Active', activeCount),
                  buildSummary('Inactive', inactiveCount),
                ],
              ),

              const SizedBox(height: 16),

              Expanded(
                child: isLoading
                    ? const Center(child: CircularProgressIndicator())
                    : genericNames.isEmpty
                        ? const Center(
                            child: Text('No generic names found'),
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
                                          WidgetStatePropertyAll(
                                        Theme.of(context)
                                            .colorScheme
                                            .surfaceContainerHighest,
                                      ),
                                      columns: const [
                                        DataColumn(label: Text('Sl. No.')),
                                        DataColumn(label: Text('Generic Name')),
                                        DataColumn(label: Text('Status')),
                                        DataColumn(label: Text('Edit')),
                                        DataColumn(
                                          label: Text('Change Status'),
                                        ),
                                      ],
                                      rows: List.generate(
                                        rows.length,
                                        (index) {
                                          final item = rows[index];
                                          final serialNumber =
                                              currentPage * pageSize +
                                                  index +
                                                  1;

                                          return DataRow(
                                            cells: [
                                              DataCell(
                                                Text('$serialNumber'),
                                              ),
                                              DataCell(Text(item.name)),
                                              DataCell(
                                                Chip(
                                                  label: Text(
                                                    item.active
                                                        ? 'Active'
                                                        : 'Inactive',
                                                  ),
                                                  backgroundColor: item.active
                                                      ? Colors.green
                                                          .withValues(alpha: 0.12)
                                                      : Colors.grey
                                                          .withValues(alpha: 0.15),
                                                  side: BorderSide.none,
                                                ),
                                              ),
                                              DataCell(
                                                IconButton(
                                                  tooltip: 'Edit',
                                                  icon: const Icon(
                                                    Icons.edit_outlined,
                                                  ),
                                                  onPressed: isSaving
                                                      ? null
                                                      : () => editGenericName(
                                                            item,
                                                          ),
                                                ),
                                              ),
                                              DataCell(
                                                Switch(
                                                  value: item.active,
                                                  onChanged: isSaving
                                                      ? null
                                                      : (value) =>
                                                          changeGenericNameStatus(
                                                            item,
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
                children: [
                  Text(
                    genericNames.isEmpty
                        ? 'Showing 0 records'
                        : 'Showing ${currentPage * pageSize + 1}'
                            '-${currentPage * pageSize + rows.length}'
                            ' of ${genericNames.length} records',
                  ),
                  const Spacer(),
                  IconButton(
                    tooltip: 'Previous page',
                    onPressed: currentPage > 0 && !isSaving
                        ? () => setState(() => currentPage--)
                        : null,
                    icon: const Icon(Icons.chevron_left),
                  ),
                  Text('Page ${currentPage + 1} of $totalPages'),
                  IconButton(
                    tooltip: 'Next page',
                    onPressed: currentPage < totalPages - 1 && !isSaving
                        ? () => setState(() => currentPage++)
                        : null,
                    icon: const Icon(Icons.chevron_right),
                  ),
                ],
              ),
            ],
          ),
        ),
        if (isSaving)
          Positioned(
            top: 8,
            right: 8,
            child: Card(
              child: Padding(
                padding: const EdgeInsets.all(12),
                child: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const SizedBox(
                      width: 18,
                      height: 18,
                      child: CircularProgressIndicator(strokeWidth: 2),
                    ),
                    const SizedBox(width: 10),
                    Text(
                      'Please wait...',
                      style: Theme.of(context).textTheme.bodySmall,
                    ),
                  ],
                ),
              ),
            ),
          ),
      ],
    );
  }
}
