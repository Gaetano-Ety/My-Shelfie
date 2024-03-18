package model.goals;

import model.gameObjects.BookShelf;
import model.gameObjects.Spot;

import java.util.ArrayList;

/**
 * The goal is reached when the bookshelf contains at least 2 rows with 5 different types of tiles
 */
public class CommonGoal10 extends CommonGoal {
	private final int ID = 10;
	
	/**
	 * @param bookShelf is a bookshelf
	 * @return true if the goal is achieved
	 * <p>
	 * This method scans the bookshelf horizontally, for every row it creates a ArrayList of ints colorsInRow,
	 * to keep trace of different types of cards found in the current row. At the end of each row, if the size of colorsInRow
	 * is 5, it increments an int counter. After each row has been checked, returns counter>=2
	 */
	@Override
	public boolean verifyCommonGoal(BookShelf bookShelf){
		int counter=0;
		for (int r = 0; r < 6; r++) {
			ArrayList<Integer> colorsInRow = new ArrayList<>();
			for (int c = 0; c < 5 ; c++) {
				//I'm in a column
				Spot currentSpot = bookShelf.getSpotAt(c, r);
				if(!colorsInRow.contains(currentSpot.getColorID()) && !currentSpot.isVoid()) colorsInRow.add(currentSpot.getColorID());
			}
			if(colorsInRow.size()==5) counter++;
		}
		return counter>=2;
	}
	
	@Override
	public int getID(){
		return ID;
	}
}
