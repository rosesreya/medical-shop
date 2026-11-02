
import 'dart:convert';

import 'package:http/http.dart' as http;

import '../models/category.dart';
import '../models/manufacturer.dart';
import '../models/generic_name.dart';
import '../models/product.dart';
import '../models/user.dart';
import '../models/supplier.dart';

class ApiService {
  static const String baseUrl = 'http://localhost:8080';

  // JWT authentication token
  static String? _authToken;

  static void setAuthToken(String token) {
    _authToken = token;
  }

  static void clearAuthToken() {
    _authToken = null;
  }

  // Headers for authenticated requests
  Map<String, String> get _authHeaders => {
        if (_authToken != null)
          'Authorization': 'Bearer $_authToken',
      };

  // Headers for JSON requests
  Map<String, String> get _jsonHeaders => {
        'Content-Type': 'application/json',
        ..._authHeaders,
      };

  // ============================================================
  // AUTHENTICATION
  // ============================================================

  // POST: Login
  Future<Map<String, dynamic>> login({
    required String username,
    required String password,
  }) async {
    final response = await http.post(
      Uri.parse('$baseUrl/api/auth/login'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'username': username,
        'password': password,
      }),
    );

    if (response.statusCode == 200) {
      final Map<String, dynamic> data = jsonDecode(response.body);

      final token = data['token'];

      if (token is! String || token.isEmpty) {
        throw Exception('Login response does not contain a token');
      }

      setAuthToken(token);
      return data;
    }

    throw Exception(_getErrorMessage(response));
  }

  // ============================================================
  // USER MANAGEMENT
  // ============================================================

  // GET: Fetch all users (Admin only)
  Future<List<User>> getUsers() async {
    final response = await http.get(
      Uri.parse('$baseUrl/api/users'),
      headers: _authHeaders,
    );

    if (response.statusCode == 200) {
      final List<dynamic> data = jsonDecode(response.body);

      return data
          .map(
            (item) => User.fromJson(
              item as Map<String, dynamic>,
            ),
          )
          .toList();
    }

    throw Exception(_getErrorMessage(response));
  }

  // POST: Create a Staff account (Admin only)
  Future<User> createUser({
    required String fullName,
    required String username,
    required String password,
  }) async {
    final response = await http.post(
      Uri.parse('$baseUrl/api/users'),
      headers: _jsonHeaders,
      body: jsonEncode({
        'fullName': fullName,
        'username': username,
        'password': password,
      }),
    );

    if (response.statusCode == 201) {
      return User.fromJson(
        jsonDecode(response.body) as Map<String, dynamic>,
      );
    }

    throw Exception(_getErrorMessage(response));
  }

  // PATCH: Deactivate a user (Admin only)
  Future<void> deactivateUser(int id) async {
    final response = await http.patch(
      Uri.parse('$baseUrl/api/users/$id/deactivate'),
      headers: _authHeaders,
    );

    if (response.statusCode != 200) {
      throw Exception(_getErrorMessage(response));
    }
  }

  // PATCH: Reactivate a user (Admin only)
  Future<void> activateUser(int id) async {
    final response = await http.patch(
      Uri.parse('$baseUrl/api/users/$id/activate'),
      headers: _authHeaders,
    );

    if (response.statusCode != 200) {
      throw Exception(_getErrorMessage(response));
    }
  }

  // ============================================================
  // CATEGORY MANAGEMENT
  // ============================================================

  // GET: Fetch all categories
  Future<List<Category>> getCategories() async {
    final response = await http.get(
      Uri.parse('$baseUrl/api/categories'),
      headers: _authHeaders,
    );

    if (response.statusCode == 200) {
      final List<dynamic> data = jsonDecode(response.body);

      return data
          .map((json) => Category.fromJson(json))
          .toList();
    }

    throw Exception(_getErrorMessage(response));
  }

  // POST: Create a category
  Future<Category> createCategory(String name) async {
    final response = await http.post(
      Uri.parse('$baseUrl/api/categories'),
      headers: _jsonHeaders,
      body: jsonEncode({'name': name}),
    );

    if (response.statusCode == 200 ||
        response.statusCode == 201) {
      return Category.fromJson(jsonDecode(response.body));
    }

    throw Exception(_getErrorMessage(response));
  }

  // PUT: Update category name
  Future<Category> updateCategory(int id, String newName) async {
    final response = await http.put(
      Uri.parse('$baseUrl/api/categories/$id'),
      headers: _jsonHeaders,
      body: jsonEncode({'name': newName}),
    );

    if (response.statusCode == 200) {
      return Category.fromJson(jsonDecode(response.body));
    }

    throw Exception(_getErrorMessage(response));
  }

  // PATCH: Activate a category
  Future<void> activateCategory(int id) async {
    final response = await http.patch(
      Uri.parse('$baseUrl/api/categories/$id/activate'),
      headers: _authHeaders,
    );

    if (response.statusCode != 204) {
      throw Exception(_getErrorMessage(response));
    }
  }

  // PATCH: Deactivate a category
  Future<void> deactivateCategory(int id) async {
    final response = await http.patch(
      Uri.parse('$baseUrl/api/categories/$id/deactivate'),
      headers: _authHeaders,
    );

    if (response.statusCode != 204) {
      throw Exception(_getErrorMessage(response));
    }
  }

  // ============================================================
  // MANUFACTURER MANAGEMENT
  // ============================================================

  // GET: Fetch all manufacturers
  Future<List<Manufacturer>> getManufacturers() async {
    final response = await http.get(
      Uri.parse('$baseUrl/api/manufacturers'),
      headers: _authHeaders,
    );

    if (response.statusCode == 200) {
      final List<dynamic> data = jsonDecode(response.body);

      return data
          .map((json) => Manufacturer.fromJson(json))
          .toList();
    }

    throw Exception(_getErrorMessage(response));
  }

  // POST: Create a manufacturer
  Future<Manufacturer> createManufacturer({
    required String name,
    String? address,
    String? contactNumber,
    String? email,
  }) async {
    final response = await http.post(
      Uri.parse('$baseUrl/api/manufacturers'),
      headers: _jsonHeaders,
      body: jsonEncode({
        'name': name,
        'address': address,
        'contactNumber': contactNumber,
        'email': email,
      }),
    );

    if (response.statusCode == 200 ||
        response.statusCode == 201) {
      return Manufacturer.fromJson(jsonDecode(response.body));
    }

    throw Exception(_getErrorMessage(response));
  }

  // PATCH: Update manufacturer fields
  Future<Manufacturer> updateManufacturer(
    int id,
    Map<String, dynamic> updates,
  ) async {
    final response = await http.patch(
      Uri.parse('$baseUrl/api/manufacturers/$id'),
      headers: _jsonHeaders,
      body: jsonEncode(updates),
    );

    if (response.statusCode == 200) {
      return Manufacturer.fromJson(jsonDecode(response.body));
    }

    throw Exception(_getErrorMessage(response));
  }

  // PATCH: Activate manufacturer
  Future<void> activateManufacturer(int id) async {
    final response = await http.patch(
      Uri.parse('$baseUrl/api/manufacturers/$id/activate'),
      headers: _authHeaders,
    );

    if (response.statusCode != 204) {
      throw Exception(_getErrorMessage(response));
    }
  }

  // PATCH: Deactivate manufacturer
  Future<void> deactivateManufacturer(int id) async {
    final response = await http.patch(
      Uri.parse('$baseUrl/api/manufacturers/$id/deactivate'),
      headers: _authHeaders,
    );

    if (response.statusCode != 204) {
      throw Exception(_getErrorMessage(response));
    }
  }

  // ============================================================
  // GENERIC NAME MANAGEMENT
  // ============================================================

  // GET: Fetch all generic names
  Future<List<GenericName>> getGenericNames() async {
    final response = await http.get(
      Uri.parse('$baseUrl/api/generic-names'),
      headers: _authHeaders,
    );

    if (response.statusCode == 200) {
      final List<dynamic> data = jsonDecode(response.body);

      return data
          .map(
            (item) => GenericName.fromJson(
              item as Map<String, dynamic>,
            ),
          )
          .toList();
    }

    throw Exception(_getErrorMessage(response));
  }

  // POST: Create a generic name
  Future<void> createGenericName({
    required String name,
  }) async {
    final response = await http.post(
      Uri.parse('$baseUrl/api/generic-names'),
      headers: _jsonHeaders,
      body: jsonEncode({'name': name}),
    );

    if (response.statusCode != 201) {
      throw Exception(_getErrorMessage(response));
    }
  }

  // PATCH: Update a generic name
  Future<void> updateGenericName(
    int id,
    String name,
  ) async {
    final response = await http.patch(
      Uri.parse('$baseUrl/api/generic-names/$id'),
      headers: _jsonHeaders,
      body: jsonEncode({'name': name}),
    );

    if (response.statusCode != 200) {
      throw Exception(_getErrorMessage(response));
    }
  }

  // PATCH: Activate a generic name
  Future<void> activateGenericName(int id) async {
    final response = await http.patch(
      Uri.parse('$baseUrl/api/generic-names/$id/activate'),
      headers: _authHeaders,
    );

    if (response.statusCode != 204) {
      throw Exception(_getErrorMessage(response));
    }
  }

  // PATCH: Deactivate a generic name
  Future<void> deactivateGenericName(int id) async {
    final response = await http.patch(
      Uri.parse('$baseUrl/api/generic-names/$id/deactivate'),
      headers: _authHeaders,
    );

    if (response.statusCode != 204) {
      throw Exception(_getErrorMessage(response));
    }
  }

  // ============================================================
  // PRODUCT MANAGEMENT
  // ============================================================

  // GET: Fetch all products
  Future<List<Product>> getProducts() async {
    final response = await http.get(
      Uri.parse('$baseUrl/api/products'),
      headers: _authHeaders,
    );

    if (response.statusCode == 200) {
      final List<dynamic> data = jsonDecode(response.body);

      return data
          .map((json) => Product.fromJson(json))
          .toList();
    }

    throw Exception(_getErrorMessage(response));
  }

  // POST: Create a product
  Future<void> createProduct(
    Map<String, dynamic> productData,
  ) async {
    final response = await http.post(
      Uri.parse('$baseUrl/api/products'),
      headers: _jsonHeaders,
      body: jsonEncode(productData),
    );

    if (response.statusCode != 201) {
      throw Exception(_getErrorMessage(response));
    }
  }

  // PATCH: Update a product
  Future<void> updateProduct(
    int id,
    Map<String, dynamic> productData,
  ) async {
    final response = await http.patch(
      Uri.parse('$baseUrl/api/products/$id'),
      headers: _jsonHeaders,
      body: jsonEncode(productData),
    );

    if (response.statusCode != 200) {
      throw Exception(_getErrorMessage(response));
    }
  }

  // PATCH: Activate a product
  Future<void> activateProduct(int id) async {
    final response = await http.patch(
      Uri.parse('$baseUrl/api/products/$id/activate'),
      headers: _authHeaders,
    );

    if (response.statusCode != 204) {
      throw Exception(_getErrorMessage(response));
    }
  }

  // PATCH: Deactivate a product
  Future<void> deactivateProduct(int id) async {
    final response = await http.patch(
      Uri.parse('$baseUrl/api/products/$id/deactivate'),
      headers: _authHeaders,
    );

    if (response.statusCode != 204) {
      throw Exception(_getErrorMessage(response));
    }
  }
  Future<List<Supplier>> getActiveSuppliers() async {
  final response = await http.get(
    Uri.parse('$baseUrl/api/suppliers?activeOnly=true'),
    headers: _authHeaders,
  );

  if (response.statusCode == 200) {
    final List<dynamic> data = jsonDecode(response.body);

    return data
        .map((json) => Supplier.fromJson(
              json as Map<String, dynamic>,
            ))
        .toList();
  }

  throw Exception(
    'Failed to load suppliers: ${response.statusCode}',
  );
}
Future<Map<String, dynamic>> createPurchase(
  Map<String, dynamic> purchaseData,
) async {
  final response = await http.post(
    Uri.parse('$baseUrl/api/purchases'),
    headers: _jsonHeaders,
    body: jsonEncode(purchaseData),
  );

  if (response.statusCode == 201) {
    return jsonDecode(response.body) as Map<String, dynamic>;
  }

  throw Exception(
    'Failed to create purchase: ${response.statusCode} '
    '${response.body}',
  );
}
Future<List<Map<String, dynamic>>> getPurchases() async {
  final response = await http.get(
    Uri.parse('$baseUrl/api/purchases'),
    headers: _authHeaders,
  );

  if (response.statusCode == 200) {
    final List<dynamic> data =
        jsonDecode(response.body) as List<dynamic>;

    return data
        .map((item) => item as Map<String, dynamic>)
        .toList();
  }

  throw Exception(_getErrorMessage(response));
}

Future<List<Map<String, dynamic>>> getBatchesByProduct(
  int productId,
) async {
  final response = await http.get(
    Uri.parse('$baseUrl/api/batches/product/$productId'),
    headers: _authHeaders,
  );

  if (response.statusCode == 200) {
    final List<dynamic> data =
        jsonDecode(response.body) as List<dynamic>;

    return data
        .map((item) => item as Map<String, dynamic>)
        .toList();
  }

  throw Exception(_getErrorMessage(response));
}

Future<Map<String, dynamic>> createBill(
  Map<String, dynamic> billData,
) async {
  final response = await http.post(
    Uri.parse('$baseUrl/api/bills'),
    headers: _jsonHeaders,
    body: jsonEncode(billData),
  );

  if (response.statusCode == 201) {
    return jsonDecode(response.body) as Map<String, dynamic>;
  }

  throw Exception(_getErrorMessage(response));
}

Future<List<Map<String, dynamic>>> getBills() async {
  final response = await http.get(
    Uri.parse('$baseUrl/api/bills'),
    headers: _authHeaders,
  );

  if (response.statusCode == 200) {
    final List<dynamic> data =
        jsonDecode(response.body) as List<dynamic>;

    return data
        .map((item) => item as Map<String, dynamic>)
        .toList();
  }

  throw Exception(_getErrorMessage(response));
}

  // ============================================================
  // ERROR HANDLING
  // ============================================================

  String _getErrorMessage(http.Response response) {
    try {
      final dynamic decoded = jsonDecode(response.body);

      if (decoded is Map<String, dynamic>) {
        return decoded['message']?.toString() ??
            'Request failed (${response.statusCode})';
      }

      return 'Request failed (${response.statusCode})';
    } catch (_) {
      return 'Request failed (${response.statusCode})';
    }
  }
}
