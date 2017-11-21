//
//  Data+methods.swift
//  ARC
//
//  Created by Michael Votaw on 6/26/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

import Foundation

extension NSData {
    func MD5() -> NSString {
        let digestLength = Int(CC_MD5_DIGEST_LENGTH)
        let md5Buffer = UnsafeMutablePointer<CUnsignedChar>.allocate(capacity: digestLength)
        
        CC_MD5(bytes, CC_LONG(length), md5Buffer)
        let output = NSMutableString(capacity: Int(CC_MD5_DIGEST_LENGTH * 2))
        for i in 0..<digestLength {
            output.appendFormat("%02x", md5Buffer[i])
        }
        
        return NSString(format: output)
    }
}  
