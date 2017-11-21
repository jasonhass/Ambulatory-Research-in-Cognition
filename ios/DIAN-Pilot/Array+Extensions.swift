//
//  Array+Extensions.swift
//  ARC
//
//  Created by Michael Votaw on 6/19/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

import Foundation


extension Array where Element:FloatingPoint
{
    var total:Element
    {
        return self.reduce(0, +);
    }
    
    var average:Element
    {
        return isEmpty ? 0 : total / Element(count);
    }
    
}
