//
//  ViewController.swift
//  Touchdown
//
//  Created by Thisura on 2/18/18.
//

import UIKit
import Tabman
import Pageboy
import DropDown
import Alamofire

extension NSNotification.Name{
    
    public static let userWillScrollToNewPage = NSNotification.Name("main-nav-scr-n-page")
    public static let userDidScrollToNewPage = NSNotification.Name("main-nav-scr-d-page")
    public static let startInitialResync = NSNotification.Name("main-sync-will-start")
    //public static let startManualResyncAfterEnteringForeground = NSNotification.Name("main-start-manual-resync")
    public static let manualResyncStarted = NSNotification.Name("main-manual-sync-will-start")
    public static let recievedScoreNotification = NSNotification.Name("main-rec-score-notif")
    
}

class TabsController: TabmanViewController{
    
    var loadNoNetworkController: Bool = false
    var viewDictionary: [Int: UIViewController] = [:]
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.setupPager()
        
        if SyncHelper.getLastUpdatedTime() == nil{
            if !NetworkReachabilityManager()!.isReachable{
                loadNoNetworkController = true
            }
        }
        
        if !loadNoNetworkController{
            SyncHelper.syncNow {
                GlobalData.callAllSyncListeners()
            }
        }
        
        addNotificationListeners()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        if loadNoNetworkController{
            loadNoNetworkController = false
            NoNetworkController.present(navigationController!)
        }
    }
    
    private func addNotificationListeners(){
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(initialResyncStarted),
            name: NSNotification.Name.startInitialResync,
            object: nil)
    }
    
    private func removeNotificationListeners(){
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name.startInitialResync, object: nil)
    }
    
    // Delegate overriding
    
    override func pageboyViewController(_ pageboyViewController: PageboyViewController, didScrollToPageAt index: Int, direction: PageboyViewController.NavigationDirection, animated: Bool) {
        self.navigationItem.title = Constant.TEXT_TAB_NAMES[index]
        NotificationCenter.default.post(name: .userDidScrollToNewPage, object: nil)
    }
    
    override func pageboyViewController(_ pageboyViewController: PageboyViewController, willScrollToPageAt index: Int, direction: PageboyViewController.NavigationDirection, animated: Bool) {
        NotificationCenter.default.post(name: .userWillScrollToNewPage, object: nil)
    }
    
    // Setup methods
    
    private func setupPager(){
        
        let bar = TMBar.ButtonBar()
        
        self.dataSource = self
        self.delegate = self
        
        bar.buttons.customize { (barButton) in
            barButton.font = UIFont.boldSystemFont(ofSize: Constant.SIZE_TAB_FONT_SIZE)
            barButton.tintColor = Constant.Colors.RC_BLUE
            barButton.selectedTintColor = Constant.Colors.RC_BLUE
        }
        
        bar.indicator.tintColor = Constant.Colors.RC_BLUE
        bar.backgroundView.style = TMBarBackgroundView.Style.flat(color: Constant.Colors.RC_YELLOW)
        bar.layout.contentMode = .fit
        bar.layout.transitionStyle = .snap
        
        self.addBar(bar, dataSource: self, at: .bottom)
    }
    
    @IBAction func userTappedOverflowMenu(_ sender: UIBarButtonItem){
        let dd = DropDown(anchorView: sender)
        dd.dataSource = ["Sync Now", "About Us"]
        //dd.topOffset = 10.0
        dd.width = 120.0
        dd.bottomOffset = CGPoint(x: 0.0, y: 8.0)
        dd.selectionAction = { [weak self] (index, item) in
            print(index, item)
            
            if let s = self{
                if item == "About Us"{
                    AboutUsController.presentFrom(s.navigationController!)
                }
                else if item == "Sync Now"{
                    s.startManualResync()
                }
            }
            
        }
        dd.show()
    }
    
    @objc func initialResyncStarted(){
        DispatchQueue.main.async { [weak self] in
            if let s = self{
                if !NetworkReachabilityManager()!.isReachable{
                    NoNetworkController.present(s.navigationController!)
                }
                else{
                    SyncHelper.syncNow {
                        GlobalData.callAllSyncListeners()
                    }
                    s.reloadData()
                }
            }
        }
    }
    
    /*@objc */func startManualResync(){
        NotificationCenter.default.post(name: NSNotification.Name.manualResyncStarted, object: nil)
        SyncHelper.syncNow {
            GlobalData.callAllSyncListeners()
        }
    }
    
}

extension TabsController: PageboyViewControllerDataSource, TMBarDataSource{
    
    func barItem(for bar: TMBar, at index: Int) -> TMBarItemable {
        return TMBarItem(title: Constant.TEXT_TAB_NAMES[index])
    }
    
    // Datasource
    
    func numberOfViewControllers(in pageboyViewController: PageboyViewController) -> Int {
        return Constant.NUM_MAIN_TABS
    }
    
    func viewController(for pageboyViewController: PageboyViewController, at index: PageboyViewController.PageIndex) -> UIViewController? {
        
        var vc: UIViewController? = nil
        
        // Full Release Code
        /*if(index == 0){
            let vc = LiveController.getInstance()
            return vc
        }
        else if(index == 1){
            let vc = FixtureController.getInstance(currentNavItem: self.navigationItem)
            return vc
        }
        else if(index == 2){
            let vc = PointsController.getInstance()
            return vc
        }
        else if (index == 3){
            let vc = TeamsController.getInstance()
            return vc
        }
        else{
            return UIViewController()
        }*/
        
        // Quick Release Code
        
        if viewDictionary[index] == nil{
            if(index == 0){
                vc = LiveController.getInstance()
            }
            else if(index == 1){
                vc = FixtureController.getInstance(currentNavItem: self.navigationItem)
            }
            else{
                vc = TeamsController.getInstance()
            }
            viewDictionary[index] = vc
        }
        else{
            vc = viewDictionary[index]!
        }
        
        return vc!
    }
    
    func defaultPage(for pageboyViewController: PageboyViewController) -> PageboyViewController.Page? {
        return Page.first
    }
    
}
