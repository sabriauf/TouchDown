extension LiveController: UITableViewDataSource{
    
    private func closestPreviousStartTime(to: String) -> String{
        
        if secondHalfStartTime != 0{
            let shst = Double(secondHalfStartTime.toSeconds())
            if shst < Double(to.toSeconds())!{
                let _shst = max(0, shst - (35.0 * 60.0))
                let returnValue = String(format: "%f", _shst) + "000" // to milliseconds
                return returnValue
            }
        }
        return gameStartTime.description
        
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.scoresArr.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let scoreItem = self.scoresArr[indexPath.row]
        let referenceTime = self.closestPreviousStartTime(to: scoreItem.time)
        let type = TimeLineCellsType.cellType(forScore: scoreItem)
        
        var cell: UITableViewCell!
        
        switch(type){
            
        case .DetailCell:
            let detailCell = DetailTimeLineCell.dequeue(withTable: tableView)
            detailCell.intitialize(with: scoreItem, matchStartTime: referenceTime)
            cell = detailCell
            
        case .CardCell:
            let cardCell = CardTimeLineCell.dequeue(withTable: tableView)
            cardCell.initialize(with: scoreItem, matchStartTime: referenceTime)
            cell = cardCell
            
        case .EggLetterCell:
            let eggLetterCell = EggLetterTimeLineCell.dequeue(withTable: tableView)
            eggLetterCell.intialize(with: scoreItem, matchStartTime: referenceTime)
            cell = eggLetterCell
            
        case .SquareCell:
            let squareCell = ColorSquareTimeLineCell.dequeue(withTable: tableView)
            squareCell.initialize(with: scoreItem)
            cell = squareCell
            
        case .Unknown:
            print("Error: Unknown Cell Type returned")
            cell = UITableViewCell()
            
        }
        
        return cell
    }
    
}
