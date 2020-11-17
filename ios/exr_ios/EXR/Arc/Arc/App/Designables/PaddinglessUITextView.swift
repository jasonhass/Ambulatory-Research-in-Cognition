//
// PaddinglessUITextView.swift
//



import UIKit

@IBDesignable open class PaddinglessUITextView: UITextView {
    override open func layoutSubviews() {
        super.layoutSubviews()
        setup()
    }
    func setup() {
        textContainerInset = UIEdgeInsets.zero
        textContainer.lineFragmentPadding = 0
    }
}
