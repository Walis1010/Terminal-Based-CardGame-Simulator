import static org.junit.Assert.*;
import org.junit.Test;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class CardGameTest {
    
    @Test
    public void testValidateFile() {
        BufferedWriter writer = null;
        try {
            int n = new Random().nextInt(10) + 1;  // Randomly generate n (1 to 10)
            String path = "testPack.txt";
            File f = new File(path);

            if (!f.createNewFile()) {
                writer = new BufferedWriter(new FileWriter(path));
                writer.write("");
                writer.newLine();
            }

            writer = new BufferedWriter(new FileWriter(path, true));
            Random random = new Random();

            for (int i = 1; i <= n; i++) {
                for (int j = 0; j < 8; j++) {
                    int randomNumber = random.nextInt(10) + 1;
                    writer.write(randomNumber + "");
                    writer.newLine();
                }
            }

            writer.close();

            // Use Files.lines() to read the lines from the file and count them
            long lineCount = Files.lines(Paths.get(path)).count();

            assertEquals(8 * n, lineCount);

        } catch (IOException e) {
            fail("Failed to create a test pack file");
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
