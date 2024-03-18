package modelTests.goalsTests;

import client.view.cli.CliMethods;
import model.exceptions.InvalidMatrixException;
import model.exceptions.InvalidStringExcepton;
import model.gameObjects.BookShelf;
import model.gameObjects.SpotMatrix;
import model.goals.CommonGoal5;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommonGoal5Test {
    //3 columns with AT MOST 3 different colors
    CommonGoal5 c = new CommonGoal5();

    @ParameterizedTest
    @ValueSource(strings = {"cccccc/bbbbbb/gggggg/tttttt/pppppp/", "cptpct/tcptpc/cptpct/gfpcbb/btffgc/", "bcbgcg/bgtpcg/vvvvvv/pgtpgt/bcfbcf/"})
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
    @ValueSource(strings = {"cbgtfp/cbgtfp/cbgtfp/cbgtfp/cbgtfp/", "cptpct/ttppcc/ccpptf/ttppcf/ccpptf/", "vvvvvv/vvvvvv/vvvvvv/vvvvvv/vvvvvv/"})
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