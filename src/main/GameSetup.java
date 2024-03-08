// The GameSetup class is responsible for setting up the game by creating the players, creating the decks, 
// distributing the cards to the players and starting the game. It also has method related to the proper ending
// of the game.

import java.util.*;
import java.util.concurrent.CyclicBarrier;

public class GameSetup {

    // Use inner class to hold the result of player creation so that it can be
    // returned to be used later.
    public static class PlayerSetupResult {
        public Thread[] playerThreads;
        public CardDeck[] cardDecks;

        public PlayerSetupResult(Thread[] playerThreads, CardDeck[] cardDecks) {
            this.playerThreads = playerThreads;
            this.cardDecks = cardDecks;
        }
    }

    public static PlayerSetupResult createPlayers(int players, List<List<Integer>> playerhands,
            List<List<Integer>> decks) {
        CardDeck[] cardDecks = new CardDeck[players];
        CyclicBarrier startBarrier = new CyclicBarrier(players);

        for (int i = 0; i < cardDecks.length; i++) {
            cardDecks[i] = new CardDeck(i + 1, decks.get(i));
        }

        Thread[] playerThreads = new Thread[players];

        for (int i = 0; i < players; i++) {
            int playerID = i + 1;
            int preferredDenomination = i + 1;
            List<Integer> playerHand = playerhands.get(i);
            CardDeck leftDeck = cardDecks[i];
            CardDeck rightDeck = (i == players - 1) ? cardDecks[0] : cardDecks[i + 1];
            Player player = new Player(playerID, preferredDenomination, playerHand, leftDeck, rightDeck, startBarrier);
            playerThreads[i] = new Thread(player);
        }

        return new PlayerSetupResult(playerThreads, cardDecks);
    }

    public static List<List<Integer>> getHands(List<Integer> cardValues, int numPlayers) {
        List<List<Integer>> hands = new ArrayList<>();

        int splitIndex = cardValues.size() / 2;
        List<Integer> firstHalf = cardValues.subList(0, splitIndex);

        for (int i = 0; i < firstHalf.size(); i++) {
            int playerID = i % numPlayers;

            if (hands.size() <= playerID) {
                hands.add(new ArrayList<>());
            }

            hands.get(playerID).add(firstHalf.get(i));
        }

        return hands;

    }

    public static List<List<Integer>> getDecks(List<Integer> cardValues, int numPlayers) {
        List<List<Integer>> deck = new ArrayList<>();

        int splitIndex = cardValues.size() / 2;
        List<Integer> secondHalf = cardValues.subList(splitIndex, cardValues.size());

        for (int i = 0; i < secondHalf.size(); i++) {
            int playerID = i % numPlayers;

            if (deck.size() <= playerID) {
                deck.add(new ArrayList<>());
            }

            deck.get(playerID).add(secondHalf.get(i));
        }

        return deck;
    }

    private void resetGame() {
        // Reset static variables in Player class
        Player.resetGameVariables();
    }

    public void runGame(int players, List<Integer> cardValues) {
        List<List<Integer>> playerHands = getHands(cardValues, players);
        List<List<Integer>> decks = getDecks(cardValues, players);

        System.out.println("\nPlayer Hands:");
        for (int i = 0; i < playerHands.size(); i++) {
            System.out.println("Player " + (i + 1) + ": " + playerHands.get(i));
        }

        System.out.println("\nDecks:");
        for (int i = 0; i < decks.size(); i++) {
            System.out.println("Deck " + (i + 1) + ": " + decks.get(i));
        }

        PlayerSetupResult setupResult = createPlayers(players, playerHands, decks);
        Thread[] playerThreads = setupResult.playerThreads;
        CardDeck[] cardDecks = setupResult.cardDecks;

        // Start player threads
        for (Thread thread : playerThreads) {
            thread.start();
        }

        // Wait for all player threads to finish
        try {
            for (Thread thread : playerThreads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if rerun is needed
        if (Player.getWinnerCount() > 1) {
            System.out.println("Multiple winners detected. Rerunning the game.");
            resetGame(); // Call resetGame with necessary parameters
            runGame(players, cardValues); // Recursive call to rerun the game
        } else {

            // After the game has ended, call the method for CardDeck output files
            for (CardDeck deck : cardDecks) {
                deck.writeDeckStateToFile();
            }
        }

    }
}
