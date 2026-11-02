class Supplier {
  final int id;
  final String name;
  final String contactNumber;
  final String? contactPerson;
  final String? email;
  final String? address;
  final bool active;

  Supplier({
    required this.id,
    required this.name,
    required this.contactNumber,
    this.contactPerson,
    this.email,
    this.address,
    required this.active,
  });

  factory Supplier.fromJson(Map<String, dynamic> json) {
    return Supplier(
      id: (json['id'] as num).toInt(),
      name: json['name'] as String,
      contactNumber: json['contactNumber'] as String,
      contactPerson: json['contactPerson'] as String?,
      email: json['email'] as String?,
      address: json['address'] as String?,
      active: json['active'] as bool,
    );
  }
}