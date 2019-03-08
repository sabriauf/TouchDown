import Alamofire
import ObjectMapper
import AlamofireObjectMapper
import GRDB

class PlayerTeam: Record, Mappable, ModelProtocol{
    
    var playerTeamId: Int!
    var teamId: String = ""
    var playerId: String = ""
    var year: String = ""
    var colors: String = ""
    
    struct PropertyKey{
        static let playerTeamId = "playerteam"
        static let teamId = "id_team"
        static let playerId = "id_player"
        static let year = "year"
        static let colors = "colors"
    }
    
    required init?(map: Map) {
        super.init()
    }
    
    func mapping(map: Map) {
        playerTeamId <- map[PropertyKey.playerTeamId]
        teamId <- map[PropertyKey.teamId]
        playerId <- map[PropertyKey.playerId]
        year <- map[PropertyKey.year]
        colors <- map[PropertyKey.colors]
    }
    
    // Database stuff
    
    static func setColumnDefinitions(t: TableDefinition) {
        t.autoIncrementedPrimaryKey(PropertyKey.playerTeamId)
        t.column(PropertyKey.teamId, Database.ColumnType.text)
        t.column(PropertyKey.playerId, Database.ColumnType.text)
        t.column(PropertyKey.year, Database.ColumnType.text)
        t.column(PropertyKey.colors, Database.ColumnType.text)
    }
    
    override class var databaseTableName: String{
        return Constant.TEXT_PLAYER_TEAM_TABLE
    }
    override class var persistenceConflictPolicy: PersistenceConflictPolicy {
        return PersistenceConflictPolicy(insert: .replace, update: .replace)
    }
    
    required init(row: Row) {
        super.init(row: row)
        playerTeamId = row[PropertyKey.playerTeamId]
        teamId = row[PropertyKey.teamId]
        playerId = row[PropertyKey.playerId]
        year = row[PropertyKey.year]
        colors = row[PropertyKey.colors]
    }
    
    override func encode(to container: inout PersistenceContainer) {
        container[PropertyKey.playerTeamId] = playerTeamId
        container[PropertyKey.teamId] = teamId
        container[PropertyKey.playerId] = playerId
        container[PropertyKey.year] = year
        container[PropertyKey.colors] = colors
    }
}


