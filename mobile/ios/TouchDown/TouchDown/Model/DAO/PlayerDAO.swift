//
//  PlayerDAO.swift
//  Touchdown
//
//  Created by Thisura on 3/3/19.
//

import Foundation
import GRDB

class PlayerDAO{
    
    static func getPlayer(withId: String, db: Database? = nil) -> Player?{
        
        var sql = "SELECT * FROM " + Constant.TEXT_PLAYERS_TABLE
        sql += " WHERE " + Constant.TEXT_PLAYERS_TABLE + "." + Player.PropertyKey.idPlayer
        sql += " = " + "'" + withId + "'"
        
        var playerToReturn: Player?
        
        let routine: ((Database) -> ()) = { (db) in
            do{
                if let row = try Row.fetchOne(db, sql){
                    playerToReturn = Player(row: row)
                }
            }
            catch(let error){
                print("Error while retrieving player:", error.localizedDescription)
            }
        }
        
        if db == nil{
            dbQueue.inDatabase { (db) in
                routine(db)
            }
        }
        else{
            routine(db!)
        }
        
        return playerToReturn
    }
    
    static func getPlayersForLastMatch(teamId: Int) -> (lastMatchId: Int, players: [CompositePlayer]){
        var players: [CompositePlayer] = []
        let lastMatchId = getLastMatchId()
        
        players = getPlayersForLastMatch_subRoutine(teamId: teamId, matchId: lastMatchId)
        
        if players.count == 0{
           players = getPlayersForLastMatch_subRoutine(teamId: teamId, matchId: getLastMatchIdWithPlayers())
        }
        
        return (lastMatchId: lastMatchId, players: players)
    }
    
    private static func getPlayersForLastMatch_subRoutine(teamId: Int, matchId: Int) -> [CompositePlayer]{
        
        var compositePlayers: [CompositePlayer] = []
        var innerSql = "SELECT DISTINCT A." + PlayerTeam.PropertyKey.playerId + " FROM "
        innerSql += Constant.TEXT_PLAYER_POSITION_TABLE + " B INNER JOIN "
        innerSql += Constant.TEXT_PLAYER_TEAM_TABLE + " A ON "
        innerSql += "A." + PlayerTeam.PropertyKey.playerId + " = B." + PlayerPosition.PropertyKey.idPlayer
        innerSql += " WHERE B." + PlayerPosition.PropertyKey.idMatch + " = " + matchId.description
        innerSql += " AND A." + PlayerTeam.PropertyKey.teamId + " = " + teamId.description
        
        var outerSql = "SELECT * FROM " + Constant.TEXT_PLAYERS_TABLE + " P, "
        outerSql += Constant.TEXT_PLAYER_POSITION_TABLE + " O "
        outerSql += "WHERE O." + PlayerPosition.PropertyKey.idPlayer + " = "
        outerSql += "P." + PlayerPosition.PropertyKey.idPlayer + " AND "
        outerSql += "O." + PlayerPosition.PropertyKey.idMatch + " = " + matchId.description
        outerSql += " AND P." + Player.PropertyKey.idPlayer + " IN "
        outerSql += "(" + innerSql + ") "
        outerSql += "ORDER BY " + PlayerPosition.PropertyKey.idPosition + " ASC"
        
        // print("🥺 +", outerSql)
        
        dbQueue.inDatabase { (db) in
            do{
                let rows1 = try Row.fetchAll(db, innerSql)
                let rows = try Row.fetchAll(db, outerSql)
                for row in rows{
                    let positionId = Int(truncatingIfNeeded: row[PlayerPosition.PropertyKey.idPosition] as! Int64)
                    
                    var cp = CompositePlayer()
                    cp.position = PositionDAO.getPosition(posId: positionId, db: db)
                    cp.player = Player(row: row)
                    
                    compositePlayers.append(cp)
                }
            }
            catch(let error){
                print("PlayerDAO.getPlayersForLastMatch_subRoutine", error.localizedDescription)
            }
        }
        
        // Todo
        
        return compositePlayers
    }
    
    private static func getLastMatchId() -> Int{
        if let displayMatch = MatchDAO.getDisplayMatch(){
            return Int(displayMatch.idMatch)!
        }
        else{
            return getLastMatchIdWithPlayers()
        }
    }
    
    private static func getLastMatchIdWithPlayers() -> Int{
        var lastMatchId = 0
        var sql = "SELECT MAX(" + PlayerPosition.PropertyKey.idMatch + ") AS _max_"
        sql += " FROM " + Constant.TEXT_PLAYER_POSITION_TABLE
        
        dbQueue.inDatabase { (db) in
            do{
                let value = (try Row.fetchOne(db, sql)?["_max_"] as? Int64) ?? 0
                lastMatchId = Int(truncatingIfNeeded: value)
            }
            catch(let error){
                print("PlayerDAO.getLastMatchIdWithPlayers() -", error.localizedDescription)
            }
        }
        
        return lastMatchId
    }
    
    static func getCompositePlayers(forMatchId matchId: String) -> [CompositePlayer]{
        var players: [CompositePlayer] = []
        var sql = "SELECT * FROM " + Constant.TEXT_PLAYER_POSITION_TABLE + " P"
        sql += " WHERE P." + PlayerPosition.PropertyKey.idMatch + " = " + matchId
        sql += " ORDER BY P." + PlayerPosition.PropertyKey.idPosition + " ASC"
        
        dbQueue.inDatabase { (db) in
            do{
                players = try Row.fetchAll(db, sql).map({ (row) -> CompositePlayer in
                    let playerPosition = PlayerPosition(row: row)
                    let player = PlayerDAO.getPlayer(withId: playerPosition.idPlayer.description, db: db)
                    let position = PositionDAO.getPosition(posId: playerPosition.idPosition, db: db)
                    
                    return CompositePlayer(player: player!, position: position!)
                })
            }
            catch(let error){
                print("PlayerDAO.getCompositePlayers() - ", error.localizedDescription)
            }
        }
        
        return players
    }
    
}
