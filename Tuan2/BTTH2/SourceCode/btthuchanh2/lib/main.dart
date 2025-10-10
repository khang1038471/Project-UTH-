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
      title: 'Thực hành 02',
      home: const NumberInputScreen(),
    );
  }
}

class NumberInputScreen extends StatefulWidget {
  const NumberInputScreen({super.key});

  @override
  State<NumberInputScreen> createState() => _NumberInputScreenState();
}

class _NumberInputScreenState extends State<NumberInputScreen> {
  final TextEditingController _controller = TextEditingController();
  List<int> numbers = [];
  String? errorText;

  void _generateList() {
    setState(() {
      errorText = null;
      numbers.clear();
      final input = _controller.text.trim();
      final count = int.tryParse(input);

      if (count == null || count <= 0) {
        errorText = "Dữ liệu bạn nhập không hợp lệ";
      } else {
        numbers = List<int>.generate(count, (index) => index + 1);
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: Center(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 24),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center, // 👈 giữa màn hình
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              const Text(
                "Thực hành 02",
                style: TextStyle(
                  fontSize: 22,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 20),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Expanded(
                    child: TextField(
                      controller: _controller,
                      keyboardType: TextInputType.number,
                      textAlign: TextAlign.center,
                      decoration: InputDecoration(
                        hintText: "Nhập vào số lượng",
                        border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(12),
                        ),
                        contentPadding: const EdgeInsets.symmetric(
                            vertical: 15, horizontal: 10),
                      ),
                    ),
                  ),
                  const SizedBox(width: 10),
                  ElevatedButton(
                    onPressed: _generateList,
                    style: ElevatedButton.styleFrom(
                      backgroundColor: Colors.blue,
                      padding: const EdgeInsets.symmetric(
                          vertical: 18, horizontal: 20),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(12),
                      ),
                    ),
                    child: const Text(
                      "Tạo",
                      style: TextStyle(color: Colors.white, fontSize: 16),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 15),
              if (errorText != null)
                Text(
                  errorText!,
                  style: const TextStyle(
                      color: Colors.red,
                      fontSize: 16,
                      fontWeight: FontWeight.w500),
                ),
              if (numbers.isNotEmpty)
                Column(
                  children: numbers
                      .map(
                        (num) => Container(
                      margin: const EdgeInsets.symmetric(vertical: 6),
                      width: double.infinity,
                      decoration: BoxDecoration(
                        color: Colors.red,
                        borderRadius: BorderRadius.circular(25),
                      ),
                      child: Padding(
                        padding: const EdgeInsets.symmetric(vertical: 14),
                        child: Center(
                          child: Text(
                            "$num",
                            style: const TextStyle(
                                color: Colors.white, fontSize: 20),
                          ),
                        ),
                      ),
                    ),
                  )
                      .toList(),
                ),
            ],
          ),
        ),
      ),
    );
  }
}
