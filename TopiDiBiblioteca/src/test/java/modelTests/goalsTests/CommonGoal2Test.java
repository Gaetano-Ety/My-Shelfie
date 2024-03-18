package modelTests.goalsTests;

import client.view.cli.CliMethods;
import model.exceptions.InvalidMatrixException;
import model.exceptions.InvalidStringExcepton;
import model.gameObjects.BookShelf;
import model.gameObjects.SpotMatrix;
import model.goals.CommonGoal2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommonGoal2Test {
    //AT LEAST 4 GROUPS of 4 adjacent tiles of the same type
    CommonGoal2 c = new CommonGoal2();

    @ParameterizedTest
    @ValueSource(strings = {"CBPTGF/CBPTGF/CBPTGF/CBPTGF/FGTPBC/", "gggbpt/gptbbb/ftttpb/fffbtp/pbbgtf/", "vvbbbb/vvcccc/vvtttt/vvgggg/vvvvvv/"})
    void testTrue(String s) {
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
    @ValueSource(strings = {"CBPTGF/FGTPBC/CBPTGF/FGTPBC/CBPTGF/", "vvvvvv/vvvvvv/vvvvvv/vvvvvv/vvvvvv/"})
    void testFalse(String s) {
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