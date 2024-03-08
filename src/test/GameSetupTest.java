import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class GameSetupTest {

    private GameSetup g;

    @Before
    public void setUp() {
        g = new GameSetup();
    }

    @Test
    public void testGetHands() {
        int n = new Random().nextInt(10) + 1;  // Randomly generate n (1 to 10)
        List<Integer> cardValues = generateCardValues(8 * n);  // Generate 8n card values
        List<List<Integer>> hands = g.getHands(cardValues, n);

        assertNotNull(hands);
        assertEquals(n, hands.size());  // Check if there are n players

        for (List<Integer> playerHand : hands) {
            assertEquals(4, playerHand.size());  // Check if each player has 4 cards
        }
    }
    
    @Test
    public void testGetDecks() {
        int n = new Random().nextInt(10) + 1;  // Randomly generate n (1 to 10)
        List<Integer> cardValues = generateCardValues(8 * n);  // Generate 8n card values
        List<List<Integer>> decks = g.getDecks(cardValues, n);

        assertNotNull(decks);
        assertEquals(n, decks.size());  // Check if there are n decks

        for (List<Integer> deck : decks) {
            assertEquals(4, deck.size());  // Check if each deck has 4 cards
        }
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
    public void testRandomRunGame(){
         //Generate random number of players from 1 to 10 
         int n = new Random().nextInt(10) + 1;
         
         //Initiate how many cards is needed
         int totalCards = 8 * n;
         
         //Array list for card values
         List<Integer> cardValues = generateCardValues(totalCards);
        
         //Record time start
         long startTime = System.currentTimeMillis();
         
         g.runGame(n,cardValues);
         
         //Record end time after running game
         long endTime = System.currentTimeMillis();
         
         long timeTaken = endTime - startTime;
         System.out.println("Time taken for runGame: " + timeTaken + " milliseconds");        
    }
    
   @Test
   public void testReadyRunGame() {
       // Generate random number of players from 1 to 10
       int players = 4;
   
       // Set the pack file path
       String pack = "../res/four.txt";
   
       List<Integer> cardValues = new ArrayList<>();
   
       try (Scanner fileScanner = new Scanner(new File(pack))) {
           while (fileScanner.hasNextLine()) {
               String line = fileScanner.nextLine();
               try {
                   cardValues.add(Integer.parseInt(line));
               } catch (NumberFormatException e) {
                   throw new IllegalArgumentException("Denomination values must be integers");
               }
           }
   
           // Ensure that the pack contains at least 8N values so that the game can be
           // started properly
           if (cardValues.size() < players * 8) {
               throw new IllegalArgumentException("Pack file must contain at least 8N values");
           } else {
               cardValues = new ArrayList<>(cardValues.subList(0, players * 8));
           }
       } catch (FileNotFoundException e) {
           System.out.println("Error: File not found.");
           System.out.println("Please try again:");
           return; // Exit the test if the file is not found
       }
   
       // Record start time
       long startTime = System.currentTimeMillis();
   
       // Run the game
       g.runGame(players, cardValues);
   
       // Record end time after running the game
       long endTime = System.currentTimeMillis();
   
       long timeTaken = endTime - startTime;
       System.out.println("Time taken for runGame: " + timeTaken + " milliseconds");
   }           

}
