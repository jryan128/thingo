//
//  JBZCategoryTableViewController.m
//  Bazingo
//
//  Created by Jonathan Ryan on 7/12/14.
//  Copyright (c) 2014 Joy Signal. All rights reserved.
//

#import "JBZCategoryTableViewController.h"
#import "JBZCategoryItem.h"
#import "JBZSquare.h"

@interface JBZCategoryTableViewController ()

@end

@implementation JBZCategoryTableViewController

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)populateCategoryItems
{
    NSString *phrasesPath = [[[NSBundle mainBundle] bundlePath] stringByAppendingString:@"/phrases"];
    NSArray *filePaths = [NSBundle pathsForResourcesOfType:@"tsv" inDirectory:phrasesPath];
    NSMutableArray *squares = [[NSMutableArray alloc] init];
    
    for (NSString* f in filePaths) {
        NSString *categoryName = [[f lastPathComponent] stringByDeletingPathExtension];
        JBZCategoryItem *newItem = [[JBZCategoryItem alloc] init];
        newItem.categoryName = categoryName;
        [self.categoryItems addObject: newItem];
        
        NSError *error;
        NSString *fileContents = [NSString stringWithContentsOfFile:f encoding:NSUTF8StringEncoding
                                                              error:&error];
        if (error) {
            // FIXME: error handling
        }
        
        NSArray *lines = [fileContents componentsSeparatedByString:@"\n"];
        for (int i=1; i < [lines count]; ++i) {
            NSArray *phraseAndDesc = [[lines objectAtIndex:i] componentsSeparatedByString:@"\t"];
            JBZSquare *square = [[JBZSquare alloc] init];
            square.phrase = [phraseAndDesc objectAtIndex:0];
            if ([phraseAndDesc count] > 1) {
                square.description = [phraseAndDesc objectAtIndex:1];
            }
            [squares addObject:square];
        }
    }
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    // Uncomment the following line to preserve selection between presentations.
    // self.clearsSelectionOnViewWillAppear = NO;
    
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;
    
    self.categoryItems = [[NSMutableArray alloc] init ];
    
    [self populateCategoryItems];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.categoryItems count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"CategoryListPrototypeCell" forIndexPath:indexPath];
    
    JBZCategoryItem *item = [self.categoryItems objectAtIndex:indexPath.row];
    cell.textLabel.text = item.categoryName;
    
    return cell;
}

/*
// Override to support conditional editing of the table view.
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the specified item to be editable.
    return YES;
}
*/

/*
// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        // Delete the row from the data source
        [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
    } else if (editingStyle == UITableViewCellEditingStyleInsert) {
        // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
    }   
}
*/

/*
// Override to support rearranging the table view.
- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath
{
}
*/

/*
// Override to support conditional rearranging of the table view.
- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the item to be re-orderable.
    return YES;
}
*/

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
