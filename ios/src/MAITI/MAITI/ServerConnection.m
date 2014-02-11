//
//  ServerConnection.m
//  SimplePerformanceLibrary
/*
===================
Copyright (c) 2013 Riverbed Technology, Inc.

MAITI is licensed under the terms and conditions of the MIT License as set forth in LICENSE.TXT, which accompanies the software.  MAITI is distributed “AS IS” as set forth in the MIT License.
===================
*/

#import "ServerConnection.h"
#import "AppConstants.h"
#import "PerformanceLibrary_Logic.h"

@interface ServerConnection()
{
}

@property (nonatomic, retain) SBJsonWriter* jsonWriter;
@property (nonatomic, retain) NSMutableData* Server_ReceiveData;

@end

@implementation ServerConnection


-(id)init
{
    //Write JSON Object
    self.jsonWriter = [[[SBJsonWriter alloc] init] autorelease];
    
    return self;
}

-(void)dealloc
{
    self.jsonWriter = nil;
    self.Server_ReceiveData = nil;
    [super dealloc];
}


#pragma mark -  NSURLConnection (Connect to server)
-(void)WebApi_ConnectionObject:(NSMutableArray*)AllData customerId:(NSString*) customerId{
    
    NSString *encodedJson = encodeToPercentEscapeString( [self.jsonWriter stringWithObject:AllData] );
    NSString *post_var=[NSString stringWithFormat:@"eueMon=mobile&ver=%d&jsid=%@&payload=%@",SDK_VERSION, customerId,
                        encodedJson];
    NSString *post = post_var;

    [encodedJson release];
    
    NSString *dataCollectorUrl;
    if ([[[self.logic_view Preferences] objectForKey:@"UseHTTPS"] integerValue] == 0) {
            dataCollectorUrl = [NSString stringWithFormat:@"http://%@:%@/beacon.gif",[[self.logic_view Preferences] objectForKey:@"DataCollectorHost"],[[self.logic_view Preferences] objectForKey:@"DataCollectorPort"]];
    }
    else
    {
            dataCollectorUrl = [NSString stringWithFormat:@"https://%@:%@/beacon.gif",[[self.logic_view Preferences] objectForKey:@"DataCollectorHost"],[[self.logic_view Preferences] objectForKey:@"DataCollectorPort"]];
    }
    
    //NSLog(@"URL: %@",[NSString stringWithFormat:@"%@",[[logic_view Preferences] objectForKey:@"DataCollectorHost"]]);

    
    NSData *postData = [post dataUsingEncoding:NSUTF8StringEncoding];
    NSString *postLength = [NSString stringWithFormat:@"%d", [postData length]];
    NSMutableURLRequest *request = [[[NSMutableURLRequest alloc] init] autorelease];
    [request setURL:[NSURL URLWithString:[NSString stringWithFormat:@"%@", dataCollectorUrl]]];
    
    [request setHTTPMethod:@"POST"];
    [request setValue:postLength forHTTPHeaderField:@"Content-Length"];
    [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-Type"];
    [request setHTTPBody:postData];
    
    NSLog(@"MAITI Post: %@", post);
    
    NSURLConnection *conn=[[NSURLConnection alloc] initWithRequest:request delegate:self];
    if(!conn)
    {
        //UIAlertView *information = [[UIAlertView alloc] initWithTitle:@"Server Connection is not availability" message:nil  delegate:self cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        //[information show];
        //[information release];
    }
    else
        self.Server_ReceiveData = [[[NSMutableData alloc] init] autorelease];
}

// URL Encode the query parameter
NSString* encodeToPercentEscapeString(NSString *original_text)
{
    return (NSString*)CFURLCreateStringByAddingPercentEscapes(NULL,(CFStringRef) original_text,
                                                              NULL,(CFStringRef) @";/?:@&=$+{}<>,",
                                                              kCFStringEncodingUTF8);
}


#pragma mark -  NSURLConnection Delegate Function

-(void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
    
}

-(void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
   
        //NSString *str=[[NSString alloc] initWithData:data encoding:NSISOLatin1StringEncoding];
        //NSLog(@"Receive DATA: %@",str);
   
    //[Server_ReceiveData appendData:data];
    
	
}

-(void)connectionDidFinishLoading:(NSURLConnection *)connection
{
   // NSString *responseString=[[NSString alloc] initWithData:Server_ReceiveData encoding:NSUTF8StringEncoding];
   // NSError *jsonError=nil;
   // SBJSON *json = [[SBJSON new] autorelease];
    

    
    
    //Release data
    //[responseString release];
    self.Server_ReceiveData = nil;
    
    [connection release];
    connection=nil;
    
}
- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    
    //UIAlertView *information = [[UIAlertView alloc] initWithTitle:@"Connection failed" message:[error localizedDescription]  delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK",nil];
    //[information setTag:400];
    //[information show];
    //[information release];
    
    NSLog(@"MAITI Connection Error: %@",[error localizedDescription]);
    
    self.Server_ReceiveData=nil;
    
    [connection release];
    connection=nil;
}



@end
