//
// AppNavigationController.swift
//



import UIKit
//Decides what state to put the application in based on available
//information

public protocol AppNavigationController {
	func defaultHelpState() -> UIViewController
	
    //Navigate from welcome to auth
    func defaultAuth() -> State
    
    func defaultAbout() -> State
    
    func defaultContact() -> State
    
    func defaultRescheduleAvailability() -> State
    
    func defaultPrivacy()

	//This function should ideally send you back home.
	func defaultState() -> State
	
	//The previous state
	func previousState() -> State?
	
	//This returns a view controller for an object that conforms to state
	func viewForState(state:State) -> UIViewController
	
	//Consume a state object and move to the desired location in the app
	func navigate(state:State, direction: UIWindow.TransitionOptions.Direction)
	
	//If a test session has begun what shall we do next?
	func nextAvailableSurveyState() -> State?
	
	//This will check to see what location the app should take you next
	//Generally this takes you home if the app is fully configured.
	//(Signed in, You've signed up for notifications, other config)
	func nextAvailableState(runPeriodicBackgroundTask:Bool) -> State
	
	//Just replace the current root view controller
	func navigate(vc:UIViewController, direction: UIWindow.TransitionOptions.Direction)
	
	//Just replace the current root view controller
	func navigate(vc:UIViewController, direction: UIWindow.TransitionOptions.Direction, duration:Double)
	
}


open class BaseAppNavigationController : AppNavigationController {
	public func screenShotApp() -> [URL] {
		return []
	}
	
	
	
    public init() {
        
    }
	public func defaultHelpState() -> UIViewController {
		return ACState.home.viewForState()
	}
	
	private var _previousState:State?
	private var state:State = ACState.home
	public func previousState() -> State? {
		return _previousState
	}
	
	
	public func defaultState() -> State {
		return ACState.home
	}
    
    public func defaultAuth() -> State {
        return ACState.auth
    }

    public func defaultAbout() -> State {
        return ACState.about
    }
    
    public func defaultContact() -> State {
        return ACState.contact
    }
    
    public func defaultRescheduleAvailability() -> State {
        return ACState.rescheduleAvailability
    }
    
    public func defaultPrivacy() {
        print("Must override this")
    }

	public func nextAvailableState(runPeriodicBackgroundTask: Bool) -> State {
		return ACState.home

	}
	
	public func nextAvailableSurveyState() -> State? {
		return ACState.home

	}
	public func viewForState(state:State) -> UIViewController {
		
		self._previousState = self.state
		self.state = state
		let vc = state.viewForState()
		
		
		return vc
	}
	
	
	public func navigate(state:State, direction: UIWindow.TransitionOptions.Direction = .toRight) {
		navigate(vc: viewForState(state: state), direction: direction)
	}
	public func navigate(vc: UIViewController, direction: UIWindow.TransitionOptions.Direction, duration: Double) {
		guard let window = UIApplication.shared.keyWindow else {
			assertionFailure("No Keywindow")
			
			return
		}
		
		guard let _ = window.rootViewController else {
			window.rootViewController = vc
			window.makeKeyAndVisible()
			return
		}
		var options = UIWindow.TransitionOptions(direction: direction, style: .linear)
		options.duration = duration
		let view = UIView()
		view.backgroundColor = .white
		options.background = UIWindow.TransitionOptions.Background.customView(view)
		
		window.setRootViewController(vc, options:options)
	}
	public func navigate(vc: UIViewController, direction: UIWindow.TransitionOptions.Direction) {
		navigate(vc: vc, direction: direction, duration: 0.5)
	}
	
	
	
}



