// The Player class is responsible for handling player objects and player threads.
// It is resonsible for player attributes, player methods, the start of the player threads,
// player logics within the game, and writing to the player output files.

import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.io.*;

public class Player implements Runnable {

    private final int playerID;
    private final int preferredDenomination;
    private List<Integer> playerhand;
    private List<Integer> winnerHand;

    private final CardDeck leftDeck;
    private final CardDeck rightDeck;

    private static volatile boolean winnerAnnounced = false;
    private static volatile int winnerID; // maybe set to -1 to clearly indicate no one wins yet

    private final CyclicBarrier startBarrier;
    private static final Object logLock = new Object();

    private final Object drawCardLock = new Object();
    private final Object discardCardLock = new Object();
    private static final Object conditionCheckLock = new Object();

    private static volatile int winnerCount = 0;

    private BufferedWriter writer;

    public Player(int playerID, int preferredDenomination, List<Integer> playerhand, CardDeck leftDeck,
            CardDeck rightDeck, CyclicBarrier startBarrier) {
        this.playerID = playerID;
        this.preferredDenomination = preferredDenomination;
        this.playerhand = new LinkedList<>(playerhand);
        this.leftDeck = leftDeck;
        this.rightDeck = rightDeck;
        this.startBarrier = startBarrier;

        try {
            this.writer = new BufferedWriter(new FileWriter("player" + playerID + "_output.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPlayerID() {
        return playerID;
    }

    public int getPreferredDenomination() {
        return preferredDenomination;
    }

    public List<Integer> getPlayerHand() {
        return playerhand;
    }

    public CardDeck getLeftDeck() {
        return leftDeck;
    }

    public CardDeck getRightDeck() {
        return rightDeck;
    }

    public static int getWinnerCount() {
        return winnerCount;
    }

    public static synchronized void announceWinner(int winnerPlayerID) {
        if (!winnerAnnounced) {
            winnerAnnounced = true;
            winnerID = winnerPlayerID;
            winnerCount++;
            System.out.println("Player " + winnerPlayerID + " has won and notified other threads.");
        } else if (winnerAnnounced && winnerID != winnerPlayerID) {
            // Another player also meets the winning condition
            winnerCount++;
        }
    }

    public boolean checkWinningConditionAtStart(List<Integer> playerHands, int playerID) {
        synchronized (Player.class) {
            if (winnerAnnounced || playerHands.size() != 4) {
                return false;
            }

            int firstCardDenomination = playerHands.get(0);
            for (int denomination : playerHands) {
                if (denomination != firstCardDenomination) {
                    return false;
                }
            }

            announceWinner(this.playerID);
            return true;
        }
    }

    public synchronized boolean isAnnounced() {
        return winnerAnnounced;
    }

    public synchronized int getWinnerID() {
        return winnerID;
    }

    public boolean checkWinningCondition() {
        synchronized (Player.class) {
            if (playerhand.size() != 4) {
                return false;
            }

            for (int denomination : playerhand) {
                if (denomination != preferredDenomination) {
                    return false;
                }
            }

            announceWinner(this.playerID);
            return true;

        }
    }

    public void drawCard() {
        synchronized (drawCardLock) {
            synchronized (leftDeck) {
                System.out.println("\nPlayer " + playerID + " is attempting to draw a card.");
                if (playerhand.size() == 4 && !leftDeck.isEmpty()) {
                    System.out.println("\nLeft deck is: " + leftDeck);
                    int drawnCard = leftDeck.drawCard();
                    playerhand.add(drawnCard);
                    System.out.println("\nPlayer " + playerID + " drew card: " + drawnCard);
                    System.out.println("Player " + playerID + "'s current hand: " + playerhand);
                    System.out.println("Current left deck: " + leftDeck.getCards());

                    try {
                        logDrawAction(drawnCard, leftDeck.getCardDeckID());
                        logCurrentHand();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Player " + playerID
                            + " cannot draw a card, left deck is empty or player does not have exactly 4 cards.");
                }
            }
        }
    }

    public void discardCard() {
        synchronized (discardCardLock) {
            synchronized (rightDeck) {
                if (playerhand.size() == 5) {
                    int cardToDiscard = -1;
                    for (int card : playerhand) {
                        if (card != preferredDenomination) {
                            cardToDiscard = card;
                            break;
                        }
                    }

                    if (cardToDiscard != -1) {
                        playerhand.remove((Integer) cardToDiscard);
                        rightDeck.discardedCard(cardToDiscard);
                        System.out.println("\nPlayer " + playerID + " discarded card: " + cardToDiscard);
                        System.out.println("Player " + playerID + "'s current hand: " + playerhand);

                        try {
                            logDiscardAction(cardToDiscard, rightDeck.getCardDeckID());
                            logCurrentHand();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public static synchronized void resetGameVariables() {
        winnerAnnounced = false;
        winnerID = -1;
        winnerCount = 0;
    }

    private void logInitialHand() throws IOException {
        writer.write("player " + playerID + " initial hand " + playerhand.toString().replaceAll("[\\[\\]]", ""));
        writer.newLine();
        writer.flush();
    }

    private void logDrawAction(int drawnCard, int deckID) throws IOException {
        writer.write("player " + playerID + " draws a " + drawnCard + " from deck " + deckID);
        writer.newLine();
        writer.flush();
    }

    private void logDiscardAction(int discardedCard, int deckID) throws IOException {
        writer.write("player " + playerID + " discards a " + discardedCard + " to deck " + deckID);
        writer.newLine();
        writer.flush();
    }

    private void logCurrentHand() throws IOException {
        writer.write("player " + playerID + " current hand is " + playerhand.toString().replaceAll("[\\[\\]]", ""));
        writer.newLine();
        writer.flush();
    }

    private void logWinner() throws IOException {
        if (winnerID == playerID) {
            writer.write("player " + playerID + " wins");
            writer.newLine();
            writer.write("player " + playerID + " final hand: " + playerhand.toString().replaceAll("[\\[\\]]", ""));
            writer.newLine();
            writer.write("player " + playerID + " exits");
            writer.newLine();
            writer.flush();
        }
    }

    private void logFinalState() throws IOException {
        synchronized (Player.class) {
            if (winnerID == this.playerID) {
                writer.write("player " + this.playerID + " wins");
                writer.newLine();
                writer.write("player " + this.playerID + " exits");
                writer.newLine();
                writer.write("player " + this.playerID + " final hand: "
                        + this.playerhand.toString().replaceAll("[\\[\\]]", ""));
                writer.flush();
            } else {
                writer.write(
                        "player " + winnerID + " has informed player " + this.playerID + " that player " + winnerID
                                + " has won");
                writer.newLine();
                writer.write("player " + this.playerID + " exits");
                writer.newLine();
                writer.write(
                        "player " + this.playerID + " hand: " + this.playerhand.toString().replaceAll("[\\[\\]]", ""));
                writer.flush();

            }
        }
    }

    @Override
    public void run() {
        try {

            logInitialHand();

            // Synchronize the start of all threads
            startBarrier.await();

            synchronized (logLock) {
                System.out.println("Player " + playerID + " is running.");
            }

            startBarrier.await(); // Wait for all players to finish initial log

            boolean hasWon = checkWinningConditionAtStart(playerhand, playerID);

            startBarrier.await(); // Wait for all players to finish initial win check

            if (hasWon && !winnerAnnounced) {
                synchronized (logLock) {
                    announceWinner(playerID);
                    logWinner(); // Log the win
                }
            }

            // Re-check if a winner has been announced after the initial check

            // Enter the main game loop only if no immediate winner has been declared.
            while (!winnerAnnounced) {
                synchronized (conditionCheckLock) {
                    if (checkWinningConditionAtStart(playerhand, playerID)) {
                        synchronized (logLock) {
                            if (!winnerAnnounced) {
                                announceWinner(playerID);
                            }
                        }
                    }

                }

                startBarrier.await();

                synchronized (leftDeck) {
                    synchronized (logLock) {
                        Thread.sleep(500);
                        drawCard();
                    }
                }

                startBarrier.await();

                synchronized (rightDeck) {
                    synchronized (logLock) {
                        Thread.sleep(500);
                        discardCard();
                        if (checkWinningCondition()) {
                            synchronized (logLock) {
                                if (!winnerAnnounced) {
                                    announceWinner(playerID);
                                    logWinner();
                                    break;
                                }

                            }
                        }
                    }
                }

                startBarrier.await();
            }

            synchronized (logLock) {
                logFinalState();
            }

        } catch (InterruptedException | BrokenBarrierException | IOException e) {
            e.printStackTrace();
        } finally {
            synchronized (logLock) {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String toString() {
        return "Player[" + playerID + "]" + "\n" + "Preferred Denomination: " + preferredDenomination + "\n" + "Hand: "
                + playerhand + "\n" + "Left Deck: " + leftDeck + "\n";
    }

}
