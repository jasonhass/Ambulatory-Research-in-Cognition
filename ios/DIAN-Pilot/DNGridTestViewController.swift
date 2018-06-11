/*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

import UIKit

class DNGridTestViewController: DNTestViewController, UICollectionViewDelegate, UICollectionViewDataSource {
    let imageCollectionCount = 25
    let fCollectionCount = 60
    private var phase = 0
    @IBOutlet weak var imageCollectionView: UICollectionView!
    @IBOutlet weak var fCollectionView: UICollectionView!
    @IBOutlet var fCollectionContainer: UIView!
    @IBOutlet var readyView: UIView!

    @IBOutlet var preSymbolsView: UIView!

    private var test:DNGridTest?
    var testNumber:Int = 0;
    
    var gridData:DNGridInputSet = DNGridInputSet(selectedFs:0, selectedEs:0, selectedGridItems:[]);
    var gridMetrics:DNGridTestMetrics = DNGridTestMetrics();
    var endTimer:Timer?
    var maybeEndTimer:Timer?
    override func getTest<T : DNTest>() -> T? {
        //If our test matches what the outside context wants, pass it back
        return self.test as? T
    }

    override func setTest<T : DNTest>(test: T) {
        self.test = test as? DNGridTest

    }


    override func viewDidLoad() {
        super.viewDidLoad()
        
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated);
        
        if isBeingPresented {
            self.test?.startTest()
        }
        displayPreSymbols();
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func displaySymbols()
    {
        self.preSymbolsView.removeFromSuperview();
        self.imageCollectionView.isHidden = false;
        endTimer?.invalidate();
        phase = 0;
        self.fCollectionContainer.removeFromSuperview();
        imageCollectionView.allowsSelection = false;
        fCollectionView.allowsSelection = false;
        imageCollectionView.reloadData();
        gridMetrics.displaySymbols =  Date().timeIntervalSince(test!.startTime);
        Timer.scheduledTimer(timeInterval: 3, target: self, selector: #selector(displayFs), userInfo: nil, repeats: false)
    }
    
    func displayFs()
    {
        self.fCollectionContainer.frame = self.imageCollectionView.frame
        self.fCollectionContainer.backgroundColor = UIColor(patternImage: #imageLiteral(resourceName: "background_light_16x16"))
        self.view.addSubview(self.fCollectionContainer)
        phase = 1
        self.fCollectionView.reloadData()
        self.imageCollectionView.reloadData()
        self.fCollectionView.allowsSelection = true;
        self.fCollectionView.allowsMultipleSelection = true;
        gridMetrics.displayDistraction = Date().timeIntervalSince(test!.startTime);

        Timer.scheduledTimer(timeInterval: 8, target: self, selector: #selector(displayReady), userInfo: nil, repeats: false)
    }
    
    func displayReady()
    {
        self.readyView.frame = self.view.frame;
        self.readyView.layoutSubviews()
        self.view.addSubview(readyView);
        Timer.scheduledTimer(timeInterval: 1.0, target: self, selector: #selector(displayGrid), userInfo: nil, repeats: false)
    }
    
    func displayPreSymbols()
    {
        self.imageCollectionView.isHidden = true;
        self.preSymbolsView.frame = self.view.frame;
        self.preSymbolsView.layoutSubviews()

        self.view.addSubview(preSymbolsView);
        Timer.scheduledTimer(timeInterval: 2.0, target: self, selector: #selector(displaySymbols), userInfo: nil, repeats: false)
    }
    
    func displayGrid()
    {
        self.readyView.removeFromSuperview();
        self.fCollectionContainer.removeFromSuperview()
        imageCollectionView.allowsSelection = true;
        imageCollectionView.allowsMultipleSelection = true;
        phase = 2
        
        gridMetrics.displayTestGrid = Date().timeIntervalSince(test!.startTime);

        endTimer = Timer.scheduledTimer(timeInterval: 20, target: self, selector: #selector(endTest), userInfo: nil, repeats: false)
    }
    
    override func endTest()
    {
        if phase == 3
        {
            return;
        }
        
        phase = 3
        testNumber += 1;
        
        test?.selectValue(option: gridData as AnyObject?);
        gridData = DNGridInputSet(selectedFs: 0, selectedEs: 0, selectedGridItems: []);
        
        test?.gridTestMetrics.append(self.gridMetrics);
        
        self.gridMetrics = DNGridTestMetrics();
        
        if testNumber >= test!.getNumQuestions()
        {
            test?.endTest()
            super.endTest()
        }
        else
        {
            displayPreSymbols();
        }

    }
    
    func maybeEndTest()
    {
        if gridData.selectedGridItems.count >= (test?.getNumSymbols())!
        {
            endTimer?.invalidate();
            endTest();
        }
    }

    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return  (collectionView == imageCollectionView) ? 25 : 60
    }
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }

    
    func collectionView(_ collectionView: UICollectionView, shouldSelectItemAt indexPath: IndexPath) -> Bool {
        
        if (collectionView.cellForItem(at: indexPath) as? GridImageCell) != nil
        {
            if collectionView.indexPathsForSelectedItems?.count == 3
            {
                return false;
            }
        }
        
        return true;
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        
        if let c = collectionView.cellForItem(at: indexPath) as? GridImageCell
        {
            selectedGridItem(x: indexPath.item % 5, y: indexPath.item / 5, time: c.touchTime!);
            maybeEndTimer?.invalidate();
            maybeEndTimer = Timer.scheduledTimer(timeInterval: 2.0, target: self, selector: #selector(maybeEndTest), userInfo: nil, repeats: false)
        }
        else if let c = collectionView.cellForItem(at: indexPath) as? GridFCell
        {
            c.backgroundColor = UIColor(red: 182.0/255.0, green: 221.0/255.0, blue: 236.0/255.0, alpha: 1.0);
            selectedFCell(aCell: c);
        }
        
    }
    
    func collectionView(_ collectionView: UICollectionView, didDeselectItemAt indexPath: IndexPath)
    {
        if (collectionView.cellForItem(at: indexPath) as? GridImageCell) != nil
        {
            deselectedGridItem(x: indexPath.item % 5, y: indexPath.item / 5);
        }
        else if let c = collectionView.cellForItem(at: indexPath) as? GridFCell
        {
            c.backgroundColor = UIColor.clear
            
            deselectedFCell(aCell: c);
        }
    }
    
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let type = (collectionView == imageCollectionView) ? "imageCell" : "fCell"
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: type, for: indexPath)

        guard let data = (collectionView == imageCollectionView) ? test?.testData[testNumber].imageSet : test?.testData[testNumber].fSet else {
            return cell
        }


        if collectionView == imageCollectionView {

            let iCell = cell as! GridImageCell
            
            iCell.image.isHidden = true;
            if phase == 0 {
                if data.grids[indexPath.row / (data.grids[0].count)][indexPath.row % (data.grids[0].count)] > -1 {

                    let index = data.grids[indexPath.row / (data.grids[0].count)][indexPath.row % (data.grids[0].count)];
                    iCell.setImage(image: test?.imageAt(index: index));
                    iCell.image.isHidden = false;
                }
            } else {
                iCell.touchTime = nil;
                iCell.touchLocation = nil;
                iCell.setImage(image: nil)
                iCell.image.isHidden = true;

            }

        } else {
            let fCell = cell as! GridFCell
            fCell.backgroundColor = UIColor.clear;
            
            if data.grids[indexPath.row / (data.grids[0].count)][indexPath.row % (data.grids[0].count)] == 1 {
                fCell.setCharacter(character: "F")
            }
            else
            {
                fCell.setCharacter(character: "E");
            }
        }


        return cell

    }
    
    public func selectedFCell(aCell:GridFCell)
    {
        if let t = aCell.label.text
        {
            if t == "F"
            {
                gridData.selectedFs += 1;
            }
            else if t == "E"
            {
                gridData.selectedEs += 1;
            }
        }
    }
    
    
    public func deselectedFCell(aCell:GridFCell)
    {
        if let t = aCell.label.text
        {
            if t == "F"
            {
                gridData.selectedFs -= 1;
            }
            else if t == "E"
            {
                gridData.selectedEs -= 1;
            }
        }
    }
    
    
    public func selectedGridItem(x:Int, y:Int, time:Date)
    {
        for s in gridData.selectedGridItems
        {
            if s.gridLocationX == x && s.gridLocationY == y
            {
                return;
            }
        }

        let inputData = DNGridInputData(gridLocationX: x, gridLocationY: y, timeTouched: time as NSDate);
        gridData.selectedGridItems.append(inputData);
    }

    public func deselectedGridItem(x:Int, y:Int)
    {
        for i in 0..<gridData.selectedGridItems.count
        {
            let s = gridData.selectedGridItems[i];
            if s.gridLocationX == x && s.gridLocationY == y
            {
                gridData.selectedGridItems.remove(at: i);
                return;
            }
        }
    }
    
    
}


class GridImageCell:UICollectionViewCell
{
    @IBOutlet weak var image: UIImageView!
    var touchLocation:CGPoint?
    var touchTime:Date?
    func setImage(image:UIImage?){

        self.image.image = image
    }
    
    override var isSelected: Bool {
        willSet {
            if newValue == true
            {
                self.backgroundColor = UIColor(red: 13.0 / 255.0, green: 143.0 / 255.0, blue: 192.0 / 255.0, alpha: 1.0);
                
            }
            else
            {
                self.backgroundColor = UIColor(red: 182.0/255.0, green: 221.0/255.0, blue: 236.0/255.0, alpha: 1.0);
            }
        }
    }
    
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        if let t = touches.first
        {
            touchLocation = t.location(in: self.window);
            touchTime = Date();
        }
        
        super.touchesBegan(touches, with: event);
    }
    
    override func prepareForReuse() {
        self.isSelected = false;
        super.prepareForReuse();
    }
    
}
class GridFCell:UICollectionViewCell {
    @IBOutlet weak var label: UILabel!
    func setCharacter(character:String){
        label.text = character
    }
    
    override func prepareForReuse() {
        self.isSelected = false;
        super.prepareForReuse();
    }

}

