package client.view.cli;

import client.controller.ClientController;
import client.virtualModel.GameData;
import model.exceptions.InvalidStringExcepton;
import model.gameObjects.SpotMatrix;
import networkMessages.exceptions.InvalidPlayerNickNameException;

import java.util.Objects;

/**
 * A class that contains only methods that print to the screen
 */
public class CliMethods{
	private final UserInterfaceCli ui;
	private final ClientController controller;
	private final GameData gameData;
	private final Chat chat;
	
	public CliMethods(UserInterfaceCli ui, ClientController controller, GameData gameData, Chat chat){
		this.ui = ui;
		this.controller = controller;
		this.gameData = gameData;
		this.chat = chat;
	}
	
	//OUTPUT Methods
	
	/**
	 * Print a list of players with their IDs
	 */
	public void showPlayers(){
		synchronized(System.out){
			for(int id = 0; id < gameData.getNPlayers(); id++)
				if(id != gameData.getMyID()) System.out.println(id + " - " + gameData.getNickName(id));
			
			System.out.println();
		}
	}
	
	/**
	 * Shows all the possible commands to the player
	 */
	public void printCommands(){
		synchronized(System.out){
			System.out.println("""
				These are the possible commands:
				- "getTiles"/"gt" to select cards to place in the bookshelf (possible only if it's your turn)
				- "showBookshelf"/"sbs" to see the corresponding player's bookshelf
				- "showPersonalGoal"/"spg" to visualize your personal goal
				- "showPlayers"/"sp" to see the all the player nicknames and the corresponding ID
				- "showCommonGoals"/"scg" to read a short description of the two goals
				- "showBoard"/"board"/"sb" to view the gameBoard
				- "showMyBookshelf"/"shelf"/"smb" to view your bookshelf
				- "showTurn"/"turn"/"st" to read actual turn
				- "readGameChat"/"rgc" to read the game chat
				- "gameChat"/"gc" to use the global chat"""
				+ (gameData.getNPlayers() > 2 ?
				"""
					- "readPlayerChat"/"rpc" to read the chat with a player
					- "playerChat"/"pc" to chat with a single player""" : "")
				+ """
				- "help" to read all the possible commands again""");
		}
	}
	
	/**
	 * Print a spotMatrix
	 */
	public static void printMat(SpotMatrix matrix){
		synchronized(System.out){
			String[][] myMat = matrix.toMatStrings();
			System.out.print("   ");
			for(int c = 0; c < myMat.length; c++)
				System.out.print(c + "  ");
			System.out.println();
			
			for(int y = 0; y < myMat[0].length; y++){
				System.out.print(y + " ");
				for(int x = 0; x < myMat.length; x++){
					if(x != 0) System.out.print("");
					System.out.print("[" + myMat[x][y] + "]");
				}
				System.out.println();
			}
			System.out.println();
		}
	}
	
	public static void printMat(String myMat){
		try{
			printMat(new SpotMatrix(myMat));
		}catch(InvalidStringExcepton ignored){
		}
	}
	
	// Other methods
	
	/**
	 * Private method that let the player select another player
	 */
	private int choosePlayer(){
		String str;
		while(true){
			str = ui.getInput();
			try{
				int id;
				if(str.matches("^[0-9]*$")){
					id = Integer.parseInt(str);
					if(id < 0 || id > gameData.getNPlayers()){
						System.out.println("ID not valid, please try again");
					}else return id;
				}else{
					id = gameData.searchID(str);
					return id;
				}
				
			}catch(InvalidPlayerNickNameException e){
				System.out.println("Nickname not found, please try again");
			}
		}
	}
	
	/**
	 * Method that manages the all the possible commands from the player
	 */
	public void manageInput(String command){
		if(!controller.isRunning() || Objects.isNull(command)) return;
		switch(command){
			case "showPersonalGoal", "spg" -> ui.showPersonalGoal();
			case "showCommonGoals", "scg" -> ui.showCommonGoals();
			case "showBookshelf", "sbs" -> {
				System.out.println("Insert the ID or nickname of the player you want see\nHere is a list of all players:");
				showPlayers();
				synchronized(System.out){
					printMat(gameData.getBookShelf(choosePlayer()));
				}
			}
			case "showPlayers", "sp" -> showPlayers();
			case "showTurn", "turn", "st" -> ui.printActualTurn();
			case "showMyBookshelf", "shelf", "smb" -> {
				synchronized(System.out){
					System.out.println("This is your bookshelf:");
					printMat(gameData.getBookShelf(gameData.getMyID()));
				}
			}
			case "showBoard", "board", "sb" -> ui.printBoard();
			case "getTiles", "gt" -> {
				if(gameData.isMyTurn()){
					System.out.println("Write -1 to start again");
					controller.sendUpdateMessage();
				}else
					System.out.println("It's not your turn!\n");
			}
			case "readGameChat", "rgc" -> {
				synchronized(System.out){
					if(gameData.getNPlayers() == 2) chat.readCompletePlayerChat(1 - gameData.getMyID());
					else chat.readCompletePlayerChat(-1);
				}
			}
			case "readPlayerChat", "rpc" -> {
				if(gameData.getNPlayers() == 2)
					System.out.println("Command not recognized\n");
				else{
					System.out.println("Insert the ID or nickname of the player you want chat with\nHere is a list of all players:");
					showPlayers();
					synchronized(System.out){
						chat.readCompletePlayerChat(choosePlayer());
					}
				}
			}
			case "gameChat", "gc" -> {
				if(gameData.getNPlayers() == 2)
					chat.playerChat(1 - gameData.getMyID());
				else chat.gameChat();
			}
			case "playerChat", "pc" -> {
				if(gameData.getNPlayers() == 2)
					System.out.println("Command not recognized\n");
				else{
					System.out.println("Insert the ID or nickname of the player you want chat with\nHere is a list of all players:");
					showPlayers();
					chat.playerChat(choosePlayer());
				}
			}
			case "help" -> printCommands();
			default -> System.out.println("Command not recognized\n");
		}
	}
}