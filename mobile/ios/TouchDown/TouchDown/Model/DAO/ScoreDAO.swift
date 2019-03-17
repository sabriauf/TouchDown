//
//  ScoreDAO.swift
//  Touchdown
//
//  Created by Thisura on 3/3/19.
//

import Foundation
import GRDB

class ScoreDAO{
    
    static func addScore(_ score: Score) -> Bool{
        var addScoreSuccessful = true
        dbQueue.inDatabase { (db) in
            do{
                // Get the count of scores who's ID match  this score ID
                // If the count is > 0, Then we skip adding it to the database
                var sql = "SELECT COUNT() AS C FROM " + Constant.TEXT_SCORES_TABLE + " S "
                sql += "WHERE S." + Score.PropertyKey.idScore + " = " + score.idScore
                let count = (try Row.fetchOne(db, sql)?["C"] as? Int64) ?? 0
                if count == 0{
                    try score.insert(db)
                }
                else{
                    addScoreSuccessful = false
                }
            }
            catch(let error){
                print("ScoreDAO.addScore - ", error.localizedDescription)
                addScoreSuccessful = false
            }
        }
        return addScoreSuccessful
    }
    
    static func getScores(forMatch: Match, timeAscending: Bool = false) -> [Score]?{
        
        let orderingMethod = timeAscending ? "ASC" : "DESC"
        
        var sql = "SELECT * FROM " + Constant.TEXT_SCORES_TABLE
        sql += " WHERE " + Constant.TEXT_SCORES_TABLE + "." + Score.PropertyKey.matchId
        sql += " = " + "'" + forMatch.idMatch + "'"
        sql += " ORDER BY " + Constant.TEXT_SCORES_TABLE + "." + Score.PropertyKey.time
        sql += " " + orderingMethod
        
        var scoresToReturn: [Score]?
        
        dbQueue.inDatabase { (db) in
            do{
                let rows = try Row.fetchAll(db, sql).map({ (row) -> Score in
                    return Score(row: row)
                })
                scoresToReturn = rows
            }
            catch(let error){
                print("Error while retrieving scores:", error.localizedDescription)
            }
        }
        
        return scoresToReturn
    }
    
    static func getScoreTotal(forMatch match: Match, team: String) -> Int{
        var sql = "SELECT * FROM " + Constant.TEXT_SCORES_TABLE + " S"
        sql += " WHERE S." + Score.PropertyKey.matchId + " = " + match.idMatch
        sql += " AND S." + Score.PropertyKey.teamId + " = " + team
        sql += " ORDER BY S." + Score.PropertyKey.time + " ASC"
        
        var scoreTotal = 0
        
        dbQueue.inDatabase { (db) in
            do{
                let scores = try Row.fetchAll(db, sql).map({ (row) -> Score in
                    return Score(row: row)
                })
                for score in scores{
                    scoreTotal += Int(Double(score.score)!.rounded())
                }
            }
            catch(let error){
                print("ScoreDAO.getScoreTotal() - ", error.localizedDescription)
            }
        }
        
        return scoreTotal
    }
    
    static func getScores(forMatch match: Match, thatAre action: Score.Action, andBelongsTo team: Team? = nil) -> [Score]{
        
        var sql = "SELECT * FROM " + Constant.TEXT_SCORES_TABLE + " S"
        sql += " WHERE S." + Score.PropertyKey.matchId + " = " + match.idMatch
        sql += " AND S." + Score.PropertyKey.action + " = " + action.rawValue.description
        if (team != nil){
            sql += " AND S." + Score.PropertyKey.teamId + " = " + team!.idTeam
        }
        
        var scoresToReturn: [Score] = []
        
        dbQueue.inDatabase { (db) in
            do{
                let scores = try Row.fetchAll(db, sql).map({ (row) -> Score in
                    return Score(row: row)
                })
                scoresToReturn = scores
            }
            catch(let error){
                print("ScoreDAO.getScores_forMatch_thatAre() - ", error.localizedDescription)
            }
        }
        
        return scoresToReturn
    }
    
    static func getScorers(forMatch match: Match, teamId: Int) -> (players: [Player], data: [Int: [Score]]){
        var scores: [Score] = []
        var scorers: [Player] = []
        var scorerData: [Int: [Score]] = [:]
        var sql = "SELECT * FROM " + Constant.TEXT_SCORES_TABLE + " S"
        sql += " WHERE S." + Score.PropertyKey.matchId + " = " + match.idMatch
        sql += " AND S." + Score.PropertyKey.teamId + " = " + teamId.description
        
        dbQueue.inDatabase { (db) in
            do{
                scores = try Row.fetchAll(db, sql).map({ (row) -> Score in
                    return Score(row: row)
                })
            }
            catch(let error){
                print("ScoreDAO.getScorers() - ", error.localizedDescription)
            }
        }
        
        for score in scores{
            let playerId = Int(score.player)!
            let rawScoreValue = Int(Double(score.score)!.rounded())
            if (playerId != 0 && rawScoreValue > 0){
                if scorerData.keys.contains(playerId){
                    var eArr = scorerData[playerId]!
                    eArr.append(score)
                    scorerData[playerId] = eArr
                }
                else{
                    let newPlayer = PlayerDAO.getPlayer(withId: playerId.description)!
                    scorerData[playerId] = [score]
                    scorers.append(newPlayer)
                }
            }
        }
        
        return (players: scorers, data: scorerData)
    }
    
    static func getPenalizedPlayers(forMatch match: Match, teamId: Int) -> (players: [Player], data: [Int: [Score]]){
        var scores: [Score] = []
        var scorers: [Player] = []
        var scorerData: [Int: [Score]] = [:]
        var sql = "SELECT * FROM " + Constant.TEXT_SCORES_TABLE + " S"
        sql += " WHERE S." + Score.PropertyKey.matchId + " = " + match.idMatch
        sql += " AND S." + Score.PropertyKey.teamId + " = " + teamId.description
        
        dbQueue.inDatabase { (db) in
            do{
                scores = try Row.fetchAll(db, sql).map({ (row) -> Score in
                    return Score(row: row)
                })
            }
            catch(let error){
                print("ScoreDAO.getScorers() - ", error.localizedDescription)
            }
        }
        
        for score in scores{
            let playerId = Int(score.player)!
            if (playerId != 0 && (score.action == .RED_CARD || score.action == .YELLOW_CARD)){
                if scorerData.keys.contains(playerId){
                    var eArr = scorerData[playerId]!
                    eArr.append(score)
                    scorerData[playerId] = eArr
                }
                else{
                    let newPlayer = PlayerDAO.getPlayer(withId: playerId.description)!
                    scorerData[playerId] = [score]
                    scorers.append(newPlayer)
                }
            }
        }
        
        return (players: scorers, data: scorerData)
    }
    
    static func deleteScoresWithMatchId(_ matchId: String){
        var sql = "DELETE FROM " + Constant.TEXT_SCORES_TABLE + " "
        sql += " WHERE " + Score.PropertyKey.matchId + " = " + matchId
        
        dbQueue.inDatabase { (db) in
            do{
                try db.execute(sql)
            }
            catch(let error){
                print("ScoreDAO.deleteScoresWithMatchId - ", error.localizedDescription)
            }
        }
    }
    
    static func deleteScoreWithId(_ scoreId: String){
        var sql = "DELETE FROM " + Constant.TEXT_SCORES_TABLE
        sql += " WHERE " + Score.PropertyKey.idScore + " = " + scoreId
        
        dbQueue.inDatabase { (db) in
            do{
                try db.execute(sql)
            }
            catch(let error){
                print("ScoreDAO.deleteScoreWithId - ", error.localizedDescription)
            }
        }
    }
}
