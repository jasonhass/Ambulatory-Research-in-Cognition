//
// FatalErrorUtil.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import Foundation
// overrides Swift global `fatalError`
func fatalError(_ message: @autoclosure () -> String = "", file: StaticString = #file, line: UInt = #line) -> Never {
	let log = """
	*Message:* \(message())
	
	*File:* \(file)
	
	*Line:* \(line)
	"""
	Arc.shared.appController.store(value: ErrorReport(message: log), forKey: "error")
    FatalErrorUtil.fatalErrorClosure(message(), file, line)

}

/// This is a `noreturn` function that pauses forever
func unreachable() -> Never  {
    repeat {
        RunLoop.current.run()
    } while (true)
}
public struct ErrorReport : Codable, Error {
	var message:String
	var date:Date = Date()
	public init(message:String) {
		self.message = message
	}
	public var localizedDescription: String {
		return message
	}
}
public struct FatalErrorUtil {
    
    // 1
    static var fatalErrorClosure: (String, StaticString, UInt) -> Never = defaultFatalErrorClosure
    
    // 2
    private static let defaultFatalErrorClosure = { Swift.fatalError($0, file: $1, line: $2) }
    
    // 3
    static func replaceFatalError(closure: @escaping (String, StaticString, UInt) -> Never) {
        fatalErrorClosure = closure
    }
    
    // 4
    static func restoreFatalError() {
        fatalErrorClosure = defaultFatalErrorClosure
    }
}
