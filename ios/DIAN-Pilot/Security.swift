//
//  Security.swift
//  DIAN-Pilot
//
//  Created by Philip Hayes on 11/28/16.
//  Copyright © 2016 HappyMedium. All rights reserved.
//

import Foundation
import LocalAuthentication

func auth(reply:@escaping (Bool, Error?) -> Void){
    let reason:String = "Place your finger on the home button to verify your identity";
    var error:NSError?
    let context = LAContext()
    guard context.canEvaluatePolicy(LAPolicy.deviceOwnerAuthenticationWithBiometrics, error: &error) else {
        return
    }

    context.evaluatePolicy(LAPolicy.deviceOwnerAuthenticationWithBiometrics,
                           localizedReason: reason,
                           reply:reply);
}
