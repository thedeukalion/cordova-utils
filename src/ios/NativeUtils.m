#import "NativeUtils.h"

@implementation GPSTracker
{
  GPSTrackerService* trackerService;
  GPSRequest* requestService;

  NSString* settingActivityId;
  NSInteger settingDistance;
  NSInteger settingAccuracy;
  NSInteger settingActivity;
  long settingMinInterval;
  NSString* settingURL;
}

@synthesize syncCallbackId;

- (void) setLifecycleCallback:(CDVInvokedUrlCommand*)command
{
  self.syncCallbackId = command.callbackId;
}

- (void) pluginInitialize
{
  [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onSuspend:) name:UIApplicationDidEnterBackgroundNotification object:nil];
  [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onResume:) name:UIApplicationWillEnterForegroundNotification object:nil];
}

- (void) onSuspend:(NSNotification *) notification
{
  if (self.syncCallbackId != nil)
  {
    @try
    {
      CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"Pause"];
      [result setKeepCallbackAsBool:YES];
      [self.commandDelegate sendPluginResult:result callbackId:self.syncCallbackId];
    }
    @catch (NSException *exception) { }
    @finally { }
  }
}

- (void) onResume:(NSNotification *) notification
{
  if (self.syncCallbackId != nil)
  {
    @try
    {
      CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"Resume"];
      [result setKeepCallbackAsBool:YES];
      [self.commandDelegate sendPluginResult:result callbackId:self.syncCallbackId];
    }
    @catch (NSException *exception) { }
    @finally { }

  }
}

- (void) onAppTerminate
{
  if (self.syncCallbackId != nil)
  {
    @try
    {
      CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"Destroy"];
      [result setKeepCallbackAsBool:YES];
      [self.commandDelegate sendPluginResult:result callbackId:self.syncCallbackId];
    }
    @catch (NSException *exception) { }
    @finally { }

  }
}

@end
