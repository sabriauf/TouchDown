//
//  LiveTableCell.swift
//  Touchdown
//
//  Created by Thisura on 2/18/18.
//

import UIKit

class EggLetterTimeLineCell: UITableViewCell {
    
    static let identifier = "EggLetterTimeLineCell"
    static func dequeue(withTable: UITableView) -> EggLetterTimeLineCell{
        return withTable.dequeueReusableCell(withIdentifier: self.identifier) as! EggLetterTimeLineCell
    }
    static func register(withTable: UITableView){
        let nib = UINib(nibName: self.identifier, bundle: nil)
        withTable.register(nib, forCellReuseIdentifier: self.identifier)
    }
    
    @IBOutlet private weak var collegeLabel: UILabel!
    @IBOutlet private weak var collegeTimeLabel: UILabel!
    @IBOutlet private weak var opponentLabel: UILabel!
    @IBOutlet private weak var opponentTimeLabel: UILabel!
    @IBOutlet private weak var indicatorLabel: UILabel!
    
    public func intialize(with: Score, matchStartTime: String){
        
        let action = with.action
        let details = with.details
        let time = EpochTimeHelper.interval(matchStart: matchStartTime, destination: with.time).asString()
        
        let isOpponentTeam = with.teamId != "1"
        
        let referenceLabel = isOpponentTeam ? opponentLabel! : collegeLabel!
        let refernceTimeLabel = isOpponentTeam ? opponentTimeLabel! : collegeTimeLabel!
        
        let otherLabel = isOpponentTeam ? collegeLabel! : opponentLabel!
        let otherTimeLabel = isOpponentTeam ? collegeTimeLabel! : opponentTimeLabel!
        
        let referenceLabelText = details.isEmpty ? ( isOpponentTeam ? TeamDAO.getTeam(withId: with.teamId)?.name ?? "" : PlayerDAO.getPlayer(withId: with.player)?.name ?? "" ) : details
        
        refernceTimeLabel.text = time
        referenceLabel.text = referenceLabelText
        
        otherTimeLabel.text = ""
        otherLabel.text = ""
        
        if action == .PENALTY_KICK{
            indicatorLabel.text = "P"
        }
        else if action == .TRY{
            indicatorLabel.text = "T"
        }
        else if action == .CONVERSION{
            indicatorLabel.text = "C"
        }
        else if action == .DROP_GOAL{
            indicatorLabel.text = "D"
        }
        
    }
    
}
