//
// GoalDayTileGroup.swift
//


import UIKit
import ArcUIKit
public class GoalDayTileGroup: UIStackView {
	
	weak var contents:UIStackView!
	override init(frame: CGRect) {
		super.init(frame: .zero)
		build()
	}
	
	required init(coder: NSCoder) {
		super.init(coder: coder)
		build()
	}
	
	
	func build() {
		distribution = .fillEqually
		
	}
	public func add(tile name:String, progress:Double) {
		goalDayTile {
			
			$0.progressView.progress = progress
			$0.titleLabel.text = "*\(name)*"
            $0.progressView.checkConfig.strokeColor = ACColor.highlight
            $0.progressView.ellipseConfig.color = ACColor.primaryInfo
			if arrangedSubviews.count % 2 == 1 {
				$0.backgroundColor = ACColor.calendarItemBackground
			} else {
				$0.backgroundColor = .white
			}
		}
	}
	
	public func clear() {
		removeSubviews()
	}
	public func set(progress:Double, forIndex index:Int) {
		guard arrangedSubviews.count > 0 else {
			return
		}
		if let tile = arrangedSubviews[index] as? GoalDayTile {
			tile.set(progress: progress)
		}
	}
}
extension UIView {
	@discardableResult
	public func goalDayTileGroup(apply closure: (GoalDayTileGroup) -> Void) -> GoalDayTileGroup {
		return custom(GoalDayTileGroup(), apply: closure)
	}

}
