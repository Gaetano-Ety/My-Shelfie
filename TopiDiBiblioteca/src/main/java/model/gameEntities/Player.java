package model.gameEntities;

import model.Color;
import model.exceptions.FullColumnException;
import model.exceptions.InvalidMatrixException;
import model.exceptions.InvalidStringExcepton;
import model.exceptions.NotAvailableGoalException;
import model.gameObjects.BookShelf;
import model.gameObjects.SpotMatrix;
import model.goals.PersonalGoal;
import org.json.JSONObject;

import javax.management.InvalidAttributeValueException;

public class Player{
	private final BookShelf bookShelf;
	private int score;
	private boolean finalScoreDecided;
	private boolean goal1, goal2;
	private final String nickName;
	private final PersonalGoal personalGoal;
	
	public Player(String nickName) throws NotAvailableGoalException{
		this.nickName = nickName;
		bookShelf = new BookShelf();
		score = 0;
		finalScoreDecided = false;
		goal1 = goal2 = false;
		personalGoal = new PersonalGoal();
	}
	
	/**
	 * Create a new player based on a string representation of a player.
	 * NB: The string representation is in JSON form, but is different from a hypothetical "new JSONObject(new Player(...)).toString()"
	 */
	public Player(JSONObject obj) throws InvalidStringExcepton{
		try{
			nickName = obj.getString("nickName");
			
			bookShelf = new BookShelf();
			bookShelf.copyMatrix(new SpotMatrix(obj.getString("bookShelf")));
			
			score = obj.getInt("score");
			
			goal1 = obj.getBoolean("goal1");
			goal2 = obj.getBoolean("goal2");
			
			personalGoal = new PersonalGoal(obj.getInt("personalGoal"));
		}catch(InvalidMatrixException | NotAvailableGoalException e){
			throw new InvalidStringExcepton();
		}
	}
	
	public String getNickName(){
		return nickName;
	}
	
	public int getGoalID(){
		return personalGoal.getGoalID();
	}
	
	public BookShelf getBookshelf(){
		BookShelf out = new BookShelf();
		try{
			out.copyMatrix(bookShelf.clone());
		}catch(InvalidMatrixException ignored){/* Impossible */}
		return out;
	}
	
	public int getActualScore(){
		return score;
	}
	
	/**
	 * Verify if the player has already completed the goal
	 *
	 * @param n indicates witch goal to return
	 * @return goal1 or goal2 depending on n
	 * @throws InvalidAttributeValueException -- throws if the attribute is invalid
	 */
	public boolean goalAlreadyCompleted(int n) throws InvalidAttributeValueException{
		return switch(n){
			case 1 -> goal1;
			case 2 -> goal2;
			default -> throw new InvalidAttributeValueException();
		};
	}
	
	/**
	 * sets goal1 or goal2 to true, depending on n
	 *
	 * @param n      indicates which goal has to be set to true
	 * @param points are the points that have to be added to ste total score
	 * @throws InvalidAttributeValueException -- throws if the attribute is invalid
	 */
	public void setGoalAndPoints(int points, int n) throws InvalidAttributeValueException{
		switch(n){
			case 1 -> goal1 = true;
			case 2 -> goal2 = true;
			default -> throw new InvalidAttributeValueException();
		}
		
		score += points;
	}
	
	/**
	 * Add a new card to a column of bookshelf
	 */
	public void putInBookshelf(int column, Color color) throws FullColumnException{
		bookShelf.addCard(column, color);
	}
	
	public boolean isCompleted(){
		for(int i = 0; i < bookShelf.nColumns(); i++)
			if(bookShelf.freeInColumn(i) != 0) return false;
		return true;
	}
	
	public void addPoints(int n){
		score += n;
	}
	
	/**
	 * Update and return the score of a player <br/>
	 * If the final score is decided, this method return the score without modify it.
	 * If the one or more common goals where reached, the point have already been added to the attribute score, so this method does 2 things: <br/>
	 * 1) adds to score the result of the method scoring of PersonalGoal <br/>
	 * 2) computes the score due to the groups of adjacent tiles in the bookshelf, using  bookshelf's method nOfGroups and comparing the result with the corresponding score
	 * @return the score of the player
	 */
	public int generateScore(){
		if(finalScoreDecided) return score;
		else finalScoreDecided = true;
		
		score += personalGoal.scoring(bookShelf);
		
		//checking adjacent cards
		for(int i = 6, cont = 0; i > 2; i--){
			int groups = bookShelf.nOfGroups(i);
			switch(i){
				case 6 -> score += (groups * 8);
				case 5 -> score += ((groups - cont) * 5);
				case 4 -> score += ((groups - cont) * 3);
				case 3 -> score += ((groups - cont) * 2);
			}
			cont = groups;
		}
		return score;
	}
	
	/**
	 * @return a description of the player as a JSON string.
	 * The string will look like this:
	 * <p>
	 * "nickName" : nickName
	 * "bookShelf" : [bookshelf],
	 * "score" : score,
	 * "gaol1" : goal1,
	 * "gaol2" : goal2,
	 * "personalGoal" : ID of personal goal
	 */
	@Override
	public String toString(){
		return
				"{" +
						"\"nickName\":\"" + nickName + "\"," +
						"\"bookShelf\":\"" + bookShelf.toString() + "\"," +
						"\"score\":" + score + "," +
						"\"goal1\":" + goal1 + "," +
						"\"goal2\":" + goal2 + "," +
						"\"personalGoal\":" + personalGoal.getGoalID() +
						"}";
	}
}
