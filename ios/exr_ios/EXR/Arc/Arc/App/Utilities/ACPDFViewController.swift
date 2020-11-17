//
// ACPDFViewController.swift
//



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
