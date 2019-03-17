//
//  EpochTimeHelper.swift
//  Touchdown
//
//  Created by Thisura on 3/11/18.
//

import Foundation

struct SimpleTimeStruct{
    var minutes: Int = 0
    var seconds: Int = 0
    
    func asString() -> String{
        return String(format: "%d", minutes).zeroInfixed() + ":" + String(format: "%d", seconds).zeroInfixed()
    }
}

class EpochTimeHelper{
    
    private static func safeInt(_ value: Int?) -> Int{
        return value ?? 0
    }
    
    static func interval(matchStart: String, destination: String) -> SimpleTimeStruct{
        
        if matchStart.count < 13 || destination.count < 13{
            return SimpleTimeStruct(minutes: 0, seconds: 0)
        }
        else{
            let start = Date(timeIntervalSince1970: Double(matchStart.toSeconds())!)
            let end = Date(timeIntervalSince1970: Double(destination.toSeconds())!)
            
            let cr: Set<Calendar.Component> = [
                .hour, .minute, .second
            ]
            
            let interval = Calendar.current.dateComponents(cr, from: start, to: end)
            
            return SimpleTimeStruct(
                minutes: (safeInt(interval.hour) * 60) + safeInt(interval.minute),
                seconds: safeInt(interval.second))
        }
    }
    
}
