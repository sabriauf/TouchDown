import UIKit
import DropDown

class FixtureController: UIViewController{
    
    static let identifer = "FixtureController"
    static func getInstance(currentNavItem: UINavigationItem) -> FixtureController{
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let controller = storyboard.instantiateViewController(withIdentifier: self.identifer) as! FixtureController
        controller.currentNavigationItem = currentNavItem
        return controller
    }
    
    @IBOutlet  weak var tableView: UITableView!
    @IBOutlet weak var loadingView: LoadingView!
    
    weak var currentNavigationItem: UINavigationItem!
    weak var selectorView: FixtureYearSelectorView!
    
    let yearSelectorNavItemViewTag = 359
    let syncListenerUniqueId = "FixtureController"
    
    var viewModel: FixtureViewModel!
    var currentYearString: String?
    var ignoreNextViewWillAppearCycle = false
    
    override func viewDidLoad(){
        super.viewDidLoad()
        self.setupTableView()
        registerSyncListener()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        if !ignoreNextViewWillAppearCycle{
            self.setupNotificationObservers()
            if !SyncHelper.shouldWaitForSync(){
                loadData()
            }
            else{
                loadingView.show {
                    self.tableView.isHidden = true
                }
                print("Waiting for sync listener to complete")
            }
        }
        else{
            ignoreNextViewWillAppearCycle = false
        }
    }
    
    private func setupTableView(){
        FixturePendingCell.register(tableView)
        FixtureResultCell.register(tableView)
        tableView.rowHeight = UITableViewAutomaticDimension
        tableView.estimatedRowHeight = 44.0
    
        tableView.tableHeaderView = UIView.getReallySmallView()
        tableView.tableFooterView = UIView.getReallySmallView()
        
        tableView.delegate = self
        tableView.dataSource = self
    }
    
    private func registerSyncListener(){
        GlobalData.register(
            listener: { [weak self] in
                print("🛑 - FixtureController recieved synced notification")
                self?.loadData(ignoreYearSelector: true)
            },
            withId: syncListenerUniqueId)
    }
    
    private func unregisterSyncListener(){
        GlobalData.remove(listenerWithId: syncListenerUniqueId)
    }
    
    private func loadData(ignoreYearSelector: Bool = false){
        loadingView.show {
            self.tableView.isHidden = true
            if !ignoreYearSelector{
                self.removeYearSelector()
            }
        }
        
        DispatchQueue.global().async { [weak self] in
            FixtureViewModelFactory.getViewModel({ (vm) in
                DispatchQueue.main.async {
                    self?.viewModel = vm
                    self?.currentYearString = vm.yearStrings.first
                    
                    if !ignoreYearSelector{
                        self?.setupYearSelector()
                    }
                    self?.tableView.reloadData()
                    self?.loadingView.hide {
                        self?.tableView.isHidden = false
                    }
                }
            })
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
    
    private func setupYearSelector(){
        let svFrame = CGRect(x: 0, y: 0, width: 90.0, height: 44.0)
        let selectorView = FixtureYearSelectorView(frame: svFrame)
        let selectorButton = UIButton(frame: svFrame)
        
        selectorButton.addSubview(selectorView)
        selectorView.clampConstraintOnSuperview(createWidthHeightConstraints: true)
        selectorView.isUserInteractionEnabled = false
        selectorButton.addTarget(
            self,
            action: #selector(self.yearSelectorViewTapped),
            for: .touchUpInside)
        selectorView.yearLabel.text = currentYearString
        
        self.selectorView = selectorView
        
        let barButton = UIBarButtonItem(customView: selectorButton)
        var newSet = currentNavigationItem.rightBarButtonItems ?? []
        
        barButton.tag = yearSelectorNavItemViewTag
        newSet.append(barButton)
        currentNavigationItem.setRightBarButtonItems(newSet, animated: true)
    }
    
    private func removeYearSelector(){
        var buttons = currentNavigationItem.rightBarButtonItems!
        if buttons.count == 2{
            buttons.remove(at: 1)
            currentNavigationItem.setRightBarButtonItems(buttons, animated: true)
        }
    }
    
    /**
     The user tapped on the year selector
     placed inside the navigation bar
     */
    @objc private func yearSelectorViewTapped(){
        let dropDown = DropDown(anchorView: currentNavigationItem.rightBarButtonItems![1])
        dropDown.dataSource = viewModel?.yearStrings ?? []
        dropDown.bottomOffset = CGPoint(x: 0.0, y: 8.0)
        dropDown.selectionAction = { [weak self] (index: Int, item: String) in
            print("\(index), \(item) selected!")
            self?.currentYearString = item
            self?.tableView.reloadData()
            self?.selectorView!.yearLabel.text = item
        }
        dropDown.show()
    }
    
    @objc private func userScrollingToDifferentPage(){
        removeYearSelector()
        unregisterSyncListener()
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name.userWillScrollToNewPage, object: nil)
    }
    
    @objc private func toggleLoadingMode(){
        loadingView.show {
            self.tableView.isHidden = true
        }
    }
    
}
