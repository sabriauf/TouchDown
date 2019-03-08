//
//  MatchSummaryScoreCell.swift
//  Touchdown
//
//  Created by Thisura on 3/4/19.
//

import UIKit

class MatchSummaryScoreCell: UICollectionViewCell {

    static let identifier = "MatchSummaryScoreCell"
    static func register(_ cv: UICollectionView){
        let nib = UINib(nibName: identifier, bundle: nil)
        cv.register(nib, forCellWithReuseIdentifier: identifier)
    }
    static func dequeue(_ cv: UICollectionView, ip: IndexPath, action: Score.Action, count: Int) -> MatchSummaryScoreCell{
        let cell = cv.dequeueReusableCell(withReuseIdentifier: identifier, for: ip) as! MatchSummaryScoreCell
        cell.initialize(action, count: count)
        return cell
    }
    static func proposedSize() -> CGSize{
        return CGSize(width: 50.0, height: 30)
    }
    
    @IBOutlet weak var actionImage: UIImageView!
    @IBOutlet weak var pointCountLabel: UILabel!
    
    private func initialize(_ action: Score.Action, count: Int){
        let imgName = imageForAction(action)
        actionImage.image = UIImage(named: imgName)
        pointCountLabel.text = String(format: "%02d", count)
    }
    
    func imageForAction(_ action: Score.Action) -> String{
        switch(action){
        case .TRY:
            return "score_try"
        case .DROP_GOAL:
            return "score_dropgoal"
        case .PENALTY_KICK:
            return "score_penaltykick"
        case .CONVERSION:
            return "score_conversion"
        case .YELLOW_CARD:
            return "score_yellowcard"
        case .RED_CARD:
            return "score_redcard"
        default:
            return "score_try"
        }
    }

}
