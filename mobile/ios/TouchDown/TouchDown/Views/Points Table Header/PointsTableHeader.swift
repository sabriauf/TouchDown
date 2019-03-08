//
//  PointsTableHeader.swift
//  Touchdown
//
//  Created by Thisura on 2/18/18.
//

import UIKit

class PointsTableHeader: UIView{
    
    let identifer = "PointsTableHeader"
    static func getInstance(withFrame: CGRect) -> PointsTableHeader{
        return PointsTableHeader(frame: withFrame)
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        internalInit()
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        internalInit()
    }
    
    var view: UIView!
    
    private func internalInit(){
        view = loadViewFromNib()
        view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        view.frame = self.bounds
        self.addSubview(view)
    }
    
    private func loadViewFromNib() -> UIView{
        let nib = UINib(nibName: self.identifer, bundle: nil)
        return nib.instantiate(withOwner: self, options: nil).first! as! UIView
    }
    
}
