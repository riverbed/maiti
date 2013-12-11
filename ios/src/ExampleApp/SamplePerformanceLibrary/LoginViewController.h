//
//  LoginViewController.h
//  SamplePerformanceLibrary
//
//  Copyright (c) 2013 Riverbed Technology. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "JSON.h"

@interface LoginViewController : UIViewController{
    IBOutlet UITextField *userName;
    IBOutlet UITextField *Password;
    IBOutlet UIButton *login_but;
    IBOutlet UIButton *logout_but;
    NSMutableData *Server_ReceiveData;
    
    id _AppDelegate;
    NSString *transID;
    
   SBJsonWriter *jsonWriter; //For Json writer
}
@property(nonatomic,assign)    id _AppDelegate;
@property(nonatomic,retain)IBOutlet UITextField *userName;
@property(nonatomic,retain)IBOutlet UITextField *Password;
@property(nonatomic,retain)IBOutlet UIButton *login_but;
@property(nonatomic,retain)IBOutlet UIButton *logout_but;
-(IBAction)login:(id)sender;
-(IBAction)logout:(id)sender;
@end
