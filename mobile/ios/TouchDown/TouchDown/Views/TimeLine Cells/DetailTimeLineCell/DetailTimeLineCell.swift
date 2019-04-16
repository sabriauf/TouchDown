//
//  DetailTimeLineCell.swift
//  Touchdown
//
//  Created by Thisura on 3/11/18.
//

import UIKit

class DetailTimeLineCell: UITableViewCell {

    static let identifier = "DetailTimeLineCell"
    static func dequeue(withTable: UITableView) -> DetailTimeLineCell{
        return withTable.dequeueReusableCell(withIdentifier: self.identifier) as! DetailTimeLineCell
    }
    static func register(withTable: UITableView){
        let nib = UINib(nibName: self.identifier, bundle: nil)
        withTable.register(nib, forCellReuseIdentifier: self.identifier)
    }
    
    @IBOutlet weak var timeLabel: UILabel!
    @IBOutlet weak var detailLabel: UILabel!
    
    public func intitialize(with: Score, matchStartTime: String){
        
        let details = with.details
        let action = with.action
        let interval = EpochTimeHelper.interval(matchStart: matchStartTime, destination: with.time).asString()
        
        var finalText: String = ""
        
        self.timeLabel.text = interval
        
        if action == .PENALTY{
            if details.isEmpty{
                let team = TeamDAO.getTeam(withId: with.teamId)!
                finalText = "- PENALTY conceded by " + team.name
            }
            else{
                finalText = details
            }
        }
        else if action == .KNOCK_ON{
            if details.isEmpty{
                let team = TeamDAO.getTeam(withId: with.teamId)!
                finalText = "- Knock on conceded by " + team.name
            }
            else{
                finalText = details
            }
        }
        else if action == .SCRUM{
            if details.isEmpty{
                let team = TeamDAO.getTeam(withId: with.teamId)!
                finalText = "- SCRUM conceded by " + team.name
            }
            else{
                finalText = details
            }
        }
        else if action == .MESSAGE{
            finalText = details
        }
        
        self.detailLabel.text = finalText
    }
    
}
