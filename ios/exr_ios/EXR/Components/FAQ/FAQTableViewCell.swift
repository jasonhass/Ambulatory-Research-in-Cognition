//
// FAQTableViewCell.swift
//



import UIKit
import HMMarkup

open class FAQTableViewCell: UITableViewCell {

    @IBOutlet weak var label: HMMarkupLabel!
    
    override open func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override open func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
