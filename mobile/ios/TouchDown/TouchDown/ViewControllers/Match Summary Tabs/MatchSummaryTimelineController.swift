//
//  MatchTimelineController.swift
//  Touchdown
//
//  Created by Thisura on 3/4/19.
//

import Foundation
import UIKit

class MatchSummaryTimelineController: UIViewController{
    
    static let identifier = "MatchSummaryTimelineController"
    static func getInstance(match: Match) -> MatchSummaryTimelineController{
        let sb = UIStoryboard(name: "MatchSummary", bundle: nil)
        let vc = sb.instantiateViewController(withIdentifier: identifier) as! MatchSummaryTimelineController
        vc.match = match
        return vc
    }
    
    @IBOutlet private weak var loadingView: LoadingView!
    @IBOutlet private weak var tableView: UITableView!
    
    var isFirstTime = true
    var match: Match!
    var scores: [Score] = []
    var gameStartTime: String = ""
    var secondHalfStartTime: String = ""
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        if isFirstTime{
            setupTableView()
            isFirstTime = false
            
            loadingView.show {
                self.tableView.isHidden = true
            }
            loadData()
        }
    }
    
    private func setupTableView(){
        CardTimeLineCell.register(withTable: tableView)
        DetailTimeLineCell.register(withTable: tableView)
        ColorSquareTimeLineCell.register(withTable: tableView)
        EggLetterTimeLineCell.register(withTable: tableView)
        
        tableView.rowHeight = UITableViewAutomaticDimension
        tableView.estimatedRowHeight = 44.0
        
        tableView.delegate = self
        tableView.dataSource = self
    }
    
    private func loadData(){
        
        DispatchQueue.global().async { [weak self] in
            if let s = self{
                let match = s.match!
                s.scores = (ScoreDAO.getScores(forMatch: match) ?? []).reversed()
                s.gameStartTime = MatchDAO.getStartTime(forMatch: match) ?? ""
                s.secondHalfStartTime = MatchDAO.getSecondHalfStartTime(forMatch: match) ?? ""
                
                DispatchQueue.main.async { [weak self] in
                    s.loadingView.hide {
                        s.tableView.isHidden = false
                    }
                    s.tableView.reloadData()
                }
            }
        }
        
    }
}

extension MatchSummaryTimelineController: UITableViewDelegate, UITableViewDataSource{
    
    private func closestPreviousStartTime(to: String) -> String{
        
        if !secondHalfStartTime.isEmpty{
            let shst = Double(secondHalfStartTime.toSeconds())!
            if shst < Double(to.toSeconds())!{
                let returnValue = String(format: "%f", shst - (35.0 * 60.0)) + "000" // to milliseconds
                return returnValue
            }
        }
        return gameStartTime
        
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return scores.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let scoreItem = scores[indexPath.row]
        let referenceTime = self.closestPreviousStartTime(to: scoreItem.time)
        let type = TimeLineCellsType.cellType(forScore: scoreItem)
        
        var cell: UITableViewCell!
        
        switch(type){
            
        case .DetailCell:
            let detailCell = DetailTimeLineCell.dequeue(withTable: tableView)
            detailCell.intitialize(with: scoreItem, matchStartTime: referenceTime)
            cell = detailCell
            
        case .CardCell:
            let cardCell = CardTimeLineCell.dequeue(withTable: tableView)
            cardCell.initialize(with: scoreItem, matchStartTime: referenceTime)
            cell = cardCell
            
        case .EggLetterCell:
            let eggLetterCell = EggLetterTimeLineCell.dequeue(withTable: tableView)
            eggLetterCell.intialize(with: scoreItem, matchStartTime: referenceTime)
            cell = eggLetterCell
            
        case .SquareCell:
            let squareCell = ColorSquareTimeLineCell.dequeue(withTable: tableView)
            squareCell.initialize(with: scoreItem)
            cell = squareCell
            
        case .Unknown:
            print("Error: Unknown Cell Type returned")
            cell = UITableViewCell()
            
        }
        
        cell.selectionStyle = .none
        
        return cell
    }
    
}
