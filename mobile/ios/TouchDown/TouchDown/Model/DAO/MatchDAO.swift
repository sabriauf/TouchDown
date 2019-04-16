//
//  MatchDAO.swift
//  Touchdown
//
//  Created by Thisura on 3/3/19.
//

import Foundation
import GRDB

class MatchDAO{
    
    static func getMatchForId(_ id: Int) -> Match?{
        var match: Match? = nil
        var sql = "SELECT * FROM " + Constant.TEXT_MATCHES_TABLE + " M"
        sql += " WHERE M." + Match.PropertyKey.idMatch + " = " + id.description
        
        dbQueue.inDatabase { (db) in
            do{
                match = try Row.fetchOne(db, sql).map({ (row) -> Match in
                    return Match(row: row)
                })
            }
            catch(let error){
                print("MatchDAO.getMatchForId() -", error.localizedDescription)
            }
        }
        
        return match
    }
    
    static func getAll() -> [Match]?{
        var matchesToReturn: [Match]? = nil
        
        dbQueue!.inDatabase { (db) in
            do{
                matchesToReturn = try Match.fetchAll(db)
            }
            catch(let error){
                print("MatchDAO.getAll() - ", error.localizedDescription)
            }
        }
        
        return matchesToReturn
    }
    
    static func getSecondHalfStartTime(forMatch: Match) -> String?{
        
        var sql = "SELECT " + Score.PropertyKey.time + " FROM " + Constant.TEXT_SCORES_TABLE
        sql += " WHERE " + Constant.TEXT_SCORES_TABLE + "." + Score.PropertyKey.matchId
        sql += " = " + "'" + forMatch.idMatch + "'"
        sql += " AND " + Constant.TEXT_SCORES_TABLE + "." + Score.PropertyKey.action
        sql += " = " + "'" + Score.Action.SECOND_HALF.rawValue.description + "'"
        
        var timeToReturn: String?
        
        dbQueue.inDatabase { (db) in
            do{
                let row = try Row.fetchOne(db, sql)
                if let value = row{
                    timeToReturn = value[Score.PropertyKey.time]
                }
            }
            catch(let error){
                print("Error while retrieving match start time", error.localizedDescription)
            }
        }
        
        return timeToReturn
    }
    
    static func getCurrentMatchId() -> Match?{
        
        var sql = "SELECT * FROM " + Constant.TEXT_MATCHES_TABLE
        sql += " WHERE " + Constant.TEXT_MATCHES_TABLE + "." + Match.PropertyKey.status
        sql += " = " + "'" + Match.MatchStatus.FULLTIME.rawValue.description + "'"
        
        var matchToReturn: Match?
        
        dbQueue.inDatabase { (db) in
            do{
                let rows = try Row.fetchAll(db, sql)
                if let value = rows.first{
                    matchToReturn = Match(row: value)
                }
            }
            catch(let error){
                print("Error while retrieving match", error.localizedDescription)
            }
        }
        
        return matchToReturn
    }
    
    static func getStartTime(forMatch: Match) -> String?{
        
        var sql = "SELECT " + Score.PropertyKey.time + " FROM " + Constant.TEXT_SCORES_TABLE
        sql += " WHERE " + Constant.TEXT_SCORES_TABLE + "." + Score.PropertyKey.matchId
        sql += " = " + "'" + forMatch.idMatch + "'"
        sql += " AND " + Constant.TEXT_SCORES_TABLE + "." + Score.PropertyKey.action
        sql += " = " + "'" + Score.Action.START.rawValue.description + "'"
        
        var timeToReturn: String?
        
        dbQueue.inDatabase { (db) in
            do{
                let row = try Row.fetchOne(db, sql)
                if let value = row{
                    timeToReturn = value[Score.PropertyKey.time]
                }
            }
            catch(let error){
                print("Error while retrieving match start time", error.localizedDescription)
            }
        }
        
        return timeToReturn
    }
    
    static func getDisplayMatch() -> Match?{
        let currentMatchStatus = [
            Match.MatchStatus.FIRST_HALF.rawValue,
            Match.MatchStatus.SECOND_HALF.rawValue,
            Match.MatchStatus.HALF_TIME.rawValue
        ]
        
        let matches = getMatchesOnStatus(currentMatchStatus)
        if matches.count > 0{
            return matches.first
        }
        else{
            return getCalendarMatch()
        }
    }
    
    static func getCalendarMatch() -> Match?{
        let today = Date()
        let fromDate = Calendar.current.date(byAdding: .day, value: -5, to: today)!.toSqliteDateTime()
        let toDate = Calendar.current.date(byAdding: .day, value: 3, to: today)!.toSqliteDateTime()
        var sql = "SELECT * FROM " + Constant.TEXT_MATCHES_TABLE + " M"
        sql += " WHERE M." + Match.PropertyKey.date + " > '" + fromDate
        sql += "' AND M." + Match.PropertyKey.date + " < '" + toDate + "'"
         sql += " ORDER BY M." + Match.PropertyKey.date + " DESC"
        
        var match: Match? = nil
        
        dbQueue.inDatabase { (db) in
            do{
                let matches = try Row.fetchAll(db, sql).map({ (row) -> Match in
                    return Match(row: row)
                })
                
                if matches.count > 1{
                    match = matches.first(where: { (m1) -> Bool in
                        return m1.status == .PENDING
                    })
                }
                else{
                    match = matches.first
                }
            }
            catch(let error){
                print("MatchDAO.getCalendarMatch - ", error.localizedDescription)
            }
        }
        
        return match
    }
    
    static func getMatchesOnStatus(_ statuses: [Match.MatchStatus.RawValue]) -> [Match]{
        
        assert(statuses.count > 0, "Cannot perform operation with empty statuses array")
        
        var matches: [Match] = []
        var orOperations: [String] = []
        var sql = "SELECT * FROM " + Constant.TEXT_MATCHES_TABLE + " M"
        sql += " WHERE "
        
        for status in statuses{
            let sqlLine = "M." + Match.PropertyKey.status + " = " + status.description
            orOperations.append(sqlLine)
        }
        
        sql += orOperations.joined(separator: " OR ")
        
        dbQueue.inDatabase { (db) in
            do{
                matches = try Row.fetchAll(db, sql).map({ (row) -> Match in
                    return Match(row: row)
                })
            }
            catch(let error){
                print("MatchDAO.getMatchesOnStatus() - ", error.localizedDescription)
            }
        }
        
        return matches
    }
    
    static func updateMatchStatus(matchId: String, status: Match.MatchStatus){
        var sql = "UPDATE " + Constant.TEXT_MATCHES_TABLE + " "
        sql += "SET " + Match.PropertyKey.status + " = " + status.rawValue.description
        sql += " WHERE " + Match.PropertyKey.idMatch + " = " + matchId
        
        dbQueue.inDatabase { (db) in
            do{
                try db.execute(sql)
            }
            catch(let error){
                print("MatchDAO.updateMatchStatus - ", error.localizedDescription)
            }
        }
    }
}
