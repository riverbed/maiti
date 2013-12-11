//
//  PerformanceLibrary.m
//  PerformanceLibrary
/*
===================
Copyright (c) 2013 Riverbed Technology, Inc.

MAITI is licensed under the terms and conditions of the MIT License as set forth in LICENSE.TXT, which accompanies the software.  MAITI is distributed “AS IS” as set forth in the MIT License.
===================
*/

#import "PerformanceLibrary.h"

#import "PerformanceLibrary.h"
#import "PerformanceLibrary_Logic.h"

@implementation PerformanceLibrary

//Initialize Class
-(id)initWithCustomerId:(NSString*) customerId appId:(NSString*)appId {
    
    //Logic Part of SDK
    // Copy strings to fix memory issues when integrating w/ Xamarin (.NET)
    NSString* cid_copy = [NSString stringWithString:customerId];
    NSString* aid_copy = [NSString stringWithString:appId];
    PL_Logic=[[PerformanceLibrary_Logic alloc] initWithCustomerId:cid_copy appId:aid_copy ];
    
    return self;
}


// Interval Transaction Start
-(NSString*)TransactionStart:(NSString*)name {
    
    NSError *error = nil;
    
    if (name == NULL)
        return @"";
    
    NSMutableDictionary *parameters = [[NSMutableDictionary alloc] init];
    [parameters setObject:name forKey:@"name"];
    
    NSString *transactionId=[PL_Logic SetIntervalTransaction:parameters  error:&error];
    
    [parameters release];
    
    //NSLog(@"error %@", error.localizedDescription);
    
    return transactionId;
}

-(NSString*)TransactionStart:(NSString*)name parentTransactionId:(NSString*)parentTransactionId {
    NSError *error = nil;
    
    if (name == NULL || parentTransactionId == NULL)
        return @"";
    
    NSMutableDictionary *parameters = [[NSMutableDictionary alloc] init];
    [parameters setObject:name forKey:@"name"];
    [parameters setObject:parentTransactionId forKey:@"parent_id"];
    
    NSString *transactionId=[PL_Logic SetIntervalTransaction:parameters  error:&error];
    
    [parameters release];
    
    //NSLog(@"error %@", error.localizedDescription);
    
    return transactionId;}



// Interval Transaction End
-(void)TransactionEnd:(NSString*)transactionId{
    [PL_Logic CompleteIntervalTransaction:transactionId];
}


// Interval Event Transaction
-(void)SetTransactionEvent:(NSString*)eventName transactionId:(NSString*)transactionId{
    [PL_Logic AddEventWithTransaction:eventName transactionId:transactionId];
}

-(void)SetErrorMessage:(NSString*)errorMessage transactionId:(NSString*)transactionId{
    [PL_Logic SetTransactionArgs:transactionId argType:TransactionError data:errorMessage];
}
-(void)SetUserTag1:(NSString*)tag transactionId:(NSString*)transactionId {
    [PL_Logic SetTransactionArgs:transactionId argType:UserTag1 data:tag];
}
-(void)SetUserTag2:(NSString*)tag transactionId:(NSString*)transactionId{
    [PL_Logic SetTransactionArgs:transactionId argType:UserTag2 data:tag];
}
-(void)SetUserTag3:(NSString*)tag transactionId:(NSString*)transactionId{
    [PL_Logic SetTransactionArgs:transactionId argType:UserTag3 data:tag];
}
-(void)SetUserData:(NSString*)data transactionId:(NSString*)transactionId{
    [PL_Logic SetTransactionArgs:transactionId argType:UserData data:data];
}

-(void)SetDisabled:(Boolean)disabled {
    if (disabled == NO)
        PL_Logic.Disabled = NO;
    else
        PL_Logic.Disabled = YES;
}

// Notification Transaction
-(void)Notification:(NSString*)name userTag1:(NSString*)userTag1  {
    
    NSError *error = nil;
    
    if (name == NULL)
        return;
    
    NSMutableDictionary *parameters = [[NSMutableDictionary alloc] init];
    [parameters setObject:name forKey:@"name"];
    
    if (userTag1 != NULL)
        [parameters setObject:userTag1 forKey:@"utag1"];
    
    [PL_Logic SetNotificationTransaction:parameters   error:&error];
    
    NSLog(@"error %@", error.localizedDescription);
    
    [parameters release];
}


@end
