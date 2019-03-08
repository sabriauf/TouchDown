//
//  FixtureResultCell.swift
//  Touchdown
//
//  Created by Thisura on 3/4/19.
//

import UIKit

class FixtureResultCell: UITableViewCell {

    static let identifer = "FixtureResultCell"
    static func register(_ tv: UITableView){
        let nib = UINib(nibName: identifer, bundle: nil)
        tv.register(nib, forCellReuseIdentifier: identifer)
    }
    static func dequeue(withTableView: UITableView, model: FixtureViewModel.FixtureResultDetails) -> FixtureResultCell{
        let cell =  withTableView.dequeueReusableCell(withIdentifier: identifer) as! FixtureResultCell
        cell.setupLooks()
        cell.initialize(with: model)
        return cell
    }
    
    @IBOutlet weak var oponentSchoolImage: UIImageView!
    @IBOutlet weak var oponentNameLabel: UILabel!
    @IBOutlet weak var rcPointsLabel: UILabel!
    @IBOutlet weak var oponentPointsLabel: UILabel!
    @IBOutlet weak var oponentSchoolInitialsLabel: UILabel!
    @IBOutlet weak var matchDateLabel: UILabel!
    @IBOutlet weak var matchTimeLabel: UILabel!
    @IBOutlet weak var wonLabel: UILabel!
    
    // MARK: Anim flags
    fileprivate var highlightAnimStarted: Bool = false
    fileprivate var highlightAnimComplete: Bool = false
    fileprivate let animDuration: CFTimeInterval = 0.12
    
    private func initialize(with model: FixtureViewModel.FixtureResultDetails){
        oponentSchoolImage.load.request(with: URL(string: model.opCrestUrl)!, placeholder: nil) { (_, _, _) in
            // No completion block stuff
            // ... for now.
        }
        oponentNameLabel.text = model.opponentName
        rcPointsLabel.text = model.rcScoreString
        oponentPointsLabel.text = model.opScoreString
        oponentSchoolInitialsLabel.text = model.opInitials
        matchDateLabel.text = model.matchDateString
        matchTimeLabel.text = model.matchStatus
        wonLabel.text = model.matchResult
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
    
}
