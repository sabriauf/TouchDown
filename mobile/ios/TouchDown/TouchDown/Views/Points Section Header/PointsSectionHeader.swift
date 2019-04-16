//
//  PointsSectionHeader.swift
//  Touchdown
//
//  Created by Thisura on 2/18/18.
//

import UIKit

class PointsSectionHeader: UITableViewCell {
    
    static let identifier = "PointsSectionHeader"
    static func dequeue(withTable: UITableView) -> PointsSectionHeader{
        return withTable.dequeueReusableCell(withIdentifier: self.identifier) as! PointsSectionHeader
    }
    static func register(withTable: UITableView){
        let nib = UINib(nibName: self.identifier, bundle: nil)
        withTable.register(nib, forCellReuseIdentifier: self.identifier)
    }
}
