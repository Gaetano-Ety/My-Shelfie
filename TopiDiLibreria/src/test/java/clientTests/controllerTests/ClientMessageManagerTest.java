package clientTests.controllerTests;

import client.controller.ClientController;
import client.controller.ClientMessageManager;
import model.exceptions.InvalidPlayersNumberException;
import networkMessages.messages.*;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientMessageManagerTest{
	ClientController cc = new ClientController(0, true, "localhost");
	ClientMessageManager cmm = new ClientMessageManager(cc);
	
	@Test
	public void manageIDMessageTest1(){
		IDMessage idMessage = new IDMessage(0);
		cmm.manageIDMessage(idMessage);
		assertEquals(0, cc.getGameData().getMyID());
	}
	
	@Test
	public void manageNickNamesMessageTest1(){
		String[] nicks = new String[3];
		nicks[0] = "Maria";
		nicks[1] = "Giuseppe";
		nicks[2] = "Maddalena";
		NickNamesMessage nickNamesMessage = new NickNamesMessage(nicks);
		cmm.manageNickNamesMessage(nickNamesMessage);
		int i = 0;
		for(String nick : nicks){
			assertEquals(nick, cc.getGameData().getNickName(i++));
		}
	}
	
	@Test
	public void manageFirstInfoMessageTest1(){
		FirstInfoMessage firstInfoMessage = new FirstInfoMessage(2, 12, 2, 2);
		
		cmm.manageFirstInfoMessage(firstInfoMessage);
		assertEquals(" F   B/   C  /  P   / G    /   T  /", cc.getGameData().getPersonalGoal());
		assertEquals(2, cc.getGoalID(1));
		assertEquals(12, cc.getGoalID(2));
		assertEquals(2, cc.getGameData().getNPlayers());
	}
	
	@Test
	public void manageGameDataMessageTest1(){
		cc.getGameData().setID(0);
		cc.getGameData().setNickNames(new String[]{"", "", ""});
		try{
			cc.getGameData().setNumOfPlayer(2);
		}catch(InvalidPlayersNumberException e){
			throw new RuntimeException(e);
		}
		
		String[] bookShelves = new String[2];
		bookShelves[0] = "      /      /      /      /      /";
		bookShelves[1] = "      /      /      /      /      /";
		String board = "         /    P    /         / C    G  /    C    /         /    C    /   F     /         /";
		
		Stack<Integer>[] stacks = new Stack[2];
		stacks[0] = new Stack<>();
		stacks[0].add(6);
		stacks[1] = new Stack<>();
		stacks[1].add(6);
		
		GameDataMessage gameDataMessage = new GameDataMessage(board, bookShelves, 1, stacks);
		cmm.manageGameDataMessage(gameDataMessage);
		assertEquals(1, cc.getGameData().getTurn());
		assertEquals(bookShelves[0], cc.getGameData().getBookShelf(0).toString());
		assertEquals(bookShelves[1], cc.getGameData().getBookShelf(1).toString());
		assertEquals(board, cc.getGameData().getGameBoard().toString());
	}
	
	@Test
	public void manageFinalMessageTest1(){
		cc.getGameData().setNickNames(new String[]{"", "", ""});
		cc.setKeyBoardOn();
		int[] scores = {31, 12, 3};
		FinalMessage finalMessage = new FinalMessage(scores);
		cmm.manageFinalMessage(finalMessage);
	}
}