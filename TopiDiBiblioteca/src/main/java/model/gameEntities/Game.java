package model.gameEntities;

import model.Color;
import model.exceptions.*;
import model.gameObjects.Board;
import model.gameObjects.SpotMatrix;
import model.goals.CommonGoal;
import model.goals.CommonGoalFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import server.interfaces.Observable;
import server.interfaces.Observer;

import javax.management.InvalidAttributeValueException;
import java.util.*;

public class Game implements Observable{
	private final ArrayList<Observer> virtualPlayers = new ArrayList<>();
	private final int nPlayers;
	private int turn;
	private final Player[] players;
	private final Board board;
	private final CommonGoal goal1, goal2;
	
	public Game(int nPlayers) throws InvalidPlayersNumberException {
		if(nPlayers < 0 || nPlayers > 4) throw new InvalidPlayersNumberException();
		this.nPlayers = nPlayers;
		
		players = new Player[nPlayers];
		
		goal1 = CommonGoalFactory.createCommonGoal(nPlayers);
		goal2 = CommonGoalFactory.createCommonGoal(nPlayers);
		
		board = new Board(nPlayers);
		board.fill();
		
		turn = 0;
	}
	
	/**
	 * Create a game based on a JSONObject generate by a precedent game.toString() string
	 */
	public Game(JSONObject obj) throws InvalidStringExcepton {
		try{
			nPlayers = obj.getInt("nPlayers");
			players = new Player[nPlayers];
			
			turn = obj.getInt("turn");
			
			board = new Board(nPlayers);
			board.copyMatrix(new SpotMatrix(obj.getString("board")));
			board.resumeCardsBag(obj.getString("cardsBag"));
			
			goal1 = CommonGoalFactory.createCommonGoal(obj.getJSONObject("goal1"));
			goal2 = CommonGoalFactory.createCommonGoal(obj.getJSONObject("goal2"));
			
			JSONArray playersData = obj.getJSONArray("players");
			for(int c = 0; c < playersData.length(); c++)
				players[c] = new Player(playersData.getJSONObject(c));
			
		}catch(InvalidMatrixException | NotAvailableGoalException | InvalidPlayersNumberException |
			   InvalidActionException e){
			throw new InvalidStringExcepton();
		}
	}
	
	public void assignPlayer(int id, String nickName) throws NotAvailableGoalException{
		players[id] = new Player(nickName);
	}
	
	public int getPlayersNumber(){
		return nPlayers;
	}
	
	public String getPlayerNickName(int id){
		if(Objects.isNull(players[id])) return null;
		return players[id].getNickName();
	}
	
	public int getPersonalGoalID(int playerID){
		return players[playerID].getGoalID();
	}
	
	public int[] getGoalsIDs(){
		int[] result = new int[2];
		result[0] = goal1.getID();
		result[1] = goal2.getID();
		return result;
	}
	
	public String getBoard(){
		return board.toString();
	}
	
	public String getBookshelf(int id){
		return players[id].getBookshelf().toString();
	}
	
	public boolean isAFreeSpot(int x, int y){
		return board.isFree(x, y);
	}
	
	public boolean isVoidSpot(int x, int y){
		return board.getSpotAt(x, y).isVoid();
	}
	
	public int getActualScore(int playerID){
		return players[playerID].getActualScore();
	}
	
	/**
	 * Check if all the players are connected
	 */
	public boolean ready(){
		return Arrays.stream(players).noneMatch(Objects::isNull);
	}
	
	/**
	 * Increment the turn by 1
	 */
	public int nextTurn(){
		turn = (turn + 1) % nPlayers;
		return turn;
	}
	
	/**
	 * Refill the board if that is necessary
	 */
	public void refill(){
		if(board.isToRefill()) board.fill();
	}
	
	/**
	 * Make void the spot of the board in those coordinates
	 */
	public void removeFromBoard(int x, int y){
		board.remove(x, y);
	}
	
	/**
	 * Insert a card in a bookshelf
	 */
	public void addACardToBsPlayer(int playerID, int column, Color color) throws FullColumnException{
		players[playerID].putInBookshelf(column, color);
	}
	
	/**
	 * @return the stack of remaining scores of the goal
	 */
	public Stack<Integer> getGoalStack(int n){
		return switch(n){
			case 1 -> goal1.getStack();
			case 2 -> goal2.getStack();
			default -> throw new IllegalStateException();
		};
	}
	
	/**
	 * Check if the player has completed one of the goals and in that case assign the goal score to the player
	 */
	public void verifyGoal(int playerID){
		try{
			// Goal 1
			if(!players[playerID].goalAlreadyCompleted(1)) if(goal1.verifyCommonGoal(players[playerID].getBookshelf()))
				players[playerID].setGoalAndPoints(goal1.achievedGoal(), 1);
			
			// Goal 2
			if(!players[playerID].goalAlreadyCompleted(2)) if(goal2.verifyCommonGoal(players[playerID].getBookshelf()))
				players[playerID].setGoalAndPoints(goal2.achievedGoal(), 2);
		}catch(InvalidAttributeValueException ignored){/* Impossible */}
	}
	
	public boolean isCompletedBookshelf(int playerID){
		return players[playerID].isCompleted();
	}
	
	public void addPointsToPlayer(int playerID, int points){
		players[playerID].addPoints(points);
	}
	
	/**
	 * @return HashMap with player as a key and their score as the value
	 */
	public HashMap<Integer, Integer> getFinalScores(){
		HashMap<Integer, Integer> res = new HashMap<>(nPlayers);
		for(int i = 0; i < nPlayers; i++)
			res.put(i, players[i].generateScore());
		
		return res;
	}
	
	/**
	 * @return a description of the game as a JSON string.
	 * The string will look like this:
	 * <p>
	 * "nPlayers" : number of players
	 * "turn" : actual turn
	 * "board" : [gameBoard]
	 * "cardsBag" : [cards bag of board]
	 * "goal1" : [goal 1]
	 * "goal2:" [goal 2]
	 * "players" : [...{player 0...3 data}...]
	 */
	@Override
	public String toString(){
		StringBuilder str = new StringBuilder("{" + "\"nPlayers\":" + nPlayers + "," + "\"turn\":" + turn + "," + "\"board\":\"" + board.toString() + "\"," + "\"cardsBag\":\"" + board.getCardsBag() + "\"," + "\"goal1\":" + goal1.toString() + "," + "\"goal2\":" + goal2.toString() + ",");
		
		str.append("\"players\":[");
		for(int n = 0; n < nPlayers; n++)
			str.append(players[n].toString()).append(n != nPlayers - 1 ? "," : "");
		str.append("]}");
		
		return str.toString();
	}
	
	public JSONObject toJSON(){
		return new JSONObject(toString());
	}
	
	// Observer pattern
	@Override
	public void attach(Observer observer){
		virtualPlayers.add(observer);
	}
	
	@Override
	public void remove(Observer observer){
		virtualPlayers.remove(observer);
	}
	
	@Override
	public void updateAllObservers(){
		virtualPlayers.forEach(Observer::update);
	}
	
	@Override
	public void broadcast(JSONObject obj){virtualPlayers.forEach(x -> x.sendMessage(obj));}
}