//
// ParticipationView.swift
//



import UIKit
import ArcUIKit
public class ParticipationView: ACTemplateView {

    /*
    // Only override draw() if you perform custom drawing.
    // An empty implementation adversely affects performance during animation.
    override func draw(_ rect: CGRect) {
        // Drawing code
    }
    */
	
	public override init() {
		super.init()
		backgroundColor = .white
		
	}
	
	required init?(coder aDecoder: NSCoder) {
		fatalError("init(coder:) has not been implemented")
	}
	
	public override func content(_ view: UIView) {
		view.infoContent {
			$0.alignment = .leading
			$0.textColor = ACColor.primaryText
			$0.setHeader("Thank you for your time.")
			$0.setSeparatorWidth(0.15)
			$0.setContent("Thank you for letting us know you are not able to commit to the study at this time. If you need any questions answered before continuing with the study, please contact the study team by tapping HELP in the upper right hand corner of the screen.")
			
		}
	}
}
