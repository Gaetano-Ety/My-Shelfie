package client.view.gui;

import client.controller.ClientController;
import client.view.UserInterface;
import client.view.gui.mainSceneItems.BoardGUI;
import client.view.gui.mainSceneItems.ChatWindow;
import client.view.gui.mainSceneItems.leftMenuItems.LeftMenu;
import client.view.gui.mainSceneItems.leftMenuItems.PlayersGUI;
import client.view.gui.mainSceneItems.rightMenuItems.PlayMenu;
import client.view.gui.mainSceneItems.rightMenuItems.RightMenu;
import client.view.gui.otherWindows.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import model.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class UserInterfaceGUI extends UserInterface{
	private Window window;
	private final Thread windowThread;
	
	// Main items
	private final BorderPane mainPane;
	private PlayMenu playMenu;
	private PlayersGUI playersGUI;
	private BoardGUI board;
	private boolean myTurn, gameEnded;
	private ChatWindow[] playersChat;
	private ChatWindow gameChat;
	
	/**
	 * lastError indicates the last error that was notified to the GUI <br/>
	 * This way the alert box that appears will be able to choose what to show
	 */
	private int lastError = 0;
	
	/**
	 * selectedCard will contain the coordinates of the cards selected by the user during his turn; <br/>
	 * When the user will send the cards this array will be emptied.
	 */
	private final ArrayList<int[]> selectedCard;
	
	public UserInterfaceGUI(ClientController controller){
		super(controller);
		mainPane = new BorderPane();
		windowThread = new Thread(() -> new Window().initialize());
		myTurn = false;
		gameEnded = false;
		selectedCard = new ArrayList<>();
	}
	
	/**
	 * Start the game and get the instance of the window that just startet
	 */
	public void start(){
		windowThread.start();
		window = Window.getInstance();
	}
	
	public void waitThread() throws InterruptedException{
		windowThread.join();
	}
	
	/**
	 * Change the scene
	 */
	public void newScene(Scene scene){
		Platform.runLater(() -> window.showScene(scene));
	}
	
	/**
	 * Create the main scene of the game
	 * The scene contains a left menu with goals, player list, turn indicator and chat buttons; <br/>
	 * In the center the board; <br/>
	 * On the right, the bookshelves of the various players and, during one's turn, a menu for playing.
	 */
	public Scene createMainScene(){
		board = new BoardGUI(this, gameData);
		controller.addObserverGUI(board);
		LeftMenu leftMenu = new LeftMenu(this, gameData);
		playersGUI = leftMenu.getPlayersGUI();
		mainPane.setLeft(leftMenu.getLayout());
		mainPane.setCenter(board.getLayout());
		mainPane.setRight(new RightMenu(this, gameData).getLayout());
		BorderPane.setMargin(mainPane.getLeft(), new Insets(5));
		
		playersChat = new ChatWindow[gameData.getNPlayers()];
		for(int i = 0; i < gameData.getNPlayers(); i++){
			playersChat[i] = new ChatWindow(this, "Chat with " + gameData.getNickName(i), gameData, i);
			controller.addObserverGUI(playersChat[i]);
		}
		gameChat = new ChatWindow(this, "Game chat", gameData, -1);
		controller.addObserverGUI(gameChat);
		
		mainPane.setBackground(
			new Background(
				new BackgroundImage(
					ImagesStore.getBackground(),
					BackgroundRepeat.REPEAT,
					BackgroundRepeat.REPEAT,
					BackgroundPosition.CENTER,
					new BackgroundSize(window.getWidth(), window.getHeight(), false, false, true, false)
				)));
		
		return new Scene(mainPane, window.getWidth(), window.getHeight());
	}
	
	/**
	 * Put a new observer item into a list of controller
	 */
	public void sayNewObserver(ObserverGUIItem item){
		controller.addObserverGUI(item);
	}
	
	/**
	 * Show the main scene
	 */
	@Override
	public void firstPrint(){
		newScene(createMainScene());
	}
	
	/**
	 * Change the scene with the waiting message
	 */
	@Override
	public void tellWaiting(int who){
		String message = switch(who){
			case 0 -> "Waiting for response from server...";
			case 1 -> "Waiting for other players...";
			default -> "Waiting...";
		};
		StackPane layout = new StackPane();
		layout.getChildren().add(new Label(message));
		
		newScene(new Scene(layout, Window.sizeW, Window.sizeH));
	}
	
	
	/**
	 * Set some internal parameter to signal that the game is over; <br/>
	 * Call the controller to check the printout of the latest update; <br/>
	 * Show the ranking screen to the user.
	 */
	@Override
	public void gameEnd(HashMap<Integer, Integer> rank){
		gameEnded = true;
		controller.updateObserverGUI();
		Platform.runLater(() -> FinalMessageWindow.show(rank, gameData.getNickNames()));
	}
	
	public boolean isGameEnded(){
		return gameEnded;
	}
	
	/**
	 * Set the variable "lastError"
	 */
	@Override
	public void tellError(int errorType){
		lastError = errorType;
	}
	
	/**
	 * Shows the last received error message and opens the alert box to close the game
	 */
	@Override
	public void turnOffInterface(){
		switch(lastError){
			case 2 ->
				Platform.runLater(() -> AlertBox.showAlertBox("Network problems", "There was a problem with the connection!!!", "OK", true));
			case 3 ->
				Platform.runLater(() -> AlertBox.showAlertBox("Server message", "There are enough players", "Close", true));
			default -> Platform.runLater(() -> AlertBox.showAlertBox("Error", "Something went wrong :(", "OK", true));
		}
	}
	
	/**
	 * Notify the user that someone has achieved a goal, by saying who and what goal
	 */
	@Override
	public void tellGoalAchieved(int playerID, int goal){
		Platform.runLater(() ->
			AlertBox.showAlertBox(
				"Goal Achieved",
				(playerID == gameData.getMyID() ? "You" : gameData.getNickName(playerID)) + " achieved the goal " + goal,
				"Ok",
				false
			));
	}
	
	/**
	 * Notify the user that someone has achieved a goal, by saying who and what goal
	 */
	@Override
	public void tellMessage(int sender, int recipient, boolean inAnotherChat){
		if(recipient == -1) playersGUI.changeColor(-1);
		else playersGUI.changeColor(sender);
	}
	
	/**
	 * Show the box to enter the player's name, wait for the name and then return it
	 */
	@Override
	public String readNickName(){
		String nick;
		try{
			synchronized(RequestWindow.requestLock){
				NicknameRequestWindow.reset();
				Platform.runLater(() -> NicknameRequestWindow.display(lastError == 4));
				if(Objects.equals(NicknameRequestWindow.getNickname(), ""))
					RequestWindow.requestLock.wait();
				nick = NicknameRequestWindow.getNickname();
				if(Objects.equals(nick, "")) System.exit(1);
			}
		}catch(InterruptedException e){
			throw new RuntimeException(e);
		}
		
		return nick;
	}
	
	/**
	 * Show the box to enter the players number, wait for the name and then return it
	 */
	@Override
	public int readNumOfPlayers(){
		int playerNumber;
		try{
			synchronized(RequestWindow.requestLock){
				PlayersNumberRequestWindow.reset();
				Platform.runLater(PlayersNumberRequestWindow::display);
				if(PlayersNumberRequestWindow.getPlayersNumber() == 0)
					RequestWindow.requestLock.wait();
				playerNumber = PlayersNumberRequestWindow.getPlayersNumber();
				if(playerNumber == 0) System.exit(1);
			}
		}catch(InterruptedException e){
			throw new RuntimeException(e);
		}
		
		return playerNumber;
	}
	
	/**
	 * When the play menu is created it will be stored via this function.
	 * IT IS ONLY USED BY THE PLAY MENU ITSELF
	 */
	public void setPlayMenu(PlayMenu pm){
		playMenu = pm;
	}
	
	/**
	 * Shows the turn play menu
	 */
	public void playTurn(){
		myTurn = true;
		playMenu.show();
	}
	
	/**
	 * Hide the turn play menu and reset the internal information about the chosen tiles
	 */
	public void stopTurn(){
		myTurn = false;
		selectedCard.clear();
		playMenu.hide();
	}
	
	/**
	 * Check if a card can be taken and, in case, remove it from the visualization of board and put it in the play manu
	 */
	public boolean selectCard(int x, int y){
		if(!myTurn) return false;
		
		int actualSize = selectedCard.size();
		if(actualSize == 3) return false;
		
		int[][] cards = new int[actualSize + 1][2];
		for(int d = 0; d < actualSize; d++){
			cards[d][0] = selectedCard.get(d)[0];
			cards[d][1] = selectedCard.get(d)[1];
		}
		
		cards[actualSize][0] = x;
		cards[actualSize][1] = y;
		
		if(gameData.validTilesFromBoard(cards)){
			selectedCard.add(new int[]{x, y});
			playMenu.addCard(gameData.getColorAt(x, y));
			return true;
		}
		
		return false;
	}
	
	/**
	 * Set the update message and pass the data to the controller
	 */
	public void sendCardsToController(int column, Color[] order){
		int[][] coordinates = new int[selectedCard.size()][2];
		
		for(int i = 0; i < selectedCard.size(); i++){
			coordinates[i][0] = selectedCard.get(i)[0];
			coordinates[i][1] = selectedCard.get(i)[1];
		}
		
		controller.sendUpdateMessage(coordinates, order, column);
		
		stopTurn();
	}
	
	/**
	 * Reset the board according to the latest update
	 */
	public void resetBoard(){
		board.reset(selectedCard);
		selectedCard.clear();
	}
	
	/**
	 * Open the window of a specific player chat
	 */
	public void openChat(int id){
		setInChat(id);
		Platform.runLater(playersChat[id]::showChat);
	}
	
	/**
	 * Open the window of the global chat
	 */
	public void openChat(){
		setInChat(-1);
		Platform.runLater(gameChat::showChat);
	}
	
	/**
	 * Issues the instruction to send a new message to the controller
	 */
	public void createChatMessage(String message, int id){
		controller.createChatMessage(message, id);
	}
	
	/**
	 * Set a variable to say you are looking at a certain chat
	 */
	public void setInChat(int i){
		controller.setInChat(i);
	}

	public void removeFromChat(int id){
		controller.removeFromChat(id);
		playersGUI.changeBackColor(id);
	}
}