package client.view.cli;

import client.controller.ClientController;
import client.exceptions.RollBackException;
import client.view.UserInterface;
import model.exceptions.InvalidStringExcepton;
import model.exceptions.NotAvailableGoalException;
import model.gameObjects.SpotMatrix;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static client.view.cli.CliMethods.printMat;

public class UserInterfaceCli extends UserInterface{
	private BufferedReader input;
	private final CliMethods cliMethods;
	
	public UserInterfaceCli(ClientController controller){
		super(controller);
		Chat chat = new Chat(this, controller, gameData);
		cliMethods = new CliMethods(this, controller, gameData, chat);
		input = new BufferedReader(new InputStreamReader(System.in));
	}
	
	//OUTPUT METHODS
	
	/**
	 * Notifies the player that the game has started.<br/>
	 * Prints personal goal, common goals, board, the name of the player that has to play now, and the possible commands.
	 */
	@Override
	public void firstPrint(){
		synchronized(System.out){
			System.out.println("\nThe game is starting!\nHere a list of the players:");
			cliMethods.showPlayers();
			
			System.out.println("Your personal goal is:");
			showPersonalGoal();
			
			System.out.println("The common goals are:");
			showCommonGoals();
			
			printBoard();
			printActualTurn();
			
			cliMethods.printCommands();
			System.out.println();
		}
	}
	
	/**
	 * Prints the board, player bookshelf and the name of the player who has to play now.<br/>
	 * These elements will always be visible to the player.
	 */
	public void printActualTurn(){
		synchronized(System.out){
			if(gameData.isMyTurn())
				System.out.println("It's your turn\n");
			else
				System.out.println("It's " + gameData.getNickNames()[gameData.getTurn()] + "'s turn\n");
		}
	}
	
	public void printBoard(){
		synchronized(System.out){
			System.out.println("This is the current state of the board:");
			printMat(gameData.getGameBoard());
		}
	}
	
	/**
	 * Prints the final scores of the players then announces the winner
	 */
	@Override
	public void gameEnd(HashMap<Integer, Integer> rank){
		synchronized(System.out){
			rank.forEach((key, value)-> System.out.println(gameData.getNickName(key)+": "+value+" points"));

			Iterator<Map.Entry<Integer, Integer>> iterator = rank.entrySet().iterator();
			Map.Entry<Integer, Integer> winner = iterator.next();
			System.out.print("\n"+gameData.getNickName(winner.getKey()));
			boolean tie = false;

			while (iterator.hasNext()){
				Map.Entry<Integer, Integer> next = iterator.next();
				if(Objects.equals(winner.getValue(), next.getValue())) {
					System.out.print(" and "+gameData.getNickName(next.getKey()));
					tie = true;
				}
			}
			String string = (tie)? " are the winners" : " is the winner";
			System.out.print(string+" with "+winner.getValue()+" points!!!");
		}
	}
	
	/**
	 * Shows commonGoals
	 */
	public void showCommonGoals(){
		try{
			synchronized(System.out){
				System.out.println("Common goal 1:\n" + gameData.getGoalDescription(1));
				System.out.println("Points available: " + gameData.getGoalScores(1) + "\n");
				System.out.println("Common goal 2:\n" + gameData.getGoalDescription(2));
				System.out.println("Points available: " + gameData.getGoalScores(2) + "\n");
			}
		}catch(NotAvailableGoalException e){/* Impossible */}
	}
	
	/**
	 * Prints the personal goal of the player and reminds the player of the number of points received for each case
	 */
	public void showPersonalGoal(){
		try{
			synchronized(System.out){
				printMat(new SpotMatrix(gameData.getPersonalGoal()));
				System.out.println("These are the points awarded for the number of cards in the correct spot:");
				System.out.println("Cards:  1  2  3  4  5  6");
				System.out.println("Points: 1  2  4  6  9  12");
				System.out.println();
			}
		}catch(InvalidStringExcepton e){/* Ignored */ }
	}
	
	//OUTPUT METHODS FROM CONTROLLER
	
	/**
	 * Tells the player that he must wait for something
	 *
	 * @param who server, other players, generic
	 */
	@Override
	public void tellWaiting(int who){
		switch(who){
			case 0 -> System.out.println("Waiting for response from server...");
			case 1 -> System.out.println("Waiting for other players...");
			default -> System.out.println("Waiting...");
		}
	}
	
	/**
	 * tells the player that error occurred
	 *
	 * @param errorType is the type of error
	 */
	@Override
	public void tellError(int errorType){
		switch(errorType){
			case 1 -> System.out.println("These tiles are not valid to be taken");
			case 2 -> System.out.println("\nNetwork problems!");
			case 3 -> System.out.println("There are enough players");
			case 4 -> System.out.println("Nickname already chosen");
			default -> System.out.println("Something went wrong! Try again!");
		}
	}
	
	/**
	 * tells the player that someone sent a message in a private or public chat
	 *
	 * @param sender        is the sender
	 * @param recipient     is -1 if the chat is public
	 * @param inAnotherChat is useful to tell the player that he has to exit the current chat before entering another
	 */
	@Override
	public void tellMessage(int sender, int recipient, boolean inAnotherChat){
		String x = (inAnotherChat) ? "type - to exit current chat, then " : "";
		synchronized(System.in){
			if(recipient == -1){
				System.out.println(gameData.getNickName(sender) + " sent a message in the game chat");
				System.out.println(x + "type gc or gameChat to read it");
			}else{
				System.out.println(gameData.getNickName(sender) + " sent you a message");
				System.out.println(x + "type pc or playerChat to read it ");
			}
		}
	}
	
	/**
	 * tells to all players that someone achieved a goal
	 *
	 * @param playerID is the ID of player that achieved a certain goal
	 * @param goal     is the goal achieved
	 */
	@Override
	public void tellGoalAchieved(int playerID, int goal){
		System.out.println("Player " + playerID + " achieved goal " + goal);
	}
	
	@Override
	public void turnOffInterface(){
		System.out.println("Press enter to continue...");
	}
	
	/**
	 * shows a message in the current chat (the player must be in this chat)
	 *
	 * @param sender  is the sender
	 * @param message is the message
	 */
	public void showNewMessage(int sender, String message){
		System.out.println(gameData.getNickName(sender) + ": " + message);
	}
	
	
	//INPUT METHODS
	
	/**
	 * @return the username of the player
	 */
	@Override
	public String readNickName(){
		while(true){
			try{
				System.out.print("Insert your nickname: ");
				String str = input.readLine();
				
				if(!checkLocalUsernameAlphaNumeric(str))
					System.out.println("Invalid username, your username must have:\n- at least 1 character\n- only alphanumeric character");
				else return str;
			}catch(IOException ignored){
				tellError(0);
			}
		}
	}
	
	/**
	 * The first player that connects gets to decide the number of players for the game
	 *
	 * @return number of players
	 */
	@Override
	public int readNumOfPlayers(){
		System.out.println("You're the first player - Insert the number of players (2, 3 or 4):\n");
		do{
			try{
				int numPlayers = Integer.parseInt(input.readLine());
				
				if(numPlayers < 2 || numPlayers > 4)
					System.out.println("Invalid number of player. Please select number 2, 3 or 4");
				else return numPlayers;
			}catch(NumberFormatException | IOException exception){
				tellError(0);
			}
		}while(true);
	}
	
	public String getInput(){
		try{
			return input.readLine();
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Controls if the input value is an integer, and if it's above min and below max
	 */
	public int getInputInt(int max, int min) throws RollBackException{
		boolean validNumber = false;
		int val = 0;
		while(!validNumber){
			try{
				val = Integer.parseInt(input.readLine());
				if(val == -1) throw new RollBackException();
			}catch(IOException | NumberFormatException e){
				System.out.println("invalid value, please try again");
				continue;
			}
			if(val < min || val > max)
				System.out.println("invalid value, please try again");
			else
				validNumber = true;
		}
		return val;
	}
	
	/**
	 * @return the number of cards taken by the player
	 */
	public int selectNumberCards() throws RollBackException{
		int numOfTiles;
		System.out.println("Please enter the number of tiles you want to take");
		numOfTiles = getInputInt(3, 1);
		return numOfTiles;
	}
	
	/**
	 * @return the column where the cards will be placed
	 */
	public int selectColumn() throws RollBackException{
		int column;
		System.out.println("Please enter the column in which you want to place your tiles");
		column = getInputInt(4, 0);
		return column;
	}
	
	/**
	 * Returns a string with the coordinates of the card
	 */
	public StringTokenizer selectCard(){
		System.out.println("Please enter the coordinates of the tile this way:'x y' ");
		return new StringTokenizer(getInput());
	}
	
	/**
	 * Method that continues to read the input form the player
	 */
	public void readInput(){
		do{
			try{
				cliMethods.manageInput(input.readLine());
			}catch(IOException e){
				System.out.println("Something went wrong...");
				System.exit(1);
			}
		}while(controller.isRunning());
	}
	
	public void setBufferedReader(String s){
		input = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(s.getBytes())));
	}
}