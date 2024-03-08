// The CardDeck class is responsible for storing the cards in the deck and providing methods related to the deck.

import java.util.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CardDeck {

    private final int cardDeckID;
    private final List<Integer> cards;

    public CardDeck(int cardDeckID, List<Integer> cards) {
        this.cardDeckID = cardDeckID;
        this.cards = cards;
    }

    public int getCardDeckID() {
        return cardDeckID;
    }

    public synchronized List<Integer> getCards() {
        return cards;
    }

    public synchronized int drawCard() {
        if (!cards.isEmpty()) {
            return cards.remove(0);
        } else {
            throw new IllegalStateException("Attempted to draw a card from an empty deck");
        }
    }

    public synchronized void discardedCard(int card) {
        cards.add(card);
        System.out.println("Discarded card " + card + " to Deck " + cardDeckID);
    }

    public synchronized boolean isEmpty() {
        return cards.isEmpty();
    }

    public void writeDeckStateToFile() {
        String filename = "deck" + cardDeckID + "_output.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("Deck " + cardDeckID + " contains ");
            for (Integer card : cards) {
                writer.write(card.toString() + " ");
            }
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized String toString() {
        return "CardDeck[" + cardDeckID + "]" + "\n" + "Cards in Deck: " + cards + "\n";
    }
}
