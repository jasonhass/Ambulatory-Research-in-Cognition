//
// FAQAnswerViewController.swift
//



import UIKit
import HMMarkup
import ArcUIKit
class FAQAnswerViewController: UIViewController {

    @IBOutlet weak var backButton: HMMarkupButton!
    @IBOutlet weak var headerLabel: ACLabel!
    @IBOutlet weak var answerLabel: HMMarkupLabel!

    var question:FaqItem;
    

    init(faqQuestion:FaqItem) {
    
        self.question = faqQuestion;
        super.init(nibName: "FAQAnswerViewController", bundle: Bundle(for: Arc.self));
    }
    
    required init?(coder aDecoder: NSCoder) {
        self.question = FaqItem();
        super.init(coder: aDecoder);
    }
    
    open override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.backButton.addAction {
            self.navigationController?.popViewController(animated: true);
        }
        
        self.headerLabel.text = question.question;
        
        self.answerLabel.text = question.answer;
    }


    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
