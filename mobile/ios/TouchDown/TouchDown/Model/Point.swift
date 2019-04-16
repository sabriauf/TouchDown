import Alamofire
import ObjectMapper
import AlamofireObjectMapper
import GRDB

class Point: Record, Mappable{
    
    var idPoints: String = ""
    var teamId: String = ""
    var played: Int = 0
    var won: Int = 0
    var lost: Int = 0
    var points: String = ""
    var groupId: String = ""
    
    struct PropertyKey{
        static let idPoints = "idPoint"
        static let teamId = "teamId"
        static let played = "played"
        static let won = "won"
        static let lost = "lost"
        static let points = "points"
        static let groupId = "idgroup"
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
        
        idPoints <- map[PropertyKey.idPoints]
        teamId <- map[PropertyKey.teamId]
        played <- (map[PropertyKey.played], transform)
        won <- (map[PropertyKey.won], transform)
        lost <- (map[PropertyKey.lost], transform)
        points <- map[PropertyKey.points]
        groupId <- map[PropertyKey.groupId]
    }
    
    // Database stuff
    
    static func setColumnDefinitions(t: TableDefinition) {
        t.column(PropertyKey.idPoints, Database.ColumnType.text).primaryKey()
        t.column(PropertyKey.teamId, Database.ColumnType.text)
        t.column(PropertyKey.played, Database.ColumnType.integer)
        t.column(PropertyKey.won, Database.ColumnType.integer)
        t.column(PropertyKey.lost, Database.ColumnType.integer)
        t.column(PropertyKey.points, Database.ColumnType.text)
        t.column(PropertyKey.groupId, Database.ColumnType.text)
    }
    
    override class var databaseTableName: String{
        return Constant.TEXT_POINTS_TABLE
    }
    override class var persistenceConflictPolicy: PersistenceConflictPolicy {
        return PersistenceConflictPolicy(insert: .replace, update: .replace)
    }
    
    required init(row: Row) {
        super.init(row: row)
        self.idPoints = row[PropertyKey.idPoints]
        self.teamId = row[PropertyKey.teamId]
        self.played = itoi(row[PropertyKey.played])
        self.won = itoi(row[PropertyKey.won])
        self.lost = itoi(row[PropertyKey.lost])
        self.points = row[PropertyKey.points]
        self.groupId = row[PropertyKey.groupId]
    }
    
    /**
     Converts 64 bit integers to swift Int type
     */
    private func itoi(_ val: Int64) -> Int{
        return Int(truncatingIfNeeded: val)
    }
    
    override func encode(to container: inout PersistenceContainer) {
        container[PropertyKey.idPoints] = self.idPoints
        container[PropertyKey.teamId] = self.teamId
        container[PropertyKey.played] = self.played
        container[PropertyKey.won] = self.won
        container[PropertyKey.lost] = self.lost
        container[PropertyKey.points] = self.points
        container[PropertyKey.groupId] = self.groupId
    }
}
