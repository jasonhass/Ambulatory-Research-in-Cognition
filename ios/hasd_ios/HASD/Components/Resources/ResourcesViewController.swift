//
// ResourcesViewController.swift
//




import UIKit


open class ResourcesViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {
 
    public var resourceItems:Array<(String, String, Any)> = [];
    
    @IBOutlet public weak var tableView: UITableView!
    
    open override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    override open func viewDidLoad() {
        super.viewDidLoad()

        self.resourceItems = [
            ("resources_availability", "cut-ups/icons/alarm-clock-solid-teal", navigateToChangeSchedule),
            ("resources_contact", "cut-ups/icons/avatar-solid-teal", navigateToContact),
            ("resources_about_link", "cut-ups/icons/app-solid-teal", navigateToAbout),
            ("resources_faq_link", "cut-ups/icons/study-faq-solid-teal", FAQViewController.self),
            ("resources_privacy_link", "cut-ups/icons/privacy-solid-teal", {Arc.shared.appNavigation.defaultPrivacy(); }),
        ];
        
        
        self.tableView.register(UINib(nibName: "ResourcesTableViewCell", bundle: Bundle(for: Arc.self)), forCellReuseIdentifier: "resources_cell");
//        self.tableView.rowHeight = 68;
        self.tableView.estimatedRowHeight = 68
        
        let header: ResourcesHeaderView = .get();
        header.backButton.isHidden = true;
        header.headerLabel.translationKey = "resources_header";
        header.headerLabel.text = "resources_header";
        header.headerLabel.font = UIFont(name: "Roboto-Medium", size: 26)
        self.tableView.tableHeaderView = header;
        
    }
	open override func viewWillAppear(_ animated: Bool) {
		super.viewWillAppear(animated)
		self.tableView.reloadData()
	}
    override open func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        
        // Dynamic sizing for the header view
        if let headerView = tableView.tableHeaderView {
            let height = headerView.systemLayoutSizeFitting(UIView.layoutFittingCompressedSize).height
            var headerFrame = headerView.frame
            headerFrame.size.width = self.view.frame.width;
            
            // If we don't have this check, viewDidLayoutSubviews() will get
            // repeatedly, causing the app to hang.
            if height != headerFrame.size.height {
                headerFrame.size.height = height
                headerView.frame = headerFrame
                tableView.tableHeaderView = headerView
                self.tableView.addConstraint(NSLayoutConstraint(item: headerView, attribute: .width, relatedBy: .equal, toItem: self.tableView, attribute: .width, multiplier: 1, constant: 0));
            }
        }
    }


    public func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.resourceItems.count;
    }
    
    public func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let row = self.resourceItems[indexPath.row];
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "resources_cell") as! ResourcesTableViewCell;
        cell.icon.image = UIImage(named: row.1);
        cell.label.translationKey = row.0;
        cell.label.text = row.0;
        if row.0 == "resources_availability" && (Arc.shared.availableTestSession != nil && Arc.environment?.hidesChangeAvailabilityDuringTest == true) {
            cell.detailLabel.text = "".localized("availability_changedenied_test")
            cell.isUserInteractionEnabled = false
            cell.label.alpha = 0.5
            cell.detailLabel.alpha = 0.5
        } else {
            cell.detailLabel.text = nil
            cell.isUserInteractionEnabled = true
            cell.label.alpha = 1
            cell.detailLabel.alpha = 1


        }
        return cell;
    }
    
    public func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        let row = self.resourceItems[indexPath.row];
        if let vcClass = row.2 as? UIViewController.Type
        {
            let vc = vcClass.instanceFromType(vcClass);
            self.navigationController?.pushViewController(vc, animated: true);
        }
        else if let callable = row.2 as? ()->Void
        {
            callable();
        }
    }
    
    public func navigateToChangeSchedule() {
        let vc = Arc.shared.appNavigation.defaultRescheduleAvailability().viewForState()
        self.navigationController?.pushViewController(vc, animated: true)
    }
    
    public func navigateToAbout() {
        let vc = Arc.shared.appNavigation.defaultAbout().viewForState()
        self.navigationController?.pushViewController(vc, animated: true)
    }

    public func navigateToContact() {
        let vc = Arc.shared.appNavigation.defaultHelpState()
        Arc.shared.appNavigation.navigate(vc: vc, direction: .toRight)
    }
}
