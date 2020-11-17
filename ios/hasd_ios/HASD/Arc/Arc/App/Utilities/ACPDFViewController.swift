//
// ACPDFViewController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import Foundation
import PDFKit
public class ACPDFViewController : CustomViewController<ACPDFView> {
	public override func viewDidLoad() {
		super.viewDidLoad()
		customView.closeButton.addAction {[weak self] in
			self?.dismiss(animated: true){self?.customView.pdfView.document = nil}
		}
	}
	public func setDocument(url:URL) {
		customView.pdfView.document = PDFDocument(url: url)
		customView.pdfView.autoScales = true
		
	}
}
