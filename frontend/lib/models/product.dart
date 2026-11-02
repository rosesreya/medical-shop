
class Product {
  final int id;
  final String name;
  final String? brandName;
  final int categoryId;
  final int manufacturerId;
  final int genericNameId;
  final String? strength;
  final String? dosageForm;
  final String? description;
  final bool active;

  // Unit and GST configuration
  final String baseUnit;
  final String packUnit;
  final int unitsPerPack;
  final double cgstRate;
  final double sgstRate;

  Product({
    required this.id,
    required this.name,
    this.brandName,
    required this.categoryId,
    required this.manufacturerId,
    required this.genericNameId,
    this.strength,
    this.dosageForm,
    this.description,
    required this.active,
    this.baseUnit = 'UNIT',
    this.packUnit = 'UNIT',
    this.unitsPerPack = 1,
    this.cgstRate = 0.0,
    this.sgstRate = 0.0,
  });

  factory Product.fromJson(Map<String, dynamic> json) {
    return Product(
      id: (json['id'] as num).toInt(),
      name: json['name'] as String,
      brandName: json['brandName'] as String?,
      categoryId: (json['categoryId'] as num).toInt(),
      manufacturerId: (json['manufacturerId'] as num).toInt(),
      genericNameId: (json['genericNameId'] as num).toInt(),
      strength: json['strength'] as String?,
      dosageForm: json['dosageForm'] as String?,
      description: json['description'] as String?,
      active: json['active'] as bool,
      baseUnit: json['baseUnit'] as String? ?? 'UNIT',
      packUnit: json['packUnit'] as String? ?? 'UNIT',
      unitsPerPack: (json['unitsPerPack'] as num?)?.toInt() ?? 1,
      cgstRate: (json['cgstRate'] as num?)?.toDouble() ?? 0.0,
      sgstRate: (json['sgstRate'] as num?)?.toDouble() ?? 0.0,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'brandName': brandName,
      'categoryId': categoryId,
      'manufacturerId': manufacturerId,
      'genericNameId': genericNameId,
      'strength': strength,
      'dosageForm': dosageForm,
      'description': description,
      'active': active,
      'baseUnit': baseUnit,
      'packUnit': packUnit,
      'unitsPerPack': unitsPerPack,
      'cgstRate': cgstRate,
      'sgstRate': sgstRate,
    };
  }
}
