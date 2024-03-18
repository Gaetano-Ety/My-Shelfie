package clientTests.virtualModel;

import client.virtualModel.GameData;
import model.exceptions.InvalidPlayersNumberException;
import model.exceptions.InvalidStringExcepton;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class GameDataTest{
	/**
	 * Test if the ranking is created correctly
	 */
	@Test
	public void createRankingTest(){
		int[] scores = {2, 1, 22, 42};
		GameData.createRanking(scores).forEach((i, j) -> {
			System.out.println("Key: " + i + " - Value: " + j);
		});
		
		assertEquals("{3=42, 2=22, 0=2, 1=1}", GameData.createRanking(scores).toString());
	}
	
	/**
	 * Test if a set of coordinates can be taken from the board
	 */
	@ParameterizedTest
	@MethodSource("toValidateArguments")
	public void validTilesFromBoardTest(int nPlayer, String board, int[][] coos, boolean expected){
		try{
			GameData game = new GameData();
			game.setNumOfPlayer(nPlayer);
			game.updateBoard(board);
			assertEquals(expected, game.validTilesFromBoard(coos));
		}catch(InvalidStringExcepton | InvalidPlayersNumberException e){
			fail(e);
		}
		
	}
	
	private static Stream<Arguments> toValidateArguments(){
		return Stream.of(
			Arguments.of(4, "    CG   /   FBP   /  CFPCC  /GBCCGFBT /GPBCGTFBF/ PTPCFPCT/  GGBPG  /   TPP   /   BC    /", new int[][]{{7, 3}, {8, 3}, {8, 4}}, true),
			Arguments.of(4, "    CG   /   FBP   /  CFPCC  /GBCCGFBT /GPBCGTFBF/ PTPCFPCT/  GGBPG  /   TPP   /   BC    /", new int[][]{{7, 3}, {8, 4}, {8, 3}}, true),
			Arguments.of(3, "     G   /    GC   /  CFGGB  /TBPBTBF  / BCTPFPC /  CTGBP  /  GBPTC  /   FB    /   C     /", new int[][]{{6, 6}, {5, 6}, {6, 5}}, true)
		);
	}
	
}