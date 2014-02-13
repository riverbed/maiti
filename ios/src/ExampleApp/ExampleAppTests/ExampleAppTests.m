//
//  ExampleAppTests.m
//  ExampleAppTests
//
//  Created by Roman Makhnenko on 12/02/14.
//  Copyright (c) 2014 DataArt Solutions, Inc. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "MAITI/PerformanceLibrary.h"

@interface ExampleAppTests : XCTestCase
{
}

@property(retain, nonatomic) PerformanceLibrary *PerformanceLibrary;

@end

@implementation ExampleAppTests

- (void)setUp
{
    [super setUp];
    self.PerformanceLibrary=[[[PerformanceLibrary alloc] initWithCustomerId:@"myCustomerId" appId:@"myAppId"] autorelease];
}

- (void)tearDown
{
    self.PerformanceLibrary = nil;
    [super tearDown];
}

- (void)testIntervalTransaction
{
//    XCTFail(@"No implementation for \"%s\"", __PRETTY_FUNCTION__);
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
