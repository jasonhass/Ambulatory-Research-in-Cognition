//
//  UITextViewFixed.swift
//  ARC
//
//  Created by Michael Votaw on 6/29/17.
//  Copyright © 2017 HappyMedium. All rights reserved.
//

import UIKit

@IBDesignable class DNPaddinglessTextView: UITextView {
    override func layoutSubviews() {
        super.layoutSubviews()
        textContainerInset = UIEdgeInsets.zero;
        textContainer.lineFragmentPadding = 0;
    }
}
