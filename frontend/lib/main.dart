
import 'package:flutter/material.dart';

import 'screens/login_page.dart';

void main() {
  runApp(const MedicalShopApp());
}

class MedicalShopApp extends StatelessWidget {
  const MedicalShopApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Medical Management System',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(
          seedColor: Colors.blue,
        ),
        useMaterial3: true,
      ),
      home: const LoginPage(),
    );
  }
}
