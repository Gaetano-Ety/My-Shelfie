package modelTests.goalsTests;

import client.view.cli.CliMethods;
import model.exceptions.InvalidMatrixException;
import model.exceptions.InvalidStringExcepton;
import model.gameObjects.BookShelf;
import model.gameObjects.SpotMatrix;
import model.goals.CommonGoal12;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CommonGoal12Test {
    // Descendant or an ascendant "STAIR"
    CommonGoal12 c = new CommonGoal12();

    @ParameterizedTest
    @ValueSource(strings = {
            "cccccc/vccccc/vvcccc/vvvccc/vvvvcc/",
            "vvvvcc/vvvccc/vvcccc/vccccc/cccccc/",
            "vccccc/vvcccc/vvvccc/vvvvcc/vvvvvc/",
            "vvvvvc/vvvvcc/vvvccc/vvcccc/vccccc/"
    })
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
    @ValueSource(strings = {
            "ffffff/ffffff/ffffff/ffffff/ffffff/",
            "ffffff/      /      /      /cccccc/",
            "      /tttttt/      /      /      /",
            "vvvvvv/vvvvvv/vvvvvv/vvvvvv/vvvvvv/",
            "vvvvvv/vvvvvb/vvvvbb/vvvbbb/vvbbbb/",
            "vvbbbb/vvvbbb/vvvvbb/vvvvvb/vvvvvv/"
    })
    void testFalse(String s) {
        try {
            SpotMatrix sm = new SpotMatrix(s);
            CliMethods.printMat(sm);

            BookShelf b = new BookShelf();
            b.copyMatrix(sm);

            assertFalse(c.verifyCommonGoal(b));
        } catch (InvalidStringExcepton | InvalidMatrixException e) {
            fail();
        }
    }

}