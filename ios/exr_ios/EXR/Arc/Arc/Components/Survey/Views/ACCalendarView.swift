//
// ACCalendarView.swift
//



import Foundation
public struct ACCalendarStore {
    public var dateRange:ClosedRange<Date>
    public var selectedDateRange:ClosedRange<Date>?
    public var calendar:Calendar = Calendar(identifier: .gregorian)
    public var weekNames:Array<String> = ["S".localized(ACTranslationKey.day_abbrev_sun),
                                          "M".localized(ACTranslationKey.day_abbrev_mon),
                                          "T".localized(ACTranslationKey.day_abbrev_tues),
                                          "W".localized(ACTranslationKey.day_abbrev_weds),
                                          "T".localized(ACTranslationKey.day_abbrev_thurs),
                                          "F".localized(ACTranslationKey.day_abbrev_fri),
                                          "S".localized(ACTranslationKey.day_abbrev_sat)]
    
    init(range:ClosedRange<Date>) {
        self.dateRange = range
    }
}
@IBDesignable
public class ACCalendarView:UIView, SurveyInput {
	public weak var surveyInputDelegate: SurveyInputDelegate?

    private enum CellStyle {
        case none, header, selected, selectedFirst, selectedLast
        
        
        func styleFor(label:UILabel){
            
            label.textColor = .lightGray
            
            switch self {
                
        
            case .header:
                label.font = label.font.boldFont()
                label.textColor = UIColor(named:"Primary Text")
                let _ = label.layer.addBorder(edge: .bottom, color: .lightGray, thickness: 1.0)
               
            case .selected:
                
                label.font = label.font.boldFont()
                label.textColor = UIColor(named:"Primary Text")
                label.backgroundColor = UIColor(named: "Primary Selected")!
                
            case .selectedFirst:
                label.font = label.font.boldFont()

                _ = label.layer.addBorder(edge: .left, color: UIColor(named: "Primary")!, thickness: 1.0)
                label.textColor = UIColor(named:"Primary Text")
                label.backgroundColor = UIColor(named: "Primary Selected")!
                
            case .selectedLast:
                label.font = label.font.boldFont()

                _ = label.layer.addBorder(edge: .right, color: UIColor(named: "Primary")!, thickness: 1.0)
                label.textColor = UIColor(named:"Primary Text")
                label.backgroundColor = UIColor(named: "Primary Selected")!
            default:
                label.textColor = .lightGray

                break
            }
            
            
            label.numberOfLines = 1
            label.textAlignment = .center
            label.translatesAutoresizingMaskIntoConstraints = false
        }
    }
    
    ////////////////////////////////
    //Survey Input Delegate
    ///////////////////////////
    public func getValue() -> QuestionResponse? {
        return AnyResponse(type: .calendar, value: 1)
    }
    
    public func setValue(_ value: QuestionResponse?) {
        guard let v = value?.value as? ACCalendarStore else {return}
        self.set(calendarStore: v)
    }
    
    public var orientation: UIStackView.Alignment = .top
    
    public var didFinishSetup: (() -> ())?
    
    public var tryNext: (() -> ())?
    
    public var didChangeValue: (() -> ())?
    public func isInformational() -> Bool {
        return true
    }
    
    
    //////////////////////////////////
    //Storyboard design attributes
    ////////////////////////////////
    
    @IBInspectable var daysBefore:Int = 0
    @IBInspectable var daysAfter:Int = 0
    
    @IBInspectable var top:CGFloat = 0 {
        didSet {
            content.isLayoutMarginsRelativeArrangement = true
            content.layoutMargins.top = top
        }
    }
    @IBInspectable var left:CGFloat = 0 {
        didSet {
            content.isLayoutMarginsRelativeArrangement = true
            content.layoutMargins.left = left
        }
    }
    @IBInspectable var bottom:CGFloat = 0 {
        didSet {
            content.isLayoutMarginsRelativeArrangement = true
            content.layoutMargins.bottom = bottom
        }
    }
    @IBInspectable var right:CGFloat = 0 {
        didSet {
            content.isLayoutMarginsRelativeArrangement = true
            content.layoutMargins.right = right
        }
    }
    @IBInspectable var spacing:CGFloat = 13 {
        didSet {
            content.spacing = spacing
           
        }
    }
    @IBInspectable var rowHeight:CGFloat = 36
    
    ///////////////////////////////
    //Instance variables and members
    //////////////////////////
    
    var baseFont:UIFont = UIFont(name: "Roboto", size: 18.0)!
    var content:UIStackView
    private var styles:[UIView:CellStyle] = [:]
//    private var cellSize:CGFloat

    private var calendarStore:ACCalendarStore?
    
    
    public var numDays:Int{
        guard let calendarStore = calendarStore else {
            return 0
        }
        let dayCount = calendarStore.dateRange.upperBound.daysSince(date: calendarStore.dateRange.lowerBound)
        
        return dayCount
    }
    
    
    //Initial setup for the collection
    private func setup() {
        content.axis = .vertical
        content.alignment = .fill
        content.distribution = .fillEqually
        content.spacing = spacing
        content.isLayoutMarginsRelativeArrangement = true
        content.translatesAutoresizingMaskIntoConstraints = false
        content.layoutMargins = UIEdgeInsets(top: top, left: left, bottom: bottom, right: right)
//        cellSize = frame.width/7

    }
    override public init(frame: CGRect) {
        content = UIStackView(frame: CGRect(x: 0, y: 0, width: frame.width, height: frame.height))
//        cellSize = frame.width/7
        super.init(frame: frame)
        setup()
        self.constrain(view:content)
		
    }
    
    required public init?(coder aDecoder: NSCoder) {
        content = UIStackView()
//        cellSize = 0
        super.init(coder: aDecoder)

        setup()
        self.constrain(view:content)
    }
    public override func prepareForInterfaceBuilder() {

        super.prepareForInterfaceBuilder()
        content = UIStackView(frame: CGRect(x: 0, y: 0, width: frame.width, height: frame.height))
        setup()
        self.constrain(view:content)
        let range = (Date().addingDays(days: daysBefore) ... Date().addingDays(days: daysAfter))

        set(calendarStore: ACCalendarStore(range: range))
//
        
    }
    override public func layoutSubviews() {
        
        super.layoutSubviews()
        
        

    }
  
    
    public func set(calendarStore:ACCalendarStore?) {
        content.removeSubviews()
        styles = [:]
        self.calendarStore = calendarStore
        layoutCalendar()
        
        
    }
    
    //Will layout the calendar after all settings are made
    public func layoutCalendar() {
        guard let calendarStore = calendarStore else {
            
            return
        }
        let range = calendarStore.dateRange
        let selected = calendarStore.selectedDateRange
        //Get the days and days of the week for the represented time range.
        let days:[(weekDay: Int, day:Int, selected:Bool?)] = getWeekDays(range: range, selected: selected)
        var items:[UIView] = []
        var headerRow:[UIView] = []
        
        
        
        for d in calendarStore.weekNames.reversed() {
            headerRow.insert(createItem(title: d, style: .header), at: 0)
        }
        let view = createRow(views: headerRow)
        view.heightAnchor.constraint(equalToConstant: rowHeight).isActive = true
        content.addArrangedSubview( view)
        //keep a list of all items to be inserted
        var firstSelected = false
        var lastSelected = false
        
        for i in 0 ..< days.count {
            let d = days[i]
            if let selected = d.selected, selected, firstSelected == false {
                firstSelected = true
                items.append(createItem(title: "\(d.day)", style: .selectedFirst))
                continue
            }
            
            if let selected = d.selected,
                selected,
                lastSelected == false,
                i < days.count - 1,
                let nSelected = days[i+1].selected,
                !nSelected {
                
                lastSelected = true
                items.append(createItem(title: "\(d.day)", style: .selectedLast))
                
                continue
            }
            
            if let selected = d.selected, selected{
                firstSelected = true
                items.append(createItem(title: "\(d.day)", style: .selected))
                
                continue
            }
            items.append(createItem(title: "\(d.day)"))

            
        }
        
        //Insert blanks for days that are shown outside of the date range
        //Their spaces will be occupied, but the date will be blank
        if let first = days.first, first.weekDay > 0 {
            for _ in 0 ..< first.weekDay {
                items.insert(createItem(title: ""), at: 0)
            }
        }
        //Same here but for after the time range
        if let last = days.last, last.weekDay < 6 {
            for _ in  last.weekDay + 1 ... 6 {
                items.append(createItem(title: ""))
            }
        }
        
        
        
        let rows = items.count/headerRow.count
        
        
        for row in 0 ..< rows {
            content.addArrangedSubview( createRow(views: Array(items[0 + (7 * row) ... 6 + (7 * row)])))
        }
		surveyInputDelegate?.didFinishSetup()
    }
    
    private func getWeekDays(range:ClosedRange<Date>, selected:ClosedRange<Date>? = nil) -> [(Int, Int, Bool?)]{
        var days:[(Int, Int, Bool?)] = []
        
        for i in 0 ..< numDays {
            let day = range.lowerBound.addingDays(days: i)
            let weekDay = day.weekdayOfMonth()
            let selected = selected?.contains(day)
            // 0 is the weekday index, 1 is the day of the month
            days.append((weekDay.0 - 1, weekDay.1, selected))
            
        }
        return days
    }
    
    private func createItem(title:String, style:CellStyle = .none) -> UIView {
        let label = UILabel()
        label.font = baseFont
        
        label.text = title
        style.styleFor(label: label)
//        styles[label] = style
        
        return label
    }
    
    
    private func createRow(views:[UIView]) -> UIStackView {
        let stack = UIStackView(arrangedSubviews: views)
        stack.alignment = .fill
        stack.distribution = .fillEqually
        stack.axis = .horizontal
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.clipsToBounds = false
        return stack
    }
    
}
