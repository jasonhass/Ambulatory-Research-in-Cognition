/*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

import UIKit
fileprivate struct SurveySelectItem {
    var name:String
    var selected:Bool = false
    
    init(name:String) {
        self.name = name
    }
}
class SurveySelectViewController: DNViewController, UITableViewDelegate, UITableViewDataSource {

    var onConfirm : ( (Array<Int>,String?) -> Void)?
    var retValue:String?
    
    @IBOutlet weak var titleTextView: UITextView!
    @IBOutlet var tableView: UITableView!
    @IBOutlet weak var nextButton: UIButton!

    fileprivate var choices:Array<SurveySelectItem> = []
    var isMultiple = false
    var lastIndexPath: IndexPath? = nil
    var numberOfSelections:Int = 0
    var selectedRows = [Bool]()
    
    var overrideAnswer: String?

    var exclusiveSelectables:Array<Int> = Array();
    
    var answer: String? {
        get {
            return getSelectedAnswers()
        }
        set {
            overrideAnswer = newValue
        }
    }
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(true)
        self.tableView.flashScrollIndicators()
        if isMultiple
        {
            self.titleTextView.text = "Select all that apply:".localized(key:"list_selectall");
            self.titleTextView.sizeToFit()
        }
        else
        {
            self.titleTextView.text = "Select one:".localized(key:"list_selectone");
        }
        self.tableView.reloadData()


    }
    func setChoices(values:Array<String>){
        choices = []
        for value in values {
            choices.append(SurveySelectItem(name: value))
        }
    }
    func setSelected(_ items:[Int]){
        for i in 0..<choices.count
        {
            choices[i].selected = false;
        }
        for item in items {
            choices[item].selected = true
        }
        
        lastIndexPath = IndexPath(row: items.last!, section: 0);
        numberOfSelections = items.count;
        
       
        if numberOfSelections > 0
        {
            if nextButton != nil
            {
                nextButton.isEnabled = true;
            }
        }
        
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.view.backgroundColor = UIColor(white: 0, alpha: 0.5);
        self.view.isOpaque = false;
        self.tableView.allowsSelection = false;
        
        
        
        if numberOfSelections > 0
        {
            nextButton.isEnabled = true;
        }
    }

    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 71
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return choices.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell:SurveySelectTableViewCell = tableView.dequeueReusableCell(withIdentifier: "answer") as! SurveySelectTableViewCell
        let title = choices[indexPath.row].name.localized()
        cell.selectButton.setTitle(title, for: .normal)
        cell.selectButton.addTarget(self, action: #selector(selectedAnswer(sender:)), for: .touchUpInside)
        cell.selectButton.isSelected = choices[indexPath.row].selected
        cell.backgroundColor = UIColor.clear
        return cell
    }

    func selectedAnswer(sender: UIButton) {
        let touchPoint = sender.convert(CGPoint.zero, to: tableView)
        guard let indexPath = tableView.indexPathForRow(at: touchPoint) else
        {
            return;
        }

        
        if !isMultiple  {
            if lastIndexPath != nil && lastIndexPath != indexPath {
                choices[(lastIndexPath?.row)!].selected = false
            }
        }
        else    // isMultiple == true
        {
            if sender.isSelected
            {
                
                if exclusiveSelectables.contains(indexPath.row)
                {
                    for i in 0..<choices.count
                    {
                        choices[i].selected = false;
                    }
                }
                else
                {
                    for i in exclusiveSelectables
                    {
                        choices[i].selected = false;
                    }
                }
                
                
            }
        }
        
        
        
        lastIndexPath = indexPath

        if sender.isSelected {
            choices[indexPath.row].selected = true
        }
        else {
            choices[indexPath.row].selected = false
        }

        
        if sender.isSelected {
            numberOfSelections = numberOfSelections + 1
        }
        else {
            numberOfSelections = numberOfSelections - 1
        }
        
        nextButton.isEnabled = false;
        if numberOfSelections > 0
        {
            nextButton.isEnabled = true;
        }
        
        tableView.reloadData()
    }

    func getSelectedAnswers() -> String {

        var answersArray = [String]()

        for i in 0 ..< choices.count {
            if choices[i].selected {
                answersArray.append(choices[i].name.localized())
            }
        }

        return answersArray.joined(separator: ", ")

    }
    
    func getSelectedIndexes() -> Array<Int>
    {
        var selectedIndexes:Array<Int> = Array();
        
        for i in 0..<choices.count
        {
            if choices[i].selected
            {
                selectedIndexes.append(i);
            }
        }
        
        return selectedIndexes;
    }
    
    override func pressedNext(sender:UIButton){
        
        retValue = getSelectedAnswers();
        let selectedIndexes = getSelectedIndexes();
        let onConfirm = self.onConfirm
        // deliberately set to nil just in case there is a self reference
        self.onConfirm = nil
        guard let block = onConfirm else { return }
        
        self.view.removeFromSuperview();
        self.removeFromParentViewController();
        block(selectedIndexes, self.retValue)
        
    }

}
