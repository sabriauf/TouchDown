//
//  GroupDAO.swift
//  Touchdown
//
//  Created by Thisura on 3/3/19.
//

import Foundation
import GRDB

class GroupDAO{
    
    static func getGroupInfo(forMatch: Match) -> Group?{
        
        var sql = "SELECT * FROM " + Constant.TEXT_GROUPS_TABLE
        sql += " WHERE " + Constant.TEXT_GROUPS_TABLE + "." + Group.PropertyKey.idGroup
        sql += " = " + "'" + forMatch.idGroup + "'"
        
        var groupToReturn: Group?
        
        dbQueue.inDatabase { (db) in
            do{
                let row = try Row.fetchOne(db, sql)
                if let value = row{
                    groupToReturn = Group(row: value)
                }
            }
            catch(let error){
                print("Error while retrieving group", error.localizedDescription)
            }
        }
        
        return groupToReturn
    }
    
    static func getLeagues() -> [String]{
        var leagues: [String] = []
        var sql = "SELECT DISTINCT G." + Group.PropertyKey.leagueName + " FROM "
        sql += Constant.TEXT_GROUPS_TABLE + " G ORDER BY G." + Group.PropertyKey.idGroup + " ASC"
        
        dbQueue.inDatabase { (db) in
            do{
                leagues = try Row.fetchAll(db, sql).map({ (row) -> String in
                    return (row[Group.PropertyKey.leagueName] as? String) ?? ""
                })
            }
            catch(let error){
                print("GroupDAO.getLeagues() -", error.localizedDescription)
            }
        }
        
        return leagues
    }
    
    static func getRounds(forLeague: String) -> [String]{
        var rounds: [String] = []
        var sql = "SELECT DISTINCT G." + Group.PropertyKey.roundName + " FROM "
        sql += Constant.TEXT_GROUPS_TABLE + " G WHERE G." + Group.PropertyKey.leagueName
        sql += " = '" + forLeague + "'"
        sql += " ORDER BY G." + Group.PropertyKey.idGroup + " ASC"
        
        dbQueue.inDatabase { (db) in
            do{
                rounds = try Row.fetchAll(db, sql).map({ (Row) -> String in
                    return (Row[Group.PropertyKey.roundName] as? String) ?? ""
                })
            }
            catch(let error){
                print("GroupDAO.getRounds() -", error.localizedDescription)
            }
        }
        
        return rounds
    }
    
    static func getGroups(forLeague: String, andRound: String) -> [String]{
        var groups: [String] = []
        var sql = "SELECT DISTINCT G." + Group.PropertyKey.groupName + " FROM "
        sql += Constant.TEXT_GROUPS_TABLE + " G WHERE G." + Group.PropertyKey.leagueName
        sql += " = '" + forLeague + "' AND G." + Group.PropertyKey.roundName
        sql += " = '" + andRound + "'"
        sql += " ORDER BY G." + Group.PropertyKey.idGroup + " ASC"
        
        dbQueue.inDatabase { (db) in
            do{
                groups = try Row.fetchAll(db, sql).map({ (Row) -> String in
                    return (Row[Group.PropertyKey.groupName] as? String) ?? ""
                })
            }
            catch(let error){
                print("GroupDAO.getGroups() -", error.localizedDescription)
            }
        }
        
        return groups
    }
    
    static func getGroup(league: String, round: String, group: String) -> Group?{
        var groupObj: Group?
        var sql = "SELECT * FROM " + Constant.TEXT_GROUPS_TABLE + " G "
        sql += "WHERE G." + Group.PropertyKey.leagueName + " = '" + league + "' "
        sql += "AND G." + Group.PropertyKey.roundName + " = '" + round + "' "
        sql += "AND G." + Group.PropertyKey.groupName + " = '" + group + "' "
        
        dbQueue.inDatabase { (db) in
            do{
                groupObj = try Row.fetchOne(db, sql).map({ (row) -> Group in
                    return Group(row: row)
                })
            }
            catch(let error){
                print("GroupDAO.getGroup() -", error.localizedDescription)
            }
        }
        
        return groupObj
    }
}
