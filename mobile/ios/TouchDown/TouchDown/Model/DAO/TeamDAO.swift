//
//  TeamDAO.swift
//  Touchdown
//
//  Created by Thisura on 3/3/19.
//

import Foundation
import GRDB

class TeamDAO{
    
    static func getTeam(withId: String) -> Team?{
        
        var sql = "SELECT * FROM " + Constant.TEXT_TEAMS_TABLE
        sql += " WHERE " + Constant.TEXT_TEAMS_TABLE + "." + Team.PropertyKey.idTeam
        sql += " = " + "'" + withId + "'"
        
        var teamToReturn: Team?
        
        dbQueue.inDatabase { (db) in
            do{
                if let row = try Row.fetchOne(db, sql){
                    teamToReturn = Team(row: row)
                }
            }
            catch(let error){
                print("Error while retrieving team:", error.localizedDescription)
            }
        }
        
        return teamToReturn
    }
    
    static func getOpponentTeamShortName(forMatch match: Match) -> String{
        let team = getTeam(withId: match.getOpponentTeamId())
        return team?.name.filter({ (c) -> Bool in
            let str = String(c)
            return str.uppercased() == str
        }) ?? ""
    }
    
}
