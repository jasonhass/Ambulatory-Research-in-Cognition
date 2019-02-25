//
// GridTestViewController.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


import UIKit

public class GridTestViewController: ArcViewController, UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {
    public enum Mode {
        case none
        case image
        case fCell
    }
    
    var mode:Mode = .none
    
    var controller = Arc.shared.gridTestController
    var tests:[GridTest] = []
    var responseId:String = ""
    var testNumber:Int = 0
    var phase:Int = 0
    var endTimer:Timer?
    var maybeEndTimer:Timer?
	var isVisible = true
    var interstitial:InterstitialView = .get()
    @IBOutlet weak var collectionView: UICollectionView!
    @IBOutlet weak var collectionViewHeight:NSLayoutConstraint!
    @IBOutlet weak var tapOnTheFsLabel: UILabel!
    
    private var symbols:[UIImage] = [#imageLiteral(resourceName: "key"),
                                     #imageLiteral(resourceName: "phone"),
                                     #imageLiteral(resourceName: "pen")]
    
    private let IMAGE_HEIGHT = 105
    private let IMAGE_ROWS = 5
    private let LETTER_HEIGHT = 46
    private let LETTER_ROWS = 10
    private var LETTER_BUFFER = 20
    private let LINE_SPACING = 1


    override open func viewDidLoad() {
        super.viewDidLoad()
        ACState.testCount += 1
		let app = Arc.shared
		let studyId = Int(app.studyController.getCurrentStudyPeriod()?.studyID ?? -1)
		if let sessionId = app.currentTestSession {
			let session = app.studyController.get(session: sessionId, inStudy: studyId)
			let data = session.surveyFor(surveyType: .gridTest)
			responseId = data!.id! //A crash here means that the session is malformed
			
			tests = controller.createTest(numberOfTests: 2)
			_ = controller.createResponse(id: responseId, numSections: 2)
//			print(responseId)
		} else {
		
        	tests = controller.createTest(numberOfTests: 2)
        	responseId = controller.createResponse(numSections: 2)
		}
		collectionView.register(UINib(nibName: "GridFCell", bundle: Bundle(for: GridFCell.self)), forCellWithReuseIdentifier: "fCell")
		collectionView.register(UINib(nibName: "GridImageCell", bundle: Bundle(for: GridImageCell.self)), forCellWithReuseIdentifier: "imageCell")

        // Do any additional setup after loading the view.
        
        if let h = UIApplication.shared.keyWindow?.rootViewController?.view.frame.height, h > 568 {
            LETTER_BUFFER = 60
        }
		
		controller.set(symbols: responseId, gridTests: tests)
        
    }
    override open func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
		isVisible = true

			
		_ = controller.start(test: responseId)
		_  = controller.mark(filled: responseId)

        displayPreSymbols();
    }
	public override func viewDidDisappear(_ animated: Bool) {
		super.viewDidDisappear(animated)
		isVisible = false
        maybeEndTimer?.invalidate()
        maybeEndTimer = nil
	}
    func displaySymbols()
    {
		guard isVisible else {
			return
		}
        
        self.collectionViewHeight.constant = CGFloat((IMAGE_HEIGHT*IMAGE_ROWS) + (LINE_SPACING*(IMAGE_ROWS-1)))
        
        interstitial.set(message: nil)
        interstitial.removeFromSuperview()
        self.mode = .image
        
        endTimer?.invalidate();
        
        phase = 0;
        
        collectionView.allowsSelection = false;
        
        collectionView.reloadData();
        
        _ = controller.markTime(gridDisplayedSymbols: responseId, questionIndex: testNumber)
       
        
        Timer.scheduledTimer(withTimeInterval: 3, repeats: false) {
			[weak self] (timer) in
            self?.displayFs()
        }
    }
    
    func displayFs()
    {
		guard isVisible else {
			return
		}
        
        self.collectionViewHeight.constant = CGFloat((LETTER_HEIGHT*LETTER_ROWS) + (LINE_SPACING*(LETTER_ROWS-1)) + LETTER_BUFFER)
        
        self.mode = .fCell

        phase = 1

        collectionView.allowsSelection = true

        collectionView.allowsMultipleSelection = true

        collectionView.reloadData()

        _ = controller.markTime(gridDisplayedFs: responseId, questionIndex: testNumber)
        
        tapOnTheFsLabel.isHidden = false
        
        Timer.scheduledTimer(withTimeInterval: 8, repeats: false) {[weak self] (timer) in
            self?.displayReady()
        }
        
    }
    
    func displayReady()
    {
        
		guard isVisible else {
			return
		}

		Arc.shared.displayAlert(message: "Ready", options: [.wait(waitTime: 1.0, {
			[weak self] in
			self?.displayGrid()
			if let s = self {
				s.tapOnTheFsLabel.isHidden = true
			}
		})])

		
    }
    
    func displayPreSymbols()
    {
		guard isVisible else {
			return
		}
		self.tapOnTheFsLabel.isHidden = true

		Arc.shared.displayAlert(message: "Try to remember the location\nof the items!",
									options: [.wait(waitTime: 2.0, {
										self.displaySymbols()
			
									})])

		
    }
    
    func displayGrid()
    {
		guard isVisible else {
			return
		}
        interstitial.set(message: nil)
        interstitial.removeFromSuperview()
        self.collectionViewHeight.constant = CGFloat((IMAGE_HEIGHT*IMAGE_ROWS) + (LINE_SPACING*(IMAGE_ROWS-1)))
        mode = .image
        
        collectionView.allowsSelection = true;
        
        collectionView.allowsMultipleSelection = true;
        
        phase = 2
        
        collectionView.reloadData()
        
        _ = controller.markTime(gridDisplayedTestGrid: responseId, questionIndex: testNumber)
        
        endTimer = Timer.scheduledTimer(timeInterval: 20, target: self, selector: #selector(endTest), userInfo: nil, repeats: false)
    }
    @objc func endTest()
    {
        if phase == 3
        {
            return;
        }
        
        phase = 3
        
        testNumber += 1;
        
//        test?.selectValue(option: gridData as AnyObject?);
//        gridData = DNGridInputSet(selectedFs: 0, selectedEs: 0, selectedGridItems: []);
        
//        test?.gridTestMetrics.append(self.gridMetrics);
        
//        self.gridMetrics = DNGridTestMetrics();
        
        if testNumber >= controller.get(testCount: responseId)
        {
			Arc.shared.nextAvailableState()
        }
        else
        {
            displayPreSymbols();
        }
        
    }
    
    func maybeEndTest()
    {
        if controller.get(numChoicesFor: responseId, testIndex: testNumber) >= 3
        {
            endTimer?.invalidate();
            endTest();
        }
    }
    
    public func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        switch mode {
        case .image:
            return 25

        case .fCell:
            return 60
        default:
            return 0
        }
        
    }
    
    public func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let type = (mode == .image) ? "imageCell" : "fCell"
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: type, for: indexPath)
        let index = indexPath.row
        
        if (mode == .image) {
            
            let iCell = cell as! GridImageCell
            
            iCell.image.isHidden = true;
            if phase == 0 {
                let value = controller.get(item: index, section: testNumber, gridType: .image)
                if value > -1 {
                    iCell.setImage(image: self.symbols[value]);
                    iCell.image.isHidden = false;
                }
            } else {
                iCell.clear()
                
            }
            
        } else if (mode == .fCell) {
            let fCell = cell as! GridFCell
            
            fCell.contentView.layer.cornerRadius = 22.0
            fCell.contentView.layer.backgroundColor = UIColor.clear.cgColor;
            fCell.contentView.layer.masksToBounds = true
            
            let value = controller.get(item: index, section: testNumber, gridType: .distraction)

            if value == 1 {
                fCell.setCharacter(character: "F")
            }
            else
            {
                fCell.setCharacter(character: "E");
            }
        }
        return cell
    }
    
    public func collectionView(_ collectionView: UICollectionView, shouldSelectItemAt indexPath: IndexPath) -> Bool {
        
        if (collectionView.cellForItem(at: indexPath) as? GridImageCell) != nil
        {
            if collectionView.indexPathsForSelectedItems?.count == 3
            {
                return false;
            }
        }
        
        return true;
    }
    
    public func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        
        if let c = collectionView.cellForItem(at: indexPath) as? GridImageCell
        {
            
            let _ = controller.setValue(responseIndex: indexPath.row,

                                questionIndex: testNumber,
                                gridType: .image,
                                time: c.touchTime!,
                                id: responseId)
//            print(value.toString())

            maybeEndTimer?.invalidate();
            maybeEndTimer = Timer.scheduledTimer(withTimeInterval: 2.0, repeats: false, block: { (timer) in
                self.maybeEndTest()
            })
                
            
        }
        else if let c = collectionView.cellForItem(at: indexPath) as? GridFCell
        {
           // c.contentView.layer.backgroundColor = UIColor(red: 191.0/255.0, green: 215.0/255.0, blue: 224.0/255.0, alpha: 1.0).cgColor
            //c.backgroundColor = UIColor(red: 191.0/255.0, green: 215.0/255.0, blue: 224.0/255.0, alpha: 1.0) //UIColor(red: 182.0/255.0, green: 221.0/255.0, blue: 236.0/255.0, alpha: 1.0);
			//UIColor(red:0, green:0.37, blue:0.52, alpha:0.25)
			c.contentView.layer.backgroundColor = UIColor(named: "Primary Selected")!.cgColor
			c.label.textColor = UIColor(named: "Primary")
			if c.label.text == "F" {
                _ = controller.update(fCountSteps: 1, testIndex: testNumber, id: responseId)
            } else if c.label.text == "E"{
                _ = controller.update(eCountSteps: 1, testIndex: testNumber, id: responseId)

            }
			//c.isSelected = true

        }

    }
    
    public func collectionView(_ collectionView: UICollectionView, didDeselectItemAt indexPath: IndexPath)
    {
        if (collectionView.cellForItem(at: indexPath) as? GridImageCell) != nil
        {
            let value = controller.unsetValue(responseIndex: indexPath.row,
                                questionIndex: testNumber,
                                gridType: .image,
                                id: responseId)
			
//			print(value.toString())
			

        }
        else if let c = collectionView.cellForItem(at: indexPath) as? GridFCell
        {
            c.contentView.layer.backgroundColor = UIColor.clear.cgColor
            c.label.textColor = UIColor(named: "Primary")
            if c.label.text == "F" {
                _ = controller.update(fCountSteps: -1, testIndex: testNumber, id: responseId)
            } else if c.label.text == "E"{
                _ = controller.update(eCountSteps: -1, testIndex: testNumber, id: responseId)
                
            }
			//c.isSelected = false

        }
    }

    
    //MARK: Flow layout
    public func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        if mode == .image {
            return CGSize(width: 60, height: IMAGE_HEIGHT)

        } else if mode == .fCell {
            return CGSize(width: 45, height: LETTER_HEIGHT)

        } else {
            return CGSize(width: 1, height: 1)
        }
    }
    
    public func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumInteritemSpacingForSectionAt section: Int) -> CGFloat {
        if mode == .image {
            return 2
            
        } else if mode == .fCell {
            return 4
            
        } else {
            return 0
        }
    }
    
    public func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
        if mode == .image {
            return 1
            
        } else if mode == .fCell {
            return 1
            
        } else {
            return 0
        }
    }
    public func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForSectionAt section: Int) -> UIEdgeInsets {
        if mode == .image {
            return UIEdgeInsets(top: 4, left: 4, bottom: 4, right: 4)
            
        } else if mode == .fCell {
            return UIEdgeInsets(top: 8, left: 10, bottom: 4, right: 10)

            
        } else {
            return .zero
        }
    }
    
}
