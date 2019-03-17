extension TeamsController: UICollectionViewDataSource{
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 2
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        if section == 0{
            return players.count
        }
        else if section == 1{
            return staff.count
        }
        assertionFailure("Invalid section index")
        return -1
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, referenceSizeForHeaderInSection section: Int) -> CGSize {
        if section == 1{
            return CGSize(width: collectionView.frame.width, height: 20.0)
        }
        return CGSize.zero
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        if indexPath.section == 0{
            let model = players[indexPath.item]
            let cell = TeamCollectionCell.dequeue(withCollectionView: collectionView, atIndexPath: indexPath, model: model)
            return cell
        }
        else if (indexPath.section == 1){
            let model = staff[indexPath.item]
            let cell = TeamCollectionStaffCell.dequeue(collectionView, indexPath, model: model)
            return cell
        }
        
        assertionFailure("Invalid section index")
        return UICollectionViewCell()
    }
    
}

extension TeamsController: UICollectionViewDelegateFlowLayout{
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        if indexPath.section == 0{
            return TeamCollectionCell.size(inCollectionView: collectionView)
        }
        else{
            return TeamCollectionStaffCell.size(collectionView)
        }
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        if indexPath.section == 0{
            let model = players[indexPath.item]
            self.presentPlayerDetails(player: model.player, position: model.position)
        }
    }
    
}
