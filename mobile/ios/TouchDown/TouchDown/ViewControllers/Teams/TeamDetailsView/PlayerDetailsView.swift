//
//  TeamDetailsView.swift
//  Touchdown
//
//  Created by Thisura on 2/24/18.
//

import UIKit

class PlayerDetailsView: UIViewController{
    
    static let identifer = "PlayerDetailsView"
    static func getInstance(withPlayer: Player, and position: Position, yearRef: Int) -> PlayerDetailsView{
        let sb = UIStoryboard(name: "Main", bundle: nil)
        let i = sb.instantiateViewController(withIdentifier: self.identifer) as! PlayerDetailsView
        i.player = withPlayer
        i.position = position
        i.year = yearRef
        return i
    }
    
    @IBOutlet weak var playerImage: UIImageView!
    @IBOutlet weak var playerNumberLabel: UILabel!
    @IBOutlet weak var playerNameLabel: UILabel!
    @IBOutlet weak var playerAgeLabel: UILabel!
    @IBOutlet weak var playerHeightLabel: UILabel!
    @IBOutlet weak var playerYearLabel: UILabel!
    @IBOutlet weak var playerWeightLabel: UILabel!
    
    @IBOutlet weak var containerView: UIView!
    var player: Player!
    var position: Position!
    var year: Int!
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        setData()
        
        let zeroFrame = CATransform3DMakeScale(0.0, 0.0, 0.0)
        let normalFrame = CATransform3DMakeScale(1.0, 1.0, 1.0)
        //self.containerView.layer.transform = zeroFrame
        
        let scaleAnim = CABasicAnimation()
        scaleAnim.keyPath = "transform"
        scaleAnim.duration = 0.3
        scaleAnim.fromValue = zeroFrame as Any
        scaleAnim.toValue = normalFrame as Any
        
        self.containerView.layer.add(scaleAnim, forKey: "transform")
    }
    
    func setData(){
        if let url = URL(string: player.getImageUrl()){
            playerImage.load.request(with: url, placeholder: Constant.IMAGE_PLACEHOLDER) { (_, _, _) in
                // No compleion block yet
            }
        }
        playerNumberLabel.text = String(format: "%02d", position.posNo)
        playerNameLabel.text = player.name
        playerAgeLabel.text = "Age: " + player.getAge()
        playerHeightLabel.text = "Height: " + player.getHeight()
        playerYearLabel.text = "Getting year..."
        playerWeightLabel.text = "Weight: " + player.getWeight()
        DispatchQueue.global().async { [weak self] in
            if let s = self{
                if let playerTeam = PlayerTeamDAO.getPlayerTeam(playerId: s.player.idPlayer, year: s.year){
                    let colors = Int(playerTeam.colors)!
                    let ordinals = ["st", "nd", "rd"]
                    let year = String(
                        format: "%d%@ year",
                        colors,
                        colors > 2 ? "th" : ordinals[max(0, colors - 1)])
                    
                    DispatchQueue.main.async {
                        s.playerYearLabel.text = year
                    }
                }
                else{
                    DispatchQueue.main.async {
                        s.playerYearLabel.text = ""
                    }
                }
            }
        }
    }
    
    @IBAction func outsideTap(gr: UIGestureRecognizer){
        let point = gr.location(ofTouch: 0, in: self.view)
        if(!self.containerView.frame.contains(point)){
            self.dismiss(animated: true, completion: nil)
        }
    }
    
}
