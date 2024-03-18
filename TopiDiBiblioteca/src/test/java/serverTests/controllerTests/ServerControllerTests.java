package serverTests.controllerTests;

import model.Color;
import model.exceptions.FullColumnException;
import model.exceptions.InvalidActionException;
import model.exceptions.InvalidPlayersNumberException;
import model.exceptions.InvalidStringExcepton;
import model.goals.CommonGoalFactory;
import model.goals.PersonalGoal;
import modelTests.GameExampleJSON;
import networkMessages.exceptions.InvalidPlayerNickNameException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import server.controller.ServerController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class ServerControllerTests{
	ServerController controller;
	
	/**
	 * Tests of the setters
	 */
	@Nested
	class SetterTests{
		/**
		 * Test the nickname assignment by the controller
		 */
		@Test
		public void set_getNickNames1(){
			try{
				createController(3);
				
				ArrayList<String> names = new ArrayList<>(Arrays.asList("Pippo", "Pluto", "Paperino"));
				
				IntStream.range(0, 3).forEach(i -> {
					try{
						controller.setNickName(i, names.get(i));
					}catch(InvalidPlayerNickNameException e){
						fail(e);
					}
				});
				
				assertEquals(names, controller.getNickNames());
			}catch(InvalidPlayersNumberException e){
				fail(e);
			}
		}
		
		/**
		 * Test the behavior of the controller in case you enter a name of an already existing user
		 */
		@Test
		public void set_getNickNames2(){
			try{
				createController(2);
				
				controller.setNickName(0, "Pippo");
				
				assertThrows(InvalidPlayerNickNameException.class, () ->
					controller.setNickName(1, "Pippo")
				);
			}catch(InvalidPlayersNumberException | InvalidPlayerNickNameException e){
				fail();
			}
		}
	}
	
	@Nested
	class FunctionalTests{
		/**
		 * Tests if the method that removes the cards with given coordinates from the board works
		 */
		@Test
		public void takenCardsTest(){
			try{
				controller = new ServerController();
				controller.resumeAGame(GameExampleJSON.ex2);
				
				int[][] coordinates = {{0, 5}, {1, 5}, {1, 4}};
				String expected = "         /         /  CFGGB  /TBPBTBF  / BCTPFPC /  CTGBPCC/  GBPTC  /   FB    /   C     /";
				
				controller.takenCards(coordinates);
				assertEquals(expected, controller.getBoard());
			}catch(InvalidStringExcepton | InvalidActionException e){
				System.out.println(e.getClass().getSimpleName());
				fail();
			}
		}
		
		/**
		 * Tests if the method to put tiles in a bookshelf works
		 */
		@Test
		public void putInBookshelfTest(){
			try{
				controller = new ServerController();
				controller.resumeAGame(GameExampleJSON.ex2);
				
				controller.putInBookshelf(0, 0, new Color[]{Color.C, Color.B, Color.G});
				String expected = "   GBC/      /      /      /      /";
				
				assertEquals(controller.getBookShelf(0), expected);
			}catch(InvalidStringExcepton | FullColumnException e){
				fail();
			}
		}
		
		/**
		 * Tests the method that changes turn, with appropriate checks before, and appropriate changes after
		 */
		@Test
		public void nextTurnTest(){
			try{
				controller = new ServerController();
				controller.resumeAGame(GameExampleJSON.ex4);
				
				String oldBoard = controller.getBoard();
				
				controller.nexTurn();
				
				HashMap<Integer, Integer> scores = controller.getPlayersScores();
				
				assertEquals(0, controller.getTurn());
				assertTrue(controller.isLastRound());
				assertEquals(34, scores.get(1));
				assertEquals(18, scores.get(0));
				assertNotEquals(oldBoard, controller.getBoard());
			}catch(InvalidStringExcepton e){
				fail();
			}
		}
	}
	
	/**
	 * Create a controller for tests and reset the static variable of the classes
	 */
	private void createController(int nPlayers) throws InvalidPlayersNumberException{
		controller = new ServerController();
		controller.setNumberOfPlayers(nPlayers);
		PersonalGoal.resetPossiblePersonalGoal();
		CommonGoalFactory.resetCommonGoal();
	}
}