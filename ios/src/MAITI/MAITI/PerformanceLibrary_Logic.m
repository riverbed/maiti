//
//  PerformanceLibrary_Logic.m
//  SimplePerformanceLibrary
/*
===================
Copyright (c) 2013 Riverbed Technology, Inc.

MAITI is licensed under the terms and conditions of the MIT License as set forth in LICENSE.TXT, which accompanies the software.  MAITI is distributed “AS IS” as set forth in the MIT License.
===================
*/

#import "PerformanceLibrary_Logic.h"
#import "AppConstants.h"
#import "Reachability.h"
#import <mach/mach.h>
#import <mach/mach_host.h>
#include <sys/types.h>
#include <sys/sysctl.h>
#import "ServerConnection.h"

@interface PerformanceLibrary_Logic ()
{
}

@property(nonatomic,copy)NSString *app_code_ver;
@property(nonatomic,copy)NSString *package_id;

@property(nonatomic,copy) NSString *Session_Id;
@property(nonatomic,copy) NSString *customer_id;
@property(nonatomic,copy) NSString *app_id;

@property(nonatomic,retain) NSMutableDictionary *active_interval_transaction;
@property(nonatomic,retain) NSMutableArray *completed_interval_transaction;
@property(nonatomic,retain) NSMutableDictionary *event_transaction;
@property(nonatomic,retain) NSMutableArray *notification_transaction;

@property (nonatomic, retain) NSMutableArray* AllSendingData_Array;
@property (nonatomic, retain) NSTimer* TimeIntervalToConnectServer;

-(NSString*)CreateTransactionID; //Create unique transaction ID
-(void)CreateSessionId;
-(int)calculateDuration:(NSDate*)start_time end_time:(NSDate*)end_time;
-(int)calculateBufferDuration:(NSDate*)time;
-(long)iPhoneFreeMemory;
-(long)iPhoneTotalMemory;
-(NSString*)DeviceModel;
-(NSString*)DeviceUDID;
-(NSString*)CurrentNetwork;
-(void)sendDataToServer;
-(NSDictionary*)returnErrorDescription:(NSInteger)errorcode;

@end

@implementation PerformanceLibrary_Logic

-(void)dealloc
{
    self.AllSendingData_Array = nil;
    [self.TimeIntervalToConnectServer invalidate];
    self.TimeIntervalToConnectServer = nil;
    self.Preferences = nil;
    self.active_interval_transaction = nil;
    self.completed_interval_transaction = nil;
    self.event_transaction = nil;
    self.notification_transaction = nil;
    self.Session_Id = nil;
    self.customer_id = nil;
    self.app_id = nil;
    self.app_code_ver = nil;
    self.package_id = nil;
    [super dealloc];
}

enum errorcode
{
    NullParameters =100,
    InvalidNameParameters=101,
    TooLongUserTag=102,
    TooLongUserData=103,
    InvalidParentTransaction=104
};

-(id)initWithCustomerId:(NSString*) customerId appId:(NSString*) appId {
    
    self.customer_id = customerId;
    self.app_id = appId;

    //NSLog(@"%@, %@", customer_id, app_id);
    self.package_id = [[NSBundle mainBundle] bundleIdentifier];
    if (self.package_id == NULL)
        self.package_id = @"unknown";
    
    CFStringRef ver = CFBundleGetValueForInfoDictionaryKey(CFBundleGetMainBundle(), kCFBundleVersionKey);
    self.app_code_ver = (NSString*) ver;
    
    if (self.app_code_ver == NULL)
        self.app_code_ver = @"unknown";
    
    NSLog(@"Initialized Riverbed Mobile App Instrumentation Telemetry Interface (MAITI)" );
    
    return self.init;
}

-(id)init
{
    //Set Preferences
    NSString *preferences_path = [[NSBundle mainBundle] pathForResource:@"riverbed_preferences" ofType:@"plist"];
    self.Preferences=[[[NSDictionary alloc] initWithContentsOfFile:preferences_path] autorelease];
    
    if (self.Preferences != NULL)
        NSLog(@"MAITI initialized with customer-supplied preferences: %@",self.Preferences);
    else
    {
        NSLog(@"MAITI initialized with default preferences");
        self.Preferences=[[[NSDictionary alloc] initWithObjectsAndKeys:@"mobile.collect-opnet.com",PREF_HOST,
                     @"1",PREF_ENABLED,
                     @"1",PREF_RECORD_CONN,
                     @"1",PREF_RECORD_MEM,
                     @"1",PREF_RECORD_SERIAL,
                     @"0",PREF_USE_HTTPS,
                     @"80",PREF_PORT,
                     nil] autorelease];
         //NSLog(@"Using Default Preferences: %@",Preferences);
    }
    
    self.Disabled = ![[self.Preferences objectForKey:PREF_ENABLED] boolValue];
    
    self.active_interval_transaction=[[[NSMutableDictionary alloc] init] autorelease];
    self.completed_interval_transaction=[[[NSMutableArray alloc] init] autorelease];
    self.event_transaction=[[[NSMutableDictionary alloc] init] autorelease];
    self.notification_transaction=[[[NSMutableArray alloc] init] autorelease];

    
    self.TimeIntervalToConnectServer=[NSTimer scheduledTimerWithTimeInterval:SEND_BUFFER_TIMER_INTERVAL_SEC target:self selector:@selector(sendDataToServer) userInfo:nil repeats:YES];
    self.AllSendingData_Array=[[[NSMutableArray alloc] init] autorelease];
    
    // Create the session ID immediately -- before the app has a chance to show its screen.  We don't want this value to ever be null
    [self CreateSessionId];
    
    NSNotificationCenter *notificationCenter = [NSNotificationCenter defaultCenter];
    
    // Register the session ID to be recreated everytime the app is displayed
    [notificationCenter addObserver: self selector: @selector(CreateSessionId) name:UIApplicationDidBecomeActiveNotification  object:nil];
     
    return self;
}

-(Boolean)IsDisabled
{
    if (self.customer_id == NULL || self.app_id == NULL)
        return YES;
    
    return self.Disabled;
    //return ([[Preferences objectForKey:@"Enabled"] boolValue] == NO);
}



//Set Interval Parameter
-(NSString*)SetIntervalTransaction:(NSMutableDictionary*)parameters  error:(NSError**)error
{
    if ([self IsDisabled] == YES)
        return @"";
    
    NSMutableDictionary *_parameters=[[[NSMutableDictionary alloc] initWithDictionary:parameters] autorelease];
    //Create transaction Id
    NSString *transactionId = [self CreateTransactionID];
    
    //check error
    if (parameters == NULL)
    {
        *error = [[[NSError alloc] initWithDomain:@"ParametersErrorDomain" code:NullParameters userInfo:[self returnErrorDescription:NullParameters]] autorelease];
        return nil;
    }
    else
    if ([_parameters objectForKey:JSONKEY_TRANSACTION_NAME] == NULL)
    {
        *error = [[[NSError alloc] initWithDomain:@"ParametersErrorDomain" code:InvalidNameParameters userInfo:[self returnErrorDescription:InvalidNameParameters]] autorelease];
        return nil;
    }
    else
    if ([_parameters objectForKey:JSONKEY_PARENT_ID] != NULL)
    {
        // If they've specified a parent id, set a hidden event that is our start time relative to the parent's
        // This way, when viewing the data, the relative times will be 100% accurate and not affected by bw/latency
        NSMutableDictionary *events = nil;
        @synchronized(self.active_interval_transaction)
        {
            NSString* parentId = [_parameters objectForKey:JSONKEY_PARENT_ID];
            if ([self.active_interval_transaction objectForKey:parentId] != NULL)
            {
                // calculate the difference between now and our parent's start time
                NSMutableDictionary *parentParemeters = [self.active_interval_transaction objectForKey:parentId];
                int duration=[self calculateDuration:[parentParemeters objectForKey:JSONKEY_STARTTIME] end_time:[NSDate date]];
            
                //Add Private event to this transaction
                events=[[[NSMutableDictionary alloc] init] autorelease];
                [events setObject:[NSNumber numberWithInt:duration] forKey:PARENT_OFFSET_KEY];
            }
            else
            {
                *error = [[[NSError alloc] initWithDomain:@"ParametersErrorDomain"
                                                    code:InvalidParentTransaction
                                                userInfo:[self returnErrorDescription:InvalidParentTransaction]] autorelease];
                return nil;
            }
        }
        @synchronized(self.event_transaction)
        {
            [self.event_transaction setObject:events forKey:transactionId];
        }
    }

    //Add Other Parameters
    [_parameters setObject:INTERVAL_TRANS_NAME forKey:JSONKEY_TRANSACTION_TYPE];
    [_parameters setObject:self.customer_id forKey:JSONKEY_CUSTOMER_ID];
    [_parameters setObject:self.app_id forKey:JSONKEY_APP_ID];
    [_parameters setObject:transactionId forKey:JSONKEY_TRANSACTION_ID];
    [_parameters setObject:self.Session_Id forKey:JSONKEY_SESSION_ID];
    [_parameters setObject:[NSNumber numberWithInt:SDK_VERSION] forKey:JSONKEY_MAITI_VERSION];
    [_parameters setObject:AGENT_TYPE forKey:JSONKEY_AGENT_TYPE];
    [_parameters setObject:[self DeviceModel] forKey:JSONKEY_HW_MODEL];
    [_parameters setObject:[UIDevice currentDevice].systemVersion forKey:JSONKEY_OS_VERSION ];
    [_parameters setObject:self.package_id forKey:JSONKEY_PACKAGE_ID];
    [_parameters setObject:self.app_code_ver forKey:JSONKEY_CODE_VER];
        
    if ([[self.Preferences objectForKey:PREF_RECORD_SERIAL] integerValue] == 1)
    {
         [_parameters setObject:[self DeviceUDID] forKey:JSONKEY_HW_SERIAL_NUMBER];
    }
    if ([[self.Preferences objectForKey:PREF_RECORD_CONN]  integerValue] == 1)
    {
         [_parameters setObject:[self CurrentNetwork] forKey:JSONKEY_CONNECTION_TYPE];
    }
    if ([[self.Preferences objectForKey:PREF_RECORD_MEM] integerValue] == 1)
    {
        long mem_free = [self iPhoneFreeMemory];
        long mem_total = [self iPhoneTotalMemory];
        [_parameters setObject:[NSNumber numberWithUnsignedLong: mem_free] forKey:JSONKEY_MEM_FREE];
        [_parameters setObject:[NSNumber numberWithUnsignedLong: mem_total]  forKey:JSONKEY_MEM_TOTAL];
    }
    
    [_parameters setObject:[NSDate date] forKey:JSONKEY_STARTTIME];
    
    @synchronized(self.active_interval_transaction)
    {
        [self.active_interval_transaction setObject:_parameters forKey:transactionId];
    }
    
    return transactionId;
}

//Complete Interval Parameter
-(void)CompleteIntervalTransaction:(NSString*)transactionId{
    
    if ([self IsDisabled] == YES)
        return;
    
    NSMutableDictionary *_parameters = nil;
    
    @synchronized(self.active_interval_transaction)
    {
        if ([self.active_interval_transaction objectForKey:transactionId] != NULL)
        {
            
            //Add Other Parameters
            _parameters=[[[NSMutableDictionary alloc] initWithDictionary:[self.active_interval_transaction objectForKey:transactionId]] autorelease];
            
            int duration=[self calculateDuration:[_parameters  objectForKey:JSONKEY_STARTTIME] end_time:[NSDate date]];
            [_parameters setObject:[NSNumber numberWithInt:duration] forKey:JSONKEY_DURATION]; //Calculate Duration
            
            [_parameters removeObjectForKey:JSONKEY_STARTTIME];    //Remove start time
            
            [_parameters setObject:[NSDate date] forKey:JSONKEY_ENDTIME];  //Transaction End Time
            
            if(([_parameters objectForKey:JSONKEY_USERTAG1] != NULL) && ([[_parameters objectForKey:JSONKEY_USERTAG1] length] > 128)){
                NSString *str = [_parameters objectForKey:JSONKEY_USERTAG1];
                str = [str substringToIndex: 128];
                [_parameters setObject:str forKey:JSONKEY_USERTAG1];
            }
            if(([_parameters objectForKey:JSONKEY_USERTAG2] != NULL) && ([[_parameters objectForKey:JSONKEY_USERTAG2] length] > 128)){
                NSString *str = [_parameters objectForKey:JSONKEY_USERTAG2];
                str = [str substringToIndex: 128];
                [_parameters setObject:str forKey:JSONKEY_USERTAG2];
            }
            if(([_parameters objectForKey:JSONKEY_USERTAG3] != NULL) && ([[_parameters objectForKey:JSONKEY_USERTAG3] length] > 128)){
                NSString *str = [_parameters objectForKey:JSONKEY_USERTAG3];
                str = [str substringToIndex: 128];
                [_parameters setObject:str forKey:JSONKEY_USERTAG3];
            }
            if(([_parameters objectForKey:JSONKEY_USERDATA] != NULL) && ([[_parameters objectForKey:JSONKEY_USERDATA] length] > 16384)){
                NSString *str = [_parameters objectForKey:JSONKEY_USERDATA];
                str = [str substringToIndex: 16384];
                [_parameters setObject:str forKey:JSONKEY_USERDATA];
            }
            
            [self.active_interval_transaction removeObjectForKey:transactionId];
        }
    }
    if (_parameters)
        @synchronized(self.completed_interval_transaction)
    {
        [self.completed_interval_transaction addObject:_parameters];
    }
    
    //NSLog(@"completed_interval_transaction: %@",completed_interval_transaction);
}

-(void)SetTransactionArgs:(NSString*)transactionId argType:(SupplementalTransactionArgs)argument data:(NSString*)data
{
    if ([self IsDisabled] == YES)
        return;
    if (!data) return;
    
    @synchronized(self.active_interval_transaction)
    {
        if ([self.active_interval_transaction objectForKey:transactionId] != NULL)
        {
            if (argument == UserTag1)
                [[self.active_interval_transaction objectForKey:transactionId] setObject:data forKey:JSONKEY_USERTAG1];
            else if (argument == UserTag2)
                [[self.active_interval_transaction objectForKey:transactionId] setObject:data forKey:JSONKEY_USERTAG2];
            else if (argument == UserTag3)
                [[self.active_interval_transaction objectForKey:transactionId] setObject:data forKey:JSONKEY_USERTAG3];
            else if (argument == UserData)
                [[self.active_interval_transaction objectForKey:transactionId] setObject:data forKey:JSONKEY_USERDATA];
            else if (argument == ParentTransactionId)
                [[self.active_interval_transaction objectForKey:transactionId] setObject:data forKey:JSONKEY_PARENT_ID];
            else if (argument == TransactionError)
                [[self.active_interval_transaction objectForKey:transactionId] setObject:data forKey:JSONKEY_ERROR];
        }
    }
}



-(void)AddEventWithTransaction:(NSString*)eventName transactionId:(NSString*)transactionId
{
    if ([self IsDisabled] == YES)
        return;
    
    int duration = 0;
    @synchronized(self.active_interval_transaction)
    {
        if ([self.active_interval_transaction objectForKey:transactionId] == NULL) return;
        NSMutableDictionary *_parameters=[[[NSMutableDictionary alloc] initWithDictionary:[self.active_interval_transaction objectForKey:transactionId]] autorelease];
        duration=[self calculateDuration:[_parameters  objectForKey:JSONKEY_STARTTIME] end_time:[NSDate date]];
    }
    @synchronized(self.event_transaction)
    {
        NSMutableDictionary *events = nil;
        if ([self.event_transaction objectForKey:transactionId] != NULL)
            events=[[[NSMutableDictionary alloc] initWithDictionary:[self.event_transaction objectForKey:transactionId]] autorelease];
        else
            events=[[[NSMutableDictionary alloc] init] autorelease];
        [events setObject:[NSNumber numberWithInt:duration] forKey:eventName];
        [self.event_transaction setObject:events forKey:transactionId];
    }
}

//Set Notification Transaction
-(void)SetNotificationTransaction:(NSMutableDictionary*)parameters  error:(NSError**)error{
    
    if ([self IsDisabled] == YES)
        return;
    
    NSString *transactionId = [self CreateTransactionID]; //Create transaction Id
    
    //Add Other Parameters
    NSMutableDictionary *_parameters=[[[NSMutableDictionary alloc] initWithDictionary:parameters] autorelease];
    
    //check error
    if (parameters == NULL)
    {
        *error = [[[NSError alloc] initWithDomain:@"ParametersErrorDomain" code:NullParameters userInfo:[self returnErrorDescription:NullParameters]] autorelease];
        return;
    }
    else if([_parameters objectForKey:JSONKEY_TRANSACTION_NAME] == NULL)
    {
        *error = [[[NSError alloc] initWithDomain:@"ParametersErrorDomain" code:InvalidNameParameters userInfo:[self returnErrorDescription:InvalidNameParameters]] autorelease];
        return;
    }
    
    if(([_parameters objectForKey:JSONKEY_USERTAG1] != NULL) && ([[_parameters objectForKey:JSONKEY_USERTAG1] length] > 128))
    {
        NSString *str = [_parameters objectForKey:JSONKEY_USERTAG1];
        str = [str substringToIndex: 128];
        [_parameters setObject:str forKey:JSONKEY_USERTAG1];
    }
    if(([_parameters objectForKey:JSONKEY_USERTAG2] != NULL) && ([[_parameters objectForKey:JSONKEY_USERTAG2] length] > 128))
    {
        NSString *str = [_parameters objectForKey:JSONKEY_USERTAG2];
        str = [str substringToIndex: 128];
        [_parameters setObject:str forKey:JSONKEY_USERTAG2];
    }
    if(([_parameters objectForKey:JSONKEY_USERTAG3] != NULL) && ([[_parameters objectForKey:JSONKEY_USERTAG3] length] > 128))
    {
        NSString *str = [_parameters objectForKey:JSONKEY_USERTAG3];
        str = [str substringToIndex: 128];
        [_parameters setObject:str forKey:JSONKEY_USERTAG3];
    }
    if(([_parameters objectForKey:JSONKEY_USERDATA] != NULL) && ([[_parameters objectForKey:JSONKEY_USERDATA] length] > 16384))
    {
        NSString *str = [_parameters objectForKey:JSONKEY_USERDATA];
        str = [str substringToIndex: 16384];
        [_parameters setObject:str forKey:JSONKEY_USERDATA];
    }
    
    
    [_parameters setObject:NOTIFICATION_TRANS_NAME forKey:JSONKEY_TRANSACTION_TYPE];
    [_parameters setObject:self.customer_id forKey:JSONKEY_CUSTOMER_ID];
    [_parameters setObject:self.app_id forKey:JSONKEY_APP_ID];
    [_parameters setObject:transactionId forKey:JSONKEY_TRANSACTION_ID];
    [_parameters setObject:self.Session_Id forKey:JSONKEY_SESSION_ID];
    [_parameters setObject:[NSNumber numberWithInt:SDK_VERSION] forKey:JSONKEY_MAITI_VERSION];
    [_parameters setObject:AGENT_TYPE forKey:JSONKEY_AGENT_TYPE];
    [_parameters setObject:[self DeviceModel] forKey:JSONKEY_HW_MODEL];
    [_parameters setObject:[UIDevice currentDevice].systemVersion forKey:JSONKEY_OS_VERSION ];
    [_parameters setObject:self.package_id forKey:JSONKEY_PACKAGE_ID];
    [_parameters setObject:self.app_code_ver forKey:JSONKEY_CODE_VER];
        
    if ([[self.Preferences objectForKey:PREF_RECORD_SERIAL] integerValue] == 1)
    {
        [_parameters setObject:[self DeviceUDID] forKey:JSONKEY_HW_SERIAL_NUMBER];
    }
    if ([[self.Preferences objectForKey:PREF_RECORD_CONN]  integerValue] == 1)
    {
        [_parameters setObject:[self CurrentNetwork] forKey:JSONKEY_CONNECTION_TYPE];
    }
    if ([[self.Preferences objectForKey:PREF_RECORD_MEM] integerValue] == 1)
    {
        long mem_free = [self iPhoneFreeMemory];
        long mem_total = [self iPhoneTotalMemory];
        [_parameters setObject:[NSNumber numberWithUnsignedLong: mem_free] forKey:JSONKEY_MEM_FREE];
        [_parameters setObject:[NSNumber numberWithUnsignedLong: mem_total]  forKey:JSONKEY_MEM_TOTAL];
    }
    
    [_parameters setObject:[NSDate date] forKey:JSONKEY_ENDTIME];  //Transaction End Time
    
    @synchronized(self.notification_transaction)
    {
        [self.notification_transaction addObject:_parameters];
    }
    //NSLog(@"notification_transaction: %@",notification_transaction);
    
    
   
}

//Create unique transaction ID
-(NSString*)CreateTransactionID
{
    
    CFUUIDRef newTransactionID = CFUUIDCreate(kCFAllocatorDefault);
    CFStringRef fcstr = CFUUIDCreateString(kCFAllocatorDefault, newTransactionID);
    NSString* TransactionID = [NSString stringWithString:(NSString*)fcstr];
    CFRelease(newTransactionID);
    CFRelease(fcstr);
    return TransactionID;
}

//Create unique Session Id
-(void)CreateSessionId{
    
    CFUUIDRef newSessionID = CFUUIDCreate(kCFAllocatorDefault);
    CFStringRef cfstr = CFUUIDCreateString(kCFAllocatorDefault, newSessionID);
    self.Session_Id = [NSString stringWithString:(NSString*)cfstr];
    CFRelease(newSessionID);
    CFRelease(cfstr);
    //NSLog(@"CreateSessionID %@", Session_Id);
}

//Calculate Duration in milliseconds
-(int) calculateDuration:(NSDate*)start_time end_time:(NSDate*)end_time
{
    
    double interval=[start_time timeIntervalSinceDate:end_time] * -1000.0;  //duration in milliseconds
    
    return (int) interval;

}

//Calculate Buffer Duration
-(int)calculateBufferDuration:(NSDate*)time
{
    
    double interval=[time timeIntervalSinceDate:[NSDate date]] * -1000.0;  //duration in milliseconds
    
    return (int) interval;
}

-(long)iPhoneFreeMemory
{
    mach_port_t host_port;
    mach_msg_type_number_t host_size;
    vm_size_t pagesize;
    
    host_port = mach_host_self();
    host_size = sizeof(vm_statistics_data_t) / sizeof(integer_t);
    host_page_size(host_port, &pagesize);
    
    vm_statistics_data_t vm_stat;
    
    if (host_statistics(host_port, HOST_VM_INFO, (host_info_t)&vm_stat, &host_size) != KERN_SUCCESS)
        NSLog(@"Failed to fetch vm statistics");
    
    /* Stats in bytes */
    long mem_free = vm_stat.free_count * pagesize;
    
    return (long) mem_free;
}

-(long)iPhoneTotalMemory
{
    mach_port_t host_port;
    mach_msg_type_number_t host_size;
    vm_size_t pagesize;
    
    host_port = mach_host_self();
    host_size = sizeof(vm_statistics_data_t) / sizeof(integer_t);
    host_page_size(host_port, &pagesize);
    
    vm_statistics_data_t vm_stat;
    
    if (host_statistics(host_port, HOST_VM_INFO, (host_info_t)&vm_stat, &host_size) != KERN_SUCCESS)
        NSLog(@"Failed to fetch vm statistics");
    
    /* Stats in bytes */
    long mem_used = (vm_stat.active_count +
                          vm_stat.inactive_count +
                          vm_stat.wire_count) * pagesize;
    long mem_free = vm_stat.free_count * pagesize;
    long mem_total = mem_used + mem_free;
    
    return (long) mem_total;
}

- (NSString *)DeviceModel
{
    NSString *machine;
    size_t size;
    sysctlbyname("hw.machine", NULL, &size, NULL, 0);
    char *name = malloc(size);
    sysctlbyname("hw.machine", name, &size, NULL, 0);
    machine = [NSString stringWithUTF8String:name];
    free(name);
    return machine;
}

- (NSString *)DeviceUDID
{
    NSString *UDID;
    
    @try
    {
        UDID=[[[UIDevice currentDevice] identifierForVendor] UUIDString];
    }
    @catch (NSException *e)
    {
        //UDID=[[UIDevice currentDevice] uniqueIdentifier];     //Deprecated in iOS 5
        UDID = [self CreateTransactionID];
    }
    
    if (UDID == NULL)
        return @"unknown";
    
    return UDID;
}


- (NSString *)CurrentNetwork
{
    NSString *network;
    
    Reachability *reachability = [Reachability reachabilityForInternetConnection];
    [reachability startNotifier];
    
    NetworkStatus status = [reachability currentReachabilityStatus];
    
    if(status == NotReachable)
    {
        //No internet
        network=NETWORK_NOT_AVAILABLE_KEY;
    }
    else if (status == ReachableViaWiFi)
    {
        //WiFi
        network=NETWORK_WIFI_KEY;
    }
    else if (status == ReachableViaWWAN)
    {
        //3G
        network=NETWORK_3G_KEY;
    }
    else
    {
        network=NETWORK_UNKNOWN_KEY;
    }
    
    return network;
}


-(void)sendDataToServer
{  //Send Data to Server
    
    if ([self IsDisabled] == YES)
        return;
    
    if (![[self CurrentNetwork] isEqualToString:NETWORK_NOT_AVAILABLE_KEY])
    {  //If Network reachable
        
        
        NSMutableArray *AllData=[[[NSMutableArray alloc] init] autorelease];
        [self.AllSendingData_Array removeAllObjects];

        @synchronized(self.completed_interval_transaction)
        {
            if([self.completed_interval_transaction count] != 0)
            {    //Interval Transaction
                [AllData addObjectsFromArray:self.completed_interval_transaction];
                [self.completed_interval_transaction removeAllObjects];
            }
        }
        
        @synchronized(self.notification_transaction)
        {
            if([self.notification_transaction count] != 0)
            {  //Notification Transaction
                [AllData addObjectsFromArray:self.notification_transaction];
                [self.notification_transaction removeAllObjects];
            }
        }
        
        
        if([AllData count] != 0)
        {
            for (int count=0; count <[AllData count] ; count++)
            {
                NSMutableDictionary *_parameters=[[[NSMutableDictionary alloc] initWithDictionary:[AllData objectAtIndex:count]] autorelease];
                
                //Calculate buffer offset
                int BufferDuration=[self calculateBufferDuration:[_parameters  objectForKey:JSONKEY_ENDTIME]];
                [_parameters setObject:[NSNumber numberWithInt:BufferDuration] forKey:@"offset_ms"]; //Calculate Duration
                [_parameters removeObjectForKey:JSONKEY_ENDTIME];
                
                
                //Add Events
                @synchronized(self.event_transaction)
                {
                    if ([self.event_transaction objectForKey:[_parameters  objectForKey:JSONKEY_TRANSACTION_ID]] != NULL)
                    {
                        //Add Events
                        NSMutableDictionary *events=[[[NSMutableDictionary alloc] initWithDictionary:[self.event_transaction objectForKey:[_parameters  objectForKey:JSONKEY_TRANSACTION_ID]]] autorelease];
                        [_parameters setObject:events forKey:@"events"];
                    
                        [self.event_transaction removeObjectForKey:[_parameters  objectForKey:JSONKEY_TRANSACTION_ID]];
                    }
                }
                [self.AllSendingData_Array addObject:_parameters];
            }
            
            //*************************
            //  Send data to server
            //*************************
            
            //Server Connection
            ServerConnection *_ServerConnection=[[[ServerConnection alloc] init] autorelease];
            _ServerConnection.logic_view=self;
            [_ServerConnection WebApi_ConnectionObject:self.AllSendingData_Array customerId:self.customer_id];
            
            //NSLog(@"All Data: %@",AllSendingData_Array);
        }
    }
}


//Error Description
-(NSDictionary*)returnErrorDescription:(NSInteger)errorcode{
   
    NSDictionary *errorDictionary = nil;
    
    switch (errorcode) {
        case NullParameters:
            errorDictionary = @{ NSLocalizedDescriptionKey : @"null parameters is not allowed" };
            break;
        case InvalidNameParameters:
            errorDictionary = @{ NSLocalizedDescriptionKey : @"invalid name parameters"};
            break;
        case TooLongUserTag:
            errorDictionary = @{ NSLocalizedDescriptionKey : @"user tag parameter value is invalid or too long."};
            break;
        case TooLongUserData:
            errorDictionary = @{ NSLocalizedDescriptionKey : @"user data parameter value is invalid or too long."};
            break;
        case InvalidParentTransaction:
            errorDictionary = @{ NSLocalizedDescriptionKey : @"invalid parent transaction"};
            break;
        default:
            break;
    }
 
    return errorDictionary;
}


@end
