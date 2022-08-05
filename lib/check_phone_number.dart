
import 'dart:async';

import 'package:check_phone_number/phone_number.dart';
import 'package:check_phone_number/region_info.dart';
import 'package:flutter/services.dart';

class CheckPhoneNumber {
  static const MethodChannel _channel = MethodChannel('com.biennt/check_phone_number');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<PhoneNumber?> parse(
      String phoneNumberString, String regionCode) async {
    final result = await _channel.invokeMapMethod<String, dynamic>(
      'parse',
      {
        'phone': phoneNumberString,
        'region': regionCode,
      },
    );

    if (result == null) {
      return null;
    }

    return PhoneNumber.fromJson(result);
  }

  static Future<Map<String, PhoneNumber?>?> parseList(
      List<String> phoneNumberStrings,
        String regionCode) async {
    final result = await _channel.invokeMapMethod<String, dynamic>(
      'parse_list',
      {
        'phones': phoneNumberStrings,
        'region': regionCode,
      },
    );

    if (result == null) {
      return null;
    }

    return result.map(
          (key, value) => MapEntry(
        key,
        value == null ? null : PhoneNumber.fromJson(Map<String, dynamic>.from(value)),
      ),
    );
  }

  static Future<String> format(
      String phoneNumberString,
      String regionCode,
      ) async {
    final result = await _channel.invokeMapMethod<String, dynamic>(
      'format',
      {
        'phone': phoneNumberString,
        'region': regionCode,
      },
    );

    if (result == null) {
      return phoneNumberString;
    }

    return result['formatted'];
  }

  static Future<bool> validate(
      String phoneNumberString,
      String regionCode,
      ) async {
    final result = await _channel.invokeMapMethod<String, dynamic>(
      'validate',
      {
        'phone': phoneNumberString,
        'region': regionCode,
      },
    );

    return result?['isValid'] ?? false;
  }

  static Future<List<RegionInfo>> allSupportedRegions({String? locale}) async {
    final result =
    await _channel.invokeListMethod<Map>('get_all_supported_regions', {
      'locale': locale,
    });

    return result
        ?.map((value) => RegionInfo.fromJson(value.cast()))
        .toList(growable: false) ??
        [];
  }
}
