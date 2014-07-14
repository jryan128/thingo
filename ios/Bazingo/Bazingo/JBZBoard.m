//
//  JBZBoard.m
//  Bazingo
//
//  Created by Jonathan Ryan on 7/13/14.
//  Copyright (c) 2014 Joy Signal. All rights reserved.
//

#import "JBZBoard.h"
#import "JBZSquare.h"

@implementation JBZBoard

+(NSString *) getPhrasesPath {
    static NSString *phrasesPath = nil;
    if (phrasesPath == nil) {
        phrasesPath = [[[NSBundle mainBundle] bundlePath] stringByAppendingString:@"/phrases"];
    }
    return phrasesPath;
}

- (NSArray *)getSquaresForCategory:(NSString *)categoryName {
    NSError *error;
    NSString *f = [NSBundle pathForResource:categoryName ofType:@"tsv" inDirectory:[JBZBoard getPhrasesPath]];
    NSString *fileContents = [NSString stringWithContentsOfFile:f encoding:NSUTF8StringEncoding
                                                          error:&error];
    if (error) {
        // FIXME: error handling
    }
    
    NSMutableArray *squares = [[NSMutableArray alloc] init];
    NSArray *lines = [fileContents componentsSeparatedByString:@"\n"];
    for (int i=1; i < [lines count]; ++i) {
        NSArray *phraseAndDesc = [[lines objectAtIndex:i] componentsSeparatedByString:@"\t"];
        JBZSquare *square = [[JBZSquare alloc] init];
        if ([phraseAndDesc count] > 0) {
            square.phrase = [phraseAndDesc objectAtIndex:0];
        }
        if ([phraseAndDesc count] > 1) {
            square.description = [phraseAndDesc objectAtIndex:1];
        }
        [squares addObject:square];
    }
    return squares;
}

- (void)shuffleArray:(NSMutableArray *)squares {
    // FIXME: not most efficient
    NSUInteger count = [squares count];
    for (NSUInteger i = 0; i < count; ++i) {
        NSInteger remainingCount = count - i;
        NSInteger exchangeIndex = i + arc4random_uniform(remainingCount);
        [squares exchangeObjectAtIndex:i withObjectAtIndex:exchangeIndex];
    }
}

-(id)initWithCategoryName:(NSString *) categoryName {
    self = [super init];
    if (self) {
        // FIXME: this is all so inefficient, so much array copying
        NSMutableArray *squares = [[NSMutableArray alloc] initWithArray:[self getSquaresForCategory:categoryName]];
        JBZSquare *freeSquare = [squares objectAtIndex:0];
        squares = [[NSMutableArray alloc] initWithArray: [squares subarrayWithRange:NSMakeRange(1, [squares count] - 1)]];
        [self shuffleArray:squares];
        squares = [[NSMutableArray alloc] initWithArray: [squares subarrayWithRange:NSMakeRange(0, 24)]];
        [squares insertObject:freeSquare atIndex:12];
        self.squares = squares;
    }
    return self;
}

@end
