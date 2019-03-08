//
//  FixtureViewModelFactory.swift
//  Touchdown
//
//  Created by Thisura on 2/6/19.
//

import Foundation
import GRDB

public class FixtureViewModelFactory{
    
    public static func getViewModel(_ completion: @escaping ((FixtureViewModel) -> ())){
        
        /// Contains the fixtures seperated by
        /// the year
        var data: [String: [FixtureViewModel.FixtureItemDetails]] = [:]
        var years: [String] = []
        
        DispatchQueue.global().async {
            let rawMatches = MatchDAO.getAll()
            if let matches = rawMatches{
                
                for match in matches{
                    let matchYear = getYear(match.date)
                    var fixturesForYear = data[matchYear] ?? []
                    let fixtureItem = FixtureViewModel.FixtureItemDetails()
                    let opTeamId = match.getOpponentTeamId()
                    let team = TeamDAO.getTeam(withId: opTeamId)
                    
                    if !years.contains(matchYear){
                        years.append(matchYear)
                    }
                    
                    fixtureItem.match = match
                    if match.status == .PENDING{
                        fixtureItem.fixtureType = FixtureViewModel.FixtureType.Pending
                        fixtureItem.pendingData = FixtureViewModel.FixturePendingDetails(
                            opCrest: team?.logoUrl ?? "",
                            op: team?.name ?? "",
                            matchDate: match.date,
                            locString: match.venue,
                            locLong: Double(match.longitude)!,
                            locLat: Double(match.latitide)!,
                            lastMatch: match.lastMatch)
                    }
                    else{
                        let result = getMatchResult(match)
                        fixtureItem.fixtureType = FixtureViewModel.FixtureType.Result
                        fixtureItem.resultData = FixtureViewModel.FixtureResultDetails(
                            crestUrl: team?.logoUrl ?? "",
                            op: team?.name ?? "",
                            matchDate: match.date,
                            rcScore: result.rc,
                            opScore: result.op,
                            opi: result.opi,
                            mStatus: match.status.getFriendlyMapping(),
                            mResult: match.result)
                    }
                    
                    fixturesForYear.append(fixtureItem)
                    data[matchYear] = fixturesForYear
                }
            }
            
            years = years.sorted(by: { (year1, year2) -> Bool in
                return Int(year1)! > Int(year2)!
            })
            
            let vm = FixtureViewModel(years: years, data: data)
            completion(vm)
        }
        
    }
    
    private static func getYear(_ date: Date) -> String{
        //let date = posixTime.toDate()
        let cReq: Set<Calendar.Component> = [.year]
        let cRes = Calendar.current.dateComponents(cReq, from: date)
        return String(format: "%d", cRes.year!)
    }
    
    private static func getMatchResult(_ match: Match) -> (rc: Int, op: Int, opi: String){
        let rcScore = ScoreDAO.getScoreTotal(forMatch: match, team: 1.description)
        let opScore = ScoreDAO.getScoreTotal(forMatch: match, team: match.getOpponentTeamId())
        let opi = TeamDAO.getOpponentTeamShortName(forMatch: match)
        return (rc: rcScore, op: opScore, opi: opi)
    }
}
