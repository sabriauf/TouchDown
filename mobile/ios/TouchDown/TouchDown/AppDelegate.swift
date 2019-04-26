//
//  AppDelegate.swift
//  Touchdown
//
//  Created by Thisura on 2/18/18.
//

import UIKit
import DropDown
import Firebase
import UserNotifications
import ObjectMapper

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    private enum NotificationType{
        case Generic
        case Score
        case Sync
        case FullSync
    }
    
    var window: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
        
        DropDown.startListeningToKeyboard()
        DbHelper.initialize()
        FirebaseApp.configure()
        
        if #available(iOS 10.0, *) {
            // For iOS 10 display notification (sent via APNS)
            UNUserNotificationCenter.current().delegate = self
            
            var authOptions: UNAuthorizationOptions!
            if SettinsHelper.shouldRegisterWithSound(){
                authOptions = [.alert, .badge, .sound]
            }
            else{
                authOptions = [.alert, .badge]
            }
                
            UNUserNotificationCenter.current().requestAuthorization(options: authOptions) { (status, error) in
                print("Registered for notifications:", status, error?.localizedDescription ?? "No error")
            }
        } else {
            let settings: UIUserNotificationSettings =
                UIUserNotificationSettings(types: [.alert, .badge, .sound], categories: nil)
            application.registerUserNotificationSettings(settings)
        }
        
        application.registerForRemoteNotifications()
        
        Messaging.messaging().delegate = self
        
        application.applicationIconBadgeNumber = 0
        
        return true
    }

    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        let token = deviceToken.map { String(format: "%02.2hhx", $0) }.joined()
        print("❇️ Registered for Notifications", token)
        Messaging.messaging().apnsToken = deviceToken
        
        // Subscribe to topics set 2 based on app environment
        // TODO
    }
    
    func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable : Any], fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
        print("🔔 Recieved remote notification: ", userInfo)
        completionHandler(.newData)
    }
    
    func applicationDidEnterBackground(_ application: UIApplication) {
        print("Gone background")
    }
    
    func applicationWillEnterForeground(_ application: UIApplication) {
        print("Going foreground")
    }

}

extension UINavigationController{
    
    func clearHairLineShadow(){
        navigationBar.setBackgroundImage(UIImage(), for: UIBarMetrics.default)
        navigationBar.shadowImage = UIImage()
    }
    
}

extension AppDelegate: UNUserNotificationCenterDelegate{
    
    func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
        let data = response.notification.request.content.userInfo
        print("Recieved notification.", data)
        
        let type = getNotificationType(data)
        if type == .Score{
            if let score = dataAsScoreObject(data){
                print("DIDRECEIVE: ⭐️⭐️ Handling notification as score object")
                handleScoreNotification(score)
            }
        }
        else if type == .Sync{
            SyncHelper.handleSyncNotification(window: window!, fullSync: false)
        }
        else if type == .FullSync{
            SyncHelper.handleSyncNotification(window: window!, fullSync: true)
        }
        completionHandler()
    }
    
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        let data = notification.request.content.userInfo
        print("Will present notification.", data)
        
        // TODO
        // Show the notification if it doesn't have
        // an "object" key
        
        let type = getNotificationType(data)
        var notificationWasHandled: Bool = false
        if type == .Score{
            if let score = dataAsScoreObject(data){
                print("WILLRECEIVE: ⭐️⭐️ Handling notification as score object")
                handleScoreNotification(score)
                notificationWasHandled = true
            }
        }
        else if type == .Sync{
            SyncHelper.handleSyncNotification(window: window!, fullSync: false)
            notificationWasHandled = true
        }
        else if type == .FullSync{
            SyncHelper.handleSyncNotification(window: window!, fullSync: true)
            notificationWasHandled = true
        }
        
        if !notificationWasHandled{
            print("WILLRECEIVE: ⭐️ Handling notification as ... notification")
            if SettinsHelper.shouldRegisterWithSound(){
                completionHandler([.sound, .alert, .badge])
            }
            else{
                completionHandler([.alert, .badge])
            }
        }
        else{
            completionHandler([])
        }
    }
    
    private func getNotificationType(_ data: [AnyHashable: Any]) -> NotificationType{
        if data["score"] != nil{
            return .Score
        }
        else if let o = data["object"] as? String{
            if ScoreAsNotificationObject(JSONString: o) != nil{
                return .Score
            }
        }
        else if data["sync"] != nil{
            return .Sync
        }
        else if data["full_sync"] != nil{
            return .FullSync
        }
        return .Generic
    }
    
    private func dataAsScoreObject(_ data: [AnyHashable: Any]) -> Score?{
        
        // Tries two different methods of extracting the score
        // object from the notification details
        
        if let s = data["score"] as? String{
            let scoreObject = Score(JSONString: s)
            return scoreObject
        }
        else if let o = data["object"] as? String{
            if let scoreFromNotificationObject = ScoreAsNotificationObject(JSONString: o){
                return scoreFromNotificationObject.score ?? nil
            }
        }
        return nil
    }
    
    private func handleScoreNotification(_ score: Score){
        DispatchQueue.global().async {
            let msg = SyncHelper.handleScoreNotification(score)
            DispatchQueue.main.async {
                NotificationCenter.default.post(
                    name: NSNotification.Name.recievedScoreNotification,
                    object: nil,
                    userInfo: ["message": msg])
            }
        }
    }
    
}

extension AppDelegate: MessagingDelegate{
    
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String) {
        print("🎃 Firebase registration token: \(fcmToken)")
        
        // Unregister from legacy topics
        // (ones without the .ios postfix)
        
        unSubFromLegacyTopics()
        
        // Re-register for topics
        
        subToTopic("score.ios")
        subToTopic("other_matches.ios")
        subToTopic("general.ios")
        subToTopic("Other_matches.ios")
        subToTopic("General.ios")
        subToTopic("settings.ios")
        
        #if DEBUG
            print("🇱🇰 The app is in debug mode")
            subToTopic("test2.ios")
            AlertBoxHelper.showAlertBox(
                from: window!.rootViewController!,
                "Developer Mode",
                "You are using a developer signed version of Touchdown")
        #else
            print("💃 The app is in release mode")
        #endif
    }
    
    func messaging(_ messaging: Messaging, didReceive remoteMessage: MessagingRemoteMessage) {
        print("⭐️ Recieved Notification: ")
    }
    
    private func unSubFromLegacyTopics(){
        unSubFromTopic("score")
        unSubFromTopic("other_matches")
        unSubFromTopic("general")
        unSubFromTopic("Other_matches")
        unSubFromTopic("General")
        unSubFromTopic("settings")
    }
    
    private func unSubFromTopic(_ topic: String){
        Messaging.messaging().unsubscribe(fromTopic: topic) { (error) in
            if let e = error{
                print("Error while unsubscribing from topic \"\(topic)\". Error:", e.localizedDescription)
            }
            else{
                print("Successfully unsubscribed from topic \"\(topic)\"")
            }
        }
    }
    
    private func subToTopic(_ topic: String){
        Messaging.messaging().subscribe(toTopic: topic) { (error) in
            if let e = error{
                print("Error while subscribing to topic \"\(topic)\". Error:", e.localizedDescription)
            }
            else{
                print("Successfully subscribed to topic \"\(topic)\"")
            }
        }
    }
}
