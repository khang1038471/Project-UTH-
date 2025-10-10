import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Thực hành 01',
      home: AgeCheckerScreen(),
    );
  }
}

class AgeCheckerScreen extends StatefulWidget {
  @override
  _AgeCheckerScreenState createState() => _AgeCheckerScreenState();
}

class _AgeCheckerScreenState extends State<AgeCheckerScreen> {
  final TextEditingController _nameController = TextEditingController();
  final TextEditingController _ageController = TextEditingController();

  String result = '';

  void checkAge() {
    String name = _nameController.text.trim();
    int? age = int.tryParse(_ageController.text);

    if (name.isEmpty || age == null) {
      setState(() {
        result = 'Vui lòng nhập đầy đủ họ tên và tuổi hợp lệ!';
      });
      return;
    }

    String type;
    if (age > 65) {
      type = 'Người già';
    } else if (age >= 6) {
      type = 'Người lớn';
    } else if (age >= 2) {
      type = 'Trẻ em';
    } else {
      type = 'Em bé';
    }

    setState(() {
      result = '$name là $type';
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('THỰC HÀNH 01'),
        centerTitle: true,
        backgroundColor: Colors.blueAccent,
      ),
      body: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: Colors.grey[200],
                borderRadius: BorderRadius.circular(10),
              ),
              child: Column(
                children: [
                  TextField(
                    controller: _nameController,
                    decoration: const InputDecoration(
                      labelText: 'Họ và tên',
                    ),
                  ),
                  const SizedBox(height: 10),
                  TextField(
                    controller: _ageController,
                    keyboardType: TextInputType.number,
                    decoration: const InputDecoration(
                      labelText: 'Tuổi',
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: checkAge,
              style: ElevatedButton.styleFrom(
                backgroundColor: Colors.blue,
                padding: const EdgeInsets.symmetric(horizontal: 30, vertical: 12),
              ),
              child: const Text(
                'Kiểm tra',
                style: TextStyle(fontSize: 18),
              ),
            ),
            const SizedBox(height: 20),
            Text(
              result,
              style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
          ],
        ),
      ),
    );
  }
}
