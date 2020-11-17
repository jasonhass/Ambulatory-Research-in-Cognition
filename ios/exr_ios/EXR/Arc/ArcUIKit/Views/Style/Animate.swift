//
// Animate.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import Foundation
import CoreGraphics

public struct Math{
	public static func toRadians(_ number: Double) -> Double {
		return number * .pi / 180
	}
	public static func toDegrees(_ number: Double) -> Double {
		return number * 180 / .pi
	}
	
	public static func lerp<T:FloatingPoint> (a:T, b:T, t:T) -> T{
		
		return (a + t * (b - a))
		
	}
	public static func lerp(a:CGPoint, b:CGPoint, t:CGFloat) -> CGPoint {
		return CGPoint(x: lerp(a: a.x, b: b.x, t: t),
					   y:  lerp(a: a.y, b: b.y, t: t))
	}
	public static func clamp(_ value:Double) -> Double {
		
		return min(1.0, max(0.0, value))
	}
	public static func clamp<T:FloatingPoint>(_ value:T, minValue:T, maxValue:T) -> T {
		
		return min(maxValue, max(minValue, value))
	}
	public enum Curve {
		case none, linear, easeIn, easeOut, easeInOut
		
		func evaluate (currentTime:Double) -> Double {
			let t = Math.clamp(currentTime)
			
			switch self {
			case .none:
				return 1.0
			case .linear:
				return Math.lerp(a: 0.0, b: 1.0, t: t)
			case .easeOut:
				return sin(t * Double.pi * 0.5)

			case .easeIn:
				return  1.0 - cos(t * Double.pi * 0.5)
			
			case .easeInOut:
				
				let sqt = pow(t, 2.0)
			
				return sqt / (2.0 * (sqt - t) + 1.0)
			}
		}
	}
}


public class Animate {
	
	public struct Config {
		public var delay:Double = 0.0
		public var duration:Double = 0.2
		public var curve:Math.Curve = .easeOut
		public var translation:CGPoint?
		public var rotation:CGFloat?
		public var scale:CGSize?
		
		public init() {
			
		}
	}
	
	
	private var _delay:Double = 0.0
	private var _duration:Double = 0.2
	private var _curve:Math.Curve = .easeOut
	private var _progress:Double = 0
	var id:String = UUID().uuidString
	public var time:Double {
		get {
			return max(0.0, (updater?.time ?? 0.0) - (updater?.delay ?? 0.0))
		}
		set {
			updater?.time = newValue
		}
	}
	fileprivate var updater:UpdateLooper?
	
	
	
	
	
	public init() {
		
		
	}
	public func duration(_ value:Double) -> Animate {
        let t = self
		t._duration = value
		return t
	}
	public func delay(_ value:Double) -> Animate {
        let t = self
		t._delay = value
		return t
	}
	public func curve(_ value:Math.Curve) -> Animate {
        let t = self
		t._curve = value
		return t
	}
	
	
	/// Provides a context that passes in an animated time value. This can be used
	/// to perform various rudimentary animations
	/// - Parameter update: <#update description#>
	@discardableResult
	public func run(_ update:@escaping (Double)->Bool) -> Animate {
        let s = self
		s.updater = UpdateLooper()
		s.updater?.id = id
		s.updater?.time = 0
		s.updater?.maxTime = _duration
		s.updater?.curve = _curve
		s.updater?.delay = _delay
		s.updater?.run(update)
		return s
	}
	public func stop(forceEnd:Bool = false){
		guard let updater = updater else {
			return
		}
		if forceEnd {
			updater.time = updater.maxTime + updater.delay
		} else {
			updater.stop()
		}
		
	}
	public func pause() {
		updater?.pause()
	}
	public func resume() {
		updater?.resume()
	}
	
	public struct State {
		var _isValid:(()->Bool)
		func isValid(condition:((TimeInterval)->Bool)) {
			
		}
	}
	fileprivate class UpdateLooper {
		
		var displayLink:CADisplayLink?
		
		var update:((Double)->Bool)?
		var _current:Double = 0.0
		var time:Double = 0
		var delay:Double = 0
		var maxTime:Double = 1.0
		var curve:Math.Curve = .linear
		public var id = UUID().uuidString
		init() {
		}
		func start() {
			//print("started:\(id)")
			displayLink?.invalidate()
			displayLink = nil
			displayLink = CADisplayLink(target: self, selector: #selector(loop))
			displayLink?.add(to: .current, forMode: .common)
		}
		func pause() {
			displayLink?.isPaused = true
		}
		func resume() {
			displayLink?.isPaused = false
		}
		func stop() {
			
			displayLink?.invalidate()
			displayLink = nil
			UIView.animations.removeValue(forKey: id)
			update = nil
			//print("stopped:\(id)")
		}
		public func run(_ update:@escaping (Double)->Bool) {
			
			self.update = update
			
			start()
		}
		@objc private func loop() {
			//print("updating: \(id):\(time)")
			guard let dl = displayLink else {
				
				stop()
				return
			}
			time += dl.targetTimestamp - dl.timestamp
			
			if curve == .none {
				
				if time - delay < 0 {
					return
				}
				
				_current = curve.evaluate(currentTime: (time - delay)/maxTime)
				
				guard update?(_current) == true else {
					stop()
					return
				}
				time = maxTime + delay
			
				
			} else {
				_current = curve.evaluate(currentTime: (time - delay)/maxTime)
				if time - delay < 0 {
					return
				}
				
				guard update?(_current) == true else {
					stop()
					return
				}
				
				
				
			}
			if time - delay >= maxTime {
				
				stop()
			}
			
			
		}
	}
	
}
public extension UIView {
	static var animations:[String:Animate] = [:]
	
	
	@discardableResult func fadeIn(_ config:Animate.Config) -> UIView {
		return fadeIn(duration: config.duration, delay: config.delay)
	}
	
	@discardableResult func fadeIn(duration:Double = 0.5, delay:Double = 0.0) -> UIView {
		
		let view = self
		view.alpha = 0
		let animation = Animate()
		UIView.animations[animation.id] = animation.delay(delay)
			.duration(duration)
			.curve(.easeOut)
			.run({  (time) -> Bool in
				
				
				view.alpha = CGFloat(time)
				
				return true
			})
		return view
	}
	
	@discardableResult func fadeOut(_ config:Animate.Config) -> UIView {
		return fadeOut(duration: config.duration, delay: config.delay)
	}
	
	@discardableResult func fadeOut(duration:Double = 0.5, delay:Double = 0.0)  -> UIView{
		let animation = Animate()
		let view = self

		UIView.animations[animation.id] = animation.delay(delay)
			.duration(duration)
			.curve(.easeOut)
			.run({  (time) -> Bool in
				
				
				view.alpha = CGFloat(1.0 - time)
				
				return true
			})
		return view
	}
	
	@discardableResult func translate(_ config:Animate.Config) -> UIView {
		return translate(duration: config.duration, translation: config.translation ?? CGPoint(x: 0, y: 40.0), delay: config.delay)
		
	}
	
	@discardableResult func translate(duration:Double = 0.5, translation:CGPoint = CGPoint(x: 0, y: 40.0), delay:Double = 0.0) -> UIView {
		let animation = Animate()
		let view = self

		UIView.animations[animation.id] = animation.delay(delay)
			.duration(duration)
			.curve(.easeOut)
			.run({  (time) -> Bool in
				
				let value = CGFloat(1.0 - time)
				
				view.transform = CGAffineTransform.identity.translatedBy(x: translation.x * value, y: translation.y * value)
				
				return true
			})
		return view

	}
}
