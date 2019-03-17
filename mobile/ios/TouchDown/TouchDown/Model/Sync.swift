import Alamofire
import ObjectMapper
import AlamofireObjectMapper

class Status: Mappable{
    var success: Bool = false
    var code: Int = 0
    var description: String = ""
    
    struct PropertyKey{
        static let success = "success"
        static let code = "code"
        static let description = "description"
    }
    
    init(){}
    
    required init?(map: Map) {
    }
    
    func mapping(map: Map) {
        self.success <- map[PropertyKey.success]
        self.code <- map[PropertyKey.code]
        self.description <- map[PropertyKey.description]
    }
}

class ServerResponseSync: Mappable{
    
    var jsonStatus: Status!
    var bradbyExpress: BradbyExpress!
    var matches: [Match]!
    var teams: [Team]!
    var positions: [Position]!
    var scores: [Score]!
    var players: [Player]!
    var playerPosition: [PlayerPosition]!
    var points: [Point]!
    var groups: [Group]!
    var playerTeams: [PlayerTeam]!
    var metaData: [MetaDataItem]!
    var staff: [SupportStaff]!
    
    struct PropertyKey{
        static let jsonStatus = "JsonStatus"
        static let bradbyExpress = "bradby_express"
        static let matches = "matches"
        static let teams = "teams"
        static let positions = "positions"
        static let scores = "scores"
        static let players = "players"
        static let playerPosition = "playerPosition"
        static let points = "points"
        static let groups = "groups"
        static let playerTeams = "playersTeams"
        static let metaData = "meta_data"
        static let staff = "supportStaff"
    }
    
    required init?(map: Map) {
    }
    
    func mapping(map: Map) {
        self.jsonStatus <- map[PropertyKey.jsonStatus]
        self.bradbyExpress <- map[PropertyKey.bradbyExpress]
        self.matches <- map[PropertyKey.matches]
        self.teams <- map[PropertyKey.teams]
        self.positions <- map[PropertyKey.positions]
        self.scores <- map[PropertyKey.scores]
        self.players <- map[PropertyKey.players]
        self.playerPosition <- map[PropertyKey.playerPosition]
        self.points <- map[PropertyKey.points]
        self.groups <- map[PropertyKey.groups]
        self.playerTeams <- map[PropertyKey.playerTeams] 
        self.metaData <- map[PropertyKey.metaData]
        self.staff <- map[PropertyKey.staff]
    }
    
}
