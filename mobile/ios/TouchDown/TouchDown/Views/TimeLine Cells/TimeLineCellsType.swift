//
//  TimeLineCellsType.swift
//  Touchdown
//
//  Created by Thisura on 3/4/19.
//

import Foundation

class TimeLineCellsType{
    
    enum CellType {
        case DetailCell
        case SquareCell
        case EggLetterCell
        case CardCell
        case Unknown
    }
    
    static func cellType(forScore: Score) -> TimeLineCellsType.CellType{
        
        var cellType: TimeLineCellsType.CellType = .Unknown
        let actionType = forScore.action ?? .UNDEFINED
        
        if(actionType == .UNDEFINED){
            print("Unknown Action Type in Score object:", forScore.action)
            return cellType
        }
        
        if Score.Action.cardTypes().contains(actionType){
            cellType = .CardCell
        }
        else if Score.Action.eggTypes().contains(actionType){
            cellType = .EggLetterCell
        }
        else if Score.Action.squareTypes().contains(actionType){
            cellType = .SquareCell
        }
        else{
            cellType = .DetailCell
        }
        
        return cellType
    }
    
}
