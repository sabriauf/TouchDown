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
    
}
