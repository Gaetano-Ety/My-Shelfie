package clientTests;

import client.controller.ClientController;
import client.view.gui.UserInterfaceGUI;
import client.virtualModel.GameData;
import modelTests.GameExampleJSON;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

import static java.lang.Thread.sleep;

public class GUI_Tests{
	private static final ClientController controller = new ClientController(1, false, "localhost");
	private static final UserInterfaceGUI gui = new UserInterfaceGUI(controller);
	
	public static void main(String[] args) throws Exception{
		GameData gameData = controller.getGameData();
		gameData.setNumOfPlayer(4);
		gameData.setID(1);
		gameData.setTurn(1);
		gameData.setGoals(1, 2);
		gameData.setPersonalGoal(3);
		gameData.setNickNames(new String[]{"Bingo", "Bongo", "Bango", "Bungo"});
		gameData.setPlayerNickName("Bongo");
		gameData.updateBoard(GameExampleJSON.ex4.getString("board"));
		gameData.initializeChat();
		gameData.updatePlayersChat(0, 1, "ciao");
		gameData.updatePlayersChat(1, 0, "hei");
		
		gameData.updateBookShelves(new String[]{
			GameExampleJSON.ex3.getJSONArray("players").getJSONObject(0).getString("bookShelf"),
			GameExampleJSON.ex3.getJSONArray("players").getJSONObject(2).getString("bookShelf"),
			GameExampleJSON.ex4.getJSONArray("players").getJSONObject(0).getString("bookShelf"),
			GameExampleJSON.ex2.getJSONArray("players").getJSONObject(1).getString("bookShelf")
		});
		
		Stack<Integer> s1, s2;
		s1 = new Stack<>();
		s2 = new Stack<>();
		s1.addAll(Arrays.stream(new Integer[]{2, 4, 6, 8}).toList());
		s2.addAll(Arrays.stream(new Integer[]{2, 4, 6, 8}).toList());
		gameData.updateScores(s1, s2);
		gui.start();
		
		gui.newScene(gui.createMainScene());
		controller.updateObserverGUI();
		
		sleep(5000);
		gameData.updateBoard(GameExampleJSON.ex3.getString("board"));
		s1 = new Stack<>();
		s2 = new Stack<>();
		s1.addAll(Arrays.stream(new Integer[]{2, 4, 6}).toList());
		gameData.updateScores(s1, s2);
		controller.updateObserverGUI();
		
		HashMap<Integer, Integer> rank = new HashMap<>();
		rank.put(0, 12);
		rank.put(1, 12);
		rank.put(2, 6);
		
		gui.gameEnd(rank);
		
		gui.waitThread();
	}
}
