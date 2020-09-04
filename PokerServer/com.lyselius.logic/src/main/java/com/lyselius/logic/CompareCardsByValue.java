
package com.lyselius.logic;

import java.util.Comparator;


/**
 *
 * Objects of this class are used to sort cards by value.
 *
 */

public class CompareCardsByValue implements Comparator<Card>{

    /**
     * Compares cards based on their int value.
     * @return A positive value if the first card has a higher value, a negative value if the second card has the
     * highest value. If the cards have the same value, they are sorted by suit.
     */

    public int compare(Card card1, Card card2) {

        if(card1.getValue() == card2.getValue()) {
            // If the cards have the same value, they are sorted by suit. This is only necessary for testing purposes.
            return (getSuitValue(card1) - getSuitValue(card2));
        }
        else {
            return (card1.getValue() - card2.getValue());
        }
    }



    private int getSuitValue(Card card)
    {
        if(card.getSuit() == Suit.HEARTS) { return 4; }
        if (card.getSuit() == Suit.DIAMONDS) { return 3; }
        if (card.getSuit() == Suit.CLUBS) { return 2; }
        return 1;
    }
}
