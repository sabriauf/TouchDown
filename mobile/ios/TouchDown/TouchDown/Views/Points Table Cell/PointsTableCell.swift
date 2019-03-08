//
//  PointsTableCell.swift
//  Touchdown
//
//  Created by Thisura on 2/18/18.
//

import UIKit

class PointsTableCell: UITableViewCell {

    static let identifier = "PointsTableCell"
    static func dequeue(withTable: UITableView) -> PointsTableCell{
        return withTable.dequeueReusableCell(withIdentifier: self.identifier) as! PointsTableCell
    }
    static func register(withTable: UITableView){
        let nib = UINib(nibName: self.identifier, bundle: nil)
        withTable.register(nib, forCellReuseIdentifier: self.identifier)
    }
    
}
