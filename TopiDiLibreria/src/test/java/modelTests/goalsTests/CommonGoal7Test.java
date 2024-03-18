package modelTests.goalsTests;

import client.view.cli.CliMethods;
import model.exceptions.InvalidMatrixException;
import model.exceptions.InvalidStringExcepton;
import model.gameObjects.BookShelf;
import model.gameObjects.SpotMatrix;
import model.goals.CommonGoal7;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommonGoal7Test {
    //DIAGONAL of 5 cards of the same type
    CommonGoal7 c = new CommonGoal7();

    @ParameterizedTest
    @ValueSource(strings = {"cpgtfb/bcftgp/pgctfb/bftcgp/pgtfcb/", "bcftgp/pgctfb/bftcgp/pgtfcb/pgtfcc/", "pgtfcb/bftcgp/pgctfb/bcftgp/cpgtfb/", "pgtfcc/pgtfcb/bftcgp/pgctfb/bcftgp/"})
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
    @ValueSource(strings = {"cpgtfb/bbftgp/pgctfb/bftcgp/pgtfcb/", "vvvvvv/vvvvvv/vvvvvv/vvvvvv/vvvvvv/"})
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