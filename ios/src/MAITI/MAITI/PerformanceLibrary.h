//
//  PerformanceLibrary.h
//  PerformanceLibrary
/*
===================
Copyright (c) 2013 Riverbed Technology, Inc.

MAITI is licensed under the terms and conditions of the MIT License as set forth in LICENSE.TXT, which accompanies the software.  MAITI is distributed “AS IS” as set forth in the MIT License.
===================
*/

#import <Foundation/Foundation.h>

@class PerformanceLibrary_Logic;

@interface PerformanceLibrary : NSObject{
    
    PerformanceLibrary_Logic *PL_Logic;
}


/**************
 Parameters Example:
 
 NSMutableDictionary *parameters = [[NSMutableDictionary alloc] init];
 [parameters setObject:@"" forKey:@"name"]; //mandatory parameters
 [parameters setObject:@"" forKey:@"usertag1"];     //optional parameters
 [parameters setObject:@"" forKey:@"usertag2"];     //optional parameters
 [parameters setObject:@"" forKey:@"usertag3"];     //optional parameters
 [parameters setObject:@"" forKey:@"userdata"];     //optional parameters
 [parameters setObject:@"" forKey:@"parent_id"];     //optional parameters
 
 ***************/

-(id)initWithCustomerId:(NSString*) customerId appId:(NSString*)appId;

// Interval Transaction
// Interval Transaction Start
-(NSString*)TransactionStart:(NSString*)name;
-(NSString*)TransactionStart:(NSString*)name parentTransactionId:(NSString*)parentTransactionId;

// Interval Transaction End
-(void)TransactionEnd:(NSString*)transactionId;


// Interval Event Transaction
-(void)SetTransactionEvent:(NSString*)eventName transactionId:(NSString*)transactionId;
-(void)SetErrorMessage:(NSString*)errorMessage transactionId:(NSString*)transactionId;
-(void)SetUserTag1:(NSString*)tag transactionId:(NSString*)transactionId;
-(void)SetUserTag2:(NSString*)tag transactionId:(NSString*)transactionId;
-(void)SetUserTag3:(NSString*)tag transactionId:(NSString*)transactionId;
-(void)SetUserData:(NSString*)data transactionId:(NSString*)transactionId;
-(void)SetDisabled:(Boolean)disabled;

// Notification Transaction
-(void)Notification:(NSString*)name userTag1:(NSString*)userTag1;

@end
