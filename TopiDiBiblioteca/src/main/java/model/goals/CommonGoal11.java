package model.goals;

import model.Color;
import model.gameObjects.BookShelf;

/**
 * The goal is reached when the bookshelf contains
 * a cross made of spots of the same type
 */
public class CommonGoal11 extends CommonGoal{
	private final int ID = 11;
	
	@Override
	public boolean verifyCommonGoal(BookShelf bookShelf){
		for(int i = 0; i < BookShelf.getNumCols() - 2; i++){
			for(int j = 0; j < BookShelf.getNumRows() - 2; j++){
				Color color = bookShelf.getColorAt(i, j);
				if(color != Color.VOID &&
					color == bookShelf.getColorAt(i + 2, j) &&
					color == bookShelf.getColorAt(i, j + 2) &&
					color == bookShelf.getColorAt(i + 1, j + 1) &&
					color == bookShelf.getColorAt(i + 2, j + 2)
				) return true;
			}
		}
		return false;
	}
	
	@Override
	public int getID(){
		return ID;
	}
}
