//
//  PerformanceLibrary_Logic.h
//  SimplePerformanceLibrary
/*
===================
Copyright (c) 2013 Riverbed Technology, Inc.

MAITI is licensed under the terms and conditions of the MIT License as set forth in LICENSE.TXT, which accompanies the software.  MAITI is distributed “AS IS” as set forth in the MIT License.
===================
*/

#import <Foundation/Foundation.h>

@interface PerformanceLibrary_Logic : NSObject{
    NSDictionary *Preferences;          //user Preferences
    
    NSMutableDictionary *active_interval_transaction;   //For active interval transaction
    NSMutableArray *completed_interval_transaction;    //For completed interval transaction
    NSMutableDictionary *event_transaction;            //For Event
    NSMutableArray *notification_transaction;          //For Notification transaction
    
    NSString *Session_Id;      // Session ID updates whenever the user logs into the app
    NSString *customer_id;     // Customer ID set in the constructor
    NSString *app_id;          // App ID set int he constructor
    
    NSString *app_code_ver;    // Version of the App defined in the bundle
    NSString *package_id;      // Package ID defined in the bundle
    Boolean Disabled;          // Whether the library is enabled or disabled
    
    NSTimer *TimeIntervalToConnectServer;
    NSMutableArray *AllSendingData_Array;
    
    
}

typedef enum {
    UserTag1,
    UserTag2,
    UserTag3,
    UserData,
    ParentTransactionId,
    TransactionError
    
} SupplementalTransactionArgs;


@property(nonatomic,retain)NSDictionary *Preferences;

@property(nonatomic,retain)NSMutableDictionary *active_interval_transaction;
@property(nonatomic,retain)NSMutableArray *completed_interval_transaction;
@property(nonatomic,retain)NSMutableDictionary *event_transaction;
@property(nonatomic,retain)NSMutableArray *notification_transaction;


@property(nonatomic,copy)NSString *Session_Id;
@property(nonatomic,copy)NSString *customer_id;
@property(nonatomic,copy)NSString *app_id;

@property(nonatomic,copy)NSString *app_code_ver;
@property(nonatomic,copy)NSString *package_id;
@property(nonatomic)Boolean Disabled;


-(id)initWithCustomerId:(NSString*) customerId appId:(NSString *)appId;
-(NSString*)SetIntervalTransaction:(NSMutableDictionary*)parameters error:(NSError**)error;
-(void)CompleteIntervalTransaction:(NSString*)transactionId;
-(void)SetTransactionArgs:(NSString*)transactionId argType:(SupplementalTransactionArgs)argument data:(NSString*)data;
-(void)AddEventWithTransaction:(NSString*)eventName transactionId:(NSString*)transactionId;
-(void)SetNotificationTransaction:(NSMutableDictionary*)parameters error:(NSError**)error;

@end
