// The Card class is kept as simple as possible with a single responsibility of
// storing the denomination of the card.

public class Card {

    private final int denomination;

    public Card(int denomination) {
        if (denomination < 0) {
            throw new IllegalArgumentException("Denomination must be a nonnegative integer");
        }
        this.denomination = denomination;
    }

    public int getDenomination() {
        return denomination;
    }

    public String cardValue() {
        return Integer.toString(denomination);
    }

    public String toString() {
        return "Card[" + denomination + "]";
    }

}