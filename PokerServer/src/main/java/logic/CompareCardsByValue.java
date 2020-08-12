
package logic;

import java.util.Comparator;

import logic.Card;



/**
 *
 * Objects of this class are used to sort cards by value.
 *
 */

public class CompareCardsByValue implements Comparator<Card>{

    /**
     * Compares cards based on their int value.
     * @return A positive value if the first card has a higher value, a negative value if the second card has the
     * highest value, and zero if they have the same value.
     */

    public int compare(Card card1, Card card2) {

        if(card1.getValue() == card2.getValue()) {
            return 0;
        }
        else {
            return (card1.getValue() - card2.getValue());
        }
    }
}
