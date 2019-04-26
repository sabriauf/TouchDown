//
//  File.swift
//  Touchdown
//
//  Created by Thisura on 4/15/19.
//

import UIKit
import Foundation
import ImageLoader

extension Loadable where Base: UIImageView{
    
    public func request(with url: URL?, placeholder: UIImage?, options: [Option] = [], onCompletion: @escaping (UIImage?, Error?, FetchOperation) -> Void){
        
        if let u = url{
            self.request(with: u, placeholder: placeholder, options: options, onCompletion: onCompletion)
        }
    }
    
}
