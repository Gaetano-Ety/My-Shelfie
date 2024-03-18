package model.goals;

import model.gameObjects.BookShelf;

/**
 * The goal is reached when the bookshelf contains at least
 * 6 COUPLES of adjacent tiles of the same type
 */
public class CommonGoal1 extends CommonGoal {
	private final int ID = 1;

	/**
	 *
	 * @param bookShelf is a bookshelf
	 * @return true if bookshelf contains at least 6 couples of adjacent tiles of the same type
	 *
	 * Uses nOfGroups method in class bookshelf
	 */
	@Override
	public boolean verifyCommonGoal(BookShelf bookShelf){
		return (bookShelf.nOfGroups(2)>=6);
	}
	
	@Override
	public int getID(){
		return ID;
	}
}
