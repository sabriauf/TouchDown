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
        
        if let score = dataAsScoreObject(data){
            print("DIDRECEIVE: ⭐️ Handling notification as score object")
            handleScoreNotification(score)
        }
        completionHandler()
    }
    
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        let data = notification.request.content.userInfo
        print("Will present notification.", data)
        
        // TODO
        // Show the notification if it doesn't have
        // an "object" key
        
        if let score = dataAsScoreObject(data){
            print("WILLRECEIVE: ⭐️ Handling notification as score object")
            handleScoreNotification(score)
        }
        else{
            print("WILLRECEIVE: ⭐️ Handling notification as ... notification")
            if SettinsHelper.shouldRegisterWithSound(){
                completionHandler([.sound, .alert, .badge])
            }
            else{
                completionHandler([.alert, .badge])
            }
        }
    }
    
    private func dataAsScoreObject(_ data: [AnyHashable: Any]) -> Score?{
        if let s = data["score"] as? String{
            let scoreObject = Score(JSONString: s)
            return scoreObject
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
        
        // Re-register for topics
        subToTopic("score")
        subToTopic("other_matches")
        subToTopic("general")
        subToTopic("Other_matches")
        subToTopic("General")
        subToTopic("settings")
    }
    
    func messaging(_ messaging: Messaging, didReceive remoteMessage: MessagingRemoteMessage) {
        print("⭐️ Recieved Notification: ")
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
