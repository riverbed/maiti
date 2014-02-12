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

@interface PerformanceLibrary_Logic : NSObject

typedef enum
{
    UserTag1,
    UserTag2,
    UserTag3,
    UserData,
    ParentTransactionId,
    TransactionError
    
} SupplementalTransactionArgs;


@property(nonatomic,retain) NSDictionary *Preferences;
@property(nonatomic) Boolean Disabled;

-(id)initWithCustomerId:(NSString*) customerId appId:(NSString *)appId;
-(NSString*)SetIntervalTransaction:(NSMutableDictionary*)parameters error:(NSError**)error;
-(void)CompleteIntervalTransaction:(NSString*)transactionId;
-(void)SetTransactionArgs:(NSString*)transactionId argType:(SupplementalTransactionArgs)argument data:(NSString*)data;
-(void)AddEventWithTransaction:(NSString*)eventName transactionId:(NSString*)transactionId;
-(void)SetNotificationTransaction:(NSMutableDictionary*)parameters error:(NSError**)error;

@end
