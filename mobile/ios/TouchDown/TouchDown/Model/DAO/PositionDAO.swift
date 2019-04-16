//
//  PositionDAO.swift
//  Touchdown
//
//  Created by Thisura on 3/6/19.
//

import Foundation
import GRDB

class PositionDAO{
    
    static func getPosition(posId: Int, db _db: Database?) -> Position?{
        var position: Position? = nil
        var sql = "SELECT * FROM " + Constant.TEXT_POSITION_TABLE + " P"
        sql += " WHERE P." + PlayerPosition.PropertyKey.idPosition + " = " + posId.description
        
        if _db == nil{
            dbQueue.inDatabase { (db) in
                position = getPositionFrom(db, sql: sql)
            }
        }
        else{
            let db = _db!
            position = getPositionFrom(db, sql: sql)
        }
        
        return position
    }
    
    private static func getPositionFrom(_ db: Database, sql: String) -> Position?{
        var positionToReturn: Position? = nil
        do{
            let position = try Row.fetchOne(db, sql).map({ (row) -> Position in
                return Position(row: row)
            })
            positionToReturn = position
        }
        catch(let error){
            print("PositionDAO.getPositionFrom() - ", error.localizedDescription)
        }
        return positionToReturn
    }
}
