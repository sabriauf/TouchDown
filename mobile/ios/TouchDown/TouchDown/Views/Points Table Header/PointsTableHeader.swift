//
//  PointsTableHeader.swift
//  Touchdown
//
//  Created by Thisura on 2/18/18.
//

import UIKit

protocol PointsTableHeaderDelegate{
    func getLeagues() -> [String]
    func getRounds() -> [String]
    func getGroups() -> [String]
    func showDropdown(
        withAnchor view: UIView,
        options: [String],
        mode: PointsTableHeader.DropdownMode,
        callback: @escaping ((_ mode: PointsTableHeader.DropdownMode, _ selectedIndex: Int) -> ()))
    func didSelectLeague(_ league: String)
    func didSelectRound(_ round: String)
    func didSelectGroup(_ group: String)
}

class PointsTableHeader: UIView{
    
    enum DropdownMode{
        case League
        case Round
        case Group
    }
    
    let identifer = "PointsTableHeader"
    static func getInstance(withFrame: CGRect) -> PointsTableHeader{
        return PointsTableHeader(frame: withFrame)
    }
    
    @IBOutlet private weak var leagueLabel: UILabel!
    @IBOutlet private weak var roundLabel: UILabel!
    @IBOutlet private weak var groupLabel: UILabel!
    
    private var leagues: [String]{
        get{
            return delegate!.getLeagues()
        }
    }
    private var rounds: [String]{
        get{
            return delegate!.getRounds()
        }
    }
    private var groups: [String]{
        get{
            return delegate!.getGroups()
        }
    }
    private var delegate: PointsTableHeaderDelegate!
    
    func initialize(_ delegate: PointsTableHeaderDelegate){
        self.delegate = delegate
    }
    
    func setData(mode: DropdownMode){
        switch(mode){
        case .League:
            leagueLabel.text = leagues.first
            delegate?.didSelectLeague(leagueLabel.text!)
        case .Round:
            roundLabel.text = rounds.first
            delegate?.didSelectRound(roundLabel.text!)
        case .Group:
            groupLabel.text = groups.first
            delegate?.didSelectGroup(groupLabel.text!)
        }
    }
    
    func getSelection() -> (league: String, round: String, group: String){
        return (
            league: leagueLabel.text!,
            round: roundLabel.text!,
            group: groupLabel.text!
        )
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        internalInit()
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        internalInit()
    }
    
    var view: UIView!
    
    private func internalInit(){
        view = loadViewFromNib()
        view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        view.frame = self.bounds
        self.addSubview(view)
    }
    
    private func loadViewFromNib() -> UIView{
        let nib = UINib(nibName: self.identifer, bundle: nil)
        return nib.instantiate(withOwner: self, options: nil).first! as! UIView
    }
    
    private func callDelegate(_ mode: DropdownMode){
        switch(mode){
        case .League:
            delegate.showDropdown(withAnchor: leagueLabel, options: leagues, mode: .League, callback: callbackHandler(_:optionIndex:))
        case .Round:
            delegate.showDropdown(withAnchor: roundLabel, options: rounds, mode: .Round, callback: callbackHandler(_:optionIndex:))
        case .Group:
            delegate.showDropdown(withAnchor: groupLabel, options: groups, mode: .Group, callback: callbackHandler(_:optionIndex:))
        }
    }
    
    private func callbackHandler(_ mode: DropdownMode, optionIndex i: Int){
        switch(mode){
        case .League:
            leagueLabel.text = leagues[i]
            delegate.didSelectLeague(leagues[i])
        case .Group:
            groupLabel.text = groups[i]
            delegate.didSelectGroup(groups[i])
        case .Round:
            roundLabel.text = rounds[i]
            delegate.didSelectRound(rounds[i])
        }
    }
    
    @IBAction func leagueSelectorTapped(){
        callDelegate(.League)
    }
    
    @IBAction func roundSelectorTapped(){
        callDelegate(.Round)
    }
    
    @IBAction func groupSelectorTapped(){
        callDelegate(.Group)
    }
    
}
