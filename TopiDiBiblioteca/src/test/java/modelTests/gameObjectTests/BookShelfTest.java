package modelTests.gameObjectTests;

import client.view.cli.CliMethods;
import model.Color;
import model.exceptions.FullColumnException;
import model.exceptions.InvalidMatrixException;
import model.exceptions.InvalidStringExcepton;
import model.gameObjects.BookShelf;
import model.gameObjects.SpotMatrix;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class BookShelfTest{
	/**
	 * Functional tests of addCard method that controls if the colors match
	 */
	@Test
	public void testAddCard(){
		BookShelf bookShelf = new BookShelf();
		try{
			bookShelf.addCard(4, Color.CATS);
			for(int i = BookShelf.getNumRows() - 1; i >= 0; i--)
				bookShelf.addCard(1, Color.BOOKS);
		}catch(FullColumnException ignored){
		}
		CliMethods.printMat(bookShelf);
		for(int j = BookShelf.getNumRows() - 1; j >= 0; j--)
			assertEquals(Color.BOOKS, bookShelf.getColorAt(1, j));
		assertEquals(Color.CATS, bookShelf.getColorAt(4, 5));
		
	}
	
	/**
	 * Test of addCard method that controls if the exception is thrown when the column is full
	 */
	@Test
	public void testAddCardWhenColumnIsFull(){
		BookShelf bookShelf = new BookShelf();
		for(int row = BookShelf.getNumRows() - 1; row >= 0; row--){
			try{
				bookShelf.addCard(2, Color.CATS);
			}catch(FullColumnException ignored){
			}
		}
		CliMethods.printMat(bookShelf);
		assertThrows(FullColumnException.class, () -> bookShelf.addCard(2, Color.CATS));
	}

	/**
	 * Tests the moethod that returns the number of groups of minimum nOf adjacent tiles
	 *
	 * @param bookshelf bookshelf
	 * @param nOf       minimum number of tiles forming the group
	 * @param expected  number
	 */
	@ParameterizedTest
	@MethodSource("nOfGroupTestArguments")
	void nOfGroupsTest(String bookshelf, int nOf, int expected){
		try{
			BookShelf bs = new BookShelf();
			bs.copyMatrix(new SpotMatrix(bookshelf));
			System.out.println("Search group of at lest " + nOf);
			CliMethods.printMat(bs);
			
			assertEquals(expected, bs.nOfGroups(nOf));
		}catch(InvalidStringExcepton | InvalidMatrixException e){
			fail();
		}
	}

	static Stream<Arguments> nOfGroupTestArguments(){
		return Stream.of(
			Arguments.of("      /cccccc/      /    tt/    tt/", 4, 2),
			Arguments.of("      /cccccc/bbbbbb/   fff/  fffG/", 6, 3),
			Arguments.of("      /cccccc/      /      /      /", 7, 0),
			Arguments.of("     c/     c/     c/     c/     c/", 5, 1),
			Arguments.of("   ccc/     c/     c/     c/   ccc/", 9, 1),
			Arguments.of("     c/     c/     t/     c/     c/", 2, 2),
			Arguments.of("     f/     f/   ccc/   ctc/   ccc/", 8, 1),
			Arguments.of("     c/     b/     p/     g/     f/", 1, 5),
			Arguments.of("     c/     b/     p/     g/     f/", 2, 0),
			Arguments.of("CTTTCC/PTPTPP/TTPBBB/GGPPGB/CGGGGC/", 7, 2),
			Arguments.of("      /      /      /GGPPGB/CGGGGC/", 7, 1),
			Arguments.of(" TTTCC/ TPTPP/TTPBBB/      /      /", 7, 1),
			Arguments.of(" TTTCC/ TPTPP/TTPBBB/GGPPGB/ GGGGC/", 4, 4)
		);
	}
}