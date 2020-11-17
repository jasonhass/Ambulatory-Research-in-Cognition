//
// Event.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import Foundation

public class Event<T> {

  public typealias EventHandler = (T) -> ()

	fileprivate var eventHandlers = [Invocable]()

  public func raise(data: T) {
  for handler in self.eventHandlers {
	handler.invoke(data: data)
    }
  }

  public func addHandler<U: AnyObject>(target: U,
									   handler: @escaping (U) -> EventHandler) -> Disposable {
    let wrapper = EventHandlerWrapper(target: target,
                         handler: handler, event: self)
    eventHandlers.append(wrapper)
    return wrapper
  }
}

private protocol Invocable: class {
  func invoke(data: Any)
}


private class EventHandlerWrapper<T: AnyObject, U>
                                  : Invocable, Disposable {
  weak var target: T?
  let handler: (T) -> (U) -> ()
  let event: Event<U>

	init(target: T?, handler: @escaping (T) -> (U) -> (), event: Event<U>) {
    self.target = target
    self.handler = handler
    self.event = event;
  }

  func invoke(data: Any) -> () {
    if let t = target, let data = data as? U{
		handler(t)(data)
    }
  }

  func dispose() {
    event.eventHandlers =
       event.eventHandlers.filter { $0 !== self }
  }
}

public protocol Disposable {
  func dispose()
}
