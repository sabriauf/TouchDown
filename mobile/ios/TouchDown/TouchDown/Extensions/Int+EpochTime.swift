//
//  Int+EpochTime.swift
//  Touchdown
//
//  Created by Thisura on 2/6/19.
//

import Foundation

extension Int{
    
    /**
     Converts the int's value to a Date
     */
    public func toDate() -> Date{
        
        let inSeconds = toSeconds()
        let timeInterval = TimeInterval(exactly: Double(inSeconds))
        
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
