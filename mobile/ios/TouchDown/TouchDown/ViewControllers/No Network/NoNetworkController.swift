//
//  NoNetworkController.swift
//  Touchdown
//
//  Created by Thisura on 3/6/19.
//

import Foundation
import Alamofire

class NoNetworkController: UIViewController{
    
    static let identifier = "NoNetworkController"
    static func present(_ controller: UIViewController){
        let sb = UIStoryboard(name: "Extras", bundle: nil)
        let vc = sb.instantiateViewController(withIdentifier: identifier)
        vc.modalPresentationStyle = .overCurrentContext
        vc.modalTransitionStyle = .crossDissolve
        UIApplication.shared.statusBarStyle = .lightContent
        controller.present(vc, animated: true, completion: nil)
    }
    
    @IBOutlet private weak var spinner: UIActivityIndicatorView!
    @IBOutlet private weak var statusLabel: UILabel!
    
    private var netMan: NetworkReachabilityManager!
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        initialize()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        showSpinnerAndStatus()
        listenForDataConnection()
    }
    
    func initialize(){
        spinner.alpha = 0.0
        statusLabel.alpha = 0.0
    }
    
    func showSpinnerAndStatus(){
        UIView.animate(withDuration: 0.2, animations: { [weak self] in
            self?.spinner?.alpha = 1.0
            self?.statusLabel?.alpha = 1.0
        }) { (completed) in
            // Completion block initialized
            // just in case we might need it in
            // the future
        }
    }
    
    func listenForDataConnection(){
        netMan = NetworkReachabilityManager()
        netMan!.listener = { [weak self] status in
            switch(status){
            case .reachable:
                self?.statusLabel?.text = "Network found!"
                
                DispatchQueue.main.asyncAfter(deadline: .now() + 2, execute: {
                    self?.dismiss(animated: true, completion: {
                        UIApplication.shared.statusBarStyle = .default
                        NotificationCenter.default.post(name: NSNotification.Name.startInitialResync, object: nil)
                    })
                })
                
            case .notReachable:
                self?.statusLabel?.text = "Looking for a network connection..."
            case .unknown:
                self?.statusLabel?.text = "Unstable connection"
            }
        }
        netMan!.startListening()
    }
    
}
