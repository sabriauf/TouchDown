//
//  MatchSummaryPlayerDetailRowView.swift
//  Touchdown
//
//  Created by Thisura on 3/4/19.
//

import Foundation
import ImageLoader
import UIKit

class MatchSummaryPlayerDetailRowView: UIView{
    
    static func proposedSize(containerWidth: CGFloat) -> CGSize{
        return CGSize(width: containerWidth, height: 85.0)
    }
    
    @IBOutlet private weak var playerImage: UIImageView!
    @IBOutlet private weak var playerName: UILabel!
    @IBOutlet private weak var scoreActionsCollectionView: UICollectionView!
    
    var scores: [Score.Action: Int] = [:]
    
    @discardableResult
    func initialize(player: Player, scores: [Score]) -> MatchSummaryPlayerDetailRowView{
        if let fineUrl = URL(string: player.getImageUrl()){
            playerImage.load.request(with: fineUrl, placeholder: Constant.IMAGE_PLACEHOLDER) { (_, _, _) in
                // No completion block
                // ... for now
            }
        }
        
        playerName.text = player.name
        
        for score in scores{
            self.scores[score.action] = (self.scores[score.action] ?? 0) + 1
        }
        
        setupCollectionView()
        scoreActionsCollectionView.reloadData()
        
        return self
    }
    
    func setupCollectionView(){
        MatchSummaryScoreCell.register(scoreActionsCollectionView)
        scoreActionsCollectionView.delegate = self
        scoreActionsCollectionView.dataSource = self
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        internalInit()
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        internalInit()
    }
    
    private var view: UIView!
    
    private func internalInit(){
        view = loadViewFromNib()
        view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        view.frame = bounds
        addSubview(view)
    }
    
    private func loadViewFromNib() -> UIView{
        let nib = UINib(nibName: "MatchSummaryPlayerDetailRowView", bundle: nil)
        let loadedView = nib.instantiate(withOwner: self, options: nil).first as! UIView
        return loadedView
    }
    
}

extension MatchSummaryPlayerDetailRowView: UICollectionViewDataSource, UICollectionViewDelegate, UICollectionViewDelegateFlowLayout{
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return scores.count
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return MatchSummaryScoreCell.proposedSize()
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
        return 2.0
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumInteritemSpacingForSectionAt section: Int) -> CGFloat {
        return 2.0
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let keys = scores.keys
        let index = keys.index(keys.startIndex, offsetBy: indexPath.item)
        let key = keys[index]
        let value = scores[key] ?? 0
        return MatchSummaryScoreCell.dequeue(
            collectionView,
            ip: indexPath,
            action: key,
            count: value)
    }
    
}
