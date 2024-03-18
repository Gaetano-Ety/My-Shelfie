package model.goals;

import model.gameObjects.BookShelf;

/**
 * The goal is reached if the bookshelf contains a DIAGONAL of 5 cards of the same type
 */
public class CommonGoal7 extends CommonGoal{
	private final int ID = 7;
	@Override
	public boolean verifyCommonGoal(BookShelf bookShelf){
		boolean isVerified1 = true;
		boolean isVerified2 = true;
		boolean isVerified3 = true;
		boolean isVerified4 = true;
		
		for(int c = 0, x = 4; c <= 3; c++, x--){
			if(isVerified1)
				if(bookShelf.getSpotAt(c, c).isVoid() || bookShelf.getColorAt(c, c) != bookShelf.getColorAt(c + 1, c + 1))
					isVerified1 = false;
			
			if(isVerified2)
				if(bookShelf.getSpotAt(c, c).isVoid() || bookShelf.getColorAt(c, c + 1) != bookShelf.getColorAt(c + 1, c + 2))
					isVerified2 = false;
			
			if(isVerified3)
				if(bookShelf.getSpotAt(c, c).isVoid() || bookShelf.getColorAt(c, x) != bookShelf.getColorAt(c + 1, x - 1))
					isVerified3 = false;
			
			if(isVerified4)
				if(bookShelf.getSpotAt(c, c).isVoid() || bookShelf.getColorAt(c, x + 1) != bookShelf.getColorAt(c + 1, x))
					isVerified4 = false;
		}
		
		return isVerified1 || isVerified2 || isVerified3 || isVerified4;
	}
	
	@Override
	public int getID(){
		return ID;
	}
}
