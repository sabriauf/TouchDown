//
//  PointsController.swift
//  Touchdown
//
//  Created by Thisura on 2/18/18.
//

import GRDB
import UIKit
import DropDown

class PointsController: UIViewController{
    
    static let identifer = "PointsController"
    static func getInstance() -> PointsController{
        let sb = UIStoryboard(name: "Main", bundle: nil)
        let i = sb.instantiateViewController(withIdentifier: self.identifer) as! PointsController
        return i
    }
    
    @IBOutlet weak var loadingView: LoadingView!
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var statusLabel: UILabel!
    
    private var isFirstTime: Bool = true
    private var leagues: [String] = []
    private var rounds: [String] = []
    private var groups: [String] = []
    internal var pointsToView: [TeamPoint] = []
    private var header: PointsTableHeader!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        if isFirstTime{
            setupTableView()
            setupNotificationObservers()
            registerSyncListener()
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        if isFirstTime{
            if !SyncHelper.shouldWaitForSync(){
                loadData()
            }
            else{
                loadingView.show {
                    self.tableView.isHidden = true
                    self.statusLabel.isHidden = true
                }
                print("Waiting for sync listener to complete")
            }
            isFirstTime = false
        }
    }
    
    // Setup Methods
    
    private func setupNotificationObservers(){
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(self.toggleLoadingMode),
            name: NSNotification.Name.manualResyncStarted,
            object: nil)
    }
    
    private func getHeaderFrame() -> CGRect{
        self.view.layoutIfNeeded()
        let width: CGFloat = tableView.frame.width
        let height: CGFloat = 130
        return CGRect(x: 0, y: 0, width: width, height: height)
    }
    
    private func setupTableView(){
        header = PointsTableHeader.getInstance(withFrame: getHeaderFrame())
        
        self.tableView.tableFooterView = UIView.getReallySmallView()
        self.tableView.tableHeaderView = header
        self.tableView.dataSource = self
        self.tableView.delegate = self
        
        header.initialize(self)
        PointsSectionHeader.register(withTable: self.tableView)
        PointsTableCell.register(withTable: self.tableView)
    }
    
    @objc private func toggleLoadingMode(){
        loadingView.show {
            self.tableView.isHidden = true
            self.statusLabel.isHidden = true
        }
    }
    
    private func registerSyncListener(){
        GlobalData.register(
            listener: { [weak self] in
                print("🛑 - PointsController recieved synced notification")
                self?.loadData()
            },
            withId: PointsController.identifer)
    }
    
    private func loadData(){
        
        loadingView.show {
            self.tableView.isHidden = true
            self.statusLabel.isHidden = true
        }
        
        DispatchQueue.global().async { [weak self] in
            if let s = self{
                let lastUpdateTime = MetaDataDAO.getMetaData(key: Constant.META_DATA_KEYS.POINTS_UPDATED)
                s.leagues = GroupDAO.getLeagues()
                DispatchQueue.main.async {
                    s.header?.setData(mode: .League)
                    
                    if let lut = lastUpdateTime{
                        let asDate = lut.toSeconds().toDate()
                        let formatter = DateFormatter()
                        let format = "dd MMMM yyyy"
                        formatter.dateFormat = format
                        let res = formatter.string(from: asDate)
                        s.statusLabel.text = "Updated on " + res
                    }
                    else{
                        s.statusLabel.text = ""
                    }
                    
                    s.loadingView?.hide {
                        s.tableView.isHidden = false
                        s.statusLabel.isHidden = false
                    }
                }
            }
        }
        
    }
    
}

extension PointsController: PointsTableHeaderDelegate{
    func getLeagues() -> [String] {
        return leagues
    }
    
    func getRounds() -> [String] {
        return rounds
    }
    
    func getGroups() -> [String] {
        return groups
    }
    
    func showDropdown(withAnchor view: UIView, options: [String], mode: PointsTableHeader.DropdownMode, callback: @escaping ((PointsTableHeader.DropdownMode, Int) -> ())) {
        let dd = DropDown(
            anchorView: view,
            selectionAction: { (index, option) in
                callback(mode, index)
            },
            dataSource: options,
            topOffset: CGPoint(x: 0, y: 44.0),
            bottomOffset: nil,
            cellConfiguration: nil,
            cancelAction: nil)
        dd.show()
    }
    
    func didSelectLeague(_ league: String) {
        rounds = GroupDAO.getRounds(forLeague: league)
        header!.setData(mode: .Round)
    }
    
    func didSelectRound(_ round: String) {
        let data = header.getSelection()
        groups = GroupDAO.getGroups(forLeague: data.league, andRound: data.round)
        header!.setData(mode: .Group)
    }
    
    func didSelectGroup(_ group: String) {
        let data = header.getSelection()
        if let groupObj = GroupDAO.getGroup(league: data.league, round: data.round, group: data.group){
            pointsToView = PointsDAO.getPointsForGroup(groupId: groupObj.idGroup)
            tableView.reloadData()
        }
    }
    
}
