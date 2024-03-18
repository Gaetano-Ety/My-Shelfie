package model.goals;

import model.Color;
import model.gameObjects.BookShelf;

/**
 * The goal is reached when the bookshelf contains
 * two squares made of spots of the same color
 */
public class CommonGoal4 extends CommonGoal{
	private final int ID = 4;
	
	@Override
	public boolean verifyCommonGoal(BookShelf bookShelf){
		int c = 0;
		int r = 0;
		boolean OneSquareFound = false;
		for(int i = 0; i < BookShelf.getNumCols() - 1; i++){
			for(int j = 0; j < BookShelf.getNumRows() - 1; j++){
				Color color = bookShelf.getColorAt(i, j);
				if(color != Color.VOID &&
					color == bookShelf.getColorAt(i + 1, j) &&
					color == bookShelf.getColorAt(i, j + 1) &&
					color == bookShelf.getColorAt(i + 1, j + 1)
				){
					if(!OneSquareFound){
						c = i; //  saving the column of the first square found
						r = j; //  saving the row of the first square found
						OneSquareFound = true;
					}else if(!CommonGoal4.areOverlapping(i, j, c, r)) // if we find one square, the next one could be overlapping
						return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * this function controls if the two squares found are overlapping, that is to say that the bookshelf contains a
	 * 2x6 sub matrix instead of actually 2 squares
	 */
	private static boolean areOverlapping(int i1, int j1, int i2, int j2){
		return Math.abs(i1 - i2) < 2 && Math.abs(j1 - j2) < 2;
	}
	
	@Override
	public int getID(){
		return ID;
	}
}
