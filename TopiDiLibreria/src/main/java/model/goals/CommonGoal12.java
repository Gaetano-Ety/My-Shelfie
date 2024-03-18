package model.goals;

import model.gameObjects.BookShelf;

/**
 * The goal is reached when the bookshelf contains
 * a descendant or an ascendant "STAIR"
 */

public class CommonGoal12 extends CommonGoal{
	private final int ID = 12;
	
	//Tested working
	@Override
	public boolean verifyCommonGoal(BookShelf bookShelf){
		int free0 = bookShelf.freeInColumn(0);
		
		// Each column must have at least a tile
		if(free0 == bookShelf.nRows()) return false;
		
		boolean isAscendant = true, isDescendant = true;
		for(int i = 1; i < bookShelf.nColumns(); i++){
			int t = bookShelf.freeInColumn(i);
			
			// Each column must have at least a tile
			if(t == bookShelf.nRows()) return false;
			
			// Ascendant stair
			if(isAscendant) isAscendant = (free0 == t + i);
			
			// Descendant stair
			if(isDescendant) isDescendant = (free0 == t - i);
			
			if(!(isDescendant || isAscendant)) return false;
		}
		return true;
	}
	
	@Override
	public int getID(){
		return ID;
	}
}