//
//  SymbolsTestGlyph+AltOutput.swift
//  ARC
//
//  Created by Philip Hayes on 5/22/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

import Foundation
import CoreData
extension SymbolsTestItem {
    override func dictionaryOfAttributes(excludedKeys:NSSet) -> AnyObject{
        let data = NSMutableArray()

        if let g = glyphs?.array as? [SymbolsTestGlyph] {
            for glyph in g {
                data.add(glyph.symbol ?? "")
            }
        }
        return data as AnyObject
        
    }
}
