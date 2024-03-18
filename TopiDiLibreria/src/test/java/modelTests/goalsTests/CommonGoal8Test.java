package modelTests.goalsTests;

import client.view.cli.CliMethods;
import model.exceptions.InvalidMatrixException;
import model.exceptions.InvalidStringExcepton;
import model.gameObjects.BookShelf;
import model.gameObjects.SpotMatrix;
import model.goals.CommonGoal8;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommonGoal8Test {
    // Four rows with at most 3 different types of cards
    CommonGoal8 c = new CommonGoal8();

    @ParameterizedTest
    @ValueSource(strings = {"ccffbb/ccffbb/ccffbb/bbffcc/bbffcc/", "ccpcff/cbbbtt/cftppt/cbpggb/cfbtbb/", "ctfctf/tfctfc/cftcft/ftcftc/ctfctf/"})
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
    @ValueSource(strings = {"vvvvvv/vvvvvv/vvvvvv/vvvvvv/vvvvvv/", "cccccc/bbbbbb/gggggg/tttttt/ffffff/", "vvvptc/fccpbc/vvvvvv/vvvvvv/vvvvfg/", "vvvbpf/vvvvtf/vfcctf/vvvvpf/vvvbpf/"})
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