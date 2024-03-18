package modelTests.goalsTests;

import client.view.cli.CliMethods;
import model.exceptions.InvalidMatrixException;
import model.exceptions.InvalidStringExcepton;
import model.gameObjects.BookShelf;
import model.gameObjects.SpotMatrix;
import model.goals.CommonGoal10;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommonGoal10Test {
    //at least 2 rows with 5 different types of tiles
    CommonGoal10 c = new CommonGoal10();
    @ParameterizedTest
    @ValueSource(strings = {"ccbgfp/cfgtfp/cpftfp/ctpbfp/cgcbfp/", "ccbgfp/cfgtfg/cpftff/ctpbfb/cgcbfc/", "cbgptf/bcgptf/ftpgbc/tfpgbc/gpgptf/"})
    void testTrue(String s){
        try {
            SpotMatrix sm = new SpotMatrix(s);
            CliMethods.printMat(sm);
            BookShelf b = new BookShelf();
            try {
                b.copyMatrix(sm);
            } catch (InvalidMatrixException e) {
                throw new RuntimeException(e);
            }
            assertTrue(c.verifyCommonGoal(b));
        } catch (InvalidStringExcepton e) {
            throw new RuntimeException(e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"ffffff/ffffff/ffffff/ffffff/ffffff/", "vvvvvv/vvvvvv/vvvvvv/vvvvvv/vvvvvv/"})
    void testFalse(String s){
        try {
            SpotMatrix sm = new SpotMatrix(s);
            CliMethods.printMat(sm);
            BookShelf b = new BookShelf();
            try {
                b.copyMatrix(sm);
            } catch (InvalidMatrixException e) {
                throw new RuntimeException(e);
            }
            assertFalse(c.verifyCommonGoal(b));
        } catch (InvalidStringExcepton e) {
            throw new RuntimeException(e);
        }
    }
}