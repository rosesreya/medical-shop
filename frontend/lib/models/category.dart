
class Category {
  final int id;
  final String name;
  final bool active;

  Category({
    required this.id,
    required this.name,
    required this.active,
  });

  factory Category.fromJson(Map<String, dynamic> json) {
    return Category(
      id: json['id'],
      name: json['name'],
      active: json['active'] as bool,
    );
  }
}
