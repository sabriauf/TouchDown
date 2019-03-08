import Alamofire
import ObjectMapper
import AlamofireObjectMapper
import GRDB

class Position: Record, Mappable{
    
    var idPosition: Int = 0
    var posNo: Int = 0
    var posName: String = ""
    
    struct PropertyKey{
        static let idPosition = "idPosition"
        static let posNo = "posNo"
        static let posName = "posName"
    }
    
    required init?(map: Map) {
        super.init()
    }
    
    func mapping(map: Map) {
        let transform = TransformOf<Int, String>(fromJSON: { (optStr) -> Int? in
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
        self.idPosition <- (map[PropertyKey.idPosition], transform)
        self.posNo <- (map[PropertyKey.posNo], transform)
        self.posName <- map[PropertyKey.posName]
    }
    
    // Database stuff
    
    static func setColumnDefinitions(t: TableDefinition){
        t.column(PropertyKey.idPosition, .integer).primaryKey()
        t.column(PropertyKey.posNo, .integer)
        t.column(PropertyKey.posName, .text)
    }
    
    override class var databaseTableName: String{
        return Constant.TEXT_POSITION_TABLE
    }
    override class var persistenceConflictPolicy: PersistenceConflictPolicy {
        return PersistenceConflictPolicy(insert: .replace, update: .replace)
    }
    
    required init(row: Row) {
        super.init(row: row)
        self.idPosition = Int(truncatingIfNeeded: row[PropertyKey.idPosition] as? Int64 ?? -1)
        self.posNo = Int(truncatingIfNeeded: row[PropertyKey.posNo] as? Int64 ?? -1)
        self.posName = row[PropertyKey.posName]
    }
    
    override func encode(to container: inout PersistenceContainer) {
        container[PropertyKey.idPosition] = self.idPosition
        container[PropertyKey.posNo] = self.posNo
        container[PropertyKey.posName] = self.posName
    }
}






