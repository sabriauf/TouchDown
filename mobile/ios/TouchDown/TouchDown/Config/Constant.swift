class Constant{
    
    // Generic placeholder
    
    static private var _ACTUAL_PLACEHOLDER: UIImage?
    static var IMAGE_PLACEHOLDER: UIImage{
        get{
            if _ACTUAL_PLACEHOLDER == nil{
                _ACTUAL_PLACEHOLDER = UIImage(named: "School Image Placeholder")!
            }
            return _ACTUAL_PLACEHOLDER!
        }
    }
    
    // Used by TabsController
    
    static var NUM_MAIN_TABS: Int{
        get{
            if BradexController.shouldShowBradexTab(){
                return self.TEXT_TAB_NAMES.count
            }
            else{
                return self.TEXT_TAB_NAMES.count - 1
            }
        }
    }
    
    static let TEXT_TAB_NAMES = [
        "LIVE", "FIXTURE", "POINTS", "TEAM", "BRADEX"
    ]
    
    // Used by MatchSummaryTabsController
    
    static let MATCH_SUMMARY_TAB_NAMES = [
        "SUMMARY", "TIMELINE", "TEAM"
    ]
    
    // Common tabs stuff
    
    static let SIZE_TAB_FONT_SIZE: CGFloat = 14.0
    static let SIZE_TAB_HEIGHT: CGFloat = 44.0
    
    // Database table names
    
    static let TEXT_MATCHES_TABLE = "matches"
    static let TEXT_GROUPS_TABLE = "groups"
    static let TEXT_POSITION_TABLE = "positions"
    static let TEXT_SCORES_TABLE = "score"
    static let TEXT_POINTS_TABLE = "points"
    static let TEXT_PLAYER_POSITION_TABLE = "playerpositions"
    static let TEXT_TEAMS_TABLE = "teams"
    static let TEXT_PLAYERS_TABLE = "players"
    static let TEXT_PLAYER_TEAM_TABLE = "playerteam"
    static let TEXT_META_DATA_TABLE = "metadata"
    static let TEXT_SUPPORT_STAFF_TABLE = "staff"
    
    static func ALL_TABLES() -> [String]{
        return [
            TEXT_MATCHES_TABLE,
            TEXT_GROUPS_TABLE,
            TEXT_POSITION_TABLE,
            TEXT_SCORES_TABLE,
            TEXT_POINTS_TABLE,
            TEXT_PLAYER_POSITION_TABLE,
            TEXT_TEAMS_TABLE,
            TEXT_PLAYERS_TABLE,
            TEXT_PLAYER_TEAM_TABLE,
            TEXT_META_DATA_TABLE,
            TEXT_SUPPORT_STAFF_TABLE
        ]
    }
    
    struct META_DATA_KEYS{
        static let BRADEX_IMAGE = "bradex_img"
        static let BRADEX_LINK = "bradex_link"
        static let POINTS_UPDATED = "points_updated"
    }
    
    /*
    // Match Status
    
    static let TEXT_FULL_TIME_MATCHSTATUS = "FULL_TIME"
    static let TEXT_HALF_TIME_MATCHSTATUS = "HALF_TIME"
    static let TEXT_PENDING_MATCHSTATUS = "PENDING"
    static let TEXT_PLAYING_NOW_MATCHSTATUS = "PLAYING_NOW"
    
    // Time line constants
    
    enum TIME_LINE_ACTION: String{
        case TEXT_START_TLC = "START"
        case TEXT_PENALTY_TLC = "PENALTY"
        case TEXT_PENALTY_KICK_TLC = "PENALTY_KICK"
        case TEXT_KNOCK_ON_TLC = "KNOCK_ON"
        case TEXT_SCRUM_TLC = "SCRUM"
        case TEXT_MESSAGE_TLC = "MESSAGE"
        case TEXT_TRY_TLC = "TRY"
        case TEXT_CONVERSION_TLC = "CONVERSION"
        case TEXT_HALF_TIME_TLC = "HALF_TIME"
        case TEXT_SECOND_HALF_TLC = "SECOND_HALF"
        case TEXT_FULL_TIME_TLC = "FULL_TIME"
        case TEXT_DROP_GOAL_TLC = "DROP_GOAL"
        case TEXT_YELLOW_CARD_TLC = "YELLOW_CARD"
        case TEXT_RED_CARD_TLC = "RED_CARD"
        case TEXT_LINE_OUT_TLC = "LINE_OUT"
        
        case TEXT_UNDEFINED_TLC = "-1"
        
        func toString() -> String{
            switch(self){
            case .TEXT_START_TLC: return "Start"
            case .TEXT_HALF_TIME_TLC: return "Half Time"
            case .TEXT_FULL_TIME_TLC: return "Full Time"
            case .TEXT_SECOND_HALF_TLC: return "Second Half"
            case .TEXT_TRY_TLC: return "Try"
            case .TEXT_CONVERSION_TLC: return "Conversion"
            case .TEXT_DROP_GOAL_TLC: return "Drop Goal"
            case .TEXT_PENALTY_KICK_TLC: return "Penalty Kick"
            case .TEXT_LINE_OUT_TLC: return "Line Out"
            case .TEXT_YELLOW_CARD_TLC: return "Yellow Card"
            case .TEXT_RED_CARD_TLC: return "Red Card"
            case .TEXT_PENALTY_TLC: return "Penalty"
            case .TEXT_KNOCK_ON_TLC: return "Knock On"
            case .TEXT_SCRUM_TLC: return "SCRUM"
            case .TEXT_MESSAGE_TLC: return "MESSAGE"
            case .TEXT_UNDEFINED_TLC: return "UNDEFINED"
            }
        }
        
        static func array() -> [TIME_LINE_ACTION]{
            let arr: [TIME_LINE_ACTION] = [
                .TEXT_START_TLC, .TEXT_PENALTY_TLC, .TEXT_PENALTY_KICK_TLC,
                .TEXT_KNOCK_ON_TLC, .TEXT_SCRUM_TLC, .TEXT_MESSAGE_TLC,
                .TEXT_TRY_TLC, .TEXT_CONVERSION_TLC, .TEXT_HALF_TIME_TLC,
                .TEXT_SECOND_HALF_TLC, .TEXT_FULL_TIME_TLC, .TEXT_DROP_GOAL_TLC,
                .TEXT_YELLOW_CARD_TLC, .TEXT_RED_CARD_TLC,
            ]
            return arr
        }
        
        static func squareTypes() -> [TIME_LINE_ACTION]{
            let arr: [TIME_LINE_ACTION] = [
                .TEXT_START_TLC, .TEXT_HALF_TIME_TLC, .TEXT_SECOND_HALF_TLC,
                .TEXT_FULL_TIME_TLC
            ]
            return arr
        }
        
        static func eggTypes() -> [TIME_LINE_ACTION]{
            let arr: [TIME_LINE_ACTION] = [
                .TEXT_PENALTY_KICK_TLC, .TEXT_TRY_TLC, .TEXT_CONVERSION_TLC,
                .TEXT_DROP_GOAL_TLC
            ]
            return arr
        }
        
        static func cardTypes() -> [TIME_LINE_ACTION]{
            let arr: [TIME_LINE_ACTION] = [
                .TEXT_RED_CARD_TLC, .TEXT_YELLOW_CARD_TLC
            ]
            return arr
        }
        
        /// Returns the point value
        /// for the action.
        func pointValue() -> Int{
            switch (self){
            case .TEXT_PENALTY_KICK_TLC:
                return 3
            case .TEXT_TRY_TLC:
                return 5
            case .TEXT_CONVERSION_TLC:
                return 2
            case .TEXT_DROP_GOAL_TLC:
                return 3
            default:
                return 0
            }
        }
        
    }*/
}

extension Constant{
    class Colors{
        static let RC_YELLOW = #colorLiteral(red: 0.9403255582, green: 0.7635715008, blue: 0.0611666292, alpha: 1)
        static let RC_BLUE = #colorLiteral(red: 0.07316308469, green: 0.1868256629, blue: 0.3544519842, alpha: 1)
    }
}

extension Constant{
    
    static let SECOND_HALF_START_TIME_SECONDS: Double = 2100
    
    static var TEXT_BASE_URL: String{
        get{
            #if DEBUG
                //return "http://usrawahada.xyz/touchdown_test"
                return "http://touchdownroyal.com"
            #else
                //return "http://usrawahada.xyz/touchdown"
                return "http://touchdownroyal.com"
            #endif
        }
    }
    
    static func getRequestUrl() -> String{
        return TEXT_BASE_URL + "/sync.php"
    }
    
    static func getHeaderParams() -> [String: String]{
        return [
            "key": "1",
            "platform": "2",
            "deviceid": "1",
            "pkg": "lk.rc07.tenyears.touchdown",
            "ver": "21",
            "api_ver": "1"
        ]
    }
    
    /*static func getRequestParams() -> [String: String]{
        let epochTime = Date().timeIntervalSince1970
        print("epochTime now:", epochTime)
        
        return [
            "last_updated":
        ]
    }*/
    
}
