import Alamofire
import ObjectMapper
import AlamofireObjectMapper
import GRDB

class Team: Record, Mappable, ModelProtocol{
    
    var idTeam: String = ""
    var name: String = ""
    var flagUrl: String = ""
    var logoUrl: String = ""
    var year: String = ""
    
    struct PropertyKey{
        static let idTeam = "idTeam"
        static let name = "name"
        static let flagUrl = "flag_url"
        static let logoUrl = "logo_url"
        static let year = "year"
    }
    
    required init?(map: Map) {
        super.init()
    }
    
    func mapping(map: Map) {
        self.idTeam <- map[PropertyKey.idTeam]
        self.name <- map[PropertyKey.name]
        self.flagUrl <- map[PropertyKey.flagUrl]
        self.logoUrl <- map[PropertyKey.logoUrl]
        self.year <- map[PropertyKey.year]
    }
    
    // Database stuff
    
    static func setColumnDefinitions(t: TableDefinition) {
        t.column(PropertyKey.idTeam, Database.ColumnType.text).primaryKey()
        t.column(PropertyKey.name, Database.ColumnType.text)
        t.column(PropertyKey.flagUrl, Database.ColumnType.text)
        t.column(PropertyKey.logoUrl, Database.ColumnType.text)
        t.column(PropertyKey.year, Database.ColumnType.text)
    }
    
    override class var databaseTableName: String{
        return Constant.TEXT_TEAMS_TABLE
    }
    override class var persistenceConflictPolicy: PersistenceConflictPolicy {
        return PersistenceConflictPolicy(insert: .replace, update: .replace)
    }
    
    required init(row: Row) {
        super.init(row: row)
        self.idTeam = row[PropertyKey.idTeam]
        self.name = row[PropertyKey.name]
        self.flagUrl = row[PropertyKey.flagUrl]
        self.logoUrl = row[PropertyKey.logoUrl]
        self.year = row[PropertyKey.year]
    }
    
    override func encode(to container: inout PersistenceContainer) {
        container[PropertyKey.idTeam] = self.idTeam
        container[PropertyKey.name] = self.name
        container[PropertyKey.flagUrl] = self.flagUrl
        container[PropertyKey.logoUrl] = self.logoUrl
        container[PropertyKey.year] = self.year
    }
}



