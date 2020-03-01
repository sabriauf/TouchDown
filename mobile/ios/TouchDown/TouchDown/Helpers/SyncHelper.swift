import Foundation
import UIKit
import GRDB
import Alamofire

class SyncHelper{
    
    struct UDKeys{
        static let lastSyncTime = "last_updated_timestamp"
    }
    
    private enum SyncState{
        case Initial
        case Running
        case NoNetwork
        case Complete
    }
    
    private static var internalSyncState: SyncState = .Initial
    
    /// Sets the last synced timestamp as now
    static func setLastUpdatedAsNow(){
        let epochTime = String(format: "%d000", Int(Date().timeIntervalSince1970))
        let key = UDKeys.lastSyncTime
        UserDefaults.standard.set(epochTime, forKey: key)
        print(key, "set as", epochTime)
    }
    
    /// Gets the last synced timestamp as a String
    static func getLastUpdatedTime() -> String?{
        let key = UDKeys.lastSyncTime
        if let value = UserDefaults.standard.value(forKey: key) as? String{
            print(key, "retrieved as", value)
            return value
        }
        else{
            print("Failed to retrieve", key)
            return nil
        }
    }
    
    static func shouldWaitForSync() -> Bool{
        return internalSyncState == .Running || internalSyncState == .Initial
    }
    
    static func syncNow(completeSync: Bool, completionHandler: @escaping () -> ()){
        let reachability = NetworkReachabilityManager()!
        
        if(reachability.isReachable){
            
            internalSyncState = .Running
            
            if completeSync{
                DbHelper.clearDb()
            }
            
            let params = [
                "last_updated": completeSync ? "00" : (getLastUpdatedTime() ?? "00")
            ]
            let headers = Constant.getHeaderParams()
            let url = Constant.getRequestUrl()
            
            Alamofire.request(
                url,
                method: .get,
                parameters: params,
                headers: headers).responseObject(completionHandler: { (response: DataResponse<ServerResponseSync>) in
                    if let optionalResponse = response.result.value{
                        print("Sync retrieved")
                        DispatchQueue.global().async {
                            DbHelper.saveToDb(serverResponse: optionalResponse)
                            self.internalSyncState = .Complete
                            self.setLastUpdatedAsNow()
                            print("Sync complete - Calling completion handlers")
                            
                            DispatchQueue.main.async{
                                completionHandler()
                            }
                        }
                        
                    }
                    else{
                        print("Sync failed - Response was nil")
                    }
                })
            
        }
        else{
            print("Sync failed - No network access")
            internalSyncState = .NoNetwork
            completionHandler()
        }
    }
    
    static func handleScoreNotification(_ score: Score) -> ScoreMessageDatabaseAction{
        let action = score.action
        if action == .UNDEFINED{
            if score.idScore == "0"{
                ScoreDAO.deleteScoresWithMatchId(score.matchId)
                MatchDAO.updateMatchStatus(matchId: score.matchId, status: .PENDING)
                return .RemoveMatch
            }
            else{
                ScoreDAO.deleteScoreWithId(score.idScore)
                return .RemoveScore(score)
            }
        }
        else{
            // Insertion will fail if there is already a score
            // with the ID as same as score.idScore
            let inserted = ScoreDAO.addScore(score)
            return inserted ? .NewScore(score) : .UpdateScore(score)
        }
    }
    
    static func handleSyncNotification(window: UIWindow, fullSync: Bool){
        if let rc = window.rootViewController as? TabsNavigationController{
            if let vc = rc.viewControllers.first as? TabsController{
                vc.startManualResync(completeSync: fullSync)
            }
        }
    }
    
}
