//
// UIViewController+Extensions.swift
//



import UIKit
public extension UIViewController {
	
	static func get<T:UIViewController>(nib:String? = nil, bundle:Bundle? = nil) -> T {
		//For multi-module functionality.
		var _bundle:Bundle? = bundle
		if bundle == nil {
			_bundle = Bundle(for: T.self)

		}
        let vc = T(nibName: nib ?? String(describing: self), bundle: _bundle)
        
        return vc
    }
    
    static func instanceFromType(_ type:UIViewController.Type, nib:String? = nil, bundle:Bundle? = nil) -> UIViewController {
        //For multi-module functionality.
        var _bundle:Bundle? = bundle
        if bundle == nil {
            _bundle = Bundle(for: type.self)
            
        }
        let vc = type.init(nibName: nib ?? String(describing: self), bundle: _bundle)
        
        return vc
    }
    
	static func present<T:UIViewController>(nib:String? = nil, onSetup:((T)->Void)? = nil, onCompletion:(()->Void)? = nil) -> T {
        
        let vc:T = .get()
        
        onSetup?(vc)
        
        let window = UIApplication.shared.keyWindow?.rootViewController
        
        window?.present(vc, animated: true, completion: {
            
            onCompletion?()
            
        })
        
        return vc
    }
    static func replace<T:UIViewController>(nib:String? = nil, onSetup:((T)->Void)? = nil, onCompletion:((T)->Void)? = nil) -> T {
        
        let vc:T = .get()
        
        onSetup?(vc)
        
        UIApplication.shared.keyWindow?.rootViewController = vc
        UIApplication.shared.keyWindow?.makeKeyAndVisible()
        
        onCompletion?(vc)
        return vc
    }
}
