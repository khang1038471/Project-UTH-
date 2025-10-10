import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Bài Tập Flutter',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const EmailValidationScreen(),
    );
  }
}

class EmailValidationScreen extends StatefulWidget {
  const EmailValidationScreen({super.key});

  @override
  State<EmailValidationScreen> createState() => _EmailValidationScreenState();
}

class _EmailValidationScreenState extends State<EmailValidationScreen> {
  // Controller to read the input value from the text field
  final TextEditingController _emailController = TextEditingController();
  // Variable to store the validation message to display
  String? _validationMessage;
  // Variable to track if the current message is a success (green) or error (red)
  bool _isSuccess = false;

  @override
  void dispose() {
    _emailController.dispose();
    super.dispose();
  }

  // Function containing the core validation logic
  void _validateEmail() {
    String email = _emailController.text.trim(); // Get text and remove leading/trailing spaces

    if (email.isEmpty) {
      // 1. Nếu email là null hoặc rỗng, hiển thị "Email không hợp lệ"
      setState(() {
        _validationMessage = "Email không hợp lệ";
        _isSuccess = false;
      });
    } else if (!email.contains('@')) {
      // 2. Nếu email không chứa "@", hiển thị "Email không đúng định dạng"
      setState(() {
        _validationMessage = "Email không đúng định dạng";
        _isSuccess = false;
      });
    } else {
      // 3. Email hợp lệ
      setState(() {
        _validationMessage = "Bạn đã nhập email hợp lệ";
        _isSuccess = true;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        // Title matching the context
        title: const Text('Bài Tập | Thực Hành 2'),
        backgroundColor: Colors.blue,
        foregroundColor: Colors.white,
      ),
      body: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: <Widget>[
            const Text(
              'Thực hành 02',
              style: TextStyle(
                fontSize: 20,
                fontWeight: FontWeight.bold,
              ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 30),

            // Email Input Field
            TextField(
              controller: _emailController,
              keyboardType: TextInputType.emailAddress,
              decoration: const InputDecoration(
                labelText: 'Email',
                hintText: 'Nhập email của bạn',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 10),

            // Validation Message Display
            if (_validationMessage != null)
              Text(
                _validationMessage!,
                style: TextStyle(
                  // Set color based on validation result
                  color: _isSuccess ? Colors.green.shade700 : Colors.red,
                  fontWeight: FontWeight.bold,
                ),
              ),

            const SizedBox(height: 20),

            // "Kiểm tra" (Check) Button
            ElevatedButton(
              onPressed: _validateEmail,
              style: ElevatedButton.styleFrom(
                backgroundColor: Colors.blue,
                foregroundColor: Colors.white,
                padding: const EdgeInsets.symmetric(vertical: 15),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(8),
                ),
              ),
              child: const Text(
                'Kiểm tra',
                style: TextStyle(fontSize: 18),
              ),
            ),
          ],
        ),
      ),
    );
  }
}