//
//  WebViewController.m
//  SamplePerformanceLibrary
//
//  Copyright (c) 2013 Riverbed Technology. All rights reserved.
//

#import "WebViewController.h"
#import "MEAAppDelegate.h"

// hit apple w/ a 5000 millisecond delay
#define WEBPAGE_URL @"http://deelay.me/5000?http://www.apple.com/"

@interface WebViewController ()

@property(nonatomic,assign) MEAAppDelegate* AppDelegate;
@property(nonatomic,retain) IBOutlet UIWebView *webview;
@property(nonatomic,retain) NSString *transID;

@end

@implementation WebViewController

-(void)dealloc
{
    self.webview.delegate = nil;
    [self.webview stopLoading];
    self.webview = nil;
    self.transID = nil;
    [super dealloc];
}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
    //Assign Application Delegate
	_AppDelegate=(MEAAppDelegate*)[[UIApplication sharedApplication] delegate];
    
    NSURLRequest *theRequest=[NSURLRequest requestWithURL:[NSURL URLWithString:WEBPAGE_URL]];
   [self.webview loadRequest:theRequest];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark UIWebView delegates

- (void)webView:(UIWebView *)_webView didFailLoadWithError:(NSError *)error
{
}

- (void)webViewDidStartLoad:(UIWebView *)_webView
{

    self.transID=[self.AppDelegate.performanceLibrary TransactionStart: @"Load Web Page" ];
    
    
    [self.AppDelegate.performanceLibrary SetUserTag1:WEBPAGE_URL transactionId:self.transID];
    
    
    [self.AppDelegate.performanceLibrary SetTransactionEvent:@"Load \"&\" Start" transactionId:self.transID];
    [self.AppDelegate.performanceLibrary SetErrorMessage:@"Error doing something" transactionId:self.transID];
    
    [NSThread sleepForTimeInterval:0.302];
    
    NSString* simpleSubTransId = [self.AppDelegate.performanceLibrary TransactionStart: @"Load Web Page" parentTransactionId:self.transID ];
    
    [self.AppDelegate.performanceLibrary TransactionEnd:simpleSubTransId];
    


}

-(void)webViewDidFinishLoad:(UIWebView *)_webView
{
    [self.AppDelegate.performanceLibrary TransactionEnd:self.transID];
}

@end
