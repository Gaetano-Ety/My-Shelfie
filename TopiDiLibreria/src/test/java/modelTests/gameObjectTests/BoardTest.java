package modelTests.gameObjectTests;

import client.view.cli.CliMethods;
import model.exceptions.InvalidMatrixException;
import model.exceptions.InvalidPlayersNumberException;
import model.exceptions.InvalidStringExcepton;
import model.gameObjects.Board;
import model.gameObjects.SpotMatrix;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest{
	private Board board;
	
	/**
	 * Tests concerning the structure of the game board based on the number of players
	 */
	@Nested
	class structureTests{
		/**
		 *Tests if the generated number of tiles is correct, depending on the number of players
		 * @param n i the number of players
		 */
		@ParameterizedTest
		@ValueSource(ints = {2, 3, 4})
		void shapeTest(int n){
			try{
				board = new Board(n);
				board.fill();
			}catch(InvalidPlayersNumberException ignored){
			}
			
			int spotOccupied = SpotMatrix.countNotVoidElement(board);
			
			CliMethods.printMat(board);
			assertEquals(29 + (n - 2) * 8, spotOccupied);
		}

		/**
		 * Tests the method to print a SpotMatrix
		 */
		@Test
		void copyMatrixTest1(){
			try{
				board = new Board(4);
				SpotMatrix spots = new SpotMatrix(
					"VVVVBBVVV/" +
						"VVVVVVVVV/" +
						"VVVVVVVVV/" +
						"VVVgctVVV/" +
						"VVVVVVVVV/" +
						"VVVVVtVVV/" +
						"VVVVVVVVV/" +
						"VVVVVVVVV/" +
						"VVVVVVVVV/"
				);
				board.copyMatrix(spots);
				CliMethods.printMat(board);
				assertTrue(SpotMatrix.compare(board, spots));
			}catch(InvalidPlayersNumberException | InvalidStringExcepton ignored){
			}catch(InvalidMatrixException e){
				fail();
			}
		}

		/**
		 * Tests the method to print a SpotMatrix
		 */
		@Test
		void copyMatrixTest2(){
			Assertions.assertThrows(InvalidMatrixException.class,
				() -> {
					board = new Board(2);
					SpotMatrix spots = new SpotMatrix(
						"VVVVBBVVV/" +
							"VVVVVVVVV/" +
							"VVVVVVVVV/" +
							"VVVgctVVV/" +
							"VVVVVVVVV/" +
							"VVVVVtVVV/" +
							"VVVVVVVVV/" +
							"VVVVVVVVV/" +
							"VVVVVVVVV/"
					);
					board.copyMatrix(spots);
					CliMethods.printMat(board);
				}
			);
			System.out.println("Exception received");
		}
	}
	
	/**
	 * Tests concerning the functionality of the board
	 */
	@Nested
	class functionalTests{
		/**
		 * Tests if a board si refilled correctly
		 */
		@Test
		void refillTest(){
			try{
				board = new Board(2);
			}catch(InvalidPlayersNumberException ignore){
			}
			
			String boardStr = "VVVVVVVVV/VVVVGVVVV/VVVVFVVVV/VVVVTVVVV/VVVVVVVVV/VVVVVVVVV/VVVVVVVVV/VVVVVVVVV/VVVVVVVVV/";
			try{
				board.copyMatrix(new SpotMatrix(boardStr));
			}catch(InvalidMatrixException e){
				fail();
			}catch(InvalidStringExcepton ignore){
			}
			
			board.fill();
			CliMethods.printMat(board);
			assertEquals(29, SpotMatrix.countNotVoidElement(board));
		}
		
		@ParameterizedTest
		@MethodSource("isToRefillTestArguments")
		void isToRefillTest(String boardString, boolean expected){
			try{
				board = new Board(2);
				board.copyMatrix(new SpotMatrix(boardString));
				CliMethods.printMat(board);
			}catch(InvalidPlayersNumberException | InvalidMatrixException | InvalidStringExcepton e){
				fail(e);
			}
			
			assertEquals(expected, board.isToRefill());
		}

		private static Stream<Arguments> isToRefillTestArguments(){
			return Stream.of(
				Arguments.of("VVVVVVVVV/VVVVGVVVV/VVVVVfVVV/VVVVTVVVV/VVVVVVVVV/VVVVVVVVV/VVVVVVVVV/VVVVVVVVV/VVVVVVVVV/", true),
				Arguments.of("VVVVVVVVV/VVVVGGVVV/VVVVVfVVV/VVVVTVVVV/VVVVVVVVV/VVVVVVVVV/VVVVVVVVV/VVVVVVVVV/VVVVVVVVV/", false),
				Arguments.of("         /         /         /     cp  /       g /         /     p   /         /         /", false),
				Arguments.of("         /         /         /         /         /         /         /         /         /", true)
			);
		}

		/**
		 * Tests if a certain free spot in the board is detected correctly
		 */
		@Test
		void isFreeTest(){
			try{
				board = new Board(4);
				board.copyMatrix(
					new SpotMatrix(
						"    TP   /   TFG   /  GGBFG  /GPPBCTBC /FPCPPPCCG/ TPBFFFFP/  CFTFG  /   FPG   /   BG    /"
					)
				);
			}catch(InvalidPlayersNumberException | InvalidStringExcepton | InvalidMatrixException ignore){
			}
			CliMethods.printMat(board);
			
			int[][] expectedFree = {
				{0, 4}, {1, 3}, {3, 1}, {0, 0}
			};
			
			int[][] expectedNotFree = {
				{1, 4}, {4, 4}, {7, 4}
			};
			
			for(int[] coo : expectedFree)
				assertTrue(board.isFree(coo[0], coo[1]));
			
			for(int[] coo : expectedNotFree)
				assertFalse(board.isFree(coo[0], coo[1]));
		}

		/**
		 * Tests the method that returns a boolean, true if in the SpotMatrix there is at least one group of num tiles
		 * @param matrix SpotMatrix
		 * @param num of tiles forming the group
		 * @param expected number expected
		 */
		@ParameterizedTest
		@MethodSource("thereAreGroupsOfArguments")
		void thereAreGroupsOfTest(String matrix, int num, boolean expected){
			CliMethods.printMat(matrix);
			try{
				board = new Board(4);
				board.copyMatrix(new SpotMatrix(matrix));
				assertEquals(expected, board.thereAreGroupsOf(num));
			}catch(Exception e){
				fail(e);
			}
		}
		
		private static Stream<Arguments> thereAreGroupsOfArguments(){
			return Stream.of(
				Arguments.of("    CG   /   FBP   /  CFPCC  /GBCCGFBT /GPBCGTFBF/ PTPCFPCT/  GGBPG  /   TPP   /   BC    /", 2, true),
				Arguments.of("    CG   /   FBP   /  CF     /     FBT /      FBF/       CT/    BPG  /   TPP   /   BC    /", 9, false)
			);
		}
	}
}
