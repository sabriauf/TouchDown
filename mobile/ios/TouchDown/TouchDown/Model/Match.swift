import Alamofire
import ObjectMapper
import AlamofireObjectMapper
import GRDB

class Match: Record, Mappable, ModelProtocol{
    
    /*public enum Status {
        PENDING, DONE, FULL_TIME, CALLED_OFF, CANCELED, HALF_TIME, FIRST_HALF, SECOND_HALF;
        
        public String toStringValue() {
        switch (this) {
        case PENDING:
        case CANCELED:
        return this.name();
        case DONE:
        case FULL_TIME:
        return "Full Time";
        case CALLED_OFF:
        return "Called Off";
        case HALF_TIME:
        return "Half Time";
        case FIRST_HALF:
        return "First Half";
        case SECOND_HALF:
        return "Second Half";
        }
        return "";
        }
     PENDING, DONE, FULL_TIME, CALLED_OFF, CANCELED, HALF_TIME, FIRST_HALF, SECOND_HALF
    }*/
    
    enum MatchStatus: Int{
        case PENDING = 0
        case CANCELED = 1
        case DONE = 2
        case FULLTIME = 3
        case CALLED_OFF = 4
        case HALF_TIME = 5
        case FIRST_HALF = 6
        case SECOND_HALF = 7
        case UNDEFINED = 8
        
        private func getInternalMap() -> [Int: [String]]{
            return [
                0: ["PENDING", "Pending"],
                1: ["CANCELED", "Canceled"],
                2: ["DONE", "Done"],
                3: ["FULL_TIME", "Full Time"],
                4: ["CALLED_OFF", "Called Off"],
                5: ["HALF_TIME", "Half Time"],
                6: ["FIRST_HALF", "First Half"],
                7: ["SECOND_HALF", "Second Half"],
                8: ["UNDEFINED", "Undefined"]
            ]
        }
        
        func getServerMapping() -> String{
            return getInternalMap()[self.rawValue]![0]
        }
        
        func getFriendlyMapping() -> String{
            return getInternalMap()[self.rawValue]![1]
        }
        
        static func initFromInteger(_ value: Int) -> MatchStatus{
            return MatchStatus.init(rawValue: value) ?? .UNDEFINED
        }
        
        static func initFromServerMapping(_ value: String) -> MatchStatus{
            let map = MatchStatus(rawValue: 8)!.getInternalMap()
            
            if let id = (map.first { (kvp) -> Bool in
                return kvp.value[0] == value
            }?.key){
                return MatchStatus(rawValue: id)!
            }
            return .UNDEFINED
        }
    }
    
    var idMatch: String = ""
    var teamOne: String = ""
    var teamTwo: String = ""
    var venue: String = ""
    var date: Date!
    var status: MatchStatus = .UNDEFINED
    var result: String = ""
    var lastMatch: String = ""
    var longitude: String = ""
    var latitide: String = ""
    var idGroup: String = ""
    
    struct PropertyKey{
        static let idMatch = "idmatch"
        static let teamOne = "teamOne"
        static let teamTwo = "teamTwo"
        static let venue = "venue"
        static let date = "date"
        static let status = "status"
        static let result = "result"
        static let lastMatch = "lastMatch"
        static let longitude = "longitude"
        static let latitude = "latitude"
        static let idGroup = "idgroup"
    }
    
    required init?(map: Map) {
        super.init()
    }
    
    func mapping(map: Map) {
        
        let transform = TransformOf<MatchStatus, String>(fromJSON: { (strValue) -> Match.MatchStatus? in
            if let concStr = strValue{
                return MatchStatus.initFromServerMapping(concStr)
            }
            return Match.MatchStatus.UNDEFINED
        }) { (matchStatusValue) -> String? in
            if let concMatchStatus = matchStatusValue{
                return concMatchStatus.getServerMapping()
            }
            return MatchStatus.UNDEFINED.getServerMapping()
        }
        
        let dateTransform = TransformOf<Date, String>(fromJSON: { (optDateString) -> Date? in
            if let concDateValue = optDateString{
                let realDateValue = Double(concDateValue)! / 1000
                return Date(timeIntervalSince1970: realDateValue)
            }
            return Date()
        }) { (optDateValue) -> String? in
            if let concDateValue = optDateValue{
                let value = concDateValue.timeIntervalSince1970 * 1000
                return value.description
            }
            return ""
        }
        
        idMatch <- map[PropertyKey.idMatch]
        teamOne <- map[PropertyKey.teamOne]
        teamTwo <- map[PropertyKey.teamTwo]
        venue <- map[PropertyKey.venue]
        date <- (map[PropertyKey.date], dateTransform)
        status <- (map[PropertyKey.status], transform)
        result <- map[PropertyKey.result]
        lastMatch <- map[PropertyKey.lastMatch]
        longitude <- map[PropertyKey.longitude]
        latitide <- map[PropertyKey.latitude]
        idGroup <- map[PropertyKey.idGroup]
    }
    
    /// Database stuff
    
    static func setColumnDefinitions(t: TableDefinition) {
        t.column(PropertyKey.idMatch, Database.ColumnType.text).primaryKey()
        t.column(PropertyKey.teamOne, Database.ColumnType.text)
        t.column(PropertyKey.teamTwo, Database.ColumnType.text)
        t.column(PropertyKey.venue, Database.ColumnType.text)
        t.column(PropertyKey.date, Database.ColumnType.datetime)
        t.column(PropertyKey.status, Database.ColumnType.integer)
        t.column(PropertyKey.result, Database.ColumnType.text)
        t.column(PropertyKey.lastMatch, Database.ColumnType.text)
        t.column(PropertyKey.longitude, Database.ColumnType.text)
        t.column(PropertyKey.latitude, Database.ColumnType.text)
        t.column(PropertyKey.idGroup, Database.ColumnType.text)
    }
    
    override class var databaseTableName: String{
        return Constant.TEXT_MATCHES_TABLE
    }
    override class var persistenceConflictPolicy: PersistenceConflictPolicy {
        return PersistenceConflictPolicy(insert: .replace, update: .replace)
    }
    
    required init(row: Row) {
        super.init(row: row)
        self.idMatch = row[PropertyKey.idMatch]
        self.teamOne = row[PropertyKey.teamOne]
        self.teamTwo = row[PropertyKey.teamTwo]
        self.venue = row[PropertyKey.venue]
        self.date = row[PropertyKey.date]
        
        if let s = row[PropertyKey.status] as? Int64{
            let sI = Int(truncatingIfNeeded: s)
            self.status = MatchStatus(rawValue: sI) ?? .UNDEFINED
        }
        else{
            assertionFailure("SOMGTING ALSO WONG")
        }
        
        self.result = row[PropertyKey.result]
        self.lastMatch = row[PropertyKey.lastMatch]
        self.longitude = row[PropertyKey.longitude]
        self.latitide = row[PropertyKey.latitude]
        self.idGroup = row[PropertyKey.idGroup]
    }
    
    override func encode(to container: inout PersistenceContainer) {
        container[PropertyKey.idMatch] = self.idMatch
        container[PropertyKey.teamOne] = self.teamOne
        container[PropertyKey.teamTwo] = self.teamTwo
        container[PropertyKey.venue] = self.venue
        container[PropertyKey.date] = self.date
        container[PropertyKey.status] = self.status.rawValue
        container[PropertyKey.result] = self.result
        container[PropertyKey.lastMatch] = self.lastMatch
        container[PropertyKey.longitude] = self.longitude
        container[PropertyKey.latitude] = self.latitide
        container[PropertyKey.idGroup] = self.idGroup
    }
    
    func getOpponentTeamId() -> String{
        return teamOne == "1" ? teamTwo : teamOne
    }
    
}
