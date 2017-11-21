//
//  TimeInterval+Methods.swift
//  ARC
//
//  Created by Michael Votaw on 6/14/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

import Foundation

extension TimeInterval {
    
    func format() -> String? {
        let formatter = DateComponentsFormatter()
        formatter.allowedUnits = [.day, .hour, .minute, .second, .nanosecond]
        formatter.unitsStyle = .abbreviated
        formatter.maximumUnitCount = 1
        return formatter.string(from: self)
    }
    
    func localizedInterval() -> String
    {
        let formatter = DateComponentsFormatter()
        formatter.allowedUnits = [.hour, .minute]
        formatter.unitsStyle = .short
        formatter.maximumUnitCount = 3
        return formatter.string(from: self)!
    }
}
