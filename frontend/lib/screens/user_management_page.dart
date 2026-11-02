
import 'package:flutter/material.dart';

import '../models/user.dart';
import '../services/api_service.dart';

class UserManagementPage extends StatefulWidget {
  const UserManagementPage({super.key});

  @override
  State<UserManagementPage> createState() =>
      _UserManagementPageState();
}

class _UserManagementPageState extends State<UserManagementPage> {
  final ApiService _apiService = ApiService();

  static const int _pageSize = 10;

  List<User> _users = [];
  bool _isLoading = true;
  String? _errorMessage;
  final Set<int> _updatingUserIds = {};

  int _currentPage = 0;

  @override
  void initState() {
    super.initState();
    _loadUsers();
  }

  int get _activeCount =>
      _users.where((user) => user.active).length;

  int get _inactiveCount => _users.length - _activeCount;

  int get _totalPages =>
      _users.isEmpty ? 1 : (_users.length / _pageSize).ceil();

  List<User> get _currentPageUsers {
    final start = _currentPage * _pageSize;

    if (start >= _users.length) return [];

    final end = (start + _pageSize).clamp(0, _users.length);

    return _users.sublist(start, end);
  }

  Future<void> _loadUsers() async {
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      final users = await _apiService.getUsers();

      if (!mounted) return;

      setState(() {
        _users = users;
        _currentPage = _currentPage.clamp(0, _totalPages - 1);
        _isLoading = false;
      });
    } catch (error) {
      if (!mounted) return;

      setState(() {
        _errorMessage = error.toString();
        _isLoading = false;
      });
    }
  }

  Future<void> _changeUserStatus(
    User user,
    bool activate,
  ) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (dialogContext) => AlertDialog(
        title: Text(
          activate ? 'Activate User' : 'Deactivate User',
        ),
        content: Text(
          'Are you sure you want to '
          '${activate ? 'activate' : 'deactivate'} '
          '${user.fullName}?',
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(dialogContext, false),
            child: const Text('Cancel'),
          ),
          FilledButton(
            onPressed: () => Navigator.pop(dialogContext, true),
            child: Text(activate ? 'Activate' : 'Deactivate'),
          ),
        ],
      ),
    );

    if (confirmed != true || !mounted) return;

    setState(() {
      _updatingUserIds.add(user.id);
    });

    try {
      if (activate) {
        await _apiService.activateUser(user.id);
      } else {
        await _apiService.deactivateUser(user.id);
      }

      if (!mounted) return;

      // Update the local list after the server confirms success.
      setState(() {
        _users = _users.map((item) {
          if (item.id != user.id) return item;

          return User(
            id: item.id,
            fullName: item.fullName,
            username: item.username,
            role: item.role,
            active: activate,
          );
        }).toList();
      });

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(
            activate
                ? 'User activated successfully'
                : 'User deactivated successfully',
          ),
        ),
      );
    } catch (error) {
      if (!mounted) return;

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Failed to update user status: $error'),
        ),
      );
    } finally {
      if (mounted) {
        setState(() {
          _updatingUserIds.remove(user.id);
        });
      }
    }
  }

  Widget _buildStatusBadge(bool active) {
    final color = active ? Colors.green : Colors.red;

    return Container(
      padding: const EdgeInsets.symmetric(
        horizontal: 14,
        vertical: 8,
      ),
      decoration: BoxDecoration(
        color: active
            ? Colors.green.shade100
            : Colors.red.shade100,
        borderRadius: BorderRadius.circular(8),
        border: Border.all(
          color: active
              ? Colors.green.shade200
              : Colors.red.shade200,
        ),
      ),
      child: Text(
        active ? 'Active' : 'Inactive',
        style: TextStyle(
          color: color.shade700,
          fontWeight: FontWeight.w500,
        ),
      ),
    );
  }

  Widget _buildUserTable() {
    final pageUsers = _currentPageUsers;

    if (pageUsers.isEmpty) {
      return const Center(
        child: Text('No users found.'),
      );
    }

    return SingleChildScrollView(
      scrollDirection: Axis.horizontal,
      child: SizedBox(
        width: 1000,
        child: DataTable(
          horizontalMargin: 24,
          columnSpacing: 24,
          headingRowHeight: 54,
          dataRowMinHeight: 56,
          dataRowMaxHeight: 64,
          headingRowColor: WidgetStateProperty.all(
            Colors.grey.shade300,
          ),
          columns: const [
            DataColumn(label: Text('Sl. No.')),
            DataColumn(label: Text('Full Name')),
            DataColumn(label: Text('Username')),
            DataColumn(label: Text('Role')),
            DataColumn(label: Text('Status')),
            DataColumn(label: Text('Change Status')),
          ],
          rows: pageUsers.map((user) {
            final updating = _updatingUserIds.contains(user.id);
            final index = _users.indexOf(user) + 1;

            return DataRow(
              cells: [
                DataCell(Text('$index')),
                DataCell(
                  SizedBox(
                    width: 170,
                    child: Text(user.fullName),
                  ),
                ),
                DataCell(
                  SizedBox(
                    width: 140,
                    child: Text(user.username),
                  ),
                ),
                DataCell(Text(user.role)),
                DataCell(_buildStatusBadge(user.active)),
                DataCell(
                  Center(
                    child: updating
                        ? const SizedBox(
                            width: 22,
                            height: 22,
                            child: CircularProgressIndicator(
                              strokeWidth: 2,
                            ),
                          )
                        : Switch(
                            value: user.active,
                            onChanged: (value) {
                              _changeUserStatus(user, value);
                            },
                          ),
                  ),
                ),
              ],
            );
          }).toList(),
        ),
      ),
    );
  }

  Widget _buildPagination() {
    final start = _users.isEmpty ? 0 : _currentPage * _pageSize + 1;
    final end = ((_currentPage + 1) * _pageSize)
        .clamp(0, _users.length);

    return Row(
      children: [
        Text(
          'Showing $start-$end of ${_users.length}',
          style: const TextStyle(fontSize: 14),
        ),
        const Spacer(),
        TextButton(
          onPressed: _currentPage > 0
              ? () {
                  setState(() => _currentPage--);
                }
              : null,
          child: const Text('Previous'),
        ),
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 12),
          child: Text('${_currentPage + 1} / $_totalPages'),
        ),
        TextButton(
          onPressed: _currentPage < _totalPages - 1
              ? () {
                  setState(() => _currentPage++);
                }
              : null,
          child: const Text('Next'),
        ),
      ],
    );
  }

  Widget _buildSummaryCounts() {
    return Wrap(
      spacing: 24,
      runSpacing: 8,
      children: [
        Text(
          'Total: ${_users.length}',
          style: const TextStyle(fontSize: 15),
        ),
        Text(
          'Active: $_activeCount',
          style: const TextStyle(fontSize: 15),
        ),
        Text(
          'Inactive: $_inactiveCount',
          style: const TextStyle(fontSize: 15),
        ),
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.fromLTRB(24, 24, 24, 16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'User Management',
            style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                  fontWeight: FontWeight.bold,
                ),
          ),
          const SizedBox(height: 8),
          const Text(
            'Create and manage user accounts and their access status.',
            style: TextStyle(fontSize: 15),
          ),
          const SizedBox(height: 24),

          if (!_isLoading && _errorMessage == null) ...[
            _buildSummaryCounts(),
            const SizedBox(height: 20),
          ],

          Row(
            children: [
              Text(
                'User Accounts',
                style: Theme.of(context).textTheme.titleLarge?.copyWith(
                      fontWeight: FontWeight.w600,
                    ),
              ),
              const Spacer(),
              IconButton(
                onPressed: _isLoading ? null : _loadUsers,
                tooltip: 'Refresh users',
                icon: const Icon(Icons.refresh),
              ),
            ],
          ),
          const SizedBox(height: 12),

          Expanded(
            child: SizedBox(
              width: double.infinity,
              child: _isLoading
                  ? const Center(
                      child: CircularProgressIndicator(),
                    )
                  : _errorMessage != null
                      ? Center(
                          child: Column(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              const Icon(
                                Icons.error_outline,
                                color: Colors.red,
                                size: 40,
                              ),
                              const SizedBox(height: 12),
                              Text(
                                _errorMessage!,
                                textAlign: TextAlign.center,
                              ),
                              const SizedBox(height: 12),
                              FilledButton.icon(
                                onPressed: _loadUsers,
                                icon: const Icon(Icons.refresh),
                                label: const Text('Retry'),
                              ),
                            ],
                          ),
                        )
                      : _buildUserTable(),
            ),
          ),

          if (!_isLoading && _errorMessage == null) ...[
            const SizedBox(height: 12),
            _buildPagination(),
          ],
        ],
      ),
    );
  }
}
