//
//  SyncNowController.swift
//  Touchdown
//
//  Created by Thisura on 3/6/19.
//

import UIKit
import Foundation
import Alamofire

class SyncNowController: UIViewController{
    
    static let identifier = "SyncNowController"
    static func present(_ controller: UIViewController){
        let sb = UIStoryboard(name: "Extras", bundle: nil)
        let vc = sb.instantiateViewController(withIdentifier: identifier)
        vc.modalTransitionStyle = .crossDissolve
        vc.modalPresentationStyle = .overCurrentContext
        controller.present(vc, animated: true, completion: nil)
    }
    
    @IBOutlet weak var container: UIView!
    @IBOutlet weak var appImage: UIImageView!
    
    let netMan = NetworkReachabilityManager()!
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        container.isHidden = true
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        if netMan.isReachable{
            animateIn()
            //NotificationCenter.default.post(name: NSNotification.Name.startManualResync, object: nil)
        }
        else{
            let alert = UIAlertController(title: "No network connection", message: "A network connection is reuqired to start synchronizing", preferredStyle: .alert)
            let ok = UIAlertAction(title: "OK", style: .cancel) { [weak self] (alert) in
                self?.dismiss(animated: true, completion: nil)
            }
        }
    }
    
    private func animateIn(){
        
    }
    
}
