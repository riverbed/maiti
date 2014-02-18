//
//  MTSViewController.m
//  MAITI-TestSuite
//
//  Created by Roman Makhnenko on 13/02/14.
//  Copyright (c) 2014 DataArt Solutions, Inc. All rights reserved.
//

#import "MTSAppDelegate.h"
#import "MTSViewController.h"

#include <string>
#include <sstream>
#include <exception>

@interface MTSViewController ()
{
    long notificationId_;
    long eventCounter_;
    long errorCounter_;
    long userDataCounter_;
    long tag1Counter_;
    long tag2Counter_;
    long tag3Counter_;
}

@property (retain, nonatomic) NSString* simpleTransactionId;
@property (retain, nonatomic) NSString* mainTransactionId;
@property (retain, nonatomic) NSMutableArray* childStack;

@property (retain, nonatomic) IBOutlet UIButton *simpleTransactionButton;
@property (retain, nonatomic) IBOutlet UIButton *startTransactionButton;
@property (retain, nonatomic) IBOutlet UIButton *startChidTransactionButton;
@property (retain, nonatomic) IBOutlet UIButton *stopChildTransactionButton;
@property (retain, nonatomic) IBOutlet UIButton *stopTransactionButton;
@property (retain, nonatomic) IBOutlet UISwitch *MAITISwitch;
@property (retain, nonatomic) IBOutlet UILabel *childCounter;

-(IBAction)didSimpleTransactionTap:(id)sender;
-(IBAction)didStartTransactionTap:(id)sender;
-(IBAction)didStartChildTransactionTap:(id)sender;
-(IBAction)didStopChildTransaction:(id)sender;
-(IBAction)didStopTransactionTap:(id)sender;
-(IBAction)didMaitiChange:(id)sender;
-(IBAction)didNotificationTap:(id)sender;
-(IBAction)didExtraTap:(id)sender;
-(IBAction)didExtraChidTap:(id)sender;
-(IBAction)didMessageTap:(id)sender;
-(IBAction)didMessageChildTap:(id)sender;
-(IBAction)didUserDataTap:(id)sender;
-(IBAction)didChildDataTap:(id)sender;
-(IBAction)didTag1Tap:(id)sender;
-(IBAction)didTag2Tap:(id)sender;
-(IBAction)didTag3Tap:(id)sender;
-(IBAction)didCrashTap:(id)sender;

-(void)updateChildNum;

@end

@implementation MTSViewController

-(void)dealloc
{
    self.simpleTransactionButton = nil;
    self.simpleTransactionId = nil;
    self.mainTransactionId = nil;
    self.startTransactionButton = nil;
    self.startChidTransactionButton = nil;
    self.stopChildTransactionButton = nil;
    self.stopTransactionButton = nil;
    self.MAITISwitch = nil;
    self.childStack = nil;
    self.childCounter = nil;
    [super dealloc];
}

-(void)viewDidLoad
{
    [super viewDidLoad];
    notificationId_ = 0;
    eventCounter_ = 0;
    errorCounter_ = 0;
    userDataCounter_ = 0;
    tag1Counter_ = 0;
    tag2Counter_ = 0;
    tag3Counter_ = 0;
    self.childStack = [NSMutableArray array];
    [self updateChildNum];
	// Do any additional setup after loading the view, typically from a nib.
}

-(void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)updateChildNum
{
    self.childCounter.text = [NSString stringWithFormat:@"%d child transaction(s)",[self.childStack count]];
}

-(void)didEndSimpleTransaction
{
    [appd().performanceLibrary TransactionEnd:self.simpleTransactionId];
    self.simpleTransactionButton.enabled = YES;
}

-(void)didSimpleTransactionTap:(id)sender
{
    self.simpleTransactionButton.enabled = NO;
    self.simpleTransactionId = [appd().performanceLibrary TransactionStart:@"SimpleTransaction"];
    [self performSelector:@selector(didEndSimpleTransaction) withObject:nil afterDelay:4];
}

-(void)didStartTransactionTap:(id)sender
{
    self.startTransactionButton.enabled = NO;
    self.stopTransactionButton.enabled = YES;
    self.mainTransactionId = [appd().performanceLibrary TransactionStart:@"ParentTransaction"];
}

-(void)didStartChildTransactionTap:(id)sender
{
    if (![self.childStack count])
        [self.childStack addObject:[appd().performanceLibrary TransactionStart:@"ChildTransaction from root" parentTransactionId:self.mainTransactionId]];
    else
        [self.childStack addObject:[appd().performanceLibrary TransactionStart:[NSString stringWithFormat:@"ChildTransaction from child %d",[self.childStack count]+1] parentTransactionId:[self.childStack lastObject]]];
    [self updateChildNum];
}

-(void)didStopChildTransaction:(id)sender
{
    if (![self.childStack count]) return;
    [appd().performanceLibrary TransactionEnd:[self.childStack lastObject]];
    [self.childStack removeLastObject];
    [self updateChildNum];
}

-(void)didStopTransactionTap:(id)sender
{
    self.startTransactionButton.enabled = YES;
    self.stopTransactionButton.enabled = NO;
    [appd().performanceLibrary TransactionEnd:self.mainTransactionId];
}

-(void)didMaitiChange:(id)sender
{
    [appd().performanceLibrary SetDisabled:!self.MAITISwitch.on];
}

-(void)didNotificationTap:(id)sender
{
    [appd().performanceLibrary Notification:@"JustNotification" userTag1:[[NSNumber numberWithLong:notificationId_++] stringValue]];
}

-(void)didExtraTap:(id)sender
{
    [appd().performanceLibrary SetTransactionEvent:[NSString stringWithFormat:@"ExtraEvent_%ld",eventCounter_++] transactionId:self.mainTransactionId];
}

-(void)didExtraChidTap:(id)sender
{
    if (![self.childStack count]) return;
    [appd().performanceLibrary SetTransactionEvent:[NSString stringWithFormat:@"ExtraEvent_%ld",eventCounter_++] transactionId:[self.childStack lastObject]];
}

-(void)didMessageTap:(id)sender
{
    [appd().performanceLibrary SetErrorMessage:[NSString stringWithFormat:@"ErrorMessage_%ld",errorCounter_++] transactionId:self.mainTransactionId];
}

-(void)didMessageChildTap:(id)sender
{
    if (![self.childStack count]) return;
    [appd().performanceLibrary SetErrorMessage:[NSString stringWithFormat:@"ErrorMessage_%ld",errorCounter_++] transactionId:[self.childStack lastObject]];
}

-(void)didUserDataTap:(id)sender
{
    std::ostringstream os;
    os << userDataCounter_++ << " " << std::string(15996,'E');
    [appd().performanceLibrary SetUserData:@(os.str().c_str()) transactionId:self.mainTransactionId];
}

-(void)didChildDataTap:(id)sender
{
    if (![self.childStack count]) return;
    std::ostringstream os;
    os << userDataCounter_++ << " " << std::string(15996,'C');
    [appd().performanceLibrary SetUserData:@(os.str().c_str()) transactionId:[self.childStack lastObject]];
}

-(void)didTag1Tap:(id)sender
{
    [appd().performanceLibrary SetUserTag1:[NSString stringWithFormat:@"Tag1: %lu",tag1Counter_++] transactionId:self.mainTransactionId];
}

-(void)didTag2Tap:(id)sender
{
    [appd().performanceLibrary SetUserTag2:[NSString stringWithFormat:@"Tag2: %lu",tag2Counter_++] transactionId:self.mainTransactionId];
}

-(void)didTag3Tap:(id)sender
{
    [appd().performanceLibrary SetUserTag3:[NSString stringWithFormat:@"Tag3: %lu",tag3Counter_++] transactionId:self.mainTransactionId];
}

-(void)didCrashTap:(id)sender
{
    std::terminate();
}

@end
