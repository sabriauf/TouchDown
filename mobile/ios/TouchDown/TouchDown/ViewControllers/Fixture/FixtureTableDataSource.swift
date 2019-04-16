extension FixtureController: UITableViewDelegate, UITableViewDataSource{
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return viewModel == nil ? 0 : 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        let year = currentYearString ?? ""
        return viewModel?.fixturesForYears[year]?.count ?? 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let year = currentYearString ?? ""
        let model = viewModel!.fixturesForYears[year]![indexPath.row]
        if model.fixtureType! == .Pending{
            return FixturePendingCell.dequeue(withTableView: tableView, model: model.pendingData)
        }
        else{
            return FixtureResultCell.dequeue(withTableView: tableView, model: model.resultData)
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        let cell = tableView.cellForRow(at: indexPath)
        if cell is FixtureResultCell{
            let year = currentYearString ?? ""
            let match = viewModel!.fixturesForYears[year]![indexPath.row].match
            ignoreNextViewWillAppearCycle = true
            MatchSummaryTabsController.present(fromController: self, match: match!)
        }
        else{
            let year = currentYearString ?? ""
            let model = viewModel!.fixturesForYears[year]![indexPath.row]
            PendingFixtureDetailsController.present(self, model)
        }
    }
    
}
