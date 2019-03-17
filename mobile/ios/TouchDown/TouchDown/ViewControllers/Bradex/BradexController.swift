//
//  BradexController.swift
//  Touchdown
//
//  Created by Thisura on 3/15/19.
//

import Foundation

class BradexController: UIViewController{
    
    private static let identifier = "BradexController"
    static func shouldShowBradexTab() -> Bool{
        var canShowBradexTab: Bool = true
        do{
            canShowBradexTab = MetaDataDAO.isMetaDataPresent()
        }
        catch(let error){
            canShowBradexTab = false
        }
        
        return canShowBradexTab
    }
    static func getInstance() -> BradexController{
        let sb = UIStoryboard(name: "Main", bundle: nil)
        let vc = sb.instantiateViewController(withIdentifier: identifier) as! BradexController
        return vc
    }
    
    @IBOutlet private weak var bradexImage: UIImageView!
    @IBOutlet private weak var loadingView: LoadingView!
    @IBOutlet private weak var helpButton: UIView!
    @IBOutlet private weak var bradExImgTap: UITapGestureRecognizer!
    @IBOutlet private weak var helpImgTap: UITapGestureRecognizer!
    
    private var tapLink: String? = nil{
        didSet{
            if let l = tapLink{
                let c = l.components(separatedBy: "fb://facewebmodal/f?href=")
                tapLink = c.last
            }
        }
    }
    private var imgLink: String? = nil
    private var isFirstTime: Bool = true
    
    override func viewDidLoad() {
        super.viewDidLoad()
        if isFirstTime{
            registerSyncListener()
            setupNotificationObservers()
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        if isFirstTime{
            if !SyncHelper.shouldWaitForSync(){
                loadData()
            }
            else{
                loadingView.show {
                    self.bradexImage.isHidden = true
                    self.helpButton.isHidden = true
                    self.bradExImgTap.isEnabled = false
                    self.helpImgTap.isEnabled = false
                }
                print("Waiting for sync listener to complete")
            }
            isFirstTime = false
        }
    }
    
    private func loadData(){
        loadingView.show {
            self.bradexImage.isHidden = true
            self.helpButton.isHidden = true
            self.bradExImgTap.isEnabled = false
            self.helpImgTap.isEnabled = false
        }
        
        DispatchQueue.global().async { [weak self] in
            if let s = self{
                s.imgLink = MetaDataDAO.getMetaData(key: Constant.META_DATA_KEYS.BRADEX_IMAGE)
                s.tapLink = MetaDataDAO.getMetaData(key: Constant.META_DATA_KEYS.BRADEX_LINK)
                DispatchQueue.main.async {
                    if s.imgLink != nil && s.tapLink != nil{
                        if let url = URL(string: s.imgLink ?? ""){
                            s.bradexImage.load.request(with: url, placeholder: nil, onCompletion: { (_, _, _) in
                                // Nothing in the completion block for now
                            })
                            s.loadingView.hide {
                                s.bradexImage.isHidden = false
                                s.helpButton.isHidden = false
                                s.bradExImgTap.isEnabled = true
                                s.helpImgTap.isEnabled = true
                            }
                        }
                    }
                }
            }
        }
    }
    
    private func setupNotificationObservers(){
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(self.toggleLoadingMode),
            name: NSNotification.Name.manualResyncStarted,
            object: nil)
    }
    
    private func registerSyncListener(){
        GlobalData.register(
            listener: { [weak self] in
                print("🛑 - BradexController recieved synced notification")
                self?.loadData()
            },
            withId: BradexController.identifier)
    }
    
    @objc private func toggleLoadingMode(){
        loadingView.show {
            self.bradexImage.isHidden = true
            self.helpButton.isHidden = true
            self.bradExImgTap.isEnabled = false
            self.helpImgTap.isEnabled = false
        }
    }
    
    @IBAction private func bradexImagePressed(_ sender: UITapGestureRecognizer){
        if let url = URL(string: tapLink ?? ""){
            UIApplication.shared.open(url, options: [:], completionHandler: nil)
        }
    }
    
    @IBAction private func helpImagePressed(_ sender: UITapGestureRecognizer){
        let alert = UIAlertController(title: "Touchdown", message: "Click on the image shown to visit the official page for the Bradby Express", preferredStyle: .alert)
        let okAction = UIAlertAction(title: "OK", style: .default, handler: nil)
        alert.addAction(okAction)
        present(alert, animated: true, completion: nil)
    }
    
}
