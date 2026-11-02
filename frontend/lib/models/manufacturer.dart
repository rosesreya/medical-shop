
class Manufacturer {
  final int id;
  final String name;
  final bool active;
  final String? address;
  final String? contactNumber;
  final String? email;

  Manufacturer({
    required this.id,
    required this.name,
    required this.active,
    this.address,
    this.contactNumber,
    this.email,
  });

  factory Manufacturer.fromJson(Map<String, dynamic> json) {
    return Manufacturer(
      id: json['id'],
      name: json['name'],
      active: json['active'] as bool,
      address: json['address'] as String?,
      contactNumber: json['contactNumber'] as String?,
      email: json['email'] as String?,
    );
  }
}
