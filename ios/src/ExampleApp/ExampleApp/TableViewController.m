//
//  TableViewController.m
//  SamplePerformanceLibrary
//
//  Copyright (c) 2013 Riverbed Technology. All rights reserved.
//

#import "TableViewController.h"
#import "WebViewController.h"
#import "LoginViewController.h"

@interface TableViewController ()
{
    NSMutableArray *testArray;
}

@property(nonatomic,retain)IBOutlet UITableView *tableview;

@end

@implementation TableViewController

@synthesize tableview;

-(void)dealloc
{
    [super dealloc];
}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    testArray=[[NSMutableArray alloc] initWithObjects:@"Login",@"Open Web Site",nil];

}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}



#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Return the number of sections.
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
    return [testArray count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier] autorelease];
    }
    
    // Configure the cell...
    
    cell.textLabel.text=[testArray objectAtIndex:indexPath.row];
    
    return cell;
}

#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Navigation logic may go here. Create and push another view controller.
    
    if (indexPath.row == 0)
    {
        LoginViewController *_LoginViewController = [[[LoginViewController alloc] initWithNibName:@"LoginViewController" bundle:nil] autorelease];
        [self.navigationController pushViewController:_LoginViewController animated:YES];
        
    }
    else if(indexPath.row == 1)
    {
        WebViewController *_WebViewController = [[[WebViewController alloc] initWithNibName:@"WebViewController" bundle:nil] autorelease];
        [self.navigationController pushViewController:_WebViewController animated:YES];
        
    }
    
    
}
@end
