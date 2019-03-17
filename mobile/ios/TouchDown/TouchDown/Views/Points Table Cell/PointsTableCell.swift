//
//  PointsTableCell.swift
//  Touchdown
//
//  Created by Thisura on 2/18/18.
//

import UIKit

fileprivate let goldenColor = #colorLiteral(red: 0.9490196078, green: 0.7647058824, blue: 0.06666666667, alpha: 1)

class PointsTableCell: UITableViewCell {

    static let identifier = "PointsTableCell"
    static func dequeue(withTable: UITableView, _ data: TeamPoint) -> PointsTableCell{
        let cell = withTable.dequeueReusableCell(withIdentifier: self.identifier) as! PointsTableCell
        cell.setData(data)
        return cell
    }
    static func register(withTable: UITableView){
        let nib = UINib(nibName: self.identifier, bundle: nil)
        withTable.register(nib, forCellReuseIdentifier: self.identifier)
    }
    
    @IBOutlet private weak var schoolLbl: UILabel!
    @IBOutlet private weak var playedLbl: UILabel!
    @IBOutlet private weak var winsLbl: UILabel!
    @IBOutlet private weak var dLbl: UILabel!
    @IBOutlet private weak var lostLbl: UILabel!
    @IBOutlet private weak var pointsLbl: UILabel!
    
    private func setData(_ data: TeamPoint){
        let t = data.team!
        let p = data.point!
        schoolLbl.text = t.name
        playedLbl.text = p.played.description
        winsLbl.text = p.won.description
        dLbl.text = (p.played - (p.won + p.lost)).description
        lostLbl.text = p.lost.description
        pointsLbl.text = p.points
        
        let lbl = [schoolLbl, playedLbl, winsLbl, dLbl, lostLbl, pointsLbl]
        
        if t.idTeam == "1"{
            // Is Royal
            for l in lbl{
                l?.textColor = goldenColor
            }
        }
        else{
            // Not Royal
            for l in lbl{
                l?.textColor = UIColor.white
            }
        }
    }
    
}
