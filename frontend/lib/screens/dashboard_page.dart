
import 'package:flutter/material.dart';

class DashboardPage extends StatelessWidget {
  final ValueChanged<int> onNavigate;

  const DashboardPage({
    super.key,
    required this.onNavigate,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    // Module indices correspond to the destination order
    // defined in HomeScreen.
    final modules = <_DashboardModule>[
      _DashboardModule(
        title: 'Categories',
        description: 'Organise product categories',
        icon: Icons.category_outlined,
        destinationIndex: 1,
      ),
      _DashboardModule(
        title: 'Manufacturers',
        description: 'Manage manufacturers',
        icon: Icons.factory_outlined,
        destinationIndex: 2,
      ),
      _DashboardModule(
        title: 'Generic Names',
        description: 'Manage generic medicine names',
        icon: Icons.medication_outlined,
        destinationIndex: 3,
      ),
      _DashboardModule(
        title: 'Products',
        description: 'Manage medical products',
        icon: Icons.medication,
        destinationIndex: 4,
      ),
      _DashboardModule(
        title: 'Purchase Entry',
        description: 'Record new purchases',
        icon: Icons.shopping_cart_outlined,
        destinationIndex: 5,
      ),
      _DashboardModule(
        title: 'Purchase History',
        description: 'View previous purchases',
        icon: Icons.history,
        destinationIndex: 6,
      ),
      _DashboardModule(
        title: 'Sales Billing',
        description: 'Create and manage bills',
        icon: Icons.point_of_sale_outlined,
        destinationIndex: 7,
      ),
      _DashboardModule(
        title: 'User Management',
        description: 'Manage users and access',
        icon: Icons.manage_accounts_outlined,
        destinationIndex: 8,
        adminOnly: true,
      ),
    ];

    // Hide User Management for non-admin users.
    // Its visibility follows the navigation available
    // in HomeScreen.
    final visibleModules = modules.where((module) {
      if (!module.adminOnly) return true;
      return _isAdminAvailable(context);
    }).toList();

    return LayoutBuilder(
      builder: (context, constraints) {
        final width = constraints.maxWidth;

        final crossAxisCount = width >= 1100
            ? 4
            : width >= 700
                ? 3
                : width >= 420
                    ? 2
                    : 1;

        return SingleChildScrollView(
          padding: const EdgeInsets.all(24),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                'Welcome back',
                style: theme.textTheme.bodyMedium?.copyWith(
                  color: theme.colorScheme.onSurfaceVariant,
                ),
              ),
              const SizedBox(height: 6),
              Text(
                'Medical Management System',
                style: theme.textTheme.headlineMedium?.copyWith(
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 8),
              Text(
                'Manage your medical shop operations.',
                style: theme.textTheme.bodyMedium?.copyWith(
                  color: theme.colorScheme.onSurfaceVariant,
                ),
              ),
              const SizedBox(height: 28),
              GridView.builder(
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                itemCount: visibleModules.length,
                gridDelegate:
                    SliverGridDelegateWithFixedCrossAxisCount(
                  crossAxisCount: crossAxisCount,
                  crossAxisSpacing: 16,
                  mainAxisSpacing: 16,
                  childAspectRatio:
                      crossAxisCount == 1 ? 3.0 : 1.35,
                ),
                itemBuilder: (context, index) {
                  final module = visibleModules[index];

                  return _ModuleCard(
                    module: module,
                    onTap: () {
                      onNavigate(module.destinationIndex);
                    },
                  );
                },
              ),
            ],
          ),
        );
      },
    );
  }

  // This checks whether the User Management destination
  // exists in the current HomeScreen navigation.
  bool _isAdminAvailable(BuildContext context) {
    final rail = context.findAncestorWidgetOfExactType<
        NavigationRail>();

    // The module is hidden if its navigation is unavailable.
    // See the note below regarding role handling.
    return rail != null;
  }
}

class _DashboardModule {
  final String title;
  final String description;
  final IconData icon;
  final int destinationIndex;
  final bool adminOnly;

  const _DashboardModule({
    required this.title,
    required this.description,
    required this.icon,
    required this.destinationIndex,
    this.adminOnly = false,
  });
}

class _ModuleCard extends StatelessWidget {
  final _DashboardModule module;
  final VoidCallback onTap;

  const _ModuleCard({
    super.key,
    required this.module,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Card(
      elevation: 0,
      clipBehavior: Clip.antiAlias,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(14),
        side: BorderSide(
          color: theme.colorScheme.outlineVariant,
        ),
      ),
      child: InkWell(
        onTap: onTap,
        child: Padding(
          padding: const EdgeInsets.all(18),
          child: Row(
            children: [
              Container(
                width: 48,
                height: 48,
                decoration: BoxDecoration(
                  color: theme.colorScheme.primaryContainer,
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Icon(
                  module.icon,
                  color: theme.colorScheme.onPrimaryContainer,
                  size: 25,
                ),
              ),
              const SizedBox(width: 14),
              Expanded(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      module.title,
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                      style: theme.textTheme.titleMedium?.copyWith(
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                    const SizedBox(height: 5),
                    Text(
                      module.description,
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                      style: theme.textTheme.bodySmall?.copyWith(
                        color: theme.colorScheme.onSurfaceVariant,
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(width: 6),
              Icon(
                Icons.arrow_forward_ios,
                size: 14,
                color: theme.colorScheme.onSurfaceVariant,
              ),
            ],
          ),
        ),
      ),
    );
  }
}
