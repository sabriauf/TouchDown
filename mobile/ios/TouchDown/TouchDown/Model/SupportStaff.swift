//
//  SupportStaff.swift
//  Touchdown
//
//  Created by Thisura on 3/15/19.
//

import GRDB
import Foundation
import ObjectMapper

class SupportStaff: Record, Mappable{
    
    enum Status: Int{
        case ACTIVE = 0
        case UNDEFINED = 5
        
        private func getInternalMap() -> [Int: [String]]{
            return [
                0: ["ACTIVE", "Active"],
            ]
        }
        
        func getServerMapping() -> String{
            return getInternalMap()[self.rawValue]![0]
        }
        
        func getFriendlyMapping() -> String{
            return getInternalMap()[self.rawValue]![1]
        }
        
        static func initFromInteger(_ value: Int) -> Status{
            return Status.init(rawValue: value) ?? .UNDEFINED
        }
        
        static func initFromServerMapping(_ value: String) -> Status{
            let map = Status(rawValue: 5)!.getInternalMap()
            
            if let id = (map.first { (kvp) -> Bool in
                return kvp.value[0] == value
                }?.key){
                return Status(rawValue: id)!
            }
            return .UNDEFINED
        }
    }
    
    var id: String = ""
    var name: String = ""
    var position: String = ""
    var status: Status = .UNDEFINED
    var order: Int = 0
    
    struct PropertyKey{
        static let id = "id"
        static let name = "name"
        static let position = "position"
        static let status = "status"
        static let order = "order"
    }
    
    required init?(map: Map) {
        super.init()
    }
    
    func mapping(map: Map) {
        
        let transform = TransformOf<Status, String>(fromJSON: { (strValue) -> SupportStaff.Status? in
            if let concStr = strValue{
                return Status.initFromServerMapping(concStr)
            }
            return SupportStaff.Status.UNDEFINED
        }) { (matchStatusValue) -> String? in
            if let concMatchStatus = matchStatusValue{
                return concMatchStatus.getServerMapping()
            }
            return Status.UNDEFINED.getServerMapping()
        }
        
        let transformIntString = TransformOf<Int, String>(fromJSON: { (optStr) -> Int? in
            if let concStr = optStr{
                return Int(concStr)
            }
            return -1
        }) { (optInt) -> String? in
            if let concInt = optInt{
                return concInt.description
            }
            return "-1"
        }
        
        id <- map[PropertyKey.id]
        name <- map[PropertyKey.name]
        position <- map[PropertyKey.position]
        status <- (map[PropertyKey.status], transform)
        order <- (map[PropertyKey.order], transformIntString)
    }
    
    // Database stuff
    
    static func setColumnDefinitions(t: TableDefinition){
        t.column(PropertyKey.id, .text).primaryKey()
        t.column(PropertyKey.name, .text)
        t.column(PropertyKey.position, .text)
        t.column(PropertyKey.status, .integer)
        t.column(PropertyKey.order, .text)
    }
    
    override class var databaseTableName: String{
        return Constant.TEXT_SUPPORT_STAFF_TABLE
    }
    override class var persistenceConflictPolicy: PersistenceConflictPolicy {
        return PersistenceConflictPolicy(insert: .replace, update: .replace)
    }
    
    required init(row: Row) {
        super.init(row: row)
        id = row[PropertyKey.id]
        name = row[PropertyKey.name]
        position = row[PropertyKey.position]
        if let s = row[PropertyKey.status] as? Int64{
            let sI = Int(truncatingIfNeeded: s)
            status = Status(rawValue: sI) ?? .UNDEFINED
        }
        else{
            assertionFailure("SOMGTING ALSO WONG")
        }
        order = Int((row[PropertyKey.order] as? String) ?? "0") ?? 0
    }
    
    override func encode(to container: inout PersistenceContainer) {
        container[PropertyKey.id] = id
        container[PropertyKey.name] = name
        container[PropertyKey.position] = position
        container[PropertyKey.status] = status.rawValue
        container[PropertyKey.order] = order
    }
}
