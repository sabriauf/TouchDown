//
//  Double+GameTimeString.swift
//  Touchdown
//
//  Created by Thisura on 3/6/19.
//

import Foundation

extension Double{
    
    func toGameTimeString() -> String{
        let minutes = Int(self / 60.0)
        let seconds = Int(self.truncatingRemainder(dividingBy: 60.0))
        
        return String(format: "%02d:%02d", minutes, seconds)
    }
    
}
