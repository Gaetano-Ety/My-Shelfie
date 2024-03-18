package client.view;

import client.controller.ClientController;
import client.virtualModel.GameData;

import java.util.HashMap;
import java.util.Objects;

public abstract class UserInterface{
	protected final ClientController controller;
	protected final GameData gameData;
	
	protected UserInterface(ClientController controller){
		this.controller = controller;
		gameData = controller.getGameData();
	}
	
	/**
	 * Checks if a username is locally valid (only alphanumeric chars, mix and max length )
	 *
	 * @param username the username that will be checked
	 * @return true if the username is not null, it has a correct length, and it's only alphanumeric
	 */
	public static boolean checkLocalUsernameAlphaNumeric(String username){
		if((Objects.isNull(username) || username.length() < 1))
			return false;
		return username.matches("^[a-zA-Z0-9]*$");
	}
	
	public abstract void firstPrint();
	
	public abstract void gameEnd(HashMap<Integer, Integer> rank);
	
	public abstract void tellWaiting(int who);
	
	public abstract void tellError(int errorType);
	
	public abstract void tellGoalAchieved(int playerID, int goal);
	
	public abstract void tellMessage(int sender, int recipient, boolean inAnotherChat);
	
	public abstract void turnOffInterface();
	
	public abstract String readNickName();
	
	public abstract int readNumOfPlayers();
}
