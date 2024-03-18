package model.goals;

import model.gameObjects.BookShelf;

//Tested and working

/**
 * The goal is reached when the bookshelf contains
 * 4 cards of the SAME TYPE in the 4 CORNERS
 * <p>
 * The method just checks the four corners
 */
public class CommonGoal3 extends CommonGoal{
	private final int ID = 3;
	
	@Override
	public boolean verifyCommonGoal(BookShelf bookShelf){
		int corner1 = bookShelf.getColorAt(0, 0).ordinal();
		int corner2 = bookShelf.getColorAt(4, 0).ordinal();
		int corner3 = bookShelf.getColorAt(0, 5).ordinal();
		int corner4 = bookShelf.getColorAt(4, 5).ordinal();
		return (corner1 == corner2 && corner2 == corner3 && corner3 == corner4) && corner1 != 0;
	}
	
	@Override
	public int getID(){
		return ID;
	}
}
