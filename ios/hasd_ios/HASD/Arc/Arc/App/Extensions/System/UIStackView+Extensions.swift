//
// UIStackView+Extensions.swift
//



import UIKit
public extension UIStackView {
    func removeSubviews() {
        for view in arrangedSubviews {
            view.removeFromSuperview()
            removeArrangedSubview(view)
            
        }
        layoutSubviews()
    }
}
