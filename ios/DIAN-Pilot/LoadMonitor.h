//
//  LoadMonitor.h
//  ARC
//
//  Created by Michael Votaw on 6/27/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface LoadMonitor : NSObject

+ (float) cpu_usage;
+(float) memory_usage;
+ (NSUInteger) totalMemory;
@end
