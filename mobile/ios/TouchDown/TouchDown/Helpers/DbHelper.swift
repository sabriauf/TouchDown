//
//  DbHelper.swift
//  Touchdown
//
//  Created by Thisura on 3/11/18.
//

import GRDB
import Foundation

var dbQueue: DatabaseQueue!

class DbHelper{
    
    static private var dbPath: String!
    
    static private func setDbPath(){
        let fm = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
        let path = fm.appendingPathComponent("db.sql").path
        dbPath = path
    }
    
    static func initialize(){
        self.setDbPath()
        
        do{
            dbQueue = try DatabaseQueue(path: self.dbPath)
        }
        catch(let error){
            print("Error at initializing queue:", error.localizedDescription)
        }
        
        var migrator = DatabaseMigrator()
        
        // Database Migrator V1
        migrator.registerMigration("ver1") { (db) in
            do{
                
                try db.create(table: Constant.TEXT_MATCHES_TABLE, body: { (t) in
                    Match.setColumnDefinitions(t: t)
                })
                
                try db.create(table: Constant.TEXT_SCORES_TABLE, body: { (t) in
                    Score.setColumnDefinitions(t: t)
                })
                
                try db.create(table: Constant.TEXT_GROUPS_TABLE, body: { (t) in
                    Group.setColumnDefinitions(t: t)
                })
                
                try db.create(table: Constant.TEXT_TEAMS_TABLE, body: { (t) in
                    Team.setColumnDefinitions(t: t)
                })
                
                try db.create(table: Constant.TEXT_PLAYERS_TABLE, body: { (t) in
                    Player.setColumnDefinitions(t: t)
                })
                
                try db.create(table: Constant.TEXT_PLAYER_TEAM_TABLE, body: { (t) in
                    PlayerTeam.setColumnDefinitions(t: t)
                })
                
                try db.create(table: Constant.TEXT_PLAYER_POSITION_TABLE, body: { (t) in
                    PlayerPosition.setColumnDefinitions(t: t)
                })
                
                try db.create(table: Constant.TEXT_POSITION_TABLE, body: { (t) in
                    Position.setColumnDefinitions(t: t)
                })
            }
            catch(let error){
                print("Error at registering migrator v1:", error.localizedDescription)
            }
        }
        
        // Database migrator V2
        migrator.registerMigration("ver2") { (db) in
            do{
                try db.create(table: Constant.TEXT_META_DATA_TABLE, body: { (t) in
                    MetaDataItem.setColumnDefinitions(t: t)
                })
                
                try db.create(table: Constant.TEXT_SUPPORT_STAFF_TABLE, body: { (t) in
                    SupportStaff.setColumnDefinitions(t: t)
                })
                
                try db.create(table: Constant.TEXT_POINTS_TABLE, body: { (t) in
                    Point.setColumnDefinitions(t: t)
                })
            }
            catch(let error){
                print("Error at registering migrator v2: ", error.localizedDescription)
            }
        }
        
        do{
            try migrator.migrate(dbQueue)
        }
        catch(let error){
            print("Error at migrating", error.localizedDescription)
        }
    }
    
    static func clearDb(){
        let tables = Constant.ALL_TABLES()
        for table in tables{
            deleteContentOfTable(table)
        }
    }
    
    private static func deleteContentOfTable(_ tableName: String){
        let sql = "DELETE FROM " + tableName + " WHERE 1 = 1"
        dbQueue.inDatabase { (db) in
            do{
                try db.execute(sql)
            }
            catch(let error){
                print("DbHelper.deleteContentOfTable -", error.localizedDescription)
            }
        }
    }
    
    static func saveToDb(serverResponse: ServerResponseSync){
        
        dbQueue.inDatabase { (db) in
          
            if let scores = serverResponse.scores{
                for score in scores{
                    do {
                        try score.insert(db)
                    }
                    catch(let error){
                        print("Error while inserting score:", error.localizedDescription)
                    }
                }
            }
            
            if let matches = serverResponse.matches{
                for match in matches{
                    do{
                        try match.insert(db)
                    }
                    catch(let error){
                        print("Error while inserting match:", error.localizedDescription)
                    }
                }
            }
            
            if let groups = serverResponse.groups{
                for group in groups{
                    do{
                        try group.insert(db)
                    }
                    catch(let error){
                        print("Error while inserting group:", error.localizedDescription)
                    }
                }
            }
            
            if let teams = serverResponse.teams{
                for team in teams{
                    do{
                        try team.insert(db)
                    }
                    catch(let error){
                        print("Error while inserting match:", error.localizedDescription)
                    }
                }
            }
            
            if let players = serverResponse.players{
                for player in players{
                    do{
                        try player.insert(db)
                    }
                    catch(let error){
                        print("Error while inserting player:", error.localizedDescription)
                    }
                }
            }
            
            if let playerTeams = serverResponse.playerTeams{
                for playerTeam in playerTeams{
                    do{
                        try playerTeam.insert(db)
                    }
                    catch(let error){
                        print("Error while inserting playerTeam", error.localizedDescription)
                    }
                }
            }
            
            if let playerPositions = serverResponse.playerPosition{
                for playerPosition in playerPositions{
                    do{
                        try playerPosition.insert(db)
                    }
                    catch(let error){
                        print("Error while inserting playerPosition", error.localizedDescription)
                    }
                }
            }
            
            if let positions = serverResponse.positions{
                for position in positions{
                    do{
                        try position.insert(db)
                    }
                    catch(let error){
                        print("Error while inserting position", error.localizedDescription)
                    }
                }
            }
            
            if let metaData = serverResponse.metaData{
                for metaDataItem in metaData{
                    do{
                        try metaDataItem.insert(db)
                    }
                    catch(let error){
                        print("Error while inserting meta data", error.localizedDescription)
                    }
                }
            }
            
            if let staff = serverResponse.staff{
                for staffItem in staff{
                    do{
                        try staffItem.insert(db)
                    }
                    catch(let error){
                        print("Error while inserting staff", error.localizedDescription)
                    }
                }
            }
            
            if let points = serverResponse.points{
                for point in points{
                    do{
                        try point.insert(db)
                    }
                    catch(let error){
                        print("Error while inserting point", error.localizedDescription)
                    }
                }
            }
            
        // End of method - saveToDb()
        }
    }
}

