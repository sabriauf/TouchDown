//
//  GlobalData.swift
//  Touchdown
//
//  Created by Thisura on 3/11/18.
//

import UIKit
import Foundation

class GlobalData{
    
    static var dataSyncListeners: [String: () -> ()] = [:]
    
    public static func register(listener: @escaping () -> (), withId: String){
        self.dataSyncListeners[withId] = listener
    }
    
    public static func remove(listenerWithId: String){
        /*if self.dataSyncListeners[listenerWithId] == nil{
            print("NOTE - sync listener for id", listenerWithId, "already nil.")
        }
        else{
            self.dataSyncListeners.removeValue(forKey: listenerWithId)
        }*/
    }
    
    public static func callAllSyncListeners(){
        for (_, listener) in dataSyncListeners{
            listener()
        }
    }
    
}
