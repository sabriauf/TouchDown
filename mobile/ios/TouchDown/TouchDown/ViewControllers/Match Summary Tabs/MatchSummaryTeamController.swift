//
//  MatchSummaryTeamController.swift
//  Touchdown
//
//  Created by Thisura on 3/4/19.
//

import Foundation
import UIKit

class MatchSummaryTeamController: UIViewController{
    
    static let identifier = "MatchSummaryTeamController"
    static func getInstance(match: Match) -> MatchSummaryTeamController{
        let sb = UIStoryboard(name: "MatchSummary", bundle: nil)
        let vc = sb.instantiateViewController(withIdentifier: identifier) as! MatchSummaryTeamController
        vc.match = match
        return vc
    }
    
    var match: Match!
}
