//
//  LoadingView.swift
//  Touchdown
//
//  Created by Thisura on 3/3/19.
//

import Foundation
import UIKit

class LoadingView: UIView{
    
    func show(_ handler: @escaping () -> ()){
        isHidden = false
        isUserInteractionEnabled = true
        handler()
    }
    
    func hide(_ handler: @escaping () -> ()){
        isHidden = true
        isUserInteractionEnabled = false
        handler()
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        internalInit()
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        internalInit()
    }
    
    private var view: UIView!
    
    private func internalInit(){
        view = loadViewFromNib()
        view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        view.frame = bounds
        addSubview(view)
    }
    
    private func loadViewFromNib() -> UIView{
        let nib = UINib(nibName: "LoadingView", bundle: nil)
        let loadedView = nib.instantiate(withOwner: self, options: nil).first as! UIView
        return loadedView
    }
    
}
