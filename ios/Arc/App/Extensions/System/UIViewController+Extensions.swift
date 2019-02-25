//
// UIViewController+Extensions.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.


import UIKit
public extension UIViewController {
	static public func get<T:UIViewController>(nib:String? = nil, bundle:Bundle? = nil) -> T {
		//For multi-module functionality.
		var _bundle:Bundle? = bundle
		if bundle == nil {
			_bundle = Bundle(for: T.self)

		}
		
        let vc = T(nibName: nib ?? String(describing: self), bundle: _bundle)
        
        return vc
    }
    static public func present<T:UIViewController>(nib:String? = nil, onSetup:((T)->Void)? = nil, onCompletion:(()->Void)? = nil) -> T {
        
        let vc:T = .get()
        
        onSetup?(vc)
        
        let window = UIApplication.shared.keyWindow?.rootViewController
        
        window?.present(vc, animated: true, completion: {
            
            onCompletion?()
            
        })
        
        return vc
    }
    static public func replace<T:UIViewController>(nib:String? = nil, onSetup:((T)->Void)? = nil, onCompletion:((T)->Void)? = nil) -> T {
        
        let vc:T = .get()
        
        onSetup?(vc)
        
        UIApplication.shared.keyWindow?.rootViewController = vc
        UIApplication.shared.keyWindow?.makeKeyAndVisible()
        
        onCompletion?(vc)
        return vc
    }
}
