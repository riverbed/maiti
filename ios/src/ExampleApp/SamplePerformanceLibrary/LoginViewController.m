//
//  LoginViewController.m
//  SamplePerformanceLibrary
//
//  Copyright (c) 2013 Riverbed Technology. All rights reserved.
//

#import "LoginViewController.h"
#import "AppDelegate.h"

@interface LoginViewController ()
-(void)GenericAlert:title msg:(NSString*)msg;
-(void)WebApi_ConnectionObject;
@end

@implementation LoginViewController

@synthesize _AppDelegate;
@synthesize userName;
@synthesize Password;
@synthesize login_but;
@synthesize logout_but;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

-(IBAction)login:(id)sender{
    if ([[userName.text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]] length] <= 0){
        [self GenericAlert:nil msg:@"User Name cannot be left blank."];
    }
    else if ([[Password.text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]] length] <= 0){
        [self GenericAlert:nil msg:@"Password cannot be left blank."];
    }else{
         
        [self WebApi_ConnectionObject];
        Password.text=@"";
    }
}

-(IBAction)logout:(id)sender{
    userName.alpha=1.0;
    Password.alpha=1.0;
    login_but.alpha=1.0;
    logout_but.alpha=0.0;
    
    //Transaction Strat

    
    NSError *error = nil;
    [[_AppDelegate _PerformanceLibrary] Notification:@"Logout" userTag1:@""];
    NSLog(@"error %@", error.localizedDescription);
    
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [userName becomeFirstResponder];
    
    //Assign Application Delegate
	_AppDelegate=(AppDelegate*)[[UIApplication sharedApplication] delegate];
    jsonWriter = [[SBJsonWriter new] retain];

    userName.alpha=1.0;
    Password.alpha=1.0;
    login_but.alpha=1.0;
    logout_but.alpha=0.0;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Alert Message
-(void)GenericAlert:title msg:(NSString*)msg{
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:title
                                                    message:msg
                                                   delegate:self
                                          cancelButtonTitle:@"OK"
                                          otherButtonTitles:nil];
    [alert show];
    [alert release];
}



-(void)WebApi_ConnectionObject{
    
    NSString *post_var=[NSString stringWithFormat:@"input=%@",[jsonWriter stringWithObject:[NSDictionary dictionaryWithObjectsAndKeys:userName.text,@"userName",Password.text,@"userPassword",nil]]];
    NSString *post = post_var;
       
    NSData *postData = [post dataUsingEncoding:NSUTF8StringEncoding];
    NSString *postLength = [NSString stringWithFormat:@"%d", [postData length]];
    NSMutableURLRequest *request = [[[NSMutableURLRequest alloc] init] autorelease];
    [request setURL:[NSURL URLWithString:[NSString stringWithFormat:@"http://www.google.com/"]]];
    
    [request setHTTPMethod:@"POST"];
    [request setValue:postLength forHTTPHeaderField:@"Content-Length"];
    [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-Type"];
    [request setHTTPBody:postData];
    
    NSURLConnection *conn=[[NSURLConnection alloc] initWithRequest:request delegate:self];
    if(!conn){
        UIAlertView *information = [[UIAlertView alloc] initWithTitle:@"Server Connection is not availability" message:nil  delegate:self cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        [information show];
        [information release];
    }
    else {
        Server_ReceiveData=[[NSMutableData alloc] init];
        
        //Transaction Start

        
        transID=[[_AppDelegate _PerformanceLibrary] TransactionStart:@"Login" ];
        [[_AppDelegate _PerformanceLibrary] SetUserTag1:@"http://www.apple.com/" transactionId:transID];
         

        
    }
}
#pragma mark -  NSURLConnection Delegate Function
-(void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response{
   
}
-(void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data{
    NSString *str=[[NSString alloc] initWithData:data encoding:NSISOLatin1StringEncoding];
    NSLog(@"Receive DATA: %@",str);
    [Server_ReceiveData appendData:data];
    
	
}

-(void)connectionDidFinishLoading:(NSURLConnection *)connection{
    NSString *responseString=[[NSString alloc] initWithData:Server_ReceiveData encoding:NSUTF8StringEncoding];
       
     NSLog(@"ALL Receive DATA: %@",responseString);
    
     //Transaction End
    [[_AppDelegate _PerformanceLibrary] TransactionEnd:transID];
    
    userName.alpha=0.0;
    Password.alpha=0.0;
    login_but.alpha=0.0;
    logout_but.alpha=1.0;
    
    //Release data
    [responseString release];
    [Server_ReceiveData release];
    Server_ReceiveData=nil;
    
    [connection release];
    connection=nil;
    
}
- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    
    UIAlertView *information = [[UIAlertView alloc] initWithTitle:@"Connection failed" message:[error localizedDescription]  delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK",nil];
    //[information setTag:400];
    [information show];
    [information release];
    
    NSLog(@"%@",[error localizedDescription]);
    
    [Server_ReceiveData release];
    Server_ReceiveData=nil;
    
    [connection release];
    connection=nil;
}


@end
