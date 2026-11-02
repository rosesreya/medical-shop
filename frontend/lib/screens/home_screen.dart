
import 'package:flutter/material.dart';

import '../services/api_service.dart';
import 'login_page.dart';
import 'dashboard_page.dart';
import 'category_page.dart';
import 'manufacturer_page.dart';
import 'generic_name_page.dart';
import 'product_page.dart';
import 'user_management_page.dart';
import 'purchase_page.dart';
import 'purchase_history_page.dart';
import 'sales_bill_page.dart';

class HomeScreen extends StatefulWidget {
  final String userRole;

  const HomeScreen({
    super.key,
    required this.userRole,
  });

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  int selectedPage = 0;

  bool get isAdmin =>
      widget.userRole.toUpperCase() == 'ADMIN';

  Future<void> _logout() async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (dialogContext) => AlertDialog(
        title: const Text('Logout'),
        content: const Text(
          'Are you sure you want to log out?',
        ),
        actions: [
          TextButton(
            onPressed: () =>
                Navigator.pop(dialogContext, false),
            child: const Text('Cancel'),
          ),
          FilledButton(
            onPressed: () =>
                Navigator.pop(dialogContext, true),
            child: const Text('Logout'),
          ),
        ],
      ),
    );

    if (confirmed != true || !mounted) return;

    ApiService.clearAuthToken();

    Navigator.of(context).pushAndRemoveUntil(
      MaterialPageRoute<void>(
        builder: (_) => const LoginPage(),
      ),
      (route) => false,
    );
  }

  @override
  Widget build(BuildContext context) {
    final destinations = <_NavigationItem>[
      _NavigationItem(
        title: 'Dashboard',
        icon: Icons.dashboard_outlined,
        page: DashboardPage(
          onNavigate: (index) {
            setState(() {
              selectedPage = index;
            });
          },
        ),
      ),
      _NavigationItem(
        title: 'Categories',
        icon: Icons.category_outlined,
        page: const CategoryPage(),
      ),
      _NavigationItem(
        title: 'Manufacturers',
        icon: Icons.factory_outlined,
        page: const ManufacturerPage(),
      ),
      _NavigationItem(
        title: 'Generic Names',
        icon: Icons.medication_outlined,
        page: const GenericNamePage(),
      ),
      _NavigationItem(
        title: 'Products',
        icon: Icons.medication_outlined,
        page: const ProductPage(),
      ),
      _NavigationItem(
        title: 'Purchase Entry',
        icon: Icons.shopping_cart_outlined,
        page: const PurchasePage(),
      ),
      _NavigationItem(
        title: 'Purchase History',
        icon: Icons.history,
        page: const PurchaseHistoryPage(),
      ),
      _NavigationItem(
        title: 'Sales Billing',
        icon: Icons.point_of_sale_outlined,
        page: const SalesBillPage(),
      ),
      if (isAdmin)
        _NavigationItem(
          title: 'User Management',
          icon: Icons.manage_accounts_outlined,
          page: const UserManagementPage(),
        ),
    ];

    final currentIndex =
        selectedPage.clamp(0, destinations.length - 1);

    return Scaffold(
      appBar: AppBar(
        title: Text(destinations[currentIndex].title),
        actions: [
          Padding(
            padding: const EdgeInsets.only(right: 12),
            child: TextButton.icon(
              onPressed: _logout,
              icon: const Icon(Icons.logout),
              label: const Text('Logout'),
            ),
          ),
        ],
      ),
      body: Row(
        children: [
          NavigationRail(
            selectedIndex: currentIndex,
            onDestinationSelected: (index) {
              setState(() {
                selectedPage = index;
              });
            },
            labelType: NavigationRailLabelType.all,
            destinations: destinations.map((item) {
              return NavigationRailDestination(
                icon: Icon(item.icon),
                label: Text(item.title),
              );
            }).toList(),
          ),
          const VerticalDivider(width: 1),
          Expanded(
            child: destinations[currentIndex].page,
          ),
        ],
      ),
    );
  }
}

class _NavigationItem {
  final String title;
  final IconData icon;
  final Widget page;

  const _NavigationItem({
    required this.title,
    required this.icon,
    required this.page,
  });
}
