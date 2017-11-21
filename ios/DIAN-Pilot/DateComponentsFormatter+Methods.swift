//
//  DateComponentsFormatter+Methods.swift
//  ARC
//
//  Created by Michael Votaw on 8/28/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

import Foundation

extension DateComponentsFormatter
{
    
    static func localizedString(forUnit unit:NSCalendar.Unit, style:DateComponentsFormatter.UnitsStyle) -> String
    {
        
        let formatter = DateComponentsFormatter()
        formatter.allowedUnits = [unit]
        formatter.unitsStyle = style
        formatter.maximumUnitCount = 1;
        if let str = formatter.string(from: 0)
        {
            let localizedStr = str.replacingOccurrences(of: "0", with: "").trimmingCharacters(in: .whitespaces);
            return localizedStr;
        }
        
        return "";
    }
    
}
