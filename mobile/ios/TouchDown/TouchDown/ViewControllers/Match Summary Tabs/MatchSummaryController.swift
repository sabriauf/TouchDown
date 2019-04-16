//
//  MatchSummaryController.swift
//  Touchdown
//
//  Created by Thisura on 3/4/19.
//

import Foundation
import UIKit

class MatchSummaryController: UIViewController{
    
    static let identifier = "MatchSummaryController"
    static func getInstance(match: Match) -> MatchSummaryController{
        let sb = UIStoryboard(name: "MatchSummary", bundle: nil)
        let vc = sb.instantiateViewController(withIdentifier: identifier) as! MatchSummaryController
        vc.match = match
        return vc
    }
    
    @IBOutlet private weak var leagueNameLabel: UILabel!
    @IBOutlet private weak var roundNameLabel: UILabel!
    @IBOutlet private weak var leftSchoolCrest: UIImageView!
    @IBOutlet private weak var rightSchoolCrest: UIImageView!
    @IBOutlet private weak var leftSchoolPoints: UILabel!
    @IBOutlet private weak var rightSchoolPoints: UILabel!
    @IBOutlet private weak var scoresStack: UIStackView!
    @IBOutlet private weak var penaltiesStack: UIStackView!
    @IBOutlet private weak var scorersStack: UIStackView!
    @IBOutlet private weak var penalizedStack: UIStackView!
    
    @IBOutlet private weak var scrollView: UIScrollView!
    @IBOutlet private weak var loadingView: LoadingView!
    
    var match: Match!
    var isFirstTime: Bool = true
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        if isFirstTime{
            loadingView.show {
                self.scrollView.isHidden = false
            }
            setupViews()
            isFirstTime = false
        }
    }
    
    private func setupViews(){
        DispatchQueue.global().async { [weak self] in
            if let s = self{
                
                let match = s.match!
                let team1 = TeamDAO.getTeam(withId: match.teamOne)!
                let team2 = TeamDAO.getTeam(withId: match.teamTwo)!
                let group = GroupDAO.getGroupInfo(forMatch: match)!
                let scores = ScoreDAO.getScores(forMatch: match)!
                let penalized = ScoreDAO.getPenalizedPlayers(forMatch: match, teamId: 1)
                
                let scorers = ScoreDAO.getScorers(forMatch: match, teamId: 1)
                
                var leftScoreTotal = 0
                var rightScoreTotal = 0
                
                var goals = (left: 0, right: 0)
                var tries = (left: 0, right: 0)
                var dropGoals = (left: 0, right: 0)
                var penaltyKicks = (left: 0, right: 0)
                var redCards = (left: 0, right: 0)
                var yellowCards = (left: 0, right: 0)
                
                for score in scores{
                    let pointValue = score.action.pointValue()
                    if score.teamId == team1.idTeam{
                        leftScoreTotal += pointValue
                        
                        switch(score.action){
                        case .CONVERSION:
                            goals.left += 1
                        case .TRY:
                            tries.left += 1
                        case .DROP_GOAL:
                            dropGoals.left += 1
                        case .PENALTY_KICK:
                            penaltyKicks.left += 1
                        case .RED_CARD:
                            redCards.left += 1
                        case .YELLOW_CARD:
                            yellowCards.left += 1
                        default:
                            print("")
                        }
                    }
                    else{
                        rightScoreTotal += pointValue
                        
                        switch(score.action){
                        case .CONVERSION:
                            goals.right += 1
                        case .TRY:
                            tries.right += 1
                        case .DROP_GOAL:
                            dropGoals.right += 1
                        case .PENALTY_KICK:
                            penaltyKicks.right += 1
                        case .RED_CARD:
                            redCards.right += 1
                        case .YELLOW_CARD:
                            yellowCards.right += 1
                        default:
                            print("")
                        }
                    }
                }
                
                // Execute UI tasks in the
                // UI thread
                
                DispatchQueue.main.async {
                    // HEADER STUFF
                    
                    s.leagueNameLabel.text = group.leagueName
                    s.roundNameLabel.text = group.roundName
                    s.leftSchoolCrest.load.request(with: URL(string: team1.logoUrl)!, placeholder: Constant.IMAGE_PLACEHOLDER, onCompletion: { (_, _, _) in
                        // no completion block for now
                    })
                    s.rightSchoolCrest.load.request(with: URL(string: team2.logoUrl)!, placeholder: Constant.IMAGE_PLACEHOLDER, onCompletion: { (_, _, _) in
                        // no completion block for now
                    })
                    s.leftSchoolPoints.text = leftScoreTotal.description
                    s.rightSchoolPoints.text = rightScoreTotal.description
                    
                    // SCORES
                    
                    let sStack = s.scoresStack!
                    let frame = CGRect(x: 0, y: 0, width: sStack.frame.width, height: 30.0)
                    let goalsView = MatchSummaryDetailRowView(frame: frame).initialize(left: goals.left, "GOALS", goals.right)
                    let triesView = MatchSummaryDetailRowView(frame: frame).initialize(left: tries.left - goals.left, "Tries", tries.right - goals.right)
                    let dropGoalsView = MatchSummaryDetailRowView(frame: frame).initialize(left: dropGoals.left, "Drop Goals", dropGoals.right)
                    let penaltyKicksView = MatchSummaryDetailRowView(frame: frame).initialize(left: penaltyKicks.left, "Penalty Kicks", penaltyKicks.right)
                    
                    sStack.addArrangedSubview(goalsView)
                    sStack.addArrangedSubview(triesView)
                    sStack.addArrangedSubview(dropGoalsView)
                    sStack.addArrangedSubview(penaltyKicksView)
                    
                    // PENALTIES
                    
                    let pStack = s.penaltiesStack!
                    let redCardsView = MatchSummaryDetailRowView(frame: frame).initialize(left: redCards.left, "Red Cards", redCards.right)
                    let yellowCardsView = MatchSummaryDetailRowView(frame: frame).initialize(left: yellowCards.left, "Yellow Cards", yellowCards.right)
                    
                    pStack.addArrangedSubview(redCardsView)
                    pStack.addArrangedSubview(yellowCardsView)
                    
                    // SCORERES
                    let srStack = s.scorersStack!
                    var rowSize = MatchSummaryPlayerDetailRowView.proposedSize(containerWidth: srStack.frame.width)
                    
                    for player in scorers.players{
                        let f = CGRect(origin: CGPoint(x: 0, y: 0), size: rowSize)
                        let scores = scorers.data[Int(player.idPlayer)!]!
                        
                        let row = MatchSummaryPlayerDetailRowView(frame: f).initialize(player: player, scores: scores)
                        
                        srStack.addArrangedSubview(row)
                    }
                    
                    // PENALIZED
                    let pdStack = s.penalizedStack!
                    rowSize = MatchSummaryPlayerDetailRowView.proposedSize(containerWidth: pdStack.frame.width)
                    
                    for player in penalized.players{
                        let f = CGRect(origin: CGPoint(x: 0, y: 0), size: rowSize)
                        let scores = penalized.data[Int(player.idPlayer)!]!
                        
                        let row = MatchSummaryPlayerDetailRowView(frame: f).initialize(player: player, scores: scores)
                        
                        pdStack.addArrangedSubview(row)
                    }
                    
                    // DONE
                    // Hide loading view
                    // and show scrollview
                    
                    s.loadingView.hide {
                        s.scrollView.isHidden = false
                    }
                }
            }
        }
    }
    
    private func setupHeader(){
        
    }
    
}
