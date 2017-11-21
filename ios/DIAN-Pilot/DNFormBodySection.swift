//
//  DNFormBodySection.swift
//
//  Created by Michael Votaw on 5/22/17.
//  Copyright © 2017 happyMedum. All rights reserved.
//

import Foundation

class DNFormBodySection
{
    var headerData:Data;
    var body:InputStream;
    var contentLength:UInt64;
    var endData:Data?;
    init(headerData:Data, body:InputStream, contentLength:UInt64, endData:Data?)
    {
        self.headerData = headerData;
        self.body = body;
        self.contentLength = contentLength;
        self.endData = endData;
    }
}
