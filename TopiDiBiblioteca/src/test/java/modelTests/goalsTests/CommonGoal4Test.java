package modelTests.goalsTests;

import client.view.cli.CliMethods;
import model.exceptions.InvalidMatrixException;
import model.exceptions.InvalidStringExcepton;
import model.gameObjects.BookShelf;
import model.gameObjects.SpotMatrix;
import model.goals.CommonGoal4;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommonGoal4Test {
    //two square made of spots of the same color

    CommonGoal4 c = new CommonGoal4();

    @ParameterizedTest
    @ValueSource(strings = {"BBBBBB/BBBBBB/BBBBBB/BBBBBB/BBBBBB/", "bbgftp/bbtpgf/ggftpg/ggptff/bgtpfg/", "bbgftp/bbtpgf/gfftpp/ggptpp/bgtpfg/"})
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
    @ValueSource(strings = {"cftgbp/cftgbp/cftgbp/cftgbp/cftgbp/", "vvvvbb/vvvvbb/vvvvbb/vvvvvv/vvvvvv/", "vvvcbb/vvvcbb/vvvvvv/vvvvvv/vvvvvv/", "vvvvbb/vvvvvb/vvvvbb/vvvvvv/vvvvvv/", "vvvvvv/vvvvvv/vvvvvv/vvvvvv/vvvvvv/"})
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
