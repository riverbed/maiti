//
//  TableViewController.h
//  SamplePerformanceLibrary
//
//  Copyright (c) 2013 Riverbed Technology. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface TableViewController : UIViewController{
    IBOutlet UITableView *tableview;
    NSMutableArray *testArray;
}
@property(nonatomic,retain)IBOutlet UITableView *tableview;
@end
