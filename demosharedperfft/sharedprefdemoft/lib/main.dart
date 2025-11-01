import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'SharedPreferences Demo',
      theme: ThemeData(primarySwatch: Colors.blue),
      home: SharedPrefDemo(),
    );
  }
}

class SharedPrefDemo extends StatefulWidget {
  @override
  _SharedPrefDemoState createState() => _SharedPrefDemoState();
}

class _SharedPrefDemoState extends State<SharedPrefDemo> {
  TextEditingController _controller = TextEditingController();
  List<String> _savedUsernames = [];

  @override
  void initState() {
    super.initState();
    _loadUsernames();
  }

  // Load danh sách tên từ SharedPreferences
  Future<void> _loadUsernames() async {
    final prefs = await SharedPreferences.getInstance();
    setState(() {
      _savedUsernames = prefs.getStringList('usernames') ?? [];
    });
  }

  // Lưu thêm một tên
  Future<void> _saveUsername() async {
    final prefs = await SharedPreferences.getInstance();
    _savedUsernames.add(_controller.text);
    await prefs.setStringList('usernames', _savedUsernames);
    _controller.clear();
    _loadUsernames();
  }

  // Xóa toàn bộ danh sách tên
  Future<void> _clearAll() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove('usernames');
    _controller.clear();
    _loadUsernames();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('SharedPreferences Demo')),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            TextField(
              controller: _controller,
              decoration: InputDecoration(
                labelText: 'Nhập tên của bạn',
                border: OutlineInputBorder(),
              ),
            ),
            SizedBox(height: 10),
            Row(
              children: [
                ElevatedButton(onPressed: _saveUsername, child: Text('Thêm')),
                SizedBox(width: 10),
                ElevatedButton(onPressed: _clearAll, child: Text('Xóa hết')),
              ],
            ),
            SizedBox(height: 20),
            Text(
              'Danh sách tên đã lưu:',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            Expanded(
              child: ListView.builder(
                itemCount: _savedUsernames.length,
                itemBuilder: (context, index) {
                  return ListTile(
                    title: Text(_savedUsernames[index]),
                  );
                },
              ),
            ),
          ],
        ),
      ),
    );
  }
}
