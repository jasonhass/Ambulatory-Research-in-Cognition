//
// BlockProgressview.swift
//


import UIKit
import ArcUIKit
public class BlockProgressview: UIStackView {

	var maxBlockCount:Int = 12
	var currentBlock:Int = 0
	var color:UIColor = #colorLiteral(red: 0.400000006, green: 0.7799999714, blue: 0.7799999714, alpha: 1)
	
	init() {
		super.init(frame: .zero)
		spacing = 4
		axis = .horizontal
		alignment = .center
		distribution = .fillEqually
	}
	public func set(count:Int, progress:Int, current:Int?) {
		removeSubviews()
		
		for i in 0 ..< count {
			view {
				$0.layer.cornerRadius = 2
				
				if i <= progress {
					$0.backgroundColor = color
					//If we set current make that block larger than the rest
					if i == current {
						$0.backgroundColor = color
						$0.layout {
							$0.height == 42
						}
					//Otherwise just fill it in
					} else {
						$0.layout {
							$0.height == 32
						}
					}
					//If progress hasn't reached this far hollow it out. 
				} else {
					$0.backgroundColor = .clear
					$0.layer.borderColor = color.cgColor
					$0.layer.borderWidth = 1
					$0.layout {
						$0.height == 32
					}
					
				}
			}
			
			
		}
	}
	required init(coder: NSCoder) {
		super.init(coder: coder)
	}
	
}
extension UIView {
	
	@discardableResult
	public func blockProgress(apply closure: (BlockProgressview) -> Void) -> BlockProgressview {
		return custom(BlockProgressview(), apply: closure)
	}
	
}
