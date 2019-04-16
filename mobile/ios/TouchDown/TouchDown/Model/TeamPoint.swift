//
//  TeamPoint.swift
//  Touchdown
//
//  Created by Thisura on 3/16/19.
//

import Foundation

struct TeamPoint{
    
    var team: Team!
    var point: Point!
    
    init(_ team: Team, _ point: Point) {
        self.team = team
        self.point = point
    }
    
}
