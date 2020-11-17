//
// FAQViewController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.





import UIKit

public class FAQViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    @IBOutlet weak var tableView: UITableView!

    var faqTopics: Array<FaqTopic> = [];
    
    open override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    override public func viewDidLoad() {
        super.viewDidLoad()

        self.loadFAQData();
        self.tableView.rowHeight = UITableView.automaticDimension
        self.tableView.estimatedRowHeight = 400
        self.tableView.register(UINib(nibName: "FAQTableViewCell", bundle: Bundle(for: Arc.self)), forCellReuseIdentifier: "faq_cell");
        
        let header: ResourcesHeaderView = .get();
        header.backButton.addAction {
            self.navigationController?.popViewController(animated: true);
        }
        header.headerLabel.text = "faq_header";
        self.tableView.tableHeaderView = header;
        
        self.navigationItem.setHidesBackButton(true, animated:true)
    }
    
    override public func viewDidLayoutSubviews() {
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
    
    func loadFAQData()
    {
        do {
        
            guard let asset = NSDataAsset(name: "faq_items") else {
                fatalError("Missing asset named faq_items")
            }
            let f = try JSONDecoder().decode([FaqTopic].self, from: asset.data);
            self.faqTopics = f;
        } catch {
            fatalError("Invalid asset format: - Error: \(error.localizedDescription)")
            
        }
    }
    

    public func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.faqTopics.count;
    }
    
    public func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let topic = self.faqTopics[indexPath.row];
        let cell = tableView.dequeueReusableCell(withIdentifier: "faq_cell") as! FAQTableViewCell;
        cell.label.text = topic.topic_name;
        
        return cell;
    }
    
    
    public func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        let topic = self.faqTopics[indexPath.row];
        let vc = FAQTopicViewController(faqTopic: topic);
        
        self.navigationController?.pushViewController(vc, animated: true);
        
    }

}
