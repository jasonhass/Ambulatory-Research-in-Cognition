//
// FAQTopicViewController.swift
//



import UIKit

class FAQTopicViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {

    @IBOutlet weak var tableView: UITableView!
    var faqTopic:FaqTopic;
    
    required init?(coder aDecoder: NSCoder) {
        self.faqTopic = FaqTopic();
        super.init(coder: aDecoder);
    }
    
    init(faqTopic:FaqTopic)
    {
        self.faqTopic = faqTopic;
        super.init(nibName: "FAQTopicViewController", bundle: Bundle(for: Arc.self));
        
    }
    
    open override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.tableView.rowHeight = UITableView.automaticDimension
        self.tableView.estimatedRowHeight = 400
        self.tableView.register(UINib(nibName: "FAQTableViewCell", bundle: Bundle(for: Arc.self)), forCellReuseIdentifier: "faq_cell");
        
        let header: ResourcesHeaderView = .get();
        header.backButton.addAction {
            self.navigationController?.popViewController(animated: true);
        }

        header.headerLabel.text = self.faqTopic.topic_header;
        
        header.subLabel.text = "faq_subpage_subheader";
        self.tableView.tableHeaderView = header;
    }
    
    override func viewDidLayoutSubviews() {
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
    

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.faqTopic.questions.count;
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let question = faqTopic.questions[indexPath.row];
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "faq_cell") as! FAQTableViewCell;
        cell.label.text = question.question;
        return cell;
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        let question = faqTopic.questions[indexPath.row];
        
        let vc:FAQAnswerViewController  = FAQAnswerViewController(faqQuestion: question);
        self.navigationController?.pushViewController(vc, animated: true);
    }
}
