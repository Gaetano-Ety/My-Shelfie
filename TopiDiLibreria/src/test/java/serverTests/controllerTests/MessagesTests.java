package serverTests.controllerTests;

import model.exceptions.InvalidPlayersNumberException;
import model.exceptions.InvalidStringExcepton;
import modelTests.GameExampleJSON;
import networkMessages.exceptions.InvalidMessageException;
import networkMessages.messages.NickNameMessage;
import networkMessages.messages.NumOfPlayersMessage;
import networkMessages.messages.UpdateMessage;
import org.json.JSONObject;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import server.controller.ServerController;
import server.controller.ServerMessageManager;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class MessagesTests{
	private ServerController controller;
	private ServerMessageManager messageManager;
	
	@Nested
	class NumOfPlayersMessageTests{
		/**
		 * Tests if numOfPlayers Message is managed correctly, and so the game is generated correctly
		 */
		@Test
		public void NumOfPlayersMessageTest1(){
			try{
				startGenericTest();
				messageManager.manage(new NumOfPlayersMessage(4).toJSON());
				assertTrue(controller.gameExists());
			}catch(InvalidMessageException e){
				fail();
			}
		}

		/**
		 * Tests if InvalidPlayersNumber exception is generated correctly
		 */
		@Test
		public void NumOfPlayersMessageTest2(){
			startGenericTest();
			
			assertEquals("InvalidPlayersNumber", assertThrows(InvalidMessageException.class, () ->
				messageManager.manage(new JSONObject(new NumOfPlayersMessage(7)))
			).getMessage());
		}
	}

	/**
	 * Tests if NickNameMessage Message is managed correctly
	 */
	@Nested
	class NickNameMessageTests{
		/**
		 * Checks that the nickname is stores correctly
		 */
		@Test
		public void nickNameMessageTest1(){
			try{
				startGenericTest();
				String name = "Pippo";
				controller.setNumberOfPlayers(3);
				messageManager.manageNickNameMessage(new NickNameMessage(name, 0));
				assertTrue(controller.getNickNames().contains(name));
			}catch(InvalidMessageException | InvalidPlayersNumberException e){
				fail();
			}
		}

		/**
		 * Checks that the exception InvalidPlayerNickName is thrown when two players have the same nickname
		 */
		@Test
		public void nickNameMessageTest2(){
			try{
				startGenericTest();
				String name = "Pippo";
				controller.setNumberOfPlayers(3);
				assertEquals("InvalidPlayerNickName", assertThrows(InvalidMessageException.class, () -> {
					messageManager.manageNickNameMessage(new NickNameMessage(name, 0));
					messageManager.manageNickNameMessage(new NickNameMessage(name, 0));
				}).getMessage());
			}catch(InvalidPlayersNumberException e){
				fail();
			}
		}
	}

	/**
	 * Tests if UpdateMessage is managed correctly
	 */
	@Nested
	class UpdateMessageTests{
		@ParameterizedTest
		@MethodSource("verifyUPMTArguments")
		public void verifyUpdateMessageTest(JSONObject game, String str, boolean exceptionExpected){
			try{
				startGenericTest();
				UpdateMessage message = UpdateMessage.fromJSON(new JSONObject(str));
				controller.resumeAGame(game);
				
				if(exceptionExpected){
					assertEquals("InvalidUpdate",
						assertThrows(InvalidMessageException.class, () ->
							messageManager.manageUpdateMessage(message)).getMessage()
					);
					System.out.println("Exception received");
				}else{
					JSONObject saved = controller.getSavingOfGame();
					messageManager.manageUpdateMessage(message);
					assertNotEquals(saved.toString(), controller.getSavingOfGame().toString());
				}
			}catch(InvalidMessageException | InvalidStringExcepton e){
				System.out.println(e.getClass().getSimpleName());
				fail();
			}
		}
		
		private static Stream<Arguments> verifyUPMTArguments(){
			return Stream.of(
				Arguments.of(GameExampleJSON.ex3, "{\"playerID\":1, \"inputColumn\":2, \"cardsTaken\":[[1,4],[1,5]],\"order\":[\"CATS\", \"GAMES\"]}", false),
				Arguments.of(GameExampleJSON.ex3, "{\"playerID\":1, \"inputColumn\":2, \"cardsTaken\":[[1,2],[3,4]],\"order\":[\"CATS\", \"GAMES\", \"TROPHIES\"]}", true),
				Arguments.of(GameExampleJSON.ex5, "{\"playerID\":1, \"inputColumn\":2, \"cardsTaken\":[[1,4],[1,5]],\"order\":[\"CATS\", \"GAMES\"]}", true),
				Arguments.of(GameExampleJSON.ex5, "{\"playerID\":1, \"inputColumn\":1, \"cardsTaken\":[[1,4],[3,5]],\"order\":[\"CATS\", \"GAMES\"]}", true)
			);
		}
	}

	/**
	 * Tests to ensure that the messages are created correctly
	 */
	@Nested
	class CreateMessageTests{
		@Test
		public void createIDMessageTest(){
			try{
				controller = new ServerController();
				controller.resumeAGame(GameExampleJSON.ex2);
				
				JSONObject expected = new JSONObject("{\"type\":\"IDMessage\", \"ID\":2}");
				JSONObject actual = controller.createIDMessage(2);
				
				assertEquals(expected.toString(), actual.toString());
			}catch(InvalidStringExcepton e){
				fail("Received exception: " + e.getMessage());
			}
		}
		
		@Test
		public void createNickNamesMessageTest(){
			try{
				controller = new ServerController();
				controller.resumeAGame(GameExampleJSON.ex2);
				
				JSONObject expected = new JSONObject("{\"type\":\"NickNamesMessage\", \"nickNames\":[\"Giorgio\",\"Luca\",\"Filippa\"]}");
				JSONObject actual = controller.createNickNamesMessage();
				
				assertEquals(expected.toString(), actual.toString());
			}catch(InvalidStringExcepton e){
				fail("Received exception: " + e.getMessage());
			}
		}
		
		@Test
		public void createFirstInfoMessageTest(){
			try{
				controller = new ServerController();
				controller.resumeAGame(GameExampleJSON.ex2);
				
				JSONObject expected = new JSONObject("{\"type\":\"FirstInfoMessage\", \"goal1\": 9, \"goal2\": 12,\"personalGoal\":8, \"playersNumber\": 3}");
				JSONObject actual = controller.createFirstInfoMessage(2);
				
				assertEquals(expected.toString(), actual.toString());
			}catch(InvalidStringExcepton e){
				fail("Received exception: " + e.getMessage());
			}
		}
		
		@Test
		public void createGameDataMessageTest(){
			try{
				controller = new ServerController();
				controller.resumeAGame(GameExampleJSON.ex2);
				
				JSONObject expected = new JSONObject("{\"type\":\"GameDataMessage\", \"board\":\"     G   /    GC   /  CFGGB  /TBPBTBF  / BCTPFPC /  CTGBPCC/  GBPTC  /   FB    /   C     /\", \"bookshelves\":[\"      /      /      /      /      /\",\"      /      /      /      /      /\",\"      /      /      /      /      /\"], \"scores1\":[4], \"scores2\":[4, 6], \"turn\":0}");
				JSONObject actual = controller.createGameDataMessage();
				
				assertEquals(expected.toString(), actual.toString());
			}catch(InvalidStringExcepton e){
				fail("Received exception: " + e.getMessage());
			}
		}
	}
	
	private void startGenericTest(){
		controller = new ServerController();
		messageManager = new ServerMessageManager(controller);
		messageManager.setAllInterested();
	}
}
