//
//  FixtureYearSelectorView.swift
//  Touchdown
//
//  Created by Thisura on 2/6/19.
//

import Foundation
import UIKit

public class FixtureYearSelectorView: UIView{
    
    @IBOutlet public weak var yearLabel: UILabel!
    
    let identifer = "FixtureYearSelectorView"
    
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
