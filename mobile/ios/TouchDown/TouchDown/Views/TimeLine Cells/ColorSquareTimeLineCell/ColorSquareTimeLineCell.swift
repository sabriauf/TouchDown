//
//  ColorSquareTimeLineCell.swift
//  Touchdown
//
//  Created by Thisura on 3/11/18.
//

import UIKit

class ColorSquareTimeLineCell: UITableViewCell{
    
    static let identifier = "ColorSquareTimeLineCell"
    static func dequeue(withTable: UITableView) -> ColorSquareTimeLineCell{
        return withTable.dequeueReusableCell(withIdentifier: self.identifier) as! ColorSquareTimeLineCell
    }
    static func register(withTable: UITableView){
        let nib = UINib(nibName: self.identifier, bundle: nil)
        withTable.register(nib, forCellReuseIdentifier: self.identifier)
    }
    
    private let greenColor: UIColor = #colorLiteral(red: 0.1126797532, green: 0.7577226937, blue: 0, alpha: 1)
    private let redColor: UIColor = #colorLiteral(red: 1, green: 0.1491575837, blue: 0, alpha: 1)
    
    @IBOutlet weak var squareView: UIView!
    @IBOutlet weak var detailLabel: UILabel!
    
    public func initialize(with: Score){
        
        let action = with.action
        
        if(action == .START){
            squareView.backgroundColor = greenColor
            detailLabel.text = "Start"
        }
        else if(action == .HALF_TIME){
            squareView.backgroundColor = redColor
            detailLabel.text = "Half Time"
        }
        else if(action == .SECOND_HALF){
            squareView.backgroundColor = greenColor
            detailLabel.text = "Second Half"
        }
        else if(action == .FULL_TIME){
            squareView.backgroundColor = redColor
            detailLabel.text = "Full Time"
        }
    }
    
}
