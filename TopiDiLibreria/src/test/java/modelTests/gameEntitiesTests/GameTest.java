package modelTests.gameEntitiesTests;

import model.exceptions.InvalidPlayersNumberException;
import model.exceptions.InvalidStringExcepton;
import model.exceptions.NotAvailableGoalException;
import model.gameEntities.Game;
import modelTests.GameExampleJSON;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class GameTest{
	Game game;
	
	/**
	 *Test to see how is a game printed
	 */
	@Test
	public void toStringTest1(){
		try{
			game = new Game(2);
			game.assignPlayer(0, "Giovanna");
			game.assignPlayer(1, "Paola");
			System.out.println(game);
		}catch(InvalidPlayersNumberException | NotAvailableGoalException e){
			fail();
		}
	}

	/**
	 * Test to make sure that a game from JSON is printed the same way as the actual game
	 */
	@Test
	public void createGameFromJSON(){
		try{
			assertEquals(GameExampleJSON.ex4.toString(), new Game(GameExampleJSON.ex4).toJSON().toString());
		}catch(InvalidStringExcepton e){
			fail();
		}
	}

	/**
	 * Test if stack containing points of common Goals works
	 */
	@Test
	public void verifyGoalTest(){
		try{
			game = new Game(GameExampleJSON.ex3);
			Stack<Integer> expected1 = new Stack<>(), expected2 = new Stack<>();
			
			expected1.add(4);
			expected1.add(6);
			
			expected2.add(4);
			expected2.add(6);
			expected2.add(8);

			game.verifyGoal(0);
			assertEquals(expected1, game.getGoalStack(1));
			assertEquals(expected2, game.getGoalStack(2));
			assertEquals(8, game.getActualScore(0));
			
			game.verifyGoal(2);
			assertEquals(expected1, game.getGoalStack(2));
			assertEquals(expected1, game.getGoalStack(1));
			assertEquals(8, game.getActualScore(2));
		}catch(InvalidStringExcepton e){
			fail();
		}
		
	}
}