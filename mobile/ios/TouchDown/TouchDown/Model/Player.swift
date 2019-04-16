import Alamofire
import ObjectMapper
import AlamofireObjectMapper
import GRDB

class Player: Record, Mappable, ModelProtocol{
    
    var idPlayer: String = ""
    var name: String = ""
    var imgUrl: String = ""
    var imgUrlNew: String = ""
    var birthDay: String = ""
    var weight: String = ""
    var height: String = ""
    var colors: String = ""
    
    struct PropertyKey{
        static let idPlayer = "idPlayer"
        static let name = "name"
        static let imgUrl = "img_url"
        static let imgUrlNew = "img_new_url"
        static let birthDay = "birthDay"
        static let weight = "weight"
        static let height = "height"
        static let colors = "colors"
    }
    
    required init?(map: Map) {
        super.init()
    }
    
    func mapping(map: Map) {
        self.idPlayer <- map[PropertyKey.idPlayer]
        self.name <- map[PropertyKey.name]
        self.imgUrl <- map[PropertyKey.imgUrl]
        self.imgUrlNew <- map[PropertyKey.imgUrlNew]
        self.birthDay <- map[PropertyKey.birthDay]
        self.weight <- map[PropertyKey.weight]
        self.height <- map[PropertyKey.height]
        self.colors <- map[PropertyKey.colors]
    }
    
    // Database stuff
    
    static func setColumnDefinitions(t: TableDefinition) {
        t.column(PropertyKey.idPlayer, Database.ColumnType.text).primaryKey()
        t.column(PropertyKey.name, Database.ColumnType.text)
        t.column(PropertyKey.imgUrl, Database.ColumnType.text)
        t.column(PropertyKey.imgUrlNew, Database.ColumnType.text)
        t.column(PropertyKey.birthDay, Database.ColumnType.text)
        t.column(PropertyKey.weight, Database.ColumnType.text)
        t.column(PropertyKey.height, Database.ColumnType.text)
        t.column(PropertyKey.colors, Database.ColumnType.text)
    }
    
    override class var databaseTableName: String{
        return Constant.TEXT_PLAYERS_TABLE
    }
    override class var persistenceConflictPolicy: PersistenceConflictPolicy {
        return PersistenceConflictPolicy(insert: .replace, update: .replace)
    }
    
    required init(row: Row) {
        super.init(row: row)
        self.idPlayer = row[PropertyKey.idPlayer]
        self.name = row[PropertyKey.name]
        self.imgUrl = row[PropertyKey.imgUrl]
        self.imgUrlNew = row[PropertyKey.imgUrlNew]
        self.birthDay = row[PropertyKey.birthDay]
        self.weight = row[PropertyKey.weight]
        self.height = row[PropertyKey.height]
        self.colors = row[PropertyKey.colors]
    }
    
    override func encode(to container: inout PersistenceContainer) {
        container[PropertyKey.idPlayer] = self.idPlayer
        container[PropertyKey.name] = self.name
        container[PropertyKey.imgUrl] = self.imgUrl
        container[PropertyKey.imgUrlNew] = self.imgUrlNew
        container[PropertyKey.birthDay] = self.birthDay
        container[PropertyKey.weight] = self.weight
        container[PropertyKey.height] = self.height
        container[PropertyKey.colors] = self.colors
    }
    
    func getImageUrl() -> String{
        return Constant.TEXT_BASE_URL + imgUrl
    }
    
    func getBirthday() -> Date{
        let format1 = "M/d/yyyy"
        let format2 = "MM/dd/yyyy"
        let firstPart = String(birthDay.split(separator: " ").first!)
        let df = DateFormatter()
        
        df.dateFormat = format2
        if let _d = df.date(from: firstPart){
            return _d
        }
        else{
            df.dateFormat = format1
            return df.date(from: firstPart)!
        }
    }
    
    func getAge() -> String{
        let compReq: Set<Calendar.Component> = [.year]
        let compRes = Calendar.current.dateComponents(compReq, from: getBirthday(), to: Date())
        return compRes.year!.description
    }
    
    func getHeight() -> String{
        let h = height.replacingOccurrences(of: ".", with: "'")
        return h + "\""
    }
    
    func getWeight() -> String{
        return String(weight.split(separator: ".").first!) + " kg"
    }
}


