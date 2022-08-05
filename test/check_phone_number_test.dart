import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:check_phone_number/check_phone_number.dart';

void main() {
  const MethodChannel channel = MethodChannel('check_phone_number');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await CheckPhoneNumber.platformVersion, '42');
  });
}
