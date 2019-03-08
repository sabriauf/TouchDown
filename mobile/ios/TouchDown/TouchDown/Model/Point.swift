import Alamofire
import ObjectMapper
import AlamofireObjectMapper
import GRDB

class Point: Record, Mappable{
    
    var idPoints: Int = 0
    var teamId: Int = 0
    var played: Int = 0
    var won: Int = 0
    var lost: Int = 0
    var points: Int = 0
    var groupId: Int = 0
    var group: Int = 0
    
    struct PropertyKey{
        static let idPoints = "idpoints"
        static let teamId = "teamId"
        static let played = "played"
        static let won = "won"
        static let lost = "lost"
        static let points = "points"
        static let groupId = "group_id"
        static let group = "group"
    }
    
    required init?(map: Map) {
        super.init()
    }
    
    func mapping(map: Map) {
        idPoints <- map[PropertyKey.idPoints]
        teamId <- map[PropertyKey.teamId]
        played <- map[PropertyKey.played]
        won <- map[PropertyKey.won]
        lost <- map[PropertyKey.lost]
        points <- map[PropertyKey.points]
        groupId <- map[PropertyKey.groupId]
        group <- map[PropertyKey.group]
    }
    
    // Database stuff
    
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
        self.played = row[PropertyKey.played]
        self.won = row[PropertyKey.won]
        self.lost = row[PropertyKey.lost]
        self.points = row[PropertyKey.points]
        self.groupId = row[PropertyKey.groupId]
        self.group = row[PropertyKey.group]
    }
    
    override func encode(to container: inout PersistenceContainer) {
        container[PropertyKey.idPoints] = self.idPoints
        container[PropertyKey.teamId] = self.teamId
        container[PropertyKey.played] = self.played
        container[PropertyKey.won] = self.won
        container[PropertyKey.lost] = self.lost
        container[PropertyKey.points] = self.points
        container[PropertyKey.groupId] = self.groupId
        container[PropertyKey.group] = self.group
    }
}
