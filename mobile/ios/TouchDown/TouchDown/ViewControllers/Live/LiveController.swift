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
    @IBOutlet weak var defaultView: UIView!
    
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
    var gameStartTime: String = ""
    var secondHalfStartTime: String = ""
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
                self.defaultView.isHidden = true
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
        let matchStartTimeInMS = Double(gameStartTime)! / 1000.0
        if matchStartTimeInMS == 0.0{
            return "00:00"
        }
        else{
            let diff = abs(matchStartTimeInMS - Date().timeIntervalSince1970)
            return diff.toGameTimeString()
        }
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
                    let c2 = s.gameStartTime != "0" && !s.gameStartTime.isEmpty
                    
                    if c1 && c2{
                        
                        let secondHalfTime: Double = 2100
                        let diff = abs(
                            s.gameStartTime.toSeconds().toDate().timeIntervalSince1970
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
            self.defaultView.isHidden = true
        }
    }
    
    private func loadData(){
        
        stopTimerAndResetTimerText()
        
        loadingView.show {
            self.liveIndicator.isHidden = true
            self.mainContainerView.isHidden = true
            self.defaultView.isHidden = true
        }
        
        DispatchQueue.global().async { [weak self] in
            if let s = self{
                if let match = MatchDAO.getCalendarMatch(){
            
                    s.match = match
                    
                    let group = GroupDAO.getGroupInfo(forMatch: match)
                    
                    s.scoresArr = ScoreDAO.getScores(forMatch: match) ?? []
                    
                    let total = s.calculateCumulativeScores()
                    let teamOne = TeamDAO.getTeam(withId: match.teamOne)
                    let teamTwo = TeamDAO.getTeam(withId: match.teamTwo)
                    
                    s.gameStartTime = MatchDAO.getStartTime(forMatch: match) ?? ""
                    s.secondHalfStartTime = MatchDAO.getSecondHalfStartTime(forMatch: match) ?? ""
                    
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
                            s.gameStartTime = "0"
                            
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
                            s.defaultView.isHidden = true
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
                            s.mainContainerView.isHidden = true
                            s.liveIndicator.isHidden = true
                            s.defaultView.isHidden = false
                            s.setupTimer()
                        }
                    }
                }
            }
        }
    }
    
}
