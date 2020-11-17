//
// HintView.swift
//



import UIKit
import HMMarkup
public class HintView : IndicatorView {
	var titleLabel:ACLabel!
	private var bar:ACHorizontalBar!
	public var button:ACButton!
    public var titleStack:UIStackView!
	public var content:String? {
		get {
			return titleLabel.text
		}
		set {
			titleLabel.isHidden = (newValue == nil)
            titleStack.isHidden = (newValue == nil)
            
			titleLabel.text = newValue
		}
	}
	public var buttonTitle:String? {
		get {
			return button.titleLabel?.text
		}
		set {
			button.isHidden = (newValue == nil)
			bar.isHidden = button.isHidden
			button.setTitle(newValue, for: .normal)
			Roboto.PostProcess.link(button)

		}
	}
	public var onTap:(()->Void)?
	
	public init() {
		super.init(frame: .zero)
		titleStack = stack {
			
			$0.isLayoutMarginsRelativeArrangement = true
			$0.layoutMargins = UIEdgeInsets(top: 16, left: 16, bottom: 16, right: 16)
			titleLabel = $0.acLabel {
				$0.isHidden = true
				$0.textAlignment = .center
                Roboto.Style.subBody($0, color:.black)
                /*
                 https://stackoverflow.com/questions/44585026/convert-sketch-line-height-into-ios-line-height-multiple-property
                 lineSpacing = sketchLineHeight - sketchFontSize - (font.lineHeight - font.pointSize)
                */
                $0.spacing = 22 - 16 - (Roboto.Font.subBody.lineHeight - Roboto.Font.subBody.pointSize)

			}
		}
		
		bar = acHorizontalBar {
			$0.isHidden = true
			$0.relativeWidth = 1.0
			$0.layout {
				$0.height == 2
			}
		}
		button = acButton {
			$0.layout {
				$0.height == 36
			}
			$0.isHidden = true
			$0.primaryColor = .clear
			$0.secondaryColor = .clear
			$0.topColor = .clear
			$0.bottomColor = .clear
			$0.tintColor = .black
            $0.titleLabel?.textColor = .black
			Roboto.PostProcess.link($0)
			$0.addAction { [weak self] in
				self?.onTap?()
			}
		}
		container?.isLayoutMarginsRelativeArrangement = true
        container?.spacing = 0
		container?.layoutMargins = UIEdgeInsets(top: 8, left: 0, bottom: 8, right: 0)
		configure(with: IndicatorView.Config(primaryColor: UIColor(named:"HintFill")!,
											 secondaryColor: UIColor(named:"HintFill")!,
											 textColor: .black,
											 cornerRadius: 8.0,
											 arrowEnabled: false,
                                             arrowAbove: false))
	}
	required public init?(coder aDecoder: NSCoder) {
		fatalError("init(coder:) has not been implemented")
	}
	
	public override func draw(_ rect: CGRect) {
		super.draw(rect)
		 let context = UIGraphicsGetCurrentContext()
		context?.setStrokeColor(UIColor(named:"HorizontalSeparator")!.cgColor)
		path?.lineWidth = 8
		path?.stroke()
	}
    
    public func hideBar() {
        bar.isHidden = true
    }
    
    public func updateHintContainerMargins() {
        if isArrowEnabled && !isArrowAbove && !button.isHidden {
            container?.layoutMargins = UIEdgeInsets(top: 0, left: 0, bottom: 22, right: 0)
        }
    }
    
    public func updateTitleStackMargins() {
        if isArrowEnabled && isArrowAbove {
            titleStack.layoutMargins = UIEdgeInsets(top: 26, left: 16, bottom: 16, right: 16)
        }
        else if isArrowEnabled && !isArrowAbove {
            titleStack.layoutMargins = UIEdgeInsets(top: 16, left: 16, bottom: 26, right: 16)
        }
        else {
            titleStack.layoutMargins = UIEdgeInsets(top: 16, left: 16, bottom: 16, right: 16)
        }
    }
}

extension UIView {
	
	@discardableResult
	public func hint(apply closure: (HintView) -> Void) -> HintView {
		return custom(HintView(), apply: closure)
	}
	
}
