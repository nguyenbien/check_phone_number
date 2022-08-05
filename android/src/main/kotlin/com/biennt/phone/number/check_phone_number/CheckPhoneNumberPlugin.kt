package com.biennt.phone.number.check_phone_number

import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** CheckPhoneNumberPlugin */
class CheckPhoneNumberPlugin: FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "com.biennt/check_phone_number")
    channel.setMethodCallHandler(this)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    val checkPhone = CheckPhoneNumber()
    when (call.method) {
        "parse" -> {
          checkPhone.parse(call, result)
        }
        "parse_list" -> {
          checkPhone.parseList(call, result)
        }
        "format" -> {
          checkPhone.format(call, result)
        }
        "validate" -> {
          checkPhone.validatePhone(call, result)
        }
        "get_all_supported_regions" -> {
          checkPhone.getAllSupportedRegions(call, result)
        }
        else -> {
          result.notImplemented()
        }
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
