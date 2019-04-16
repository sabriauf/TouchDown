import GRDB
import Alamofire
import ObjectMapper
import AlamofireObjectMapper

class MetaDataItem: Record, Mappable{
    
    var id: String = ""
    var metaKey: String = ""
    var metaValue: String = ""
    
    struct PropertyKey{
        static let id = "id"
        static let metaKey = "meta_key"
        static let metaValue = "meta_value"
    }
    
    // Database stuff
    
    static func setColumnDefinitions(t: TableDefinition){
        t.column(PropertyKey.id, .text).primaryKey()
        t.column(PropertyKey.metaKey, .text)
        t.column(PropertyKey.metaValue, .text)
    }
    
    override class var databaseTableName: String{
        return Constant.TEXT_META_DATA_TABLE
    }
    override class var persistenceConflictPolicy: PersistenceConflictPolicy {
        return PersistenceConflictPolicy(insert: .replace, update: .replace)
    }
    
    override init(){
        super.init()
    }
    
    required init?(map: Map) {
        super.init()
    }
    
    func mapping(map: Map) {
        self.id <- map[PropertyKey.id]
        self.metaKey <- map[PropertyKey.metaKey]
        self.metaValue <- map[PropertyKey.metaValue]
    }
    required init(row: Row) {
        super.init(row: row)
        self.id = row[PropertyKey.id]
        self.metaKey = row[PropertyKey.metaKey]
        self.metaValue = row[PropertyKey.metaValue]
    }
    
    override func encode(to container: inout PersistenceContainer) {
        container[PropertyKey.id] = id
        container[PropertyKey.metaKey] = metaKey
        container[PropertyKey.metaValue] = metaValue
    }
}
