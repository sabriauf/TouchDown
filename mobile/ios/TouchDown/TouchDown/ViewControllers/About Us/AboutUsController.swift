//
//  AboutUsController.swift
//  Touchdown
//
//  Created by Thisura on 3/6/19.
//

import Foundation

class AboutUsController: UIViewController{
    
    static let identifier = "AboutUsController"
    static func presentFrom(_ controller: UIViewController){
        let sb = UIStoryboard(name: "Extras", bundle: nil)
        let vc = sb.instantiateViewController(withIdentifier: identifier)
        vc.modalTransitionStyle = .crossDissolve
        vc.modalPresentationStyle = .overCurrentContext
        controller.present(vc, animated: true, completion: nil)
    }
    
    @IBOutlet private weak var containerView: UIView!
    
    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        super.touchesEnded(touches, with: event)
        if let touch = touches.first{
            let pot = touch.location(in: view)
            if !containerView.frame.contains(pot){
                dismiss(animated: true, completion: nil)
            }
        }
    }
    
}
