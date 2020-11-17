//
// CircularProgressGroupStackView.swift
//


import ArcUIKit

public class ACCircularProgressGroupStackView : UIStackView {
	private var progressViews:[CircularProgressView] = []
	public var config = Drawing.CircularBar()
	public var checkConfig = Drawing.CheckMark()
	public var ellipseConfig = Drawing.Ellipse()
	private var contentStack:UIStackView!

	public init() {
		super.init(frame: .zero)
		
		config.strokeWidth =  6
		config.trackColor = ACColor.highlight
		config.barColor = ACColor.primaryInfo
		config.size = 64
		checkConfig.strokeColor = ACColor.highlight
		checkConfig.size = 34
		ellipseConfig.size = 64
		ellipseConfig.color = ACColor.primaryInfo
		
		translatesAutoresizingMaskIntoConstraints = false
		axis = .vertical
		alignment = .leading
		contentStack = stack {
			$0.distribution = .fillEqually
			$0.spacing = 8
			
		}
		

	}
	
	required init(coder: NSCoder) {
		fatalError("init(coder:) has not been implemented")
	}
	
	public func addProgressViews(count:Int) {
		
		
		
		
		for _ in 0 ..< count {
			progressViews.append (contentStack.circularProgress {
				//Make sure to pass in updated configurations

				$0.config = config
				$0.checkConfig = checkConfig
				$0.ellipseConfig = ellipseConfig
				$0.progress = 0
			})
		}
	}
	
	public func addProgressView(progress:Double) {
		
		
		
		progressViews.append (contentStack.circularProgress {
			
			//Make sure to pass in updated configurations
			$0.config = config
			$0.checkConfig = checkConfig
			$0.ellipseConfig = ellipseConfig
			$0.progress = progress
		})
	}
	public func set(progress:Double, for index:Int) {
		progressViews[index].progress = progress
	}
	public func clearProgressViews() {
		removeSubviews()
		progressViews = []
	}
}
extension UIView {
	
	@discardableResult
	public func circularProgressGroup(apply closure: (ACCircularProgressGroupStackView) -> Void) -> ACCircularProgressGroupStackView {
		return custom(ACCircularProgressGroupStackView(), apply: closure)
	}
	
}
