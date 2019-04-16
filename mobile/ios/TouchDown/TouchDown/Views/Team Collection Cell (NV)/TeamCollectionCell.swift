import UIKit
import ImageLoader

// NOTE: Folder marked NV because the View is in the storyboard.

class TeamCollectionCell: UICollectionViewCell {

    static let identifier = "TeamCollectionCell"
    static func dequeue(withCollectionView: UICollectionView, atIndexPath: IndexPath, model: CompositePlayer) -> TeamCollectionCell{
        let cell = withCollectionView.dequeueReusableCell(
            withReuseIdentifier: self.identifier,
            for: atIndexPath) as! TeamCollectionCell
        cell.initialize(model)
        return cell
    }
    static func size(inCollectionView: UICollectionView) -> CGSize{
        let selfWidth = CGFloat(80.0)
        let selfAspect = 0.7 // width / height
        let selfHeight = selfWidth * CGFloat((1.0 / selfAspect))
        return CGSize(width: selfWidth, height: selfHeight)
    }
    
    @IBOutlet weak var teamNumberLabel: UILabel!
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var playerImage: UIImageView!
    
    private func initialize(_ model: CompositePlayer){
        let firstName = model.player.name.split(separator: " ").first!
        nameLabel.text = String(firstName)
        teamNumberLabel.text = String(format: "%02d", model.position!.posNo)
        
        if let _url = URL(string: model.player?.getImageUrl() ?? ""){
            playerImage.load.request(with: _url, placeholder: Constant.IMAGE_PLACEHOLDER) { (_, _, _) in
                // No completion block content for now
            }
        }
    }
    
}
