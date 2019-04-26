//
//  DefaultView.swift
//  Touchdown
//
//  Created by Thisura on 4/15/19.
//

import UIKit
import Foundation

class DefaultView: UIView{
    
    @IBOutlet private weak var messageLabel: UILabel!
    
    func show(with msg: String? = nil){
        messageLabel.text = msg ?? "No matches are live yet"
        self.isHidden = false
    }
    
    func hide(){
        self.isHidden = true
    }
}
