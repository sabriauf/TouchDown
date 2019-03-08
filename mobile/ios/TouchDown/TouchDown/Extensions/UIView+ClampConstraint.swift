//
//  UIView+ClampConstraint.swift
//  Touchdown
//
//  Created by Thisura on 2/6/19.
//

import Foundation
import UIKit

extension UIView{
    
    /**
     Clamps this view to it's superview
     by creating new top, trailing, bottom and leading
     constraints.
     */
    public func clampConstraintOnSuperview(createWidthHeightConstraints: Bool = false){
        
        assert(self.superview != nil, "Superview cannot be nil here")
        
        let existingWidth = self.frame.width
        let existingHeight = self.frame.height
        
        self.translatesAutoresizingMaskIntoConstraints = false
        
        let to = self.superview!
        var whConstraints: [NSLayoutConstraint] = []
        
        if (createWidthHeightConstraints){
        
            /// We create width and height constraints too
            /// because the view's intrinsic width and height constraints
            /// will be removed when we set
            /// translateAutoresizingMaskIntoConstraints = false
            
            let width = NSLayoutConstraint(
                item: self, attribute: .width, relatedBy: .equal,
                toItem: nil, attribute: .notAnAttribute, multiplier: 1.0, constant: existingWidth)
            let height = NSLayoutConstraint(
                item: self, attribute: .height, relatedBy: .equal,
                toItem: nil, attribute: .notAnAttribute, multiplier: 1.0, constant: existingHeight)
            
            whConstraints.append(contentsOf: [width, height])
        }
        
        let top = NSLayoutConstraint(
            item: self, attribute: .top, relatedBy: .equal,
            toItem: to, attribute: .top, multiplier: 1.0, constant: 0.0)
        let trailing = NSLayoutConstraint(
            item: self, attribute: .trailing, relatedBy: .equal,
            toItem: to, attribute: .trailing, multiplier: 1.0, constant: 0.0)
        let bottom = NSLayoutConstraint(
            item: self, attribute: .bottom, relatedBy: .equal,
            toItem: to, attribute: .bottom, multiplier: 1.0, constant: 0.0)
        let leading = NSLayoutConstraint(
            item: self, attribute: .leading, relatedBy: .equal,
            toItem: to, attribute: .leading, multiplier: 1.0, constant: 0.0)
        
        NSLayoutConstraint.activate([top, trailing, bottom, leading])
        
        if (createWidthHeightConstraints){
            NSLayoutConstraint.activate(whConstraints)
        }
    }
    
}
