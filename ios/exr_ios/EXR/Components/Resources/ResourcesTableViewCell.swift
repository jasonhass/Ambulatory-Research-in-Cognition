//
// ResourcesTableViewCell.swift
//



import UIKit
import HMMarkup

public class ResourcesTableViewCell: UITableViewCell {

    @IBOutlet weak var icon: UIImageView!
    @IBOutlet weak var label: HMMarkupLabel!
    @IBOutlet weak var detailLabel: HMMarkupLabel!

    override public func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override public func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
}
