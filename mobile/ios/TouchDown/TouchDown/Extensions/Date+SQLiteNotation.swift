//
//  Date+SQLiteNotation.swift
//  Touchdown
//
//  Created by Thisura on 3/4/19.
//

import Foundation

extension Date{
    
    func toSqliteDateTime() -> String{
        let f = DateFormatter()
        f.dateFormat = "yyyy-MM-dd HH:mm:ss"
        return f.string(from: self) + ".000"
    }
    
}
