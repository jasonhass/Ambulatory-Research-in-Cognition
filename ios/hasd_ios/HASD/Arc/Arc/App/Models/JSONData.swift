//
// JSONData.swift
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.




import Foundation
public extension JSONData {
    func get<T:Codable>() -> T? {
		do{
        guard let data = self.data else {
            return nil
        }
        return try JSONDecoder().decode(T.self, from: data)
		} catch {
			print(error)
		}
        return nil
    }
}
