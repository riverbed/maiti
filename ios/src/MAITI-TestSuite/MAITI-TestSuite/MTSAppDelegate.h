//
//  MTSAppDelegate.h
//  MAITI-TestSuite
//
//  Created by Roman Makhnenko on 13/02/14.
//  Copyright (c) 2014 DataArt Solutions, Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MAITI/PerformanceLibrary.h"

@interface MTSAppDelegate : UIResponder <UIApplicationDelegate>

@property (retain, nonatomic) UIWindow *window;
@property (retain, nonatomic) PerformanceLibrary* performanceLibrary;

@end

inline MTSAppDelegate* appd()
{
    return (MTSAppDelegate*)[[UIApplication sharedApplication] delegate];
}