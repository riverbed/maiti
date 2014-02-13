//
//  MEAViewController.m
//  ExampleApp
//
//  Created by Roman Makhnenko on 12/02/14.
//  Copyright (c) 2014 DataArt Solutions, Inc. All rights reserved.
//

#import "MEAViewController.h"
#import "TableViewController.h"

@interface MEAViewController ()

@end

@implementation MEAViewController

- (void)viewDidLoad
{
    [super viewDidLoad];

    TableViewController *_TableViewController=[[[TableViewController alloc] initWithNibName:@"TableViewController" bundle:nil] autorelease];
    UINavigationController *_navigationController=[[[UINavigationController alloc] initWithRootViewController:_TableViewController] autorelease];
    _navigationController.view.frame=self.view.frame;
    [self addChildViewController:_navigationController];
    
    [self.view addSubview:_navigationController.view];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
