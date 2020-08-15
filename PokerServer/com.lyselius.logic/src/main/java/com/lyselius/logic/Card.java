package com.lyselius.logic;

/**
 * The class represents a card in a normal playing deck.
 *
 */

public class Card {

    private Suit suit;
    private int value;


    public Card(Suit suit, int value)
    {
        this.suit = suit;
        this.value = value;
    }


    /**
     * 	Returns the suit of the card.
     * @return The suit of the card.
     */
    public Suit getSuit() {
        return suit;
    }




    /**
     * Returns the value of the card.
     * @return The value of the card.
     */
    public int getValue() {
        return value;
    }



    /**
     * Returns a string representation of the card in the form of "value suit", and in the case of a high card,
     * the int value is exchanged for a letter "A suit", "K suit" and so on.
     */


    @Override
    public String toString()
    {
        int value = getValue();

        switch(value)
        {
            case 14:
                return "A " + getSuit();

            case 13:
                return "K " + getSuit();

            case 12:
                return "Q " + getSuit();

            case 11:
                return "J " + getSuit();

            default:
                return value + " " + getSuit();
        }

    }
}
