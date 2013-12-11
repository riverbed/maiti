//
//  ServerConnection.h
//  SimplePerformanceLibrary
/*
===================
Copyright (c) 2013 Riverbed Technology, Inc.

MAITI is licensed under the terms and conditions of the MIT License as set forth in LICENSE.TXT, which accompanies the software.  MAITI is distributed “AS IS” as set forth in the MIT License.
===================
*/

#import <Foundation/Foundation.h>
#import "JSON.h"

@interface ServerConnection : NSObject{
    SBJsonWriter *jsonWriter; //For Json writer
    NSMutableData *Server_ReceiveData;
    id logic_view;
}
@property(nonatomic,assign)id logic_view;

#pragma mark -  NSURLConnection (Connect to server)
-(void)WebApi_ConnectionObject:(NSMutableArray*)AllData customerId:(NSString*) customerId;

@end
