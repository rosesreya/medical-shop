import 'package:flutter/material.dart';

import '../models/category.dart';
import '../services/api_service.dart';

class CategoryPage extends StatefulWidget {
  const CategoryPage({super.key});

  @override
  State<CategoryPage> createState() => _CategoryPageState();
}

class _CategoryPageState extends State<CategoryPage> {
  final ApiService apiService = ApiService();

  final TextEditingController nameController = TextEditingController();

  final TextEditingController editController = TextEditingController();

  List<Category> categories = [];

  bool isLoading = true;
  bool isSaving = false;
  int currentPage = 0;
  final int pageSize = 10;

  @override
  void initState() {
    super.initState();
    loadCategories();
  }

  @override
  void dispose() {
    nameController.dispose();
    editController.dispose();
    super.dispose();
  }
  List<Category> get paginatedCategories {
  final startIndex = currentPage * pageSize;

  if (startIndex >= categories.length) {
    return [];
  }

  final endIndex = (startIndex + pageSize)
      .clamp(0, categories.length);

  return categories.sublist(startIndex, endIndex);
}
int get totalPages =>
    (categories.length / pageSize).ceil();

  
Future<void> loadCategories() async {
  try {
    final result = await apiService.getCategories();

    if (!mounted) return;

    setState(() {
      categories = result;

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

    showMessage('Failed to load categories');
  }
}


  Future<void> addCategory() async {
    final name = nameController.text.trim();

    if (name.isEmpty) {
      showMessage('Please enter a category name');
      return;
    }

    try {
      await apiService.createCategory(name);

      if (!mounted) return;

      nameController.clear();

      await loadCategories();

      if (!mounted) return;
      showMessage('Category added successfully');
    } catch (e) {
      if (!mounted) return;
      showMessage(cleanError(e));
    }
  }

  Future<void> editCategory(Category category) async {
    editController.text = category.name;

    final newName = await showDialog<String>(
      context: context,
      builder: (dialogContext) {
        return AlertDialog(
          title: const Text('Edit Category'),
          content: TextField(
            controller: editController,
            autofocus: true,
            decoration: const InputDecoration(
              labelText: 'Category Name',
              border: OutlineInputBorder(),
            ),
          ),
          actions: [
            TextButton(
              onPressed: () {
                Navigator.pop(dialogContext);
              },
              child: const Text('Cancel'),
            ),
            ElevatedButton(
              onPressed: () {
                final name = editController.text.trim();

                if (name.isEmpty) {
                  showMessage('Please enter a category name');
                  return;
                }

                Navigator.pop(dialogContext, name);
              },
              child: const Text('Save'),
            ),
          ],
        );
      },
    );

    if (newName == null || !mounted) return;

    if (newName == category.name) return;

    try {
      setState(() {
        isSaving = true;
      });

      await apiService.updateCategory(category.id, newName);

      if (!mounted) return;

      await loadCategories();

      if (!mounted) return;
      showMessage('Category updated successfully');
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

  Future<void> changeCategoryStatus(
    Category category,
    bool activate,
  ) async {
    try {
      setState(() {
        isSaving = true;
      });

      if (activate) {
        await apiService.activateCategory(category.id);
      } else {
        await apiService.deactivateCategory(category.id);
      }

      if (!mounted) return;

      await loadCategories();

      if (!mounted) return;

      showMessage(
        activate
            ? 'Category activated successfully'
            : 'Category deactivated successfully',
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
        categories.where((category) => category.active).length;

    final inactiveCount = categories.length - activeCount;

    return Padding(
      padding: const EdgeInsets.all(24),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            'Category Management',
            style: TextStyle(
              fontSize: 24,
              fontWeight: FontWeight.bold,
            ),
          ),

          const SizedBox(height: 8),

          const Text(
            'Create, edit and manage category status.',
          ),

          const SizedBox(height: 24),

          Row(
            children: [
              SizedBox(
                width: 350,
                child: TextField(
                  controller: nameController,
                  onSubmitted: (_) => addCategory(),
                  decoration: const InputDecoration(
                    labelText: 'Category Name',
                    border: OutlineInputBorder(),
                  ),
                ),
              ),

              const SizedBox(width: 16),

              ElevatedButton.icon(
                onPressed: isSaving ? null : addCategory,
                icon: const Icon(Icons.add),
                label: const Text('Add Category'),
              ),
            ],
          ),

          const SizedBox(height: 24),

          Wrap(
            spacing: 24,
            runSpacing: 8,
            children: [
              Text('Total: ${categories.length}'),
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
            : categories.isEmpty
                ? const Center(
                    child: Text('No categories found'),
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
                                DataColumn(label: Text('Sl. No.')),
                                DataColumn(
                                  label: Text('Category Name'),
                                ),
                                DataColumn(label: Text('Status')),
                                DataColumn(label: Text('Edit')),
                                DataColumn(
                                  label: Text('Change Status'),
                                ),
                              ],
                              
rows: List.generate(
  paginatedCategories.length,
  (index) {
    final category = paginatedCategories[index];

    final serialNumber =
        currentPage * pageSize + index + 1;

    return DataRow(
      cells: [
        DataCell(
          Text('$serialNumber'),
        ),
        DataCell(
          Text(category.name),
        ),
        DataCell(
          Chip(
            label: Text(
              category.active ? 'Active' : 'Inactive',
            ),
            backgroundColor: category.active
                ? Colors.green.shade100
                : Colors.grey.shade300,
          ),
        ),
        DataCell(
          IconButton(
            tooltip: 'Edit category',
            onPressed: isSaving
                ? null
                : () => editCategory(category),
            icon: const Icon(Icons.edit_outlined),
          ),
        ),
        DataCell(
          Switch(
            value: category.active,
            onChanged: isSaving
                ? null
                : (value) => changeCategoryStatus(
                      category,
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
            categories.isEmpty
                ? 'Showing 0 categories'
                : 'Showing ${currentPage * pageSize + 1}'
                    '-${((currentPage + 1) * pageSize)
                        .clamp(0, categories.length)}'
                    ' of ${categories.length}',
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
)

        ],
      ),
    );
  }
}
