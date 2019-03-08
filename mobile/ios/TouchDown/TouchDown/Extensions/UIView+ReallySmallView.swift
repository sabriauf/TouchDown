//
//  UIView+ReallySmallView.swift
//  Touchdown
//
//  Created by Thisura on 2/18/18.
//

extension UIView{
    
    static func getReallySmallView() -> UIView{
        let frame = CGRect(x: 0, y: 0, width: 0, height: 0.01)
        let rsv = UIView(frame: frame)
        rsv.alpha = 0.0
        return rsv
    }
    
}
