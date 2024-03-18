package modelTests.goalsTests;

import client.view.cli.CliMethods;
import model.exceptions.InvalidMatrixException;
import model.exceptions.InvalidStringExcepton;
import model.gameObjects.BookShelf;
import model.gameObjects.SpotMatrix;
import model.goals.CommonGoal3;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommonGoal3Test{
	//4 cards of the SAME TYPE in the 4 CORNERS
	
	CommonGoal3 c = new CommonGoal3();
	
	@ParameterizedTest
	@ValueSource(strings = {
		"CBPTGC/CBPTGF/FGTPBC/FGTPBC/CBPTGC/",
		"BBPTCB/BBPTGB/BGTPBB/FGTPBC/BBPTGB/",
		"cccccc/vvvvvv/vvvvvv/vvvvvv/cccccc/"
	})
	void testTrue(String s){
		try{
			SpotMatrix sm = new SpotMatrix(s);
			CliMethods.printMat(sm);
			BookShelf b = new BookShelf();
			try{
				b.copyMatrix(sm);
			}catch(InvalidMatrixException e){
				throw new RuntimeException(e);
			}
			assertTrue(c.verifyCommonGoal(b));
		}catch(InvalidStringExcepton e){
			throw new RuntimeException(e);
		}
	}
	
	@ParameterizedTest
	@ValueSource(strings = {"CBPTGC/CBPTGF/FGTPBC/FGTPBC/CBPTGF/", "vvvvvv/vvvvvv/vvvvvv/vvvvvv/vvvvvv/"})
	void testFalse(String s){
		try{
			SpotMatrix sm = new SpotMatrix(s);
			CliMethods.printMat(sm);
			BookShelf b = new BookShelf();
			try{
				b.copyMatrix(sm);
			}catch(InvalidMatrixException e){
				throw new RuntimeException(e);
			}
			assertFalse(c.verifyCommonGoal(b));
		}catch(InvalidStringExcepton e){
			throw new RuntimeException(e);
		}
	}
}