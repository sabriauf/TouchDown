//
//  Int+ToSeconds.swift
//  Touchdown
//
//  Created by Thisura on 3/15/19.
//

import Foundation

extension Int{
    /// Trims the last three zeros at the end of the time string
    /// to allow it to be converted in to a valid UNIX Epoch time value
    /// when converted to a double.
    func toSeconds() -> Int{
        if self > 999999999 {
            return self / 1000
        }
        else{
            return self
        }
    }
}
