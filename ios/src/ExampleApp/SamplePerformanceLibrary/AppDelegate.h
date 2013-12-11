//
//  AppDelegate.h
//  SamplePerformanceLibrary
//
//  Copyright (c) 2013 Riverbed Technology. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "PerformanceLibrary.h"

@class ViewController;

@interface AppDelegate : UIResponder <UIApplicationDelegate>

@property (strong, nonatomic) UIWindow *window;

@property (strong, nonatomic) ViewController *viewController;

@property(strong, nonatomic)PerformanceLibrary *_PerformanceLibrary;

@end
