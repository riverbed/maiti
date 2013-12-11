//
//  WebViewController.h
//  SamplePerformanceLibrary
//
//  Copyright (c) 2013 Riverbed Technology. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface WebViewController : UIViewController{
    id _AppDelegate;
    IBOutlet UIWebView *webview;
    
    NSString *transID;
}
@property(nonatomic,assign)    id _AppDelegate;
@property(nonatomic,retain)IBOutlet UIWebView *webview;
@end
