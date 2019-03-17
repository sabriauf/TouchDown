//
//  PointsDAO.swift
//  Touchdown
//
//  Created by Thisura on 3/16/19.
//

import GRDB
import Foundation

class PointsDAO{
    
    static func getPointsForGroup(groupId: String) -> [TeamPoint]{
        var points: [Point] = []
        var teamPoints: [TeamPoint] = []
        var sql = "SELECT * FROM " + Constant.TEXT_POINTS_TABLE + " P "
        sql += "WHERE P." + Point.PropertyKey.groupId + " = " + groupId
        
        dbQueue.inDatabase { (db) in
            do{
                points = try Row.fetchAll(db, sql).map({ (row) -> Point in
                    return Point(row: row)
                })
            }
            catch(let error){
                print("PointsDAO.getPointsForGroup() -", error.localizedDescription)
            }
        }
        
        for point in points{
            if let team = TeamDAO.getTeam(withId: point.teamId){
                teamPoints.append(TeamPoint(team, point))
            }
        }
        
        return teamPoints
    }
    
}
