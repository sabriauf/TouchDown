//
//  MetaDataDAO.swift
//  Touchdown
//
//  Created by Thisura on 3/15/19.
//

import GRDB
import Foundation

class MetaDataDAO{
    
    static func isMetaDataPresent() -> Bool{
        var isMetaPresent = false
        dbQueue.inDatabase { (db) in
            do{
                isMetaPresent = try db.tableExists(Constant.TEXT_META_DATA_TABLE)
            }
            catch(let error){
                print("MetaDataDAO.isMetaDataPresent -", error.localizedDescription)
            }
        }
        return isMetaPresent
    }
    
    static func getMetaData(key: String) -> String?{
        var dataToReturn: String? = nil
        var sql = "SELECT * FROM " + Constant.TEXT_META_DATA_TABLE + " M "
        sql += "WHERE M." + MetaDataItem.PropertyKey.metaKey + " = '" + key + "'"
        
        dbQueue.inDatabase { (db) in
            do{
                let mData = try Row.fetchOne(db, sql).map({ (row) -> MetaDataItem in
                    return MetaDataItem(row: row)
                })
                dataToReturn = mData?.metaValue
            }
            catch(let error){
                print("MetaDataDAO.getMetadata() -", error.localizedDescription)
            }
        }
        
        return dataToReturn
    }
    
}
