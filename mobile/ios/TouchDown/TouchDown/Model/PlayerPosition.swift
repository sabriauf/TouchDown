import Alamofire
import ObjectMapper
import AlamofireObjectMapper
import GRDB

class PlayerPosition: Record, Mappable{
    
    var idPlayerPosition: Int = 0
    var idPlayer: Int = 0
    var idMatch: Int = 0
    var idPosition: Int = 0
    
    struct PropertyKey{
        static let idPlayerPosition = "id_player_position"
        static let idPlayer = "idPlayer"
        static let idMatch = "idMatch"
        static let idPosition = "idPosition"
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
        self.idPlayerPosition <- (map[PropertyKey.idPlayerPosition], transform)
        self.idPlayer <- (map[PropertyKey.idPlayer], transform)
        self.idMatch <- (map[PropertyKey.idMatch], transform)
        self.idPosition <- (map[PropertyKey.idPosition], transform)
    }
    
    // Database stuff
    
    static func setColumnDefinitions(t: TableDefinition){
        t.column(PropertyKey.idPlayerPosition, .integer).primaryKey()
        t.column(PropertyKey.idPlayer, .integer)
        t.column(PropertyKey.idMatch, .integer)
        t.column(PropertyKey.idPosition, .integer)
    }
    
    override class var databaseTableName: String{
        return Constant.TEXT_PLAYER_POSITION_TABLE
    }
    override class var persistenceConflictPolicy: PersistenceConflictPolicy {
        return PersistenceConflictPolicy(insert: .replace, update: .replace)
    }
    
    required init(row: Row) {
        super.init(row: row)
        self.idPlayer = Int(truncatingIfNeeded: row[PropertyKey.idPlayer] as? Int64 ?? -1)
        self.idPosition = Int(truncatingIfNeeded: row[PropertyKey.idPosition] as? Int64 ?? -1)
        self.idMatch = Int(truncatingIfNeeded: row[PropertyKey.idMatch] as? Int64 ?? -1)
    }
    
    override func encode(to container: inout PersistenceContainer) {
        container[PropertyKey.idPlayer] = self.idPlayer
        container[PropertyKey.idPosition] = self.idPosition
        container[PropertyKey.idMatch] = self.idMatch
    }
}





