//
//  TeamsController.swift
//  Touchdown
//
//  Created by Thisura on 2/24/18.
//

import UIKit

class TeamsController: UIViewController{
    
    static let identifer = "TeamsController"
    static func getInstance(match: Match? = nil) -> TeamsController{
        let sb = UIStoryboard(name: "Main", bundle: nil)
        let i = sb.instantiateViewController(withIdentifier: self.identifer) as! TeamsController
        i.optionalMatch = match
        return i
    }
    
    @IBOutlet weak var loadingView: LoadingView!
    @IBOutlet weak var collectionView: UICollectionView!
    
    let listenerId = TeamsController.identifer
    var optionalMatch: Match? = nil
    var players: [CompositePlayer] = []
    var yearOfLastMatch: Int!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupCollectionView()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.setupNotificationObservers()
        
        GlobalData.register(listener: { [weak self] in
            self?.loadData()
        }, withId: listenerId)
        
        if SyncHelper.shouldWaitForSync(){
            loadingView.show {
                self.collectionView.isHidden = true
            }
        }
        else{
            loadData()
        }
    }
    
    private func setupNotificationObservers(){
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(self.userScrollingToDifferentPage),
            name: NSNotification.Name.userWillScrollToNewPage,
            object: nil)
        
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(self.toggleLoadingMode),
            name: NSNotification.Name.manualResyncStarted,
            object: nil)
    }
    
    @objc private func userScrollingToDifferentPage(){
        GlobalData.remove(listenerWithId: listenerId)
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name.userWillScrollToNewPage, object: nil)
    }
    
    @objc private func toggleLoadingMode(){
        loadingView.show {
            self.collectionView.isHidden = true
        }
    }
    
    private func loadData(){
        
        loadingView.show {
            self.collectionView.isHidden = true
        }
        
        DispatchQueue.global().async { [weak self] in
            if let s = self{
                
                // DO STUFF
                // TODO
                //s.players = PlayerDAO.
                
                if let _match = s.optionalMatch{
                    let matchId = _match.idMatch
                    s.players = PlayerDAO.getCompositePlayers(forMatchId: matchId)
                    s.yearOfLastMatch = Int(
                        GroupDAO.getGroupInfo(
                            forMatch: MatchDAO.getMatchForId(Int(matchId)!)!
                        )!.year)!
                    
                }
                else{
                    let data = PlayerDAO.getPlayersForLastMatch(teamId: 1)
                    let matchId = data.lastMatchId
                    
                    s.players = data.players
                    s.yearOfLastMatch = Int(GroupDAO.getGroupInfo(forMatch: MatchDAO.getMatchForId(matchId)!)!.year)!
                }
                
                DispatchQueue.main.async {
                    s.collectionView.reloadData()
                    s.loadingView.hide {
                        s.collectionView.isHidden = false
                    }
                }
            }
        }
    }
    
    // private func getCurrentYear()
    
    private func setupCollectionView(){
        self.collectionView.delegate = self
        self.collectionView.dataSource = self
    }
    
    internal func presentPlayerDetails(player: Player, position: Position){
        let vc = PlayerDetailsView.getInstance(
            withPlayer: player,
            and: position,
            yearRef: yearOfLastMatch)
        vc.modalTransitionStyle = .crossDissolve
        vc.modalPresentationStyle = .overCurrentContext
        self.present(vc, animated: true, completion: nil)
    }
    
}
