extension TeamsController: UICollectionViewDataSource{
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return players.count > 0 ? 1 : 0
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return players.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let model = players[indexPath.item]
        let cell = TeamCollectionCell.dequeue(withCollectionView: collectionView, atIndexPath: indexPath, model: model)
        return cell
    }
    
}

extension TeamsController: UICollectionViewDelegateFlowLayout{
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return TeamCollectionCell.size(inCollectionView: collectionView)
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let model = players[indexPath.item]
        self.presentPlayerDetails(player: model.player, position: model.position)
    }
    
}
