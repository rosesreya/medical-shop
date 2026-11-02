
class GenericName {
  final int id;
  final String name;
  final bool active;

  GenericName({
    required this.id,
    required this.name,
    required this.active,
  });

  factory GenericName.fromJson(Map<String, dynamic> json) {
    return GenericName(
      id: (json['id'] as num).toInt(),
      name: json['name'] as String,
      active: json['active'] as bool,
    );
  }
}
