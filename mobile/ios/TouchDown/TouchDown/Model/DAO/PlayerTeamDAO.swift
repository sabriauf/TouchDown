//
//  PlayerTeamDAO.swift
//  Touchdown
//
//  Created by Thisura on 3/6/19.
//

import Foundation
import GRDB

class PlayerTeamDAO{
    
    static func getPlayerTeam(playerId: String, year: Int) -> PlayerTeam?{
        var playerTeam: PlayerTeam? = nil
        // TEAM IS 1
        var sql = "SELECT * FROM " + Constant.TEXT_PLAYER_TEAM_TABLE + " T"
        sql += " WHERE T." + PlayerTeam.PropertyKey.playerId + " = " + playerId
        sql += " AND T." + PlayerTeam.PropertyKey.year + " = " + year.description
        
        dbQueue.inDatabase { (db) in
            do{
                let allPlayerTeamsMatching = try Row.fetchAll(db, sql).map({ (row) -> PlayerTeam in
                    return PlayerTeam(row: row)
                })
                
                playerTeam = allPlayerTeamsMatching.first
            }
            catch(let error){
                print("PlayerTeamDAO.getPlayerTeam() - ", error.localizedDescription)
            }
        }
        
        return playerTeam
    }
    
}
