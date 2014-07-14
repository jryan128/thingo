//
//  JBZBoard.h
//  Bazingo
//
//  Created by Jonathan Ryan on 7/13/14.
//  Copyright (c) 2014 Joy Signal. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface JBZBoard : NSObject

@property int score;
@property NSArray *squares;
@property NSString *categoryName;

+(NSString *) getPhrasesPath;
-(id)initWithCategoryName:(NSString *) categoryName;

@end
