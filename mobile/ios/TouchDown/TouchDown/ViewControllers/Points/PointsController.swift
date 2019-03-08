//
//  PointsController.swift
//  Touchdown
//
//  Created by Thisura on 2/18/18.
//

import UIKit

class PointsController: UIViewController{
    
    static let identifer = "PointsController"
    static func getInstance() -> PointsController{
        let sb = UIStoryboard(name: "Main", bundle: nil)
        let i = sb.instantiateViewController(withIdentifier: self.identifer) as! PointsController
        return i
    }
    
    @IBOutlet weak var tableView: UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.setupTableView()
    }
    
    // Setup Methods
    
    private func getHeaderFrame() -> CGRect{
        self.view.layoutIfNeeded()
        let width: CGFloat = tableView.frame.width
        let height: CGFloat = 130
        return CGRect(x: 0, y: 0, width: width, height: height)
    }
    
    private func setupTableView(){
        self.tableView.tableFooterView = UIView.getReallySmallView()
        self.tableView.tableHeaderView = PointsTableHeader.getInstance(withFrame: getHeaderFrame())
        self.tableView.dataSource = self
        self.tableView.delegate = self
        PointsSectionHeader.register(withTable: self.tableView)
        PointsTableCell.register(withTable: self.tableView)
    }
    
}
