//
//  PerformanceLibraryTests.m
//  PerformanceLibraryTests
//
//  Copyright (c) 2013 Riverbed Technology. All rights reserved.
//

#import "UnitTests.h"

@implementation PerformanceLibraryTests

@synthesize _PerformanceLibrary;

- (void)setUp
{
    [super setUp];
    
    _PerformanceLibrary=[[PerformanceLibrary alloc] initWithCustomerId:@"myCustomerId" appId:@"myAppId"];
}

- (void)tearDown
{
    // Tear-down code here.
    
    [super tearDown];
}

- (void)testIntervalTransaction
{
    // Normal Interval Transaction
    NSString* transID=[_PerformanceLibrary TransactionStart: @"Load Test1" ];
   
    // Bad value for interval transaction ID
    [_PerformanceLibrary TransactionEnd:@""];
    
    // NULL interval transaction ID
    [_PerformanceLibrary TransactionEnd:NULL];
    
    // Correct Interval transaction ID
    [_PerformanceLibrary TransactionEnd:transID];
    
    // Null Name Value
    transID=[_PerformanceLibrary TransactionStart: NULL ];
    [_PerformanceLibrary TransactionEnd:transID];
    
    // Null user tags
    transID=[_PerformanceLibrary TransactionStart: @"Load Test2" ];
    [_PerformanceLibrary SetUserTag1:NULL transactionId:transID];
    [_PerformanceLibrary SetUserTag1:@"" transactionId:NULL];
    [_PerformanceLibrary TransactionEnd:transID];
    
}

- (void)testDisabledTransactions
{
    [_PerformanceLibrary SetDisabled:YES];
    
    NSString* transID=[_PerformanceLibrary TransactionStart: @"Load Test1" ];
    [_PerformanceLibrary TransactionEnd:transID];
    
    [_PerformanceLibrary Notification:@"Notification 1" userTag1: @"Tag1" ];
    
    [_PerformanceLibrary SetDisabled:NO];
}

- (void)testNotificationTransaction
{
    // Normal Notification Transaction
    [_PerformanceLibrary Notification:@"Notification 1" userTag1: @"Tag1" ];
    
    // Null name value
    [_PerformanceLibrary Notification:NULL userTag1: @"Tag1" ];
    
    // Null Tag value
    [_PerformanceLibrary Notification:@"Notification 1" userTag1: NULL ];
    
    // Null both values
    [_PerformanceLibrary Notification:NULL userTag1: NULL ];

}

@end
