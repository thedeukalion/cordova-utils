#ifndef NATIVE_UTILS
#define NATIVE_UTILS

#import <Cordova/CDVPlugin.h>

@interface NativeUtils : CDVPlugin

@property (nonatomic, strong) NSString* syncCallbackId;

- (void) setLifecycleListener:(CDVInvokedUrlCommand*)command;

- (void) onSuspend:(NSNotification *) notification;
- (void) onResume:(NSNotification *) notification;
- (void) onAppTerminate;

@end

#endif
