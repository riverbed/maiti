//
//  WebViewController.m
//  SamplePerformanceLibrary
//
//  Copyright (c) 2013 Riverbed Technology. All rights reserved.
//

#import "WebViewController.h"
#import "AppDelegate.h"

// hit apple w/ a 5000 millisecond delay
#define WEBPAGE_URL @"http://deelay.me/5000?http://www.apple.com/"

@interface WebViewController ()

@end

@implementation WebViewController


@synthesize webview;
@synthesize _AppDelegate;

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
	_AppDelegate=(AppDelegate*)[[UIApplication sharedApplication] delegate];
    
    NSURLRequest *theRequest=[NSURLRequest requestWithURL:[NSURL URLWithString:WEBPAGE_URL]];
   [webview loadRequest:theRequest];
}

- (void)dealloc
{
    if (webview)
    {
        [webview setDelegate:NULL];
        [webview stopLoading];
    }
    
    [super dealloc];
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

    transID=[[_AppDelegate _PerformanceLibrary] TransactionStart: @"Load Web Page" ];
    
    
    [[_AppDelegate _PerformanceLibrary] SetUserTag1:WEBPAGE_URL transactionId:transID];
    
    
    [[_AppDelegate _PerformanceLibrary] SetTransactionEvent:@"Load \"&\" Start" transactionId: transID];
    [[_AppDelegate _PerformanceLibrary] SetErrorMessage:@"Error doing something" transactionId: transID];
    
    [NSThread sleepForTimeInterval:0.302];
    
    NSString* simpleSubTransId = [[_AppDelegate _PerformanceLibrary] TransactionStart: @"Load Web Page" parentTransactionId:transID ];
    
    [[_AppDelegate _PerformanceLibrary] TransactionEnd:simpleSubTransId];
    


}
- (void)webViewDidFinishLoad:(UIWebView *)_webView
{
    [[_AppDelegate _PerformanceLibrary] TransactionEnd:transID];
}
@end
