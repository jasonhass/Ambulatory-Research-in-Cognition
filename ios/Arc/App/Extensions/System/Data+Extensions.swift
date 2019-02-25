//
// Data+Extensions.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


import CommonCrypto
import Foundation
public extension Data {
	public func MD5() -> String {
		let digestLength = Int(CC_MD5_DIGEST_LENGTH)
		let md5Buffer = UnsafeMutablePointer<CUnsignedChar>.allocate(capacity: digestLength)
		
		_ = withUnsafeBytes {
			bytes in
			CC_MD5(bytes, CC_LONG(count), md5Buffer)

		}
		
		let output = NSMutableString(capacity: Int(CC_MD5_DIGEST_LENGTH * 2))
		for i in 0..<digestLength {
			output.appendFormat("%02x", md5Buffer[i])
		}
		
		return NSString(format: output) as String
	}
}
