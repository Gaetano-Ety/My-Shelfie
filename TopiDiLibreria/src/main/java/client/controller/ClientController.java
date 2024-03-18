package client.controller;

import client.controller.network.ClientInterface;
import client.exceptions.InvalidTilesException;
import client.exceptions.RollBackException;
import client.view.UserInterface;
import client.view.cli.UserInterfaceCli;
import client.view.gui.ObserverGUIItem;
import client.view.gui.UserInterfaceGUI;
import client.virtualModel.GameData;
import model.Color;
import model.exceptions.InvalidPlayersNumberException;
import model.exceptions.NotAvailableGoalException;
import networkMessages.exceptions.InvalidMessageException;
import networkMessages.messages.*;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.StringTokenizer;

public class ClientController{
	private boolean isRunning, keyBoardOn;
	ArrayList<Integer> inChat; //5 means not in chat
	final ClientInterface clientInterface;
	final GameData gameData;
	final ClientMessageManager clientMM;
	final UserInterface userInterface;
	final ArrayList<ObserverGUIItem> items;
	final boolean debug;
	int waitingState; // Used in startController to know when to start the game (start reading from keyboard and send updateMessage)
	
	/**
	 * Creates the virtual model of the client and the network interface for the client
	 *
	 * @param debugMode true if you want to activate debug mode, else false
	 * @param mode      - If it's 0 the game starts in cli, else starts in gui
	 */
	public ClientController(int mode, boolean debugMode, String serverIP){
		items = new ArrayList<>();
		gameData = new GameData();
		userInterface = (mode == 0) ? new UserInterfaceCli(this) : new UserInterfaceGUI(this);
		clientMM = new ClientMessageManager(this);
		clientInterface = new ClientInterface(this, serverIP);
		waitingState = 0;
		isRunning = true;
		keyBoardOn = false;
		debug = debugMode;
		inChat = new ArrayList<>();
	}
	
	/**
	 * The controller doesn't actually work until this method is called. <br/>
	 * The following steps are executed: <br/>
	 * 0) starts the interfaces with the server <br/>
	 * 1) wait until it receives IDMessage <br/>
	 * 2) sends NickNameMessage and eventually NumOfPlayersMessage <br/>
	 * 3) waits for the OkMessage (and eventually another one for NumOfPlayersMessage) <br/>
	 * 4) waits for FirstInfoMessage <br/>
	 * 5) waits for the first GameDataMessage <br/>
	 * 6) starts to read the input of the client (show things or play)
	 */
	public void startController(){
		try{
			if(userInterface.getClass() == UserInterfaceGUI.class)
				((UserInterfaceGUI) userInterface).start();
			
			userInterface.tellWaiting(0);
			clientInterface.start();
			
			// Waiting the IDMessage
			clientMM.addInterests(IDMessage.class.getSimpleName());
			clientMM.addInterests(WrongMessage.class.getSimpleName());
			
			synchronized(this){
				while(gameData.getMyID() == -1) this.wait();
			}
			
			clientInterface.sendMessage(new PingMessage().toJSON());
			clientMM.removeInterests(IDMessage.class.getSimpleName());
			
			clientMM.addInterests(NickNamesMessage.class.getSimpleName());
			clientMM.addInterests(OkMessage.class.getSimpleName());
			sendNicknameMessage();
			
			// stepsBeforeStarting indicates the number of messages expected before the game starts
			int stepsBeforeStarting = 3;
			if(gameData.getMyID() == 0){
				sendNumOfPlayersMessage();
				stepsBeforeStarting++;
			}
			
			userInterface.tellWaiting(1);
			
			clientMM.addInterests(FirstInfoMessage.class.getSimpleName());
			clientMM.addInterests(GameDataMessage.class.getSimpleName());
			
			//1: first ok (for nickname or numOfPlayers)
			//2: second ok (for nickname or numOfPlayers)
			//3: after FirstInfoMessage
			//4: after first GameDataMessage
			synchronized(this){
				while(waitingState < stepsBeforeStarting)
					this.wait();
			}
			
			clientMM.removeInterests(NickNamesMessage.class.getSimpleName());
			clientMM.removeInterests(FirstInfoMessage.class.getSimpleName());
			clientMM.addInterests(ChatMessage.class.getSimpleName());
			clientMM.addInterests(FinalMessage.class.getSimpleName());
			gameData.initializeChat();
			
			userInterface.firstPrint();
			
			if(userInterface.getClass() == UserInterfaceCli.class){
				keyBoardOn = true;
				((UserInterfaceCli) userInterface).readInput();
			}else
				((UserInterfaceGUI) userInterface).waitThread();
		}catch(InterruptedException e){
			throw new RuntimeException(e);
		}
	}
	
	// GETTERS
	public int getWaitingState(){
		return waitingState;
	}
	
	public GameData getGameData(){
		return gameData;
	}
	
	public int getGoalID(int i){
		synchronized(gameData){
			try{
				return gameData.getGoalID(i);
			}catch(NotAvailableGoalException e){
				throw new RuntimeException(e);
			}
		}
	}
	
	public boolean isRunning(){
		return isRunning;
	}
	
	public boolean isDebugging(){
		return debug;
	}
	
	/**
	 * @return the id of the recipient of this chat, -1 if the chat is public, 5 if the player is not in a chat
	 */
	public boolean inChat(int id){
		return inChat.contains(id);
	}

	public int openedChats(){
		return inChat.size();
	}
	
	// SETTERS
	
	/**
	 * This setter is used for test to avoid the system exit
	 */
	public void setKeyBoardOn(){
		keyBoardOn = true;
	}
	
	/**
	 * @param id is the id of the recipient of this chat, -1 if the chat is public, 5 if the player is not in a chat
	 */
	public void setInChat(int id){
		inChat.add(id);
	}

	public void removeFromChat(int id){
		Object oId = id;
		inChat.remove(oId);
	}
	
	//METHODS TO COMMUNICATE WITH THE SERVER
	
	/**
	 * @param message is a message from server, this method passes the message to the message manager.
	 */
	public void getMessage(JSONObject message){
		if(debug)
			System.out.println("Received from the server: " + message);
		
		if(message != null){
			try{
				synchronized(gameData){
					clientMM.manage(message);
				}
			}catch(InvalidMessageException e){
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * asks the player to insert a nickname and creates a nickname message with it
	 *
	 * @return json object of a nickname message
	 */
	public JSONObject createNicknameMessage(){
		// Set nickname
		String nickName;
		do{
			nickName = userInterface.readNickName();
			
			if(!isValidNick(nickName))
				userInterface.tellError(4);
			else break;
		}while(true);
		
		gameData.setPlayerNickName(nickName);
		
		// Create NickNameMessage
		NickNameMessage nickNameMessage = new NickNameMessage(nickName, gameData.getMyID());
		return nickNameMessage.toJSON();
	}
	
	/**
	 * asks the player to insert an int and creates a num of players message with it
	 *
	 * @return json object of a num of players message
	 */
	public JSONObject createNumOfPlayersMessage(){
		int numOfPlayers = userInterface.readNumOfPlayers();
		// Set NumOfPlayers
		try{
			gameData.setNumOfPlayer(numOfPlayers);
		}catch(InvalidPlayersNumberException e){
			return createNumOfPlayersMessage();
		}
		
		// Create NumOfPlayersMessage
		NumOfPlayersMessage numOfPlayersMessage = new NumOfPlayersMessage(numOfPlayers);
		return numOfPlayersMessage.toJSON();
	}
	
	/**
	 * called from the keyboard with the command getTiles in CLI mode, called by player either ways
	 *
	 * @return json object of a update message
	 */
	public JSONObject createUpdateMessage() throws InvalidTilesException, RollBackException{
		// Data
		int numOfTiles;
		int column;
		Color[] order;
		
		// Step 1: ask for number of tiles and column
		numOfTiles = ((UserInterfaceCli) userInterface).selectNumberCards();
		column = ((UserInterfaceCli) userInterface).selectColumn();
		if(notPlaceableTiles(numOfTiles, column) || !gameData.isAGroupOfBoard(numOfTiles))
			throw new InvalidTilesException();
		
		// Step 2: ask for tiles
		int[][] cardsTaken = new int[numOfTiles][2];
		order = new Color[numOfTiles];
		do{
			for(int i = 0; i < numOfTiles; i++)
				try{
					StringTokenizer position = ((UserInterfaceCli) userInterface).selectCard();
					cardsTaken[i][0] = Integer.parseInt(position.nextToken());
					if(cardsTaken[i][0] == -1) throw new RollBackException();
					cardsTaken[i][1] = Integer.parseInt(position.nextToken());
				}catch(NoSuchElementException e){
					userInterface.tellError(0);
					i = 0;
				}
			
			if(notValidTilesFromBoard(cardsTaken))
				userInterface.tellError(1);
			else break;
		}while(true);
		
		// Step 3: Get effectively color at coordinates
		for(int i = 0; i < numOfTiles; i++)
			order[i] = gameData.getColorAt(cardsTaken[i][0], cardsTaken[i][1]);
		
		// Step 4: Return
		UpdateMessage updateMessage = new UpdateMessage(gameData.getMyID(), cardsTaken, column, order);
		return updateMessage.toJSON();
	}
	
	/**
	 * creates a ping to let the server know that he is alive
	 *
	 * @return a ping
	 */
	public JSONObject createPing(){
		return new JSONObject().put("type", "PingMessage");
	}
	
	/**
	 * creates and sends a ChatMessage
	 *
	 * @param message   is the message
	 * @param recipient is the recipient's ID, if -1 it is a public message
	 */
	public void createChatMessage(String message, int recipient){
		ChatMessage chatMessage = new ChatMessage(gameData.getMyID(), recipient, message);
		clientInterface.sendMessage(chatMessage.toJSON());
	}
	
	public void sendUpdateMessage(){
		try{
			clientInterface.sendMessage(createUpdateMessage());
		}catch(InvalidTilesException e){
			userInterface.tellError(0);
		}catch(RollBackException ignored){
		}
	}
	
	public void sendUpdateMessage(int[][] coordinates, Color[] order, int column){
		clientInterface.sendMessage(
			new UpdateMessage(gameData.getTurn(), coordinates, column, order).toJSON()
		);
	}
	
	public void sendNicknameMessage(){
		clientInterface.sendMessage(createNicknameMessage());
	}
	
	public void sendNumOfPlayersMessage(){
		clientInterface.sendMessage(createNumOfPlayersMessage());
	}
	
	//OTHER METHODS
	
	/**
	 * This forces the inputStream of keyboard to be a certain string. <br/>
	 * It's used in some tests.
	 */
	public void forceKeyboardBufferedReader(String s){
		if(userInterface.getClass() == UserInterfaceCli.class)
			((UserInterfaceCli) userInterface).setBufferedReader(s);
	}
	
	/**
	 * Add an observer to the own list
	 */
	synchronized public void addObserverGUI(ObserverGUIItem obj){
		items.add(obj);
	}
	
	/**
	 * Notify all observer of a change
	 */
	synchronized public void updateObserverGUI(){
		items.forEach(ObserverGUIItem::update);
	}
	
	/**
	 * @param nick is the chosen nickname
	 * @return true if nick wasn't already chosen by other players
	 * client-side control, it is also done server-side
	 */
	public boolean isValidNick(String nick){
		if(!Objects.isNull(gameData.getNickNames()))
			for(String otherNick : gameData.getNickNames())
				if(Objects.equals(nick, otherNick)) return false;
		return true;
	}
	
	/**
	 * checks that the number of tiles that the player wants to collect from the board are placeable in the chosen column
	 *
	 * @param numOfTiles number of tiles that the player wants to collect from the board
	 * @param column     in which the player wants to place the tiles
	 * @return true if the operation is not legit
	 */
	public boolean notPlaceableTiles(int numOfTiles, int column){
		return numOfTiles > gameData.getFreeInColumn(column, gameData.getMyID());
	}
	
	/**
	 * @param spots are the coordinates of 1, 2 or 3 tiles of the board
	 * @return true if the tiles are not collectable
	 */
	public boolean notValidTilesFromBoard(int[][] spots){
		return !gameData.validTilesFromBoard(spots);
	}
	
	/**
	 * The keyboard stops reading, ClientInterface is interrupted
	 */
	public void stopGame(int error){
		if(error != 0) userInterface.tellError(error);
		clientMM.emptyInterests();
		clientInterface.interrupt();
		isRunning = false;
		
		if(userInterface.getClass() == UserInterfaceCli.class){
			if(keyBoardOn) userInterface.turnOffInterface();
			else System.exit(0);
		}else{
			if(error != 0)
				userInterface.turnOffInterface();
		}
	}
	
	/**
	 * notifies the player that he received a message in a certain chat
	 *
	 * @param sender        is the sender
	 * @param recipient     is -1 if the chat is public
	 * @param inAnotherChat tells if the player is already in another chat
	 */
	public void tellMessage(int sender, int recipient, boolean inAnotherChat){
		userInterface.tellMessage(sender, recipient, inAnotherChat);
	}
	
	/**
	 * shows a new message in the chat in which the player is
	 *
	 * @param sender  is the sender
	 * @param message is the message
	 */
	public void showNewMessage(int sender, String message){
		if(userInterface.getClass() == UserInterfaceCli.class)
			((UserInterfaceCli) userInterface).showNewMessage(sender, message);
	}
}