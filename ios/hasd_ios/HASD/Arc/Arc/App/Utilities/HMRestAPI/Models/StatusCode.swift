//
// StatusCode.swift
//



import Foundation
/**
 A convenience enum for handling status codes returned from rest apis.
    - attention: Initialize instances using:

            StatusCode.with(response:URLResponse?) -> Self
    - version: 1.0
 */
public enum StatusCode  {
	case unknown
    case code(Int)

    /**
     Create a status code with a url response
        - important: This is for use with HTTP requests only.

        - parameter response: an instance of URLResponse returned from a rest api call.
    */
    static func with(response:URLResponse?) -> Self{
        if let r = response as? HTTPURLResponse {
            return .code(r.statusCode)
        }
        return .unknown
    }

    ///Get the stored value
    var code:Int? {
        switch self {
            case .unknown:
            return nil
            case .code(let c):
            return c
        }
    }
    ///Determine success by the status code
    /// - attention: this iteration only considers values falling within 200 ... 299 as success
    /// - returns: true for any 2xx status code
    var succeeded:Bool {
        if let c = code, (200 ... 299).contains(c) {
            return true
        }
        return false
    }
    ///A localized message for end-user consumption.
    /// - returns: A string from the translation document for the current language
    var failureMessage:String? {
        if let code = code {
            if code == 401 {
                return "Invalid Rater ID or ARC ID".localized(ACTranslationKey.login_error1)
            }
            if code == 409 {
                return "Already enrolled on another device".localized(ACTranslationKey.login_error2)
            }
            if code > 199, code < 300{
                return nil
            }
        }
        return "Sorry, our app is currently experiencing issues. Please try again later.".localized(ACTranslationKey.login_error3)
    }
}
