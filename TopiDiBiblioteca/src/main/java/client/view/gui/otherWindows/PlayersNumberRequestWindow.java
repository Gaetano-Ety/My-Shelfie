package client.view.gui.otherWindows;

import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;

/**
 * Window that allows you to read the number of players
 */
public class PlayersNumberRequestWindow extends RequestWindow{
	private static int playersNumber = 0;
	
	private PlayersNumberRequestWindow(){
		super("Players number request");
		
		playersNumber = 0;
		
		label1.setText("You are the first player - Insert the number of players (2, 3 or 4):");
		label2.setText("Number not valid. Insert a number between 2 and 4");
		
		button.setText("Send number");
		button.setOnAction(e -> readPlayerNumber());
		mainScene.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER)
				readPlayerNumber();
		});
	}
	
	/**
	 * Save the players number to allow other threads to read it
	 */
	private void readPlayerNumber(){
		synchronized(requestLock){
			boolean isValid = true;
			try{
				playersNumber = Integer.parseInt(text.getText());
			}catch(NumberFormatException exception){
				isValid = false;
			}
			
			if(!isValid || playersNumber < 2 || playersNumber > 4){
				label2.setFont(Font.font(12));
				playersNumber = 0;
				return;
			}
			
			closeWindow();
		}
	}
	
	public static int getPlayersNumber(){
		return playersNumber;
	}
	
	public static void display(){
		new PlayersNumberRequestWindow();
	}
	
	public static void reset(){
		playersNumber = 0;
	}
}