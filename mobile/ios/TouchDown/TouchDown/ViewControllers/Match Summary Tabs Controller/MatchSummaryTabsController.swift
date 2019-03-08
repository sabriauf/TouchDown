//
//  MatchSummaryTabsController.swift
//  Touchdown
//
//  Created by Thisura on 3/4/19.
//

import Foundation
import UIKit
import Tabman
import Pageboy

class MatchSummaryTabsController: TabmanViewController{
    
    static func present(fromController: UIViewController, match: Match){
        let sb = UIStoryboard(name: "MatchSummary", bundle: nil)
        let nav = sb.instantiateViewController(withIdentifier: "MatchSummaryTabsNavController") as! UINavigationController
        let tabs = nav.viewControllers.first as! MatchSummaryTabsController
        tabs.match = match
        
        fromController.present(nav, animated: true, completion: nil)
    }
    
    private var match: Match!
    private var viewDictionary: [Int: UIViewController] = [:]
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.setupPager()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        navigationController!.clearHairLineShadow()
    }
    
    // Delegate overriding
    
    override func pageboyViewController(_ pageboyViewController: PageboyViewController, didScrollToPageAt index: Int, direction: PageboyViewController.NavigationDirection, animated: Bool) {
        self.navigationItem.title = Constant.MATCH_SUMMARY_TAB_NAMES[index]
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
        
        self.addBar(bar, dataSource: self, at: .top)
    }
    
    @IBAction func backButtonPressed(){
        navigationController!.dismiss(animated: true, completion: nil)
    }
    
}

extension MatchSummaryTabsController: PageboyViewControllerDataSource, TMBarDataSource{
    
    func barItem(for bar: TMBar, at index: Int) -> TMBarItemable {
        return TMBarItem(title: Constant.MATCH_SUMMARY_TAB_NAMES[index])
    }
    
    // Datasource
    
    func numberOfViewControllers(in pageboyViewController: PageboyViewController) -> Int {
        return Constant.MATCH_SUMMARY_TAB_NAMES.count
    }
    
    func viewController(for pageboyViewController: PageboyViewController, at index: PageboyViewController.PageIndex) -> UIViewController? {
        var vc: UIViewController? = nil
        
        if viewDictionary[index] == nil{
            if(index == 0){
                vc = MatchSummaryController.getInstance(match: match)
            }
            else if(index == 1){
                vc = MatchSummaryTimelineController.getInstance(match: match)
            }
            else if(index == 2){
                vc = TeamsController.getInstance(match: match)
            }
            else{
                assertionFailure()
                return UIViewController()
            }
            viewDictionary[index] = vc
        }
        else{
            vc = viewDictionary[index]!
        }
        
        return vc!
    }
    
    func defaultPage(for pageboyViewController: PageboyViewController) -> PageboyViewController.Page? {
        return PageboyViewController.Page.first
    }
    
}
