//
//  MTSViewController.m
//  MAITI-TestSuite
//
//  Created by Roman Makhnenko on 13/02/14.
//  Copyright (c) 2014 DataArt Solutions, Inc. All rights reserved.
//

#import "MTSAppDelegate.h"
#import "MTSViewController.h"

@interface MTSViewController ()
{
    long notificationId_;
}

@property (retain, nonatomic) NSString* simpleTransactionId;
@property (retain, nonatomic) NSString* mainTransactionId;
@property (retain, nonatomic) NSString* childTransactionId;

@property (retain, nonatomic) IBOutlet UIButton *simpleTransactionButton;
@property (retain, nonatomic) IBOutlet UIButton *startTransactionButton;
@property (retain, nonatomic) IBOutlet UIButton *startChidTransactionButton;
@property (retain, nonatomic) IBOutlet UIButton *stopChildTransactionButton;
@property (retain, nonatomic) IBOutlet UIButton *stopTransactionButton;
@property (retain, nonatomic) IBOutlet UISwitch *MAITISwitch;

-(IBAction)didSimpleTransactionTap:(id)sender;
-(IBAction)didStartTransactionTap:(id)sender;
-(IBAction)didStartChildTransactionTap:(id)sender;
-(IBAction)didStopChildTransaction:(id)sender;
-(IBAction)didStopTransactionTap:(id)sender;
-(IBAction)didMaitiChange:(id)sender;
-(IBAction)didNotificationTap:(id)sender;

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
    self.childTransactionId = nil;
    self.MAITISwitch = nil;
    [super dealloc];
}

-(void)viewDidLoad
{
    [super viewDidLoad];
    notificationId_ = 0;
	// Do any additional setup after loading the view, typically from a nib.
}

-(void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
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
    self.startChidTransactionButton.enabled = NO;
    self.stopChildTransactionButton.enabled = YES;
    self.childTransactionId = [appd().performanceLibrary TransactionStart:@"ChildTransaction" parentTransactionId:self.mainTransactionId];
}

-(void)didStopChildTransaction:(id)sender
{
    self.startChidTransactionButton.enabled = YES;
    self.stopChildTransactionButton.enabled = NO;
    [appd().performanceLibrary TransactionEnd:self.childTransactionId];
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




@end
