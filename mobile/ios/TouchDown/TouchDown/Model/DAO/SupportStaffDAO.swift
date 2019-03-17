//
//  SupportStaffDAO.swift
//  Touchdown
//
//  Created by Thisura on 3/15/19.
//

import GRDB
import Foundation

class SupportStaffDAO{
    
    static func getStaffOrderedByOrder() -> [SupportStaff]{
        var arr: [SupportStaff] = []
        var sql = "SELECT * FROM " + Constant.TEXT_SUPPORT_STAFF_TABLE + " S "
        sql += "WHERE S." + SupportStaff.PropertyKey.status + " = " + SupportStaff.Status.ACTIVE.rawValue.description
        sql += " ORDER BY CAST([" + SupportStaff.PropertyKey.order + "] AS INTEGER) ASC"
        
        dbQueue.inDatabase { (db) in
            do{
                arr = try Row.fetchAll(db, sql).map({ (row) -> SupportStaff in
                    return SupportStaff(row: row)
                })
            }
            catch(let error){
                print("SupportStaffDAO.getStaffOrderedByOrder -", error.localizedDescription)
            }
        }
        
        return arr
    }
    
}
