//
// IndicatorView.swift
//



import UIKit


@IBDesignable public class IndicatorView:UIView {
    public struct Config {
        public let primaryColor:UIColor
        public let secondaryColor:UIColor
        public let textColor:UIColor
        public let cornerRadius:CGFloat
		public let arrowEnabled:Bool
        public let arrowAbove:Bool
		
        public init(primaryColor:UIColor, secondaryColor:UIColor,textColor:UIColor,cornerRadius:CGFloat,arrowEnabled:Bool,arrowAbove:Bool) {
			self.primaryColor = primaryColor
			self.secondaryColor = secondaryColor
			self.textColor = textColor
			self.cornerRadius = cornerRadius
			self.arrowEnabled = arrowEnabled
            self.arrowAbove = arrowAbove
		}
    }
    @IBInspectable public var primaryColor:UIColor = .black
    @IBInspectable public var secondaryColor:UIColor = .black
    
    
    @IBInspectable public var cornerRadius:CGFloat = 0 {
        didSet {
            layer.cornerRadius = cornerRadius
        }
    }
    var indicatorCenter:CGPoint?
    var isSelected = false
    var isEnabled = true
    var isArrowEnabled = true
    var isArrowAbove = false
	public var container:UIStackView?
	var path:UIBezierPath?
	var pointerSize:CGFloat = 10.0
	var radius:CGFloat = 8.0
	public var pointerX:CGFloat = 0.0
	public weak var targetView:UIView?
    override public init(frame: CGRect) {
        super.init(frame: frame)
        translatesAutoresizingMaskIntoConstraints = false
		let s = UIStackView()
		self.addSubview(s)
		s.frame = self.bounds
		s.alignment = .fill
		s.axis = .vertical
		s.spacing = 8
		let v = self
		s.layout {
			
			$0.top == v.topAnchor
			$0.trailing == v.trailingAnchor
			$0.bottom == v.bottomAnchor
			$0.leading == v.leadingAnchor
			
		}
		
		
		container = s
        
    }
    
    
    required public init?(coder aDecoder: NSCoder) {
        
        super.init(coder: aDecoder)
        
       

    }
	override func add(_ view: UIView) {
		
		container?.addArrangedSubview(view)
	}
	
    
    override open func awakeFromNib() {
        super.awakeFromNib()
        //setup(isSelected: false)
        setNeedsDisplay()
    }
    
    override open func prepareForInterfaceBuilder() {
        super.prepareForInterfaceBuilder()
        //setup(isSelected: false)
        
     
        setNeedsDisplay()
    }
   
    public func configure(with config:Config) {
        primaryColor = config.primaryColor
        secondaryColor = config.secondaryColor
        isArrowEnabled = config.arrowEnabled
        isArrowAbove = config.arrowAbove
        layer.cornerRadius = config.cornerRadius
        backgroundColor = .clear
        setNeedsDisplay()
        
    }
    override public func draw(_ rect: CGRect) {
        super.draw(rect)
		//If we have a target and everything is properly configured. Always convert rects from a parent view, not the view itself.
		if let targetView = targetView ,
			let targetFrame = targetView.superview?.convert(targetView.frame, to: nil),
			let currentFrame = superview?.convert(frame, to: nil){
			
			pointerX = Math.clamp(targetFrame.midX - currentFrame.minX, minValue: 0 + layer.cornerRadius + pointerSize, maxValue: rect.width - layer.cornerRadius - pointerSize)
			

		} else {
			pointerX = bounds.width/2.0
		}
        var insetRect = rect
        
        // below
        if isArrowEnabled && !isArrowAbove {
            insetRect = rect
                .insetBy(dx: 0, dy: 5)
                .offsetBy(dx: 0, dy: -5)
        }
		
        // above
        if isArrowEnabled && isArrowAbove {
            insetRect = rect
                .insetBy(dx: 0, dy: 5)
                .offsetBy(dx: 0, dy: 5)
        }
        
		path = UIBezierPath()
		
		//1.
		path?.move(to: CGPoint(x: insetRect.minX + layer.cornerRadius, y: insetRect.minY));

        // above
        if isArrowEnabled && isArrowAbove {

			//2.
			path?.addLine(to: CGPoint(x: pointerX - pointerSize, y: insetRect.minY));
            path?.addLine(to: CGPoint(x:  pointerX, y: rect.minY))
			path?.addLine(to: CGPoint(x: pointerX + pointerSize, y: insetRect.minY));

			
        }
		//3.
		//path?.addLine(to: CGPoint(x: insetRect.maxX - layer.cornerRadius, y: insetRect.minY));

		
		//4.
		path?.addArc(withCenter: CGPoint(x: insetRect.maxX - layer.cornerRadius, y: insetRect.minY + layer.cornerRadius),
					 radius: layer.cornerRadius,
					 startAngle: CGFloat(Math.toRadians(270.0)),
					 endAngle: CGFloat(Math.toRadians(0.0)),
					 clockwise: true)
		
		
		//5.
		//path?.addLine(to: CGPoint(x: insetRect.maxX, y: insetRect.maxY - layer.cornerRadius))
		
		//6.
		path?.addArc(withCenter: CGPoint(x: insetRect.maxX - layer.cornerRadius, y: insetRect.maxY - layer.cornerRadius),
					 radius: layer.cornerRadius,
					 startAngle: CGFloat(Math.toRadians(0.0)),
					 endAngle: CGFloat(Math.toRadians(90.0)),
					 clockwise: true)
		
		
		//7.
		//path?.addLine(to: CGPoint(x: insetRect.minX, y: insetRect.maxY - layer.cornerRadius))

        // bottom
        if isArrowEnabled && !isArrowAbove {
			//8.
			path?.addLine(to: CGPoint(x: pointerX + pointerSize, y: insetRect.maxY));
			path?.addLine(to: CGPoint(x:  pointerX, y: rect.maxY))
			path?.addLine(to: CGPoint(x: pointerX - pointerSize, y: insetRect.maxY));
	
        }
		
		//9.
		//path?.addLine(to: CGPoint(x: insetRect.minX + layer.cornerRadius, y: insetRect.maxY))
		
		//10.
		path?.addArc(withCenter: CGPoint(x: insetRect.minX + layer.cornerRadius, y: insetRect.maxY - layer.cornerRadius),
					 radius: layer.cornerRadius,
					 startAngle: CGFloat(Math.toRadians(90.0)),
					 endAngle: CGFloat(Math.toRadians(180.0)),
					 clockwise: true)
		
		//11.
		//path?.addLine(to: CGPoint(x: insetRect.minX, y: insetRect.minY + layer.cornerRadius))
		
		//12.
		path?.addArc(withCenter: CGPoint(x: insetRect.minX + layer.cornerRadius, y: insetRect.minY + layer.cornerRadius),
					 radius: layer.cornerRadius,
					 startAngle: CGFloat(Math.toRadians(180)),
					 endAngle: CGFloat(Math.toRadians(270.0)),
					 clockwise: true)
		
		
		
        let context = UIGraphicsGetCurrentContext()
		
		path?.addClip()

        let colors = (!isSelected && isEnabled) ? [secondaryColor.cgColor,
                                                   primaryColor.cgColor] : [primaryColor.cgColor,
                                                                            primaryColor.cgColor]
        let colorSpace = CGColorSpaceCreateDeviceRGB()
        
        let colorLocations:[CGFloat] = [0.0, 1.0]
        
        let gradient = CGGradient(colorsSpace: colorSpace,
                                  colors: colors as CFArray,
                                  locations: colorLocations)!
        
        let startPoint = CGPoint.zero
        let endPoint = CGPoint(x:0, y:bounds.height)
        context?.drawLinearGradient(gradient, start: startPoint, end: endPoint, options:[])
		
		
    }
}
