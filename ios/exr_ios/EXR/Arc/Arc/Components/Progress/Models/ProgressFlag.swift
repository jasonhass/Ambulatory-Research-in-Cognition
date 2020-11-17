//
// ProgressFlag.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import Foundation

public enum ProgressFlag : String {
	//0.1.0
	case tutorial_complete
	case tutorial_grats
	case first_test_begin
	case baseline_completed
	case baseline_onboarding

    case paid_test_completed
    case grids_tutorial_shown
    case symbols_tutorial_shown
    case prices_tutorial_shown
	case tutorial_optional

    case time_picker_hint_shown
    case slider_hint_shown
    
	//For every version add a new case that runs for that version specifically.
	static public func prefilledFlagsFor(major:Int, minor:Int, patch:Int) -> Set<ProgressFlag> {
		var flags:Set<ProgressFlag> = []
		switch (major, minor, patch) {
		case let (major, _, _) where major < 2: 
			flags = flags.union([.tutorial_grats, .tutorial_complete, .first_test_begin, .baseline_completed, .paid_test_completed, .tutorial_optional])
	
		case let (major, _, _) where major >= 2:
			flags = flags.union([.tutorial_grats, .tutorial_complete, .first_test_begin, .baseline_completed, .baseline_onboarding, .paid_test_completed, .tutorial_optional, .grids_tutorial_shown, .symbols_tutorial_shown, .prices_tutorial_shown])
			
		default:
			break
		}
		return flags
		
	}
}
