package model.goals;

import model.gameObjects.BookShelf;

/**
 * The goal is reached when the bookshelf contains
 * AT LEAST 4 GROUPS of 4 adjacent tiles of the same type
 * <p>
 * uses nOfGroups method in bookshelf class
 */
public class CommonGoal2 extends CommonGoal {
	private final int ID = 2;
	
	@Override
	public boolean verifyCommonGoal(BookShelf bookShelf){
		return (bookShelf.nOfGroups(4) >= 4);
	}
	
	@Override
	public int getID(){
		return ID;
	}
}
