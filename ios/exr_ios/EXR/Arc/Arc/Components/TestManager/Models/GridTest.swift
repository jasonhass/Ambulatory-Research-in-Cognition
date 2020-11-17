//
// GridTest.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import Foundation
//For ui configuration only
public struct GridTest : Codable {
    
    
    public struct Size : Codable {
        var width:Int
        var height:Int
        init (width:Int, height:Int) {
            self.width = width
            self.height = height
        }
    }
    public struct Grid : Codable {
        
        public struct Symbol : Codable {
            var selected:Bool
            var symbol:Int
            var x:Int
            var y:Int
            
            init(symbol:Int, x:Int, y:Int) {
                self.symbol = symbol
                self.x = x
                self.y = y
                self.selected = false
            }
        }
        let size:Size
        var symbols:Array<Symbol>

        var values: Array<Array<Int>> = []
        init(size:Size) {
            self.size = size
            values = Array<Array<Int>>(repeating: Array<Int>(repeating: -1, count: size.width), count: size.height)
            symbols = []
        }
    }
    
    var imageGrid:Grid
    var fGrid:Grid
    
    init(imageGridSize:Size, fGridSize:Size) {
        imageGrid = Grid(size: imageGridSize)
        fGrid = Grid(size: fGridSize)
    }
    
}

//For server communication only
public struct GridTestResponse : HMTestCodable {
	public static var dataType: SurveyType = .gridTest

    public struct Section : Codable {
        public struct Choice : Codable {
            var selection_time:TimeInterval?
            var x:Int
            var y:Int
            init(x:Int, y:Int, selection_time:TimeInterval?) {
                self.x = x
                self.y = y
                self.selection_time = selection_time
            }
        }
        public struct Image : Codable {
            var image:String
            var x:Int
            var y:Int
            init(x:Int, y:Int, image:String) {
                self.x = x
                self.y = y
                self.image = image
            }
        }
        
        var choices:Array<Choice>
        var display_distraction:TimeInterval?
        var display_symbols:TimeInterval?
        var display_test_grid:TimeInterval?
        var e_count:Int = 0
        var f_count:Int = 0
        var images:Array<Image>
        
        init() {
            choices = []
            images = []
        }
    }
    
    public var id: String?
    public var type: SurveyType? = .gridTest
    var sections:Array<Section>
    public var date:TimeInterval?
    
    init(id:String, numSections:Int) {
        self.id = id
        sections = Array(repeating: Section(), count: numSections)
    }
    
}

extension GridTestResponse.Section.Choice : Hashable, Comparable {
    public static func == (lhs: GridTestResponse.Section.Choice, rhs: GridTestResponse.Section.Choice) -> Bool {
        return lhs.x == rhs.x && lhs.y == rhs.y
    }
    
    public func hash(into hasher: inout Hasher) {
        hasher.combine(x)
        hasher.combine(y)
    }
    
    public static func < (lhs: GridTestResponse.Section.Choice, rhs: GridTestResponse.Section.Choice) -> Bool {
        return (lhs.selection_time ?? 0) < (rhs.selection_time ?? 0)
        
    }
    
}
