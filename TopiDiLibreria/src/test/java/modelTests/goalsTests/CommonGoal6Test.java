package modelTests.goalsTests;

import client.view.cli.CliMethods;
import model.exceptions.InvalidMatrixException;
import model.exceptions.InvalidStringExcepton;
import model.gameObjects.BookShelf;
import model.gameObjects.SpotMatrix;
import model.goals.CommonGoal6;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommonGoal6Test {
    // Eight spots of the same type
    CommonGoal6 c = new CommonGoal6();

    @ParameterizedTest
    @ValueSource(strings = {"bbbbbb/bbbbbb/bbbbbb/bbbbbb/bbbbbb/", "bbbbbb/vvvvbb/vvvvvv/cccccc/vvvvvv/", "cccccc/cccccc/vvvvvv/vvvvvv/vvvvvv/"})
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
    @ValueSource(strings = {"cccccc/bbbbbb/gggggg/tttttt/pppppp/", "vvvvvv/vvvvvv/vvvvvv/vvvvvv/vvvvvv/"})
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
