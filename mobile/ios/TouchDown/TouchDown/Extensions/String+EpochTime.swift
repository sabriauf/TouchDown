//
//  String+EpochTime.swift
//  Touchdown
//
//  Created by Thisura on 2/6/19.
//

import Foundation

extension String{
    
    /**
     Converts the string's value to a Date
     object provided that it's a compatible
     string. Otherwise throws an exception.
     */
    public func toDate() -> Date{
        
        var timeInterval: TimeInterval!
        
        if self.count == 13{
            /// We get data in milliseconds from the server
            /// so we need to divide by 1000 before converting
            /// to a date object.
            
            timeInterval = TimeInterval(exactly: Double(self)! / 1000)
        }
        else{
            timeInterval = TimeInterval(exactly: Double(self)!)
        }
        
        assert(
            count == 13 || count == 10,
            "Cannot convert this string to Date. Value is not UNIX time."
        )
        // TODO
        /*assert(
            self.,
            "String can only contain numbers"
        )*/
        
        assert(
            timeInterval != nil,
            "Unknown error occured while creating TimeInterval object"
        )
        
        let date = Date(
            timeIntervalSince1970: timeInterval!
        )
        
        return date
    }
    
}
