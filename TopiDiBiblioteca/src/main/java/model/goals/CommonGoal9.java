package model.goals;

import model.gameObjects.BookShelf;
import model.gameObjects.Spot;

import java.util.ArrayList;

/**
 * The goal is reached when the bookshelf contains at least 2 columns with all different types of tiles
 */
public class CommonGoal9 extends CommonGoal {
	private final int ID = 9;
	
	/**
	 * @param bookShelf is a bookshelf
	 * @return true if the goal is reached
	 * <p>
	 * This method scans the bookshelf vertically, for every column it creates a ArrayList of ints colorsInColumn,
	 * to keep trace of different types of cards found in the current column. At the end of each column, if the size of colorsInColumn
	 * is 6, it increments an int counter. After each column has been checked, returns counter>=2
	 */
	@Override
	public boolean verifyCommonGoal(BookShelf bookShelf){
		int counter=0;
		for (int c = 0; c < 5; c++) {
			ArrayList<Integer> colorsInCol = new ArrayList<>();
			for (int r = 0; r < 6 ; r++) {
				//I'm in a column
				Spot currentSpot = bookShelf.getSpotAt(c, r);
				if(!colorsInCol.contains(currentSpot.getColorID()) && !currentSpot.isVoid()) colorsInCol.add(currentSpot.getColorID());
			}
			if(colorsInCol.size()==6) counter++;
		}
		return counter>=2;
	}
	
	@Override
	public int getID(){
		return ID;
	}
}
