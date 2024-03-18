package model.goals;

import model.gameObjects.BookShelf;
import model.gameObjects.Spot;

import java.util.ArrayList;

/**
 * The goal is reached when the bookshelf has 3 columns with AT MOST 3 different colors
 */
public class CommonGoal5 extends CommonGoal {
	private final int ID = 5;
	
	/**
	 * This method scans the bookshelf vertically, for every column it creates a ArrayList of ints colorsInColumn,
	 * to keep trace of different types of cards found in the current column. At the end of each column, if the size of colorsInColumn
	 * is less or equal of  3, it increments an int counter. After each column has been checked, returns counter>=3
	 * @param bookShelf is a bookshelf
	 * @return true if the goal is achieved
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
			if(colorsInCol.size()<=3 && colorsInCol.size()!=0) counter++;
		}
		return counter>=3;
	}
	
	@Override
	public int getID(){
		return ID;
	}
}
