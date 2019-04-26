//
//  FixtureResultCell.swift
//  Touchdown
//
//  Created by Thisura on 3/4/19.
//

import UIKit

class FixturePendingCell: UITableViewCell {
    
    static let identifer = "FixturePendingCell"
    static func register(_ tv: UITableView){
        let nib = UINib(nibName: identifer, bundle: nil)
        tv.register(nib, forCellReuseIdentifier: identifer)
    }
    static func dequeue(withTableView: UITableView, model: FixtureViewModel.FixturePendingDetails) -> FixturePendingCell{
        let cell =  withTableView.dequeueReusableCell(withIdentifier: identifer) as! FixturePendingCell
        cell.setupLooks()
        cell.initialize(with: model)
        return cell
    }
    
    @IBOutlet weak var oponentSchoolImage: UIImageView!
    @IBOutlet weak var oponentNameLabel: UILabel!
    @IBOutlet weak var matchDateLabel: UILabel!
    @IBOutlet weak var matchTimeLabel: UILabel!
    @IBOutlet weak var matchLocationLabel: UILabel!
    @IBOutlet weak var lastMatchLabel: UILabel!
    
    // MARK: Anim flags
    fileprivate var highlightAnimStarted: Bool = false
    fileprivate var highlightAnimComplete: Bool = false
    fileprivate let animDuration: CFTimeInterval = 0.12
    
    // MARK: Other details
    fileprivate var model: FixtureViewModel.FixturePendingDetails!
    
    private func initialize(with model: FixtureViewModel.FixturePendingDetails){
        self.model = model
        
        oponentSchoolImage.load.request(with: URL(string: model.opCrestUrl), placeholder: Constant.IMAGE_PLACEHOLDER) { (_, _, _) in
            // No completion block stuff
            // ... for now.
        }
        oponentNameLabel.text = model.opponentName
        matchDateLabel.text = model.matchDateString
        matchTimeLabel.text = model.matchTimeString
        matchLocationLabel.text = model.matchLocationString
        lastMatchLabel.text = "Last match: " + model.lastMatchDetails
    }
    
    override func setHighlighted(_ highlighted: Bool, animated: Bool) {
        super.setHighlighted(highlighted, animated: animated)
        
        if(highlighted){
            // shrink
            self.highlightCell()
        }
        else{
            if(self.highlightAnimStarted){
                DispatchQueue.global().async {
                    while(!self.highlightAnimComplete){ usleep(100) }
                    DispatchQueue.main.async {
                        self.unHighlightCell()
                    }
                }
            }
        }
    }
    
    private func highlightCell(){
        self.highlightAnimStarted = true
        self.highlightAnimComplete = false
        
        CATransaction.begin()
        let shrinkAnim = CABasicAnimation()
        shrinkAnim.keyPath = "transform"
        shrinkAnim.fromValue = CATransform3DMakeScale(1.0, 1.0, 1.0) as Any
        shrinkAnim.toValue = CATransform3DMakeScale(0.9, 0.9, 0.9) as Any
        shrinkAnim.duration = animDuration
        shrinkAnim.fillMode = kCAFillModeForwards
        shrinkAnim.isRemovedOnCompletion = false
        CATransaction.setCompletionBlock({
            self.highlightAnimComplete = true
        })
        self.contentView.layer.add(shrinkAnim, forKey: "transform_f")
        CATransaction.commit()
    }
    
    private func unHighlightCell(){
        self.highlightAnimStarted = false
        
        CATransaction.begin()
        let normAnim = CABasicAnimation()
        normAnim.keyPath = "transform"
        normAnim.fromValue = CATransform3DMakeScale(0.9, 0.9, 0.9) as Any
        normAnim.toValue = CATransform3DMakeScale(1.0, 1.0, 1.0) as Any
        normAnim.duration = animDuration
        normAnim.fillMode = kCAFillModeForwards
        normAnim.isRemovedOnCompletion = false
        self.contentView.layer.add(normAnim, forKey: "transform_b")
        CATransaction.commit()
    }
    
    private func setupLooks(){
        self.contentView.superview!.backgroundColor = UIColor.clear
        self.selectionStyle = .none
    }
    
    @IBAction func dateTapped(){
        
    }
    
    @IBAction func timeTapped(){
        
    }
    
    @IBAction func locationTapped(){
        
    }
    
}
