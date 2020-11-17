//
// InfoContentView.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import UIKit
import ArcUIKit
import HMMarkup
/// This view has functions that can be called in any order to change the
/// composition of the view itself. Each method once called will keep its original position.
public class InfoContentView: UIStackView {
	weak var headingLabel: UILabel?
	weak var subheadingLabel: UILabel?
	weak var contentLabel: HMMarkupLabel?
	weak var contentTextView: HMMarkupTextView?
	weak var separator:ACHorizontalBar!

	var textColor = UIColor(named: "Secondary Text")

	public init() {
		super.init(frame: .zero)
		spacing = 20
		axis = .vertical
		alignment = .fill
		
		
	}
	/// Using option type functions to lazy-add subviews allows the view to be
	/// recomposed based on the order that the options are called.
	/// - Parameter width: A value between 0 and 1
	public func setSeparatorWidth(_ width:CGFloat)
	{
		if let view = separator {
			view.relativeWidth = width
		} else {
			//Add a horizontal bar to the view AND assign it to
			//the separator variable
			separator = acHorizontalBar {
				
				$0.relativeWidth = width
				$0.layout { [weak self] in
					$0.height == 2 ~ 999
					$0.width == self!.widthAnchor ~ 500
				}
			}
		}
	}
	public func addSpacer()
	{
		view {
			$0.backgroundColor = .clear
			
		}
	}
	public func setHeader(_ text:String?)
	{
		if let view = headingLabel {
			view.text = text
		} else {
			headingLabel = acLabel {
				$0.textAlignment = .left
				$0.accessibilityIdentifier = "heading_label"
				Roboto.Style.heading($0,
										 color:textColor)
				$0.text = text
				
			}
		}
	}
	public func setMediumHeader(_ text:String?)
	{
		if let view = headingLabel {
			view.text = text
		} else {
			headingLabel = acLabel {
				$0.textAlignment = .left
				$0.accessibilityIdentifier = "heading_label"
				Roboto.Style.headingMedium($0,
										 color:textColor)
				$0.text = text
				
			}
		}
	}
    public func setIntroHeader(_ text:String?)
    {
        if let view = headingLabel {
            view.text = text
        } else {
            headingLabel = acLabel {
                $0.textAlignment = .left
				$0.accessibilityIdentifier = "heading_label"

                Roboto.Style.introHeading($0, color: .white)
                $0.text = text
                
            }
        }
    }
    
	public func setSubHeader(_ text:String?) {
		if let view = subheadingLabel {
			view.text = text
		} else {
			subheadingLabel = acLabel {
				$0.textAlignment = .center
				$0.accessibilityIdentifier = "subheading_label"

				Roboto.Style.body($0,
								  color:UIColor(red:0.4, green:0.78, blue:0.78, alpha:1))
				$0.text = text
			}
		}
	}
    
    public func setPrompt(_ text:String?){
        if let view = subheadingLabel {
            view.text = text
            if text == nil {
                subheadingLabel?.removeFromSuperview()
            }
        } else {
            guard let text = text else {
                return
            }
            subheadingLabel = acLabel {
                $0.textAlignment = .left
				$0.accessibilityIdentifier = "subheading_label"

                Roboto.Style.prompt($0,
                                  color:textColor)
                
                $0.text = text
                let renderer = HMMarkupRenderer(baseFont: $0.font)
                let attributedString = NSMutableAttributedString(attributedString: renderer.render(text: $0.text ?? ""))
                let paragraphStyle = NSMutableParagraphStyle()
                paragraphStyle.lineSpacing = 32 - 26 - (Roboto.Font.prompt.lineHeight - Roboto.Font.prompt.pointSize)
                paragraphStyle.alignment = $0.textAlignment
                attributedString.addAttribute(NSAttributedString.Key.paragraphStyle, value:paragraphStyle, range:NSMakeRange(0, attributedString.length))
                attributedString.addAttributes([.foregroundColor : self.textColor!], range: NSMakeRange(0, attributedString.length))
                $0.attributedText = attributedString
                
            }
        }
    }
    
	public func setContentLabel(_ text:String?, template:[String:String] = [:]) {
		
		let text = Text.replaceIn(text, withTemplate: template)
		
		if let view = contentLabel {
			view.text = text
		} else {
			contentLabel = acLabel {
				
				$0.backgroundColor = .clear
				$0.textAlignment = .left
				$0.accessibilityIdentifier = "content_label"

				Roboto.Style.body($0,
								  color:textColor)
				
                $0.spacing = 5.5
                
                $0.text = text
				
				$0.layout {
					$0.width == self.widthAnchor ~ 400
				}
			}
		}
		//Roboto.PostProcess.renderMarkup(contentTextView!, template: template)
	}
	public func setContent(_ text:String?, template:[String:String] = [:]) {
		
		let text = Text.replaceIn(text, withTemplate: template)
		
		if let view = contentTextView {
			view.text = text
		} else {
			contentTextView = acTextView {
				$0.contentInset = .zero
				$0.isEditable = false
				$0.backgroundColor = .clear
				$0.textAlignment = .left
				$0.contentInset = .zero
				$0.isSelectable = true
				$0.accessibilityIdentifier = "content_label"

				Roboto.Style.body($0,
								  color:textColor)
				
				$0.text = text
				
				$0.layout {
					$0.width == self.widthAnchor ~ 400
				}
				
			}
		}
		//Roboto.PostProcess.renderMarkup(contentTextView!, template: template)
	}
	public func setIntroContent(_ text:String?, template:[String:String] = [:]) {
        
        let text = Text.replaceIn(text, withTemplate: template)
        
        if let view = contentTextView {
            view.text = text
        } else {
            contentTextView = acTextView {
                $0.contentInset = .zero
                $0.isEditable = false
                $0.backgroundColor = .clear
                $0.textAlignment = .left
                $0.contentInset = .init(top: 0, left: -5, bottom: 0, right: 0)
                $0.isSelectable = true
				$0.accessibilityIdentifier = "content_label"

                Roboto.Style.body($0,
                                  color:textColor)
                
                $0.text = text
                
                $0.layout {
                    $0.width == self.widthAnchor ~ 400
                }
                
            }
        }
        //Roboto.PostProcess.renderMarkup(contentTextView!, template: template)
    }
	
	required init(coder: NSCoder) {
		fatalError("init(coder:) has not been implemented")
	}
}

extension UIView {
	
	@discardableResult
	public func infoContent(apply closure: (InfoContentView) -> Void) -> InfoContentView {
		return custom(InfoContentView(), apply: closure)
	}
	
}
