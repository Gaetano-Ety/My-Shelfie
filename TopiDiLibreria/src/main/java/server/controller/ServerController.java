package server.controller;

import client.view.cli.CliMethods;
import model.Color;
import model.exceptions.*;
import model.gameEntities.Game;
import model.goals.CommonGoalFactory;
import model.goals.PersonalGoal;
import networkMessages.exceptions.InvalidMessageException;
import networkMessages.exceptions.InvalidPlayerNickNameException;
import networkMessages.messages.*;
import org.json.JSONObject;
import server.controller.network.PlayerManager;
import server.controller.network.ServerInterface;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Stack;

public class ServerController{
	private final ServerMessageManager messageManager;
	private int turn, lastID;
	private boolean debugMode = false, lastRound, isClosing;
	private boolean testNetwork;
	final HashMap<Integer, PlayerManager> playerManagers;
	Game gameData;
	
	public ServerController(){
		playerManagers = new HashMap<>();
		messageManager = new ServerMessageManager(this);
		turn = 0;
		lastID = 0;
		testNetwork = false;
		isClosing = false;
		lastRound = false; // For most of the game lastRound will be equal to -1, in the final round of the game this variable will assume the id of the player who finished first
	}
	
	/**
	 * This method initializes last info for the game
	 */
	public void startGame(boolean debugMode){
		messageManager.addInterests(NumOfPlayersMessage.class.getSimpleName());
		messageManager.addInterests(PingMessage.class.getSimpleName());
		this.debugMode = debugMode;
		
		// Resetting the goals
		PersonalGoal.resetPossiblePersonalGoal();
		CommonGoalFactory.resetCommonGoal();
	}
	
	/**
	 * Add a new player manager to list of this controller.
	 */
	public synchronized void newPlayerManager(Socket s) throws IOException{
		PlayerManager pm = new PlayerManager(s, lastID, this, ("PlayerManager-" + lastID));
		pm.start();
		playerManagers.put(lastID, pm);
		lastID = findFirstFreeID();
	}
	
	/**
	 * Remove a playerManager thread from the so far connected list. <br/>
	 * It is used when a thread dies and should no longer be considered. <br/>
	 * Furthermore, when the remaining threads do not satisfy the number of players, they all shut down and the game ends.
	 */
	public synchronized void removePlayerManager(int idToRemove) throws IOException{
		if(!playerManagers.containsKey(idToRemove) || isClosing) return;
		playerManagers.get(idToRemove).interrupt();
		playerManagers.remove(idToRemove);
		lastID = findFirstFreeID();
		
		if(gameExists() && playerManagers.size() < gameData.getPlayersNumber() && !isClosing){
			isClosing = true;
			playerManagers.forEach((IgnoredKey, value) -> value.anomalyShutDown());
			ServerInterface.getServer().closeConnection();
		}
	}
	
	/**
	 * Remove a player manager without shut down other players; <br/>
	 * If you remove player 0 while at least one other player is waiting for the game to start, <br/>
	 * someone else will become player 0.
	 */
	public synchronized void easyRemovePlayerManager(int idToRemove){
		if(!playerManagers.containsKey(idToRemove) || isClosing) return;
		playerManagers.get(idToRemove).interrupt();
		playerManagers.remove(idToRemove);
		lastID = findFirstFreeID();
		
		if(lastID == 0 && playerManagers.size() > 0){
			// Take max id that is present
			int maxOldID = playerManagers.keySet().stream().mapToInt(x -> x).max().getAsInt();
			
			// Put the player with that id in the first place and set its id to 0
			playerManagers.put(0, playerManagers.get(maxOldID));
			playerManagers.remove(maxOldID);
			playerManagers.get(0).changeID(0);
			
			// The player 0 may have sent a nickNameMessage - It will be removed
			messageManager.resetNotReads();
			
			// Synchronization with other players
			lastID = findFirstFreeID();
			notifyAll();
			if(debugMode) System.out.println("Player-" + maxOldID + " evolved in Player-0");
		}
	}
	
	/**
	 * This method finds the first free id starting from 0
	 */
	private int findFirstFreeID(){
		for(int i = 0; i < playerManagers.size(); i++)
			if(!playerManagers.containsKey(i))
				return i;
		return playerManagers.size();
	}
	
	// *** *** ***
	// Setter
	
	/**
	 * Create a copy of a game previously saved
	 */
	public synchronized void resumeAGame(JSONObject gameJSON) throws InvalidStringExcepton{
		gameData = new Game(gameJSON);
		turn = gameJSON.getInt("turn");
		
		messageManager.emptyInterests();
		messageManager.addInterests(UpdateMessage.class.getSimpleName());
		messageManager.addInterests(ChatMessage.class.getSimpleName());
		
		notifyAll();
		
		/* TO DO: Check if all the players are there, notify them with the game data */
	}
	
	/**
	 * Set the number of players and create a new game
	 */
	public synchronized void setNumberOfPlayers(int n) throws InvalidPlayersNumberException{
		gameData = new Game(n);
		
		messageManager.emptyInterests();
		messageManager.addInterests(NickNameMessage.class.getSimpleName());
		messageManager.addInterests(UpdateMessage.class.getSimpleName());
		messageManager.addInterests(ChatMessage.class.getSimpleName());
		
		notifyAll();
	}
	
	/**
	 * Set the nickname to a player
	 *
	 * @throws InvalidPlayerNickNameException If the nickname is already chosen
	 */
	synchronized public void setNickName(int id, String nickName) throws InvalidPlayerNickNameException{
		try{
			for(String s : getNickNames())
				if(Objects.equals(nickName, s)) throw new InvalidPlayerNickNameException();
			
			gameData.assignPlayer(id, nickName);
			if(playerManagers.size() > 0) // This if only makes sense during testing phases, during gameplay it will always be true
				gameData.attach(playerManagers.get(id));
		}catch(NotAvailableGoalException e){
			/* TO DO: Restart the game */
			throw new RuntimeException(e); // De facto is impossible
		}
		
		if(gameData.ready()){
			messageManager.removeInterests(NickNameMessage.class.getSimpleName());
			gameData.updateAllObservers();
		}
	}
	
	public void setIsTestNetwork(){
		testNetwork = true;
	}
	
	// *** *** ***
	// Getter
	
	/**
	 * @return the JSONObject representing the game status in a certain moment
	 */
	synchronized public JSONObject getSavingOfGame(){
		return gameData.toJSON();
	}
	
	/**
	 * @return a string arrayList with the actual nicknames of the players; if there are no player return a 0-size arrayList
	 */
	synchronized public ArrayList<String> getNickNames(){
		ArrayList<String> nickNames = new ArrayList<>();
		
		if(!gameExists()) return nickNames;
		
		int n = gameData.getPlayersNumber();
		for(int i = 0; i < n; i++){
			String s = gameData.getPlayerNickName(i);
			if(!Objects.isNull(s)) nickNames.add(s);
		}
		
		return nickNames;
	}
	
	public HashMap<Integer, Integer> getPlayersScores(){
		return gameData.getFinalScores();
	}
	
	public int getTurn(){
		return turn;
	}
	
	public String getBookShelf(int id){
		return gameData.getBookshelf(id);
	}
	
	public String getBoard(){
		return gameData.getBoard();
	}
	
	public int getPlayersNumber(){
		return gameData.getPlayersNumber();
	}
	
	public boolean isLastRound(){
		return lastRound;
	}
	
	public boolean isDebugging(){
		return debugMode;
	}
	
	public boolean isTestNetwork(){
		return testNetwork;
	}
	
	/**
	 * Check if a specific player is already declared
	 */
	synchronized public boolean haveANickname(int playerID){
		if(gameExists())
			return !Objects.isNull(gameData.getPlayerNickName(playerID));
		return false;
	}
	// *** *** ***
	// Functionality
	
	/**
	 * @return a boolean value indicating whether the game data has already been instantiated
	 */
	synchronized public boolean gameExists(){
		return !Objects.isNull(gameData);
	}
	
	/**
	 * This method removes the cards with given coordinates from the board
	 *
	 * @throws InvalidActionException if the coordinates taken in input are not valid
	 */
	synchronized public void takenCards(int[][] coordinates) throws InvalidActionException{
		// Check if the cards to taken are in valid spots
		if(!verifyCoordinates(coordinates)) throw new InvalidActionException();
		
		for(int[] coo : coordinates)
			gameData.removeFromBoard(coo[0], coo[1]);
	}
	
	/**
	 * @return true if the coordinates taken in input are take-able from board<br/>
	 * A set of coordinates is valid if: <br/>
	 * - all the spots are not void <br/>
	 * - all the spots are free before every action
	 */
	private boolean verifyCoordinates(int[][] coordinates){
		// For each coordinate, the spot is not void and free
		for(int[] coo : coordinates)
			if(gameData.isVoidSpot(coo[0], coo[1]) || !gameData.isAFreeSpot(coo[0], coo[1])) return false;
		
		return true;
	}
	
	/**
	 * Put a card into a bookshelf of the linked id player
	 */
	synchronized public void putInBookshelf(int playerID, int column, Color[] colors) throws FullColumnException{
		for(Color c : colors)
			gameData.addACardToBsPlayer(playerID, column, c);
	}
	
	/**
	 * Advance the game turn with all the appropriate checks: <br/>
	 * - verify if the player has completed a goal <br/>
	 * - verify if the player has fulled his bookshelf <br/>
	 * - increment the turn <br/>
	 * - refill the board (if it's necessary)
	 * In debug mode this method print the game status after update
	 */
	synchronized public void nexTurn(){
		gameData.verifyGoal(turn);
		
		if(!lastRound && gameData.isCompletedBookshelf(turn)){
			lastRound = true;
			gameData.addPointsToPlayer(turn, 1); // The first player that complete a bookshelf receive one point
		}
		
		turn = gameData.nextTurn();
		gameData.refill();
		
		// Print the actual game status if controller is in debugger mode
		if(debugMode) debuggerPrinterStatus();
		
		gameData.updateAllObservers();
	}
	
	// *** *** ***
	// Messages
	
	/**
	 * Call the message manager to managing the message
	 *
	 * @throws InvalidMessageException if the message hasn't been managed correctly
	 */
	public void manageMessage(JSONObject message) throws InvalidMessageException{
		messageManager.manage(message);
	}
	
	public JSONObject createIDMessage(int id){
		return new IDMessage(id).toJSON();
	}
	
	public JSONObject createNickNamesMessage(){
		return new NickNamesMessage(getNickNames().toArray(new String[0])).toJSON();
	}
	
	public JSONObject createFirstInfoMessage(int playerID){
		int[] c_GoalsID = gameData.getGoalsIDs();
		return new FirstInfoMessage(c_GoalsID[0], c_GoalsID[1], gameData.getPersonalGoalID(playerID), gameData.getPlayersNumber()).toJSON();
	}
	
	public JSONObject createGameDataMessage(){
		int n = gameData.getPlayersNumber();
		String[] bss = new String[n];
		
		for(int i = 0; i < n; i++)
			bss[i] = gameData.getBookshelf(i);
		
		Stack<Integer>[] stacks = new Stack[2];
		stacks[0] = gameData.getGoalStack(1);
		stacks[1] = gameData.getGoalStack(2);
		
		return new GameDataMessage(gameData.getBoard(), bss, turn, stacks).toJSON();
	}
	
	public JSONObject createFinalMessage(){
		HashMap<Integer, Integer> finalScores = gameData.getFinalScores();
		int[] scores = new int[gameData.getPlayersNumber()];
		
		for(int n = 0; n < scores.length; n++)
			scores[n] = finalScores.get(n);
		
		return new FinalMessage(scores).toJSON();
	}
	
	// *** *** ***
	
	/**
	 * When the controller is running in debugger mode, every turn it prints the actual game status
	 */
	private void debuggerPrinterStatus(){
		System.out.println("Board:");
		CliMethods.printMat(gameData.getBoard());
		
		System.out.println("Stack1: " + gameData.getGoalStack(1));
		System.out.println("Stack2: " + gameData.getGoalStack(2));
		
		System.out.println("Players Data:");
		for(int id = 0; id < gameData.getPlayersNumber(); id++){
			System.out.println("Nickname-" + id + ": " + gameData.getPlayerNickName(id));
			System.out.println("Score: " + gameData.getActualScore(id));
			System.out.println("Bookshelf: ");
			CliMethods.printMat(gameData.getBookshelf(id));
		}
	}
}