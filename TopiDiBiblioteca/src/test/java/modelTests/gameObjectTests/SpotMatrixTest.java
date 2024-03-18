package modelTests.gameObjectTests;

import client.view.cli.CliMethods;
import model.Color;
import model.exceptions.InvalidStringExcepton;
import model.gameObjects.SpotMatrix;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class SpotMatrixTest{
	
	/**
	 * Tests about the right generation of the SpotMatrix using different constructor
	 */
	@Nested
	class createMatrixTests{
		@Test
		public void createMatrix1(){
			String str = "P V/ t / B /";
			
			try{
				CliMethods.printMat(new SpotMatrix(str));
			}catch(InvalidStringExcepton e){
				System.out.println("String not valid");
				fail();
			}
			
			assertTrue(true);
		}
		
		@Test
		public void createMatrix2(){
			String str = "H  / t / B /";
			
			Assertions.assertThrows(InvalidStringExcepton.class, () ->
				CliMethods.printMat(new SpotMatrix(str))
			);
			System.out.println("Exception received");
		}
		
		@Test
		public void createMatrix3(){
			String str = "H  / t / B / ";
			
			Assertions.assertThrows(InvalidStringExcepton.class, () ->
				CliMethods.printMat(new SpotMatrix(str))
			);
			System.out.println("Exception received");
		}
	}
	
	/**
	 * Tests about the main methods of SpotMatrix
	 */
	@Nested
	class functionalTests{
		SpotMatrix matrix;
		
		/**
		 * Tests the moethod that removes (changes the color to void) a tile from a spotMatrix
		 */
		@Test
		void removeTest1(){
			try{
				matrix = new SpotMatrix("TTF  /B GCB/  VGG/");
				int[][] toRemove = {{1, 2}, {0, 0}, {2, 4}};
				String result = SpotMatrix.uniformString("VTF  /B  CB/  VGV/");
				
				for(int[] r : toRemove)
					matrix.remove(r[0], r[1]);
				
				assertEquals(result, matrix.toString());
			}catch(InvalidStringExcepton e){
				fail();
			}
		}
		
		/**
		 * Tests the moethod that removes (changes the color to void) a tile from a spotMatrix
		 */
		@Test
		void removeTest2(){
			Assertions.assertThrows(IndexOutOfBoundsException.class,
				() -> {
					int[][] toRemove = {{1, 2}, {0, 0}, {2, 5}};
					
					matrix = new SpotMatrix("TTF  /B GCB/  VGG/");
					for(int[] r : toRemove)
						matrix.remove(r[0], r[1]);
				}
			);
			System.out.println("Exception received");
		}
		
		/**
		 * Tests the method that returns true if two tiles are adjacent (not void) in a spotMatrix
		 */
		@Test
		void adjacentTest(){
			try{
				matrix = new SpotMatrix("CBP/G T/TTT/");
			}catch(InvalidStringExcepton ignored){
			}
			
			int[][] requiredIdx = {
				{2, 0}, {1, 1}, {2, 2}, {2, 1}
			};
			
			Color[][] expected = {
				{null, null, Color.T, Color.G},
				{Color.G, Color.T, Color.T, Color.B},
				{Color.T, null, null, Color.T},
				{Color.T, null, Color.T, Color.V},
			};
			
			String[] keys = {"up", "right", "down", "left"};
			
			for(int c = 0; c < requiredIdx.length; c++){
				HashMap<String, Color> hm = matrix.adjacent(requiredIdx[c][0], requiredIdx[c][1]);
				
				for(int i = 0; i < 4; i++)
					if(!Objects.isNull(expected[c][i]))
						assertEquals(expected[c][i], hm.get(keys[i]));
			}
		}
		
		/**
		 * Tests if the method that transforms a SpotMatrix to a string works as desired
		 */
		@Test
		void toStringTest1(){
			try{
				String str = "BBB/CCC/TTT/";
				assertEquals(SpotMatrix.uniformString(str), (new SpotMatrix(str)).toString());
			}catch(InvalidStringExcepton e){
				fail();
			}
		}
		
		/**
		 * Tests if the method that transforms a SpotMatrix to a string works as desired
		 */
		@Test
		void toStringTest2(){
			try{
				String str = "B B/VVV/TtT/";
				assertEquals(SpotMatrix.uniformString(str), (new SpotMatrix(str)).toString());
			}catch(InvalidStringExcepton e){
				fail();
			}
		}
		
		/**
		 * Tests the method that compares two SpotMatrix
		 *
		 * @param s1       is a string representing a SpotMatrix
		 * @param s2       is a string representing a SpotMatrix
		 * @param expected is the expected result
		 */
		@ParameterizedTest
		@MethodSource("compareTestArguments")
		void compareTest(String s1, String s2, boolean expected){
			SpotMatrix a, b;
			
			try{
				a = new SpotMatrix(s1);
				b = new SpotMatrix(s2);
				assertEquals(expected, SpotMatrix.compare(a, b));
			}catch(InvalidStringExcepton e){
				fail();
			}
		}
		
		private static Stream<Arguments> compareTestArguments(){
			return Stream.of(
				Arguments.of("", "", true),
				Arguments.of("CVP/GFT/   /", "CBP/GFT/   /", false),
				Arguments.of("CBP/GFT/   /", "CBP/GFT/   /", true)
			);
		}
		
		/**
		 * Test if a certain set of coordinates is adjacent each other
		 */
		@ParameterizedTest
		@MethodSource("areAdjacentSpotsTestArguments")
		void areAdjacentSpotsTest(int[][] coos, boolean expected){
			assertEquals(expected, SpotMatrix.areAdjacentSpots(coos));
		}
		
		private static Stream<Arguments> areAdjacentSpotsTestArguments(){
			return Stream.of(
				Arguments.of(new int[][]{{0, 1}, {0, 2}, {0, 3}}, true),
				Arguments.of(new int[][]{{0, 1}}, true),
				Arguments.of(new int[][]{}, true),
				Arguments.of(new int[][]{{0, 1}, {0, 2}, {0, 3}, {1, 2}}, true),
				Arguments.of(new int[][]{{1, 4}, {1, 5}, {2, 4}, {2, 5}}, true),
				Arguments.of(new int[][]{{1, 4}, {1, 5}, {2, 4}, {2, 5}, {0, 0}}, false),
				Arguments.of(new int[][]{{6, 6}, {5, 6}, {6, 5}}, true)
			);
		}
	}
}