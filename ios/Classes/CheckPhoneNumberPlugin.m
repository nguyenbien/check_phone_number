#import "CheckPhoneNumberPlugin.h"
#if __has_include(<check_phone_number/check_phone_number-Swift.h>)
#import <check_phone_number/check_phone_number-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "check_phone_number-Swift.h"
#endif

@implementation CheckPhoneNumberPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftCheckPhoneNumberPlugin registerWithRegistrar:registrar];
}
@end
