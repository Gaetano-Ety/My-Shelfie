package modelTests.gameEntitiesTests;

import model.exceptions.InvalidStringExcepton;
import model.exceptions.NotAvailableGoalException;
import model.gameEntities.Player;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class PlayerTest{
	Player player1;
	
	/**
	 * This is only a print test
	 */
	@Test
	public void toStringTest1(){
		try{
			player1 = new Player("Pippo");
			System.out.println(player1);
		}catch(NotAvailableGoalException e){
			fail();
		}
	}
	
	/**
	 * Verifies if a Player generate a correct JSON Object representing the player
	 *
	 * @param str is a player with its bookshelf and scores
	 */
	@ParameterizedTest
	@ValueSource(strings = {
		"{\"nickName\":\"Pippo\",\"bookShelf\":\"      /      /      /      /      /\",\"score\":0,\"goal1\":false,\"goal2\":false,\"personalGoal\":0}",
		"{\"nickName\":\"Pluto\",\"bookShelf\":\"      /CCCCCC/      /      /      /\",\"score\":0,\"goal1\":false,\"goal2\":false,\"personalGoal\":1}",
		"{\"nickName\":\"Paperino\",\"bookShelf\":\"      /CCCCCC/      /      /      /\",\"score\":0,\"goal1\":true,\"goal2\":false,\"personalGoal\":4}",
		"{\"nickName\":\"SexyPlayer69\",\"bookShelf\":\"      /CCCCCC/      /BBBBBB/      /\",\"score\":0,\"goal1\":true,\"goal2\":false,\"personalGoal\":9}",
	})
	public void resumePlayerTest(String str){
		try{
			player1 = new Player(new JSONObject(str));
			System.out.println(player1 + "\n");
			assertEquals(str, player1.toString());
		}catch(InvalidStringExcepton e){
			fail();
		}
	}
	
	/**
	 * testing the method to generate score from JSON Object Player
	 *
	 * @param str      is the expected string representing the player
	 * @param expected is the score
	 */
	@ParameterizedTest
	@MethodSource("generateScoreArguments")
	public void generateScoreTest(String str, int expected){
		try{
			Player p = new Player(new JSONObject(str));
			assertEquals(expected, p.generateScore());
			assertEquals(expected, p.generateScore());
		}catch(InvalidStringExcepton e){
			fail();
		}
	}
	
	private static Stream<Arguments> generateScoreArguments(){
		return Stream.of(
			Arguments.of("{\"nickName\":\"Pippo\",\"bookShelf\":\"      /      /      /      /      /\",\"score\":0,\"goal1\":false,\"goal2\":false,\"personalGoal\":0}", 0),
			Arguments.of("{\"nickName\":\"Pippo\",\"bookShelf\":\"BBBBBB/BBBBBB/BBBBBB/BBBBBB/BBBBBB/\",\"score\":0,\"goal1\":false,\"goal2\":false,\"personalGoal\":0}", 9),
			Arguments.of("{\"nickName\":\"Pippo\",\"bookShelf\":\"BBBBBB/FFFFFF/GGGGGG/TTTTTT/FFFFFF/\",\"score\":0,\"goal1\":false,\"goal2\":false,\"personalGoal\":0}", 40),
			Arguments.of("{\"nickName\":\"Pippo\",\"bookShelf\":\"CTTTCC/PTPTPP/TTPBBB/GGPPGB/CGGGGC/\",\"score\":9,\"goal1\":false,\"goal2\":false,\"personalGoal\":6}", 40)
		);
	}
	
	/**
	 * Test method isCompleted, that returns true if a bookshelf is full
	 *
	 * @param player   is a string representing a player
	 * @param expected is a boolean that tells if the bookshelf is  full
	 */
	@ParameterizedTest
	@MethodSource("isCompletedTestArguments")
	public void isCompletedTest(String player, boolean expected){
		try{
			assertEquals(expected, new Player(new JSONObject(player)).isCompleted());
		}catch(InvalidStringExcepton e){
			fail();
		}
	}
	
	static Stream<Arguments> isCompletedTestArguments(){
		return Stream.of(
			Arguments.of("{\"nickName\":\"Pippo\",\"bookShelf\":\"      /      /      /      /      /\",\"score\":0,\"goal1\":false,\"goal2\":false,\"personalGoal\":0}", false),
			Arguments.of("{\"nickName\":\"Pippo\",\"bookShelf\":\"      /CCCCCC/      /      /      /\",\"score\":0,\"goal1\":false,\"goal2\":false,\"personalGoal\":0}", false),
			Arguments.of("{\"nickName\":\"Pippo\",\"bookShelf\":\"BBBBBB/BBBBBB/BBBBBB/BBBBBB/BBBBBB/\",\"score\":0,\"goal1\":false,\"goal2\":false,\"personalGoal\":0}", true),
			Arguments.of("{\"nickName\":\"Pippo\",\"bookShelf\":\"CCCCCC/TTTTTT/TTTTTT/TTTTTT/CCCCCC/\",\"score\":0,\"goal1\":false,\"goal2\":false,\"personalGoal\":0}", true)
		);
	}
}
