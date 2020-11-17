//
// ACPDFView.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit
import PDFKit
import ArcUIKit
public class ACPDFView: UIView {

    /*
    // Only override draw() if you perform custom drawing.
    // An empty implementation adversely affects performance during animation.
    override func draw(_ rect: CGRect) {
        // Drawing code
    }
    */
	public var pdfView:PDFView!
	public var closeButton:ACButton!
	init() {
		super.init(frame: .zero)
		build()
	}
	public required init?(coder: NSCoder) {
		super.init(coder: coder)
		build()
		
	}
	func build() {
		let root = stack { [weak self] in
			
			$0.axis = .vertical
			$0.distribution = .fill
			$0.alignment = .fill
			$0.spacing = 8
			self?.pdfView = $0.pdfView { _ in
				
			}
			
			self?.closeButton = $0.acButton {
				$0.setTitle("Close", for: .normal)
			}
		}
		root.attachTo(view: self)
	}

}
