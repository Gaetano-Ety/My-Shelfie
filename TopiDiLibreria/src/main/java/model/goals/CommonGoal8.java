package model.goals;

import model.gameObjects.BookShelf;
import model.gameObjects.Spot;

import java.util.ArrayList;

/**
 * The goal is reached when the bookshelf has
 * four rows with at most 3 different typed of cards
 */
public class CommonGoal8 extends CommonGoal {
	private final int ID = 8;
	
	/**
	 * @param bookShelf is a bookshelf
	 * @return true if the goal is achieved
	 * This method scans the bookshelf horizontally, for every row it creates a ArrayList of ints colorsInRow,
	 * to keep trace of different types of cards found in the current row. At the end of each row, if the size of colorsInRow
	 * is less or equal of 3, it increments an int counter. After each row has been checked, returns counter>=3
	 */

	@Override
	public boolean verifyCommonGoal(BookShelf bookShelf){
		int counter=0;
		for (int r = 0; r < 6; r++) {
			boolean valid = true;
			ArrayList<Integer> colorsInRow = new ArrayList<>();
			for (int c = 0; c < 5 ; c++) {
				Spot currentSpot = bookShelf.getSpotAt(c, r);
				if(currentSpot.isVoid()) valid=false;
				if(!colorsInRow.contains(currentSpot.getColorID())) colorsInRow.add(currentSpot.getColorID());
			}
			if(colorsInRow.size()<=3 && colorsInRow.size()!=0 && valid) counter++;
		}
		return counter>=4;
	}
	
	
	@Override
	public int getID(){
		return ID;
	}
}
