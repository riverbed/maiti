//
//  LoginViewController.m
//  SamplePerformanceLibrary
//
//  Copyright (c) 2013 Riverbed Technology. All rights reserved.
//

#import "LoginViewController.h"
#import "MEAAppDelegate.h"

@interface LoginViewController ()

@property(nonatomic,assign) id AppDelegate;
@property(nonatomic,retain) NSString *transID;
@property(nonatomic,retain) SBJsonWriter *jsonWriter; //For Json writer
@property(nonatomic,retain) NSMutableData *Server_ReceiveData;

@property(nonatomic,retain)IBOutlet UITextField *userName;
@property(nonatomic,retain)IBOutlet UITextField *Password;
@property(nonatomic,retain)IBOutlet UIButton *login_but;
@property(nonatomic,retain)IBOutlet UIButton *logout_but;

-(IBAction)login:(id)sender;
-(IBAction)logout:(id)sender;

-(void)GenericAlert:title msg:(NSString*)msg;
-(void)WebApi_ConnectionObject;

@end

@implementation LoginViewController

-(void)dealloc
{
    self.userName = nil;
    self.Password = nil;
    self.login_but = nil;
    self.logout_but = nil;
    self.transID = nil;
    self.jsonWriter = nil;
    self.Server_ReceiveData = nil;
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

-(IBAction)login:(id)sender{
    if ([[self.userName.text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]] length] <= 0){
        [self GenericAlert:nil msg:@"User Name cannot be left blank."];
    }
    else if ([[self.Password.text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]] length] <= 0){
        [self GenericAlert:nil msg:@"Password cannot be left blank."];
    }else{
         
        [self WebApi_ConnectionObject];
        self.Password.text=@"";
    }
}

-(IBAction)logout:(id)sender{
    self.userName.alpha=1.0;
    self.Password.alpha=1.0;
    self.login_but.alpha=1.0;
    self.logout_but.alpha=0.0;
    
    //Transaction Strat

    
    NSError *error = nil;
    [[_AppDelegate performanceLibrary] Notification:@"Logout" userTag1:@""];
    NSLog(@"error %@", error.localizedDescription);
    
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self.userName becomeFirstResponder];
    
    //Assign Application Delegate
	_AppDelegate=(MEAAppDelegate*)[[UIApplication sharedApplication] delegate];
    self.jsonWriter = [[SBJsonWriter new] retain];

    self.userName.alpha=1.0;
    self.Password.alpha=1.0;
    self.login_but.alpha=1.0;
    self.logout_but.alpha=0.0;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Alert Message
-(void)GenericAlert:title msg:(NSString*)msg{
    UIAlertView *alert = [[[UIAlertView alloc] initWithTitle:title
                                                    message:msg
                                                   delegate:self
                                          cancelButtonTitle:@"OK"
                                          otherButtonTitles:nil] autorelease];
    [alert show];
}



-(void)WebApi_ConnectionObject{
    
    NSString *post_var=[NSString stringWithFormat:@"input=%@",[self.jsonWriter stringWithObject:[NSDictionary dictionaryWithObjectsAndKeys:self.userName.text,@"userName",self.Password.text,@"userPassword",nil]]];
    NSString *post = post_var;
       
    NSData *postData = [post dataUsingEncoding:NSUTF8StringEncoding];
    NSString *postLength = [NSString stringWithFormat:@"%ld", (long)[postData length]];
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
        self.Server_ReceiveData=[[NSMutableData alloc] init];
        
        //Transaction Start

        
        self.transID=[[_AppDelegate performanceLibrary] TransactionStart:@"Login" ];
        [[_AppDelegate performanceLibrary] SetUserTag1:@"http://www.apple.com/" transactionId:self.transID];
         

        
    }
}
#pragma mark -  NSURLConnection Delegate Function
-(void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response{
   
}
-(void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data{
    NSString *str=[[NSString alloc] initWithData:data encoding:NSISOLatin1StringEncoding];
    NSLog(@"Receive DATA: %@",str);
    [self.Server_ReceiveData appendData:data];
    
	
}

-(void)connectionDidFinishLoading:(NSURLConnection *)connection{
    NSString *responseString=[[NSString alloc] initWithData:self.Server_ReceiveData encoding:NSUTF8StringEncoding];
       
     NSLog(@"ALL Receive DATA: %@",responseString);
    
     //Transaction End
    [[_AppDelegate performanceLibrary] TransactionEnd:self.transID];
    
    self.userName.alpha=0.0;
    self.Password.alpha=0.0;
    self.login_but.alpha=0.0;
    self.logout_but.alpha=1.0;
    
    //Release data
    [responseString release];
    self.Server_ReceiveData=nil;
    
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
    
    self.Server_ReceiveData=nil;
    
    [connection release];
    connection=nil;
}


@end
