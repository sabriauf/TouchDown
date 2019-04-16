//
//  MatchSummaryDetailRowView.swift
//  Touchdown
//
//  Created by Thisura on 3/4/19.
//

import Foundation
import UIKit

class MatchSummaryDetailRowView: UIView{
    
    @IBOutlet private weak var leftLabel: UILabel!
    @IBOutlet private weak var middleLabel: UILabel!
    @IBOutlet private weak var rightLabel: UILabel!
    
    @discardableResult
    func initialize(left: Int, _ middle: String, _ right: Int) -> MatchSummaryDetailRowView{
        leftLabel.text = left.description
        middleLabel.text = middle
        rightLabel.text = right.description
        
        return self
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
        let nib = UINib(nibName: "MatchSummaryDetailRowView", bundle: nil)
        let loadedView = nib.instantiate(withOwner: self, options: nil).first as! UIView
        return loadedView
    }
    
}
