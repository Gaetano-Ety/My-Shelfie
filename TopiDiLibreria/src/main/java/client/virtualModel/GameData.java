package client.virtualModel;

import model.Color;
import model.exceptions.InvalidMatrixException;
import model.exceptions.InvalidPlayersNumberException;
import model.exceptions.InvalidStringExcepton;
import model.exceptions.NotAvailableGoalException;
import model.gameObjects.Board;
import model.gameObjects.BookShelf;
import model.gameObjects.SpotMatrix;
import model.goals.CommonGoalFactory;
import model.goals.PersonalGoal;
import networkMessages.exceptions.InvalidPlayerNickNameException;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.reverseOrder;

public class GameData{
	private int playerID = -1; // -1 means that the id hasn't been assigned yet
	private int nPlayers, goal1ID, goal2ID, personalGoal;
	private Board gameBoard;
	private BookShelf[] bookShelves;
	private String[] nickNames;
	private String playerNickName;
	private String goal1Description, goal2Description;
	private int turn = -1;
	private Stack<Integer> scores1, scores2;
	private ArrayList<String> gameChat;
	private ArrayList<String>[] playersChat;
	private final static int MAX_MESSAGES = 5;
	
	// Setters and Updaters
	public void setID(int id){
		this.playerID = id;
	}
	
	public void setTurn(int turn){
		this.turn = turn;
	}
	
	public void setPersonalGoal(int personalGoal){
		this.personalGoal = personalGoal;
	}
	
	/**
	 * Sets players number reset the data of board, bookshelves and nicknames
	 */
	public void setNumOfPlayer(int n) throws InvalidPlayersNumberException{
		nPlayers = n;
		
		gameBoard = new Board(nPlayers);
		bookShelves = new BookShelf[nPlayers];
		
		for(int c = 0; c < nPlayers; c++)
			bookShelves[c] = new BookShelf();
	}
	
	public void setPlayerNickName(String playerNickName){
		this.playerNickName = playerNickName;
	}
	
	public void setNickNames(String[] nickNames){
		this.nickNames = nickNames;
	}
	
	public void setGoals(int g1ID, int g2ID) throws NotAvailableGoalException{
		goal1ID = g1ID;
		goal2ID = g2ID;
		goal1Description = CommonGoalFactory.getGoalDescription(g1ID);
		goal2Description = CommonGoalFactory.getGoalDescription(g2ID);
	}
	
	public void initializeChat(){
		gameChat = new ArrayList<>();
		playersChat = new ArrayList[getNPlayers()];
		for(int i = 0; i < getNPlayers(); i++)
			playersChat[i] = new ArrayList<>();
	}
	
	public void updateBoard(String str) throws InvalidStringExcepton{
		try{
			gameBoard.copyMatrix(new SpotMatrix(str));
		}catch(InvalidMatrixException e){
			throw new InvalidStringExcepton();
		}
	}
	
	public void updateBookShelves(String[] strings) throws InvalidStringExcepton{
		try{
			for(int c = 0; c < nPlayers; c++)
				bookShelves[c].copyMatrix(new SpotMatrix(strings[c]));
			
		}catch(InvalidMatrixException e){
			throw new InvalidStringExcepton();
		}
	}
	
	public void updateScores(Stack<Integer> scores1, Stack<Integer> scores2){
		this.scores1 = scores1;
		this.scores2 = scores2;
	}
	
	public void updatePlayersChat(int sender, int recipient, String message){
		if(recipient == -1){
			gameChat.add(getNickName(sender) + ": " + message);
		}else if(sender == getMyID()){
			playersChat[recipient].add(getMyNickName() + ": " + message);
		}else playersChat[sender].add(getNickName(sender) + ": " + message);
	}
	
	// Getters
	public SpotMatrix getGameBoard(){
		return gameBoard.clone();
	}
	
	public SpotMatrix getBookShelf(int n){
		return bookShelves[n].clone();
	}
	
	public String getPersonalGoal(){
		try{
			return PersonalGoal.choose(personalGoal);
		}catch(NotAvailableGoalException e){
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * @return a copy of the common goal scores stack required
	 */
	public Stack<Integer> getGoalScores(int n) throws NotAvailableGoalException{
		Stack<Integer> result = new Stack<>();
		result.addAll(switch(n){
			case 1 -> scores1;
			case 2 -> scores2;
			default -> throw new NotAvailableGoalException();
		});
		return result;
	}
	
	public int getNPlayers(){
		return this.nPlayers;
	}
	
	/**
	 * @param id the ID of teh recipient, if -1 the chat is public
	 * @return last MAX_MESSAGES(5) messages of a chat
	 */
	public ArrayList<String> getPlayerChat(int id){
		ArrayList<String> res = new ArrayList<>();
		if(id == -1){
			int x = (gameChat.size() < MAX_MESSAGES) ? 0 : gameChat.size() - 5;
			for(int i = x; i < gameChat.size(); i++){
				res.add(gameChat.get(i));
			}
		}else{
			int x = (playersChat[id].size() < MAX_MESSAGES) ? 0 : playersChat[id].size() - 5;
			for(int i = x; i < playersChat[id].size(); i++){
				res.add(playersChat[id].get(i));
			}
		}
		return res;
	}
	
	/**
	 * @param id the ID of teh recipient, if -1 the chat is public
	 * @return the  complete chat
	 */
	public ArrayList<String> getCompletePlayerChat(int id){
		if(id == -1)
			return gameChat;
		return playersChat[id];
	}
	
	public int getGoalID(int n) throws NotAvailableGoalException{
		return switch(n){
			case 1 -> goal1ID;
			case 2 -> goal2ID;
			default -> throw new NotAvailableGoalException();
		};
	}
	
	public String getGoalDescription(int n) throws NotAvailableGoalException{
		return switch(n){
			case 1 -> goal1Description;
			case 2 -> goal2Description;
			default -> throw new NotAvailableGoalException();
		};
	}
	
	public String[] getNickNames(){return nickNames;}
	
	public String getNickName(int n){
		return nickNames[n];
	}
	
	public String getMyNickName(){
		return playerNickName;
	}
	
	public int getMyID(){
		return playerID;
	}
	
	public int getPersonalGoalID(){
		return personalGoal;
	}
	
	public int searchID(String str) throws InvalidPlayerNickNameException{
		for(int i = 0; i < nickNames.length; i++)
			if(Objects.equals(str, nickNames[i])) return i;
		throw new InvalidPlayerNickNameException();
	}
	
	public int getTurn(){return turn;}
	
	public Color getColorAt(int x, int y){
		return gameBoard.getColorAt(x, y);
	}
	
	public int getFreeInColumn(int col, int playerID){
		return bookShelves[playerID].freeInColumn(col);
	}
	
	//Other methods
	
	/**
	 * @return true if the coordinates taken in input are take-able from board <br/>
	 * A set of coordinates is valid if: <br/>
	 * - all the spots are not void <br/>
	 * - all the spots are free before every action <br/>
	 * - all the spots are adjacent each others
	 */
	public boolean validTilesFromBoard(int[][] spots){
		if(!SpotMatrix.areAdjacentSpots(spots)) return false;
		for(int[] spot : spots)
			if(!gameBoard.isFree(spot[0], spot[1]) || gameBoard.getSpotAt(spot[0], spot[1]).isVoid()) return false;
		
		return true;
	}
	
	public boolean isMyTurn(){
		return turn == playerID;
	}
	
	public boolean isAGroupOfBoard(int n){
		return gameBoard.thereAreGroupsOf(n);
	}
	
	/**
	 * @return a hashmap with the scores of the various players in decreasing order
	 */
	public static HashMap<Integer, Integer> createRanking(int[] scores){
		HashMap<Integer, Integer> rank = new HashMap<>();
		
		for(int i = 0; i < scores.length; i++)
			rank.put(i, scores[i]);
		
		return rank.entrySet()
			.stream()
			.sorted(reverseOrder(Map.Entry.comparingByValue()))
			.collect(Collectors.toMap(
				Map.Entry::getKey,
				Map.Entry::getValue,
				(oldValue, newValue) -> oldValue, LinkedHashMap::new));
	}
	
	/**
	 * @return true if someone completed own bookshelf
	 */
	public boolean someOneCompleted(){
		for(BookShelf b : bookShelves){
			boolean full = true;
			for(int x = 0; x < BookShelf.getNumCols(); x++)
				if(b.freeInColumn(x) > 0){
					full = false;
					break;
				}
			
			if(full) return true;
		}
		
		return false;
	}
}