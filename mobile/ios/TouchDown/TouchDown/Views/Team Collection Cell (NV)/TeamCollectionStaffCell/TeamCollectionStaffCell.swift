//
//  TeamCollectionStaffCell.swift
//  Touchdown
//
//  Created by Thisura on 3/15/19.
//

import UIKit

class TeamCollectionStaffCell: UICollectionViewCell {

    private static let identifier = "TeamCollectionStaffCell"
    static func register(_ cv: UICollectionView){
        let nib = UINib(nibName: identifier, bundle: nil)
        cv.register(nib, forCellWithReuseIdentifier: identifier)
    }
    static func dequeue(_ cv: UICollectionView, _ ip: IndexPath, model: SupportStaff) -> TeamCollectionStaffCell{
        let cell = cv.dequeueReusableCell(withReuseIdentifier: identifier, for: ip) as! TeamCollectionStaffCell
        cell.initialize(model)
        return cell
    }
    static func size(_ cv: UICollectionView) -> CGSize{
        return CGSize(width: cv.frame.width, height: 39.0)
    }
    
    @IBOutlet private weak var staffNameLabel: UILabel!
    @IBOutlet private weak var positionLabel: UILabel!
    
    private func initialize(_ model: SupportStaff){
        staffNameLabel.text = model.name
        positionLabel.text = model.position
    }

}
