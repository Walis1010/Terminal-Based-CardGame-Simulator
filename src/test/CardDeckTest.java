import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.IOException;

public class CardDeckTest {

    private GameSetup g;

    @Before
    public void setUp() {
        g = new GameSetup();
    }

    private List<Integer> generateCardValues(int count) {
        List<Integer> values = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            values.add(random.nextInt(10) + 1);
        }
        return values;
    }

    @Test
    public void TestwriteDeckStateToFile() {
        // Generate random number of players from 1 to 10
        int n = new Random().nextInt(10) + 1;

        // Initiate how many cards are needed
        int totalCards = 8 * n;

        // Array list for card values
        List<Integer> cardValues = generateCardValues(totalCards);

        g.runGame(n, cardValues);

        for (int i = 1; i <= n; i++) {
            // Construct the expected file path
            String filePath = "./../src/deck" + i + "_output.txt";
            Path expectedPath = Paths.get(filePath);

            // Check if the file exists and print a message
            if (Files.exists(expectedPath)) {
                System.out.println(filePath + " exists.");
            } else {
                fail("File should exist: " + filePath);
            }
        }
    }
}
