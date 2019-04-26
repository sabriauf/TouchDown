import Alamofire
import ObjectMapper
import AlamofireObjectMapper
import GRDB

class Score: Record, Mappable, ModelProtocol{
    
    enum Action: Int{
        
        case START = 0
        case HALF_TIME = 1
        case FULL_TIME = 2
        case SECOND_HALF = 3
        case TRY = 4
        case CONVERSION = 5
        case DROP_GOAL = 6
        case PENALTY_KICK = 7
        case LINE_OUT = 8
        case YELLOW_CARD = 9
        case RED_CARD = 10
        case PENALTY = 11
        case KNOCK_ON = 12
        case SCRUM = 13
        case MESSAGE = 14
        case UNDEFINED = 15
        
        private func getInternalMap() -> [Int: [String]]{
            return [
                0: ["START", "Start"],
                1: ["HALF_TIME", "Half Time"],
                2: ["FULL_TIME", "Full Time"],
                3: ["SECOND_HALF", "Second Half"],
                4: ["TRY", "Try"],
                5: ["CONVERSION", "Conversion"],
                6: ["DROP_GOAL", "Drop Goal"],
                7: ["PENALTY_KICK", "Penalty Kick"],
                8: ["LINE_OUT", "Line Out"],
                9: ["YELLOW_CARD", "Yellow Card"],
                10: ["RED_CARD", "Red Card"],
                11: ["PENALTY", "Penalty"],
                12: ["KNOCK_ON", "Knock On"],
                13: ["SCRUM", "Scrum"],
                14: ["MESSAGE", "Message"],
                15: ["UNDEFINED", "Undefined"]
            ]
        }
        
        func toFriendlyString() -> String{
            return getInternalMap()[self.rawValue]![1]
        }
        
        func toServerMapping() -> String{
            return getInternalMap()[self.rawValue]![0]
        }
        
        func pointValue() -> Int{
            switch (self){
            case .PENALTY_KICK:
                return 3
            case .TRY:
                return 5
            case .CONVERSION:
                return 2
            case .DROP_GOAL:
                return 3
            default:
                return 0
            }
        }
        
        static func initFromInteger(_ value: Int) -> Action{
            return Action.init(rawValue: value) ?? Action.UNDEFINED
        }
        
        static func initFromServerMapping(_ value: String) -> Action{
            let map = Action(rawValue: 15)!.getInternalMap()
            if let id = (map.first { (kvp) -> Bool in
                return kvp.value[0] == value
            }?.key){
                return Action(rawValue: id)!
            }
            return .UNDEFINED
        }
        
        static func squareTypes() -> [Action]{
            let arr: [Action] = [
                .START, .HALF_TIME, .SECOND_HALF,
                .FULL_TIME
            ]
            return arr
        }
        
        static func eggTypes() -> [Action]{
            let arr: [Action] = [
                .PENALTY_KICK, .TRY, .CONVERSION,
                .DROP_GOAL
            ]
            return arr
        }
        
        static func cardTypes() -> [Action]{
            let arr: [Action] = [
                .RED_CARD, .YELLOW_CARD
            ]
            return arr
        }
    }
    
    var idScore: String = ""
    var matchId: String = ""
    var score: String = ""
    var details: String = ""
    var player: String = ""
    var time: String = ""
    var action: Action = .UNDEFINED
    var teamId: String = ""
    
    struct PropertyKey{
        static let idScore = "idscore"
        static let matchId = "matchid"
        static let score = "score"
        static let details = "details"
        static let player = "player"
        static let time = "time"
        static let action = "action"
        static let teamId = "teamId"
    }
    
    required init?(map: Map) {
        super.init()
    }
    
    func mapping(map: Map) {
        let actionTransform = TransformOf<Action, String>(fromJSON: { (optString) -> Score.Action? in
            if let concString = optString{
                return Action.initFromServerMapping(concString)
            }
            return Action.UNDEFINED
        }) { (optAction) -> String? in
            if let concAction = optAction{
                return concAction.toServerMapping()
            }
            return Action.UNDEFINED.toServerMapping()
        }
        self.idScore <- map[PropertyKey.idScore]
        self.matchId <- map[PropertyKey.matchId]
        self.score <- map[PropertyKey.score]
        self.details <- map[PropertyKey.details]
        self.player <- map[PropertyKey.player]
        self.time <- map[PropertyKey.time]
        self.action <- (map[PropertyKey.action], actionTransform)
        self.teamId <- map[PropertyKey.teamId]
    }
    
    // Database stuff
    
    static func setColumnDefinitions(t: TableDefinition) {
        t.column(PropertyKey.idScore, Database.ColumnType.text).primaryKey()
        t.column(PropertyKey.matchId, Database.ColumnType.text)
        t.column(PropertyKey.score, Database.ColumnType.text)
        t.column(PropertyKey.details, Database.ColumnType.text)
        t.column(PropertyKey.player, Database.ColumnType.text)
        t.column(PropertyKey.time, Database.ColumnType.text)
        t.column(PropertyKey.action, Database.ColumnType.integer)
        t.column(PropertyKey.teamId, Database.ColumnType.text)
    }
    
    override class var databaseTableName: String{
        return Constant.TEXT_SCORES_TABLE
    }
    override class var persistenceConflictPolicy: PersistenceConflictPolicy {
        return PersistenceConflictPolicy(insert: .replace, update: .replace)
    }
    
    required init(row: Row) {
        super.init(row: row)
        self.idScore = row[PropertyKey.idScore]
        self.matchId = row[PropertyKey.matchId]
        self.score = row[PropertyKey.score]
        self.details = row[PropertyKey.details]
        self.player = row[PropertyKey.player]
        self.time = row[PropertyKey.time]
        
        if let a = row[PropertyKey.action] as? Int64{
            let aI = Int(truncatingIfNeeded: a)
            self.action = Action(rawValue: aI) ?? .UNDEFINED
        }
        else{
            assertionFailure("Someting WONG")
        }
        self.teamId = row[PropertyKey.teamId]
    }
    
    override func encode(to container: inout PersistenceContainer) {
        container[PropertyKey.idScore] = self.idScore
        container[PropertyKey.matchId] = self.matchId
        container[PropertyKey.score] = self.score
        container[PropertyKey.details] = self.details
        container[PropertyKey.player] = self.player
        container[PropertyKey.time] = self.time
        container[PropertyKey.action] = self.action.rawValue
        container[PropertyKey.teamId] = self.teamId
    }
}

/**
 Used as an object to map notification
 details received from the touchdown server
 */
class ScoreAsNotificationObject: Mappable{
    
    var score: Score?
    
    required init?(map: Map) {
    }
    
    func mapping(map: Map) {
        score <- map["score"]
    }
    
}
