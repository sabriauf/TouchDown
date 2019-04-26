import UIKit
import ImageLoader

class LiveController: UIViewController{
    
    static let identifer = "LiveController"
    static func getInstance() -> LiveController{
        let sb = UIStoryboard(name: "Main", bundle: nil)
        let i = sb.instantiateViewController(withIdentifier: self.identifer) as! LiveController
        return i
    }
    
    @IBOutlet weak var loadingView: LoadingView!
    @IBOutlet weak var liveIndicator: UIView!
    @IBOutlet weak var mainContainerView: UIView!
    @IBOutlet weak var defaultView: DefaultView!
    
    @IBOutlet weak var tableView: UITableView!
    
    @IBOutlet weak var leagueNameLabel: UILabel!
    @IBOutlet weak var roundLabel: UILabel!
    
    @IBOutlet weak var royalScoreLabel: UILabel!
    @IBOutlet weak var opposingTeamScoreLabel: UILabel!
    
    @IBOutlet weak var royalEmblemImage: UIImageView!
    @IBOutlet weak var opposingTeamEmblemImage: UIImageView!
    
    @IBOutlet weak var timeLabel: UILabel!
    
    private let syncListenerIdentifer = "LiveController"
    
    var match: Match!
    var scoresArr: [Score] = []
    /// gameStartTime in a millisecond string
    var gameStartTime: Int = 0{
        didSet{
            gameStartTime = max(0, gameStartTime)
        }
    }
    var secondHalfStartTime: Int = 0{
        didSet{
            gameStartTime = max(0, gameStartTime)
        }
    }
    var isFirstTime: Bool = true
    var timer: Timer?
    
    override func viewDidLoad(){
        super.viewDidLoad()
        
        setupTableView()
        setupNotificationListeners()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        if isFirstTime{
            if !SyncHelper.shouldWaitForSync(){
                loadData()
            }
        
            loadingView.show {
                self.liveIndicator.isHidden = true
                self.mainContainerView.isHidden = true
                self.defaultView.hide()
            }
            
            // Start listening to sync calls.
            GlobalData.register(
                listener: {
                    print("Sync callback recieved to LiveController")
                    self.loadData()
            },
                withId: self.syncListenerIdentifer
            )
        }
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        
        // Stop listening to sync calls.
        GlobalData.remove(listenerWithId: self.syncListenerIdentifer)
    }
    
    // Setup methods
    
    private func setupNotificationListeners(){
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(self.toggleLoadingMode),
            name: NSNotification.Name.manualResyncStarted,
            object: nil)
        
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(handleScoreNotificationReceived(_:)),
            name: NSNotification.Name.recievedScoreNotification,
            object: nil)
    }
    
    private func setupTableView(){
        self.tableView.dataSource = self
        self.tableView.tableHeaderView = UIView.getReallySmallView()
        self.tableView.tableFooterView = UIView.getReallySmallView()
        EggLetterTimeLineCell.register(withTable: tableView)
        DetailTimeLineCell.register(withTable: tableView)
        CardTimeLineCell.register(withTable: tableView)
        ColorSquareTimeLineCell.register(withTable: tableView)
    }
    
    private func setLeagueGroupAndRound(with: Group){
        self.leagueNameLabel.text = with.leagueName
        self.roundLabel.text = with.roundName
    }
    
    private func calculateCumulativeScores() -> (HomeTeam: Int, OpposingTeam: Int){
        if(!scoresArr.isEmpty){
            var homeScore = 0
            var opposingScore = 0
            
            for score in scoresArr{
                let action = score.action
                if score.teamId == "1"{
                    homeScore += action.pointValue()
                }
                else{
                    opposingScore += action.pointValue()
                }
            }
            
            return (HomeTeam: homeScore, OpposingTeam: opposingScore)
        }
        else{
            return (HomeTeam: 0, OpposingTeam: 0)
        }
    }
    
    private func setDefaultValuesInMainContainer(){
        leagueNameLabel.text = ""
        roundLabel.text = ""
        royalScoreLabel.text = ""
        opposingTeamScoreLabel.text = ""
    }
    
    private func timeForGame() -> String{
        let secondHalf = ScoreDAO.getScores(forMatch: match!, thatAre: .SECOND_HALF)
        
        if secondHalf.count > 0{
            let secondHalfTimeInS = (Double(secondHalfStartTime)) / 1000.0
            if secondHalfTimeInS != 0.0{
                let diff = abs(abs(secondHalfTimeInS - Constant.SECOND_HALF_START_TIME_SECONDS) - Date().timeIntervalSince1970)
                return diff.toGameTimeString()
            }
        }
        else{
            let startTimeInS = (Double(gameStartTime)) / 1000.0
            if startTimeInS != 0.0{
                let diff = abs(startTimeInS - Date().timeIntervalSince1970)
                return diff.toGameTimeString()
            }
        }
        return "00:00"
    }
    
    /// if timerMode is true, will set the font
    /// of the timer to a digital clock style.
    /// Otherwise to a normal serif font
    /// depicting the match status
    private func setTimerFont(timerMode: Bool){
        let dClockFont = UIFont(name: "DSEG7 Classic", size: 21.0)
        let nFont = UIFont.systemFont(ofSize: 15.0)
        let color = timerMode ? #colorLiteral(red: 0.2298058867, green: 0.7177868485, blue: 0.2465086579, alpha: 1) : #colorLiteral(red: 0.9921568627, green: 0.7529411765, blue: 0.06274509804, alpha: 1)
        
        timeLabel.font = timerMode ? dClockFont! : nFont
        timeLabel.textColor = color
    }
    
    private func setupTimer(){
        // Stop the existing timer
        timer?.invalidate()
        
        timer = Timer.scheduledTimer(withTimeInterval: 1, repeats: true, block: { [weak self](t) in
            if let s = self{
                if let match = s.match{
                    let status = match.status
                    let c1 = status == Match.MatchStatus.FIRST_HALF || status == Match.MatchStatus.SECOND_HALF
                    let c2 = s.gameStartTime != 0
                    
                    // DEBUG TESTING CODE
                    // s.setTimerFont(timerMode: true)
                    // s.gameStartTime = (Int(1552144819.0 * 1000).description)
                    // DEBUG REMOVED
                    if c1 && c2{
                        let secondHalfTime = Constant.SECOND_HALF_START_TIME_SECONDS
                        let diff = abs(
                            s.gameStartTime.toDate().timeIntervalSince1970
                                -
                                Date().timeIntervalSince1970
                        )
                        
                        let c3 = status == .FIRST_HALF && diff > secondHalfTime
                        let c4 = status == .SECOND_HALF && diff > (secondHalfTime * 2)
                        
                        DispatchQueue.main.async{
                            s.timeLabel.textColor! = c3 ? UIColor.red : c4 ? #colorLiteral(red: 0.231372549, green: 0.7176470588, blue: 0.2431372549, alpha: 1) : s.timeLabel.textColor
                            s.timeLabel.text = s.timeForGame()
                        }
                    }
                }
            }
            
        })
    }
    
    private func stopTimerAndResetTimerText(){
        // Stop the existing timer
        timer?.invalidate()
        setTimerFont(timerMode: true)
        timeLabel.text = "00:00"
    }
    
    @objc private func toggleLoadingMode(){
        self.loadingView!.show {
            self.liveIndicator.isHidden = true
            self.mainContainerView.isHidden = true
            self.defaultView.hide()
        }
    }
    
    @objc private func handleScoreNotificationReceived(_ notification: NSNotification){
        if let userInfo = notification.userInfo{
            if let message = userInfo["message"] as? ScoreMessageDatabaseAction{
                switch(message){
                case .NewScore(let score):
                    if match == nil || (score.matchId != match?.idMatch){
                        // Do a load data instead of updating the match
                        // if the current match is nil
                        // or
                        // the score's matchId doesn't match with the current match id
                        loadData()
                    }
                    else{
                        DispatchQueue.global().async { [weak self] in
                            if let s = self{
                                if let newMatch = MatchDAO.getMatchForId(Int(score.matchId)!){
                                    DispatchQueue.main.async {
                                        s.match = newMatch
                                        s.updateScore(score: score, match: newMatch)
                                    }
                                }
                                else{
                                    assertionFailure("Could not find match for provided score id")
                                }
                            }
                        }
                    }
                    
                case .UpdateScore(let score):
                    DispatchQueue.global().async { [weak self] in
                        if let s = self{
                            if score.matchId == s.match!.idMatch{
                                if let newMatch = MatchDAO.getMatchForId(Int(score.matchId)!){
                                    s.match = newMatch
                                    DispatchQueue.main.async {
                                        s.setupTimer()
                                    }
                                }
                            }
                            
                            DispatchQueue.main.async {
                                let index = s.scoresArr.firstIndex(where: { (testingScore) -> Bool in
                                    return testingScore.idScore == score.idScore
                                })
                                if let i = index{
                                    let ip = IndexPath(row: i, section: 0)
                                    s.scoresArr[i] = score
                                    s.tableView.beginUpdates()
                                    s.tableView.reloadRows(at: [ip], with: .automatic)
                                    s.tableView.endUpdates()
                                    
                                    let result = s.calculateCumulativeScores()
                                    s.royalScoreLabel.text = result.HomeTeam.description
                                    s.opposingTeamScoreLabel.text = result.OpposingTeam.description
                                }
                            }
                            
                        }
                    }
                    
                case .RemoveScore(let score):
                    if match.idMatch == score.matchId{
                        let index = scoresArr.firstIndex { (testingScore) -> Bool in
                            return testingScore.idScore == score.idScore
                        }
                        if let i = index{
                            let ip = IndexPath(row: i, section: 0)
                            scoresArr.remove(at: i)
                            tableView.beginUpdates()
                            tableView.deleteRows(at: [ip], with: .automatic)
                            tableView.endUpdates()
                            
                            let result = calculateCumulativeScores()
                            royalScoreLabel.text = result.HomeTeam.description
                            opposingTeamScoreLabel.text = result.OpposingTeam.description
                        }
                    }
                    
                case .RemoveMatch:
                    gameStartTime = 0
                    loadData()
                }
            }
            else{
                assertionFailure("Could not find message information in score notification")
            }
        }
        else{
            print("🛑 Could not handle score notification. userInfo was null.")
        }
    }
    
    private func updateScore(score s: Score, match m: Match){
        let appendingIndexPath = IndexPath(row: scoresArr.count, section: 0)
        scoresArr.append(s)
        tableView.beginUpdates()
        tableView.insertRows(at: [appendingIndexPath], with: .automatic)
        tableView.endUpdates()
        tableView.scrollToRow(at: appendingIndexPath, at: .bottom, animated: true)
        
        switch(s.action){
        case .START:
            gameStartTime = Int(s.time.toSeconds())!
            liveIndicator.isHidden = true
            setTimerFont(timerMode: true)
            setupTimer()
        case .HALF_TIME:
            gameStartTime = 0
        case .SECOND_HALF:
            gameStartTime = Int((TimeInterval(s.time.toSeconds())! - Constant.SECOND_HALF_START_TIME_SECONDS))
            liveIndicator.isHidden = false
            setTimerFont(timerMode: true)
            setupTimer()
        case .FULL_TIME:
            gameStartTime = 0
        default:
            if s.teamId == "1"{
                // Increase royal score
                var eScore = Int(royalScoreLabel.text ?? "0")!
                eScore += Int(Double(s.score)!.rounded())
                royalScoreLabel.text = eScore.description
            }
            else{
                // Increase opponent score
                var eScore = Int(opposingTeamScoreLabel.text ?? "0")!
                eScore += Int(Double(s.score)!.rounded())
                opposingTeamScoreLabel.text = eScore.description
            }
        }
    }
    
    private func loadData(){
        
        stopTimerAndResetTimerText()
        
        loadingView.show {
            self.liveIndicator.isHidden = true
            self.mainContainerView.isHidden = true
            self.defaultView.hide()
        }
        
        DispatchQueue.global().async { [weak self] in
            if let s = self{
                if let match = MatchDAO.getDisplayMatch(){
            
                    s.match = match
                    
                    let group = GroupDAO.getGroupInfo(forMatch: match)
                    
                    s.scoresArr = ScoreDAO.getScores(forMatch: match) ?? []
                    
                    let total = s.calculateCumulativeScores()
                    let teamOne = TeamDAO.getTeam(withId: match.teamOne)
                    let teamTwo = TeamDAO.getTeam(withId: match.teamTwo)
                    
                    s.gameStartTime = Int(MatchDAO.getStartTime(forMatch: match) ?? "0")!
                    s.secondHalfStartTime = Int(MatchDAO.getSecondHalfStartTime(forMatch: match) ?? "0")!
                    
                    DispatchQueue.main.async {
                        
                        if let g = group{
                            s.leagueNameLabel.text = g.leagueName
                            s.roundLabel.text = g.roundName
                        }
                        else{
                            s.leagueNameLabel.text = ""
                            s.roundLabel.text = ""
                        }
                        
                        s.royalScoreLabel.text = String(format: "%d", total.HomeTeam)
                        s.opposingTeamScoreLabel.text = String(format: "%d", total.OpposingTeam)
                        
                        if teamOne != nil{
                            s.royalEmblemImage.load.request(with: teamOne!.logoUrl)
                        }
                        if teamTwo != nil{
                            s.opposingTeamEmblemImage.load.request(with: teamTwo!.logoUrl)
                        }
                        
                        if match.status == .SECOND_HALF || match.status == .FIRST_HALF{
                            s.liveIndicator.isHidden = false
                            s.setTimerFont(timerMode: true)
                            s.timeLabel.text = s.timeForGame()
                        }
                        else{
                            s.liveIndicator.isHidden = true
                            s.setTimerFont(timerMode: false)
                            // s.gameStartTime = 0
                            
                            if match.status == .PENDING{
                                let date = match.date!
                                let df = DateFormatter()
                                let f = "yyyy/MM/dd"
                                df.dateFormat = f
                                s.timeLabel.text = df.string(from: date)
                            }
                            else if match.status == .DONE || match.status == .FULLTIME{
                                s.timeLabel.text = match.result
                            }
                            else{
                                s.timeLabel.text = match.status.getFriendlyMapping()
                            }
                        }
                        
                        
                        s.tableView.reloadData()
                        
                        s.loadingView.hide {
                            s.defaultView.show()
                            s.mainContainerView.isHidden = false
                            s.setupTimer()
                        }
                    }
                    
                }
                else{
                    s.match = nil
                    
                    DispatchQueue.main.async {
                        s.loadingView.hide {
                            s.setDefaultValuesInMainContainer()
                            s.mainContainerView.isHidden = false
                            s.liveIndicator.isHidden = true
                            s.defaultView.show()
                            s.setupTimer()
                        }
                    }
                }
            }
        }
    }
    
}
