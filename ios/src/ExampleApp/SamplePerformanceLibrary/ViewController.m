//
//  ViewController.m
//  SamplePerformanceLibrary
//
//  Copyright (c) 2012 Riverbed Technology. All rights reserved.
//

#import "ViewController.h"
#import "TableViewController.h"

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    
    //Navigation
    TableViewController *_TableViewController=[[TableViewController alloc] initWithNibName:@"TableViewController" bundle:nil];
    UINavigationController *_navigationController=[[UINavigationController alloc] initWithRootViewController:_TableViewController];
    _navigationController.view.frame=self.view.frame;
    
    [self.view addSubview:_navigationController.view];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
