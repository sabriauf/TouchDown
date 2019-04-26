//
//  AlertBoxHelper.swift
//  Touchdown
//
//  Created by Thisura on 3/20/19.
//

import UIKit
import Foundation

class AlertBoxHelper{
    
    /**
     Shows an alert box with a provided title,
     message and an "OK" button
     */
    static func showAlertBox(from controller: UIViewController, _ title: String, _ msg: String, handler: (() -> ())? = nil){
        let alertController = UIAlertController(title: title, message: msg, preferredStyle: .alert)
        let okAction = UIAlertAction(title: "OK", style: .default)
        alertController.addAction(okAction)
        controller.present(alertController, animated: true) {
            if let h = handler{
                h()
            }
        }
    }
    
}
