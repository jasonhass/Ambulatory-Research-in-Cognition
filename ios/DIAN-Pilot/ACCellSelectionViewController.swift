//
//  ACCellSelectionViewController.swift
//  JoAnn iOS
//

//  Copyright © 2017 happyMedum. All rights reserved.
//

import UIKit
protocol ACCellData {
    func getTitle() -> String
    var isSelected:Bool {get set}
}
protocol ACCellSelectionViewControllerDelegate {
    func cellSelectionDidSelect(controller:ACCellSelectionViewController, indicies:Array<IndexPath>)
}
class ACCellSelectionViewController: DNViewController, UITableViewDelegate, UITableViewDataSource {
    var data:Array<ACCellData> = []
    var selected:Array<IndexPath> = []
    var maxItems:Int = 1 {
        didSet {
            
        }
    }
    
    var setTitle:String?
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var tableView:UITableView!
    @IBOutlet weak var tableViewHeight: NSLayoutConstraint!

    var delegate:ACCellSelectionViewControllerDelegate?
    override func viewDidLoad() {
        super.viewDidLoad()

        tableView.reloadData()
        tableView.estimatedRowHeight = 44.0
        tableView.rowHeight = UITableViewAutomaticDimension
    }
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        guard tableViewHeight != nil else {
            return 
        }
        if self.tableView.contentSize.height < self.tableView.frame.height {

            DispatchQueue.main.async {
                
                self.tableViewHeight.constant = self.tableView.contentSize.height
                self.view.layoutIfNeeded()
            }

        }
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    func setTitle(title:String) {
        setTitle = title
        if titleLabel != nil  {
            titleLabel.text = title
        }
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        if let title = setTitle {
            if titleLabel != nil  {
                titleLabel.text = title
            }
        }
    }
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return data.count
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "ACCell") as! ACCell
        
        cell.name?.text = data[indexPath.row].getTitle()
         cell.setCheckBoxStateTo(on: data[indexPath.row].isSelected);
        if data[indexPath.row].isSelected {
            if !selected.contains(indexPath) {

                selected.append(indexPath)
            }
            cell.setCheckBoxStateTo(on: true);

        }
        return cell
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if !selected.contains(indexPath) {
            selected.append(indexPath)
            let cell = tableView.cellForRow(at: indexPath) as! ACCell;
            cell.setCheckBoxStateTo(on: true);
            
            data[indexPath.row].isSelected = true
            if maxItems != 0 && selected.count > maxItems {
                self.tableView(tableView, didDeselectRowAt: selected.first!)
            }
        
           
        }
        (self.view.viewWithTag(1) as! UIButton).isEnabled = true
        
    }
    func tableView(_ tableView: UITableView, didDeselectRowAt indexPath: IndexPath) {
        let cell = tableView.cellForRow(at: indexPath) as! ACCell;
        cell.setCheckBoxStateTo(on: false);
        
        if let i = selected.index(of: indexPath){
            data[indexPath.row].isSelected = false
            selected.remove(at: i)
        }
        
    }
    
    @IBAction func savePressed(_ sender: Any) {
        delegate?.cellSelectionDidSelect(controller: self, indicies: selected)
        self.removeFromParentViewController()
        self.view.removeFromSuperview()
    }
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
