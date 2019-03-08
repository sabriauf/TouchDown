import Alamofire
import ObjectMapper
import AlamofireObjectMapper
import GRDB

class Group: Record, Mappable, ModelProtocol{
    
    var idGroup: String = ""
    var leagueName: String = ""
    var roundName: String = ""
    var groupName: String = ""
    var year: String = ""
    
    struct PropertyKey{
        static let idGroup = "idgroup"
        static let leagueName = "leagueName"
        static let roundName = "roundName"
        static let groupName = "groupName"
        static let year = "year"
    }
    
    required init?(map: Map) {
        super.init()
    }
    
    func mapping(map: Map) {
        self.idGroup <- map[PropertyKey.idGroup]
        self.leagueName <- map[PropertyKey.leagueName]
        self.roundName <- map[PropertyKey.roundName]
        self.groupName <- map[PropertyKey.groupName]
        self.year <- map[PropertyKey.year]
    }
    
    // Database stuff
    
    static func setColumnDefinitions(t: TableDefinition) {
        t.column(PropertyKey.idGroup, Database.ColumnType.text).primaryKey()
        t.column(PropertyKey.leagueName, Database.ColumnType.text)
        t.column(PropertyKey.roundName, Database.ColumnType.text)
        t.column(PropertyKey.groupName, Database.ColumnType.text)
        t.column(PropertyKey.year, Database.ColumnType.text)
    }
    
    override class var databaseTableName: String{
        return Constant.TEXT_GROUPS_TABLE
    }
    override class var persistenceConflictPolicy: PersistenceConflictPolicy {
        return PersistenceConflictPolicy(insert: .replace, update: .replace)
    }
    
    required init(row: Row) {
        super.init(row: row)
        self.idGroup = row[PropertyKey.idGroup]
        self.leagueName = row[PropertyKey.leagueName]
        self.roundName = row[PropertyKey.roundName]
        self.groupName = row[PropertyKey.groupName]
        self.year = row[PropertyKey.year]
    }
    
    override func encode(to container: inout PersistenceContainer) {
        container[PropertyKey.idGroup] = self.idGroup
        container[PropertyKey.leagueName] = self.leagueName
        container[PropertyKey.roundName] = self.roundName
        container[PropertyKey.groupName] = self.groupName
        container[PropertyKey.year] = self.year
    }
}




