//
//  ScoreMessage.swift
//  Touchdown
//
//  Created by Thisura on 3/10/19.
//

import Foundation

/// Used as a type to send what actions need
/// to be performed after a score notification
/// was received
enum ScoreMessageDatabaseAction{
    case NewScore(Score)
    case UpdateScore(Score)
    case RemoveMatch
    case RemoveScore(Score)
}
