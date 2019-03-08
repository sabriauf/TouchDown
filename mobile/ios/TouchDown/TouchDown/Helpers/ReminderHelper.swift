//
//  ReminderHelper.swift
//  Touchdown
//
//  Created by Thisura on 3/7/19.
//

import Foundation

class ReminderHelper{
    
    static func isReminderAddedTo(_ match: Match) -> Bool{
        return true
    }
    
    static func addReminderTo(_ match: Match, handler: @escaping ((_ success: Bool) -> ())){
        
    }
    
    private static func getPath() -> String{
        let fm = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        let appending = "_rem_store.ffx"
        let path = fm.first!.appendingPathComponent(appending).path
        return path
    }
    
    /// Returns an array of matches
    /// that reminders have been added to
    private static func getReminderStore() -> [Int]{
        let path = getPath()
        if let eArr = NSKeyedUnarchiver.unarchiveObject(withFile: path) as? [Int]{
            return eArr
        }
        else{
            return []
        }
    }
    
    /// Adds the given matchId
    /// to the list of reminders if it
    /// doesn't already exist
    private static func writeToStore(_ matchId: Int){
        
    }
    
    
    static func showCalendar(_ date: Date){
        let interval = date.timeIntervalSinceReferenceDate
        let url = URL(string: "calshow:\(interval)")
        UIApplication.shared.open(url!, options: [:], completionHandler: nil)
    }
    
}
