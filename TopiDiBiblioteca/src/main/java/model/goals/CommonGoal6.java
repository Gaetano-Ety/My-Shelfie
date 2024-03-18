package model.goals;

import model.Color;
import model.gameObjects.BookShelf;
import model.gameObjects.Spot;

/**
 * The goal is reached when the bookshelf contains
 * eight spots of the same type
 */
public class CommonGoal6 extends CommonGoal {
	private final int ID = 6;
	
	@Override
	public boolean verifyCommonGoal(BookShelf bookShelf){
		int[] colorCounters = new int[Color.nOfColors()];
		for(int col = 0; col < BookShelf.getNumCols(); col++){
			for(int row = 0; row < BookShelf.getNumRows(); row++){
				Spot spot = bookShelf.getSpotAt(col, row);
				if(spot.getColor() != Color.VOID){
					colorCounters[spot.getColor().ordinal() - 1]++;
					if(colorCounters[spot.getColor().ordinal() - 1] >= 8)
						return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public int getID(){
		return ID;
	}
}
