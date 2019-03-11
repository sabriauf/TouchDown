//
//  CardTimeLineCell.swift
//  Touchdown
//
//  Created by Thisura on 3/11/18.
//

import UIKit

class CardTimeLineCell: UITableViewCell {

    static let identifier = "CardTimeLineCell"
    static func dequeue(withTable: UITableView) -> CardTimeLineCell{
        return withTable.dequeueReusableCell(withIdentifier: self.identifier) as! CardTimeLineCell
    }
    static func register(withTable: UITableView){
        let nib = UINib(nibName: self.identifier, bundle: nil)
        withTable.register(nib, forCellReuseIdentifier: self.identifier)
    }
    
    @IBOutlet private weak var timeLabel: UILabel!
    @IBOutlet private weak var cardImage: UIImageView!
    @IBOutlet private weak var detailsLabel: UILabel!
    
    public func initialize(with: Score, matchStartTime: String){
        
        let isOpposingTeam = with.teamId != "1"
        let action = with.action
        let time = EpochTimeHelper.interval(matchStart: matchStartTime, destination: with.time).asString()
        
        timeLabel.text = time
        
        if(isOpposingTeam){
            if let team = TeamDAO.getTeam(withId: with.teamId){
                detailsLabel.text = "to " + team.name
            }
            else{
                detailsLabel.text = " "
            }
        }
        else{
            if let player = PlayerDAO.getPlayer(withId: with.player){
                detailsLabel.text = "to " + player.name
            }
            else{
                detailsLabel.text = " "
            }
        }
        
        if(action == .YELLOW_CARD){
            cardImage.image = UIImage(named: "score_yellowcard")!
        }
        else{
            cardImage.image = UIImage(named: "score_redcard")!
        }
    }
}
