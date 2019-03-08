//
//  FixtureViewModel.swift
//  Touchdown
//
//  Created by Thisura on 2/6/19.
//

import Foundation

public class FixtureViewModel{
    
    public enum FixtureType{
        case Result
        case Pending
    }
    
    public struct FixturePendingDetails{
        var opCrestUrl: String!
        var opponentName: String!
        var matchDateString: String!
        var matchTimeString: String!
        var matchLocationString: String!
        var matchLocation: (longitude: Double, latitude: Double)!
        var lastMatchDetails: String!
        
        init(opCrest: String, op: String, matchDate: Date, locString: String, locLong: Double, locLat: Double, lastMatch: String){
            let df = DateFormatter()
            let f1 = "dd MMM yyyy"
            let f2 = "hh:mm a"
            
            opCrestUrl = opCrest
            opponentName = op
            
            // Date and Time
            df.dateFormat = f1
            matchDateString = df.string(from: matchDate)
            df.dateFormat = f2
            matchTimeString = df.string(from: matchDate)
            
            matchLocationString = locString
            matchLocation = (longitude: locLong, latitude: locLat)
            lastMatchDetails = lastMatch
        }
    }
    
    public struct FixtureResultDetails{
        var opCrestUrl: String!
        var opponentName: String!
        var rcScoreString: String!
        var opScoreString: String!
        var opInitials: String!
        var matchDateString: String!
        var matchStatus: String!
        var matchResult: String!
        
        init(crestUrl: String, op: String, matchDate: Date, rcScore: Int, opScore: Int, opi: String, mStatus: String, mResult: String){
            let df = DateFormatter()
            let f = "dd MMM yyyy"
            df.dateFormat = f
            
            opCrestUrl = crestUrl
            opponentName = op
            rcScoreString = rcScore.description
            opScoreString = opScore.description
            opInitials = opi
            matchDateString = df.string(from: matchDate)
            matchStatus = mStatus
            matchResult = mResult
        }
    }
    
    public class FixtureItemDetails{
        public var fixtureType: FixtureType!
        var match: Match!
        
        // Pending Details
        public var pendingData: FixturePendingDetails!
        
        // Result Details
        public var resultData: FixtureResultDetails!
        
        public init(){}
    }
    
    public var yearStrings: [String]!
    public var fixturesForYears: [String: [FixtureItemDetails]]!
    
    public init(years: [String], data: [String: [FixtureItemDetails]]){
        yearStrings = years
        fixturesForYears = data
    }
    
}
