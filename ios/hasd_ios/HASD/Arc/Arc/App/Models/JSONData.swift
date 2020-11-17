//
// JSONData.swift
//



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
