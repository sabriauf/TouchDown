//
//  String+ToSeconds.swift
//  Touchdown
//
//  Created by Thisura on 3/11/18.
//

import Foundation

extension String{
    
    /// Trims the last three zeros at the end of the time string
    /// to allow it to be converted in to a valid UNIX Epoch time value
    /// when converted to a double.
    func toSeconds() -> String{
        if self.count > 3 {
            let range = self.index(self.endIndex, offsetBy: -3)...self.index(self.endIndex, offsetBy: -1)
            var new = self
            new.removeSubrange(range)
            return new
        }
        else{
            return "0"
        }
    }
    
}
