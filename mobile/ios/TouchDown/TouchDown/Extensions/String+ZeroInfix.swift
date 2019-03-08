//
//  String+ZeroInfix.swift
//  Touchdown
//
//  Created by Thisura on 3/11/18.
//

import Foundation

extension String{
    func zeroInfixed() -> String{
        if self.count == 1{
            var str = self
            str.insert("0", at: str.startIndex)
            return str
        }
        else{
            return self
        }
    }
}
