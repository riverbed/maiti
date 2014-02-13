//
//  MEAAppDelegate.h
//  ExampleApp
//
//  Created by Roman Makhnenko on 12/02/14.
//  Copyright (c) 2014 DataArt Solutions, Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MAITI/PerformanceLibrary.h"

@interface MEAAppDelegate : UIResponder <UIApplicationDelegate>

@property (retain, nonatomic) UIWindow *window;
@property (nonatomic, retain) PerformanceLibrary* performanceLibrary;

@end
