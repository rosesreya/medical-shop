
class User {
  final int id;
  final String fullName;
  final String username;
  final String role;
  final bool active;

  User({
    required this.id,
    required this.fullName,
    required this.username,
    required this.role,
    required this.active,
  });

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      id: (json['id'] as num).toInt(),
      fullName: json['fullName'] as String,
      username: json['username'] as String,
      role: json['role'] as String,
      active: json['active'] as bool,
    );
  }
}
