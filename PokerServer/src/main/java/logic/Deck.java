package logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Thic class represents a normal deck of cards.
 *
 */

public class Deck {

    private ArrayList<Card> deck;
    private int pointer = 0;


    public Deck() {
        deck = new ArrayList<Card>();
        createCards();
    }


    private void createCards()
    {
        for(int i = 2; i < 15; i++)
        {
            deck.add(new Card(Suit.SPADES, i));
            deck.add(new Card(Suit.HEARTS, i));
            deck.add(new Card(Suit.DIAMONDS, i));
            deck.add(new Card(Suit.CLUBS, i));
        }
    }




	/* This should hopefully be enough to achieve a perfect shuffle where all 52! permutations are
	  equally likely. */


    /**
     * Shuffles the cards in the deck.
     */

    public void shuffle()
    {
        Random random = new Random();

        for(int i = 0; i < 52; i++)
        {
            Collections.swap(deck, i, random.nextInt(52));
        }
    }


    /**
     * Deals a card from the deck.
     * @return A {@link Card Card object}.
     */

    public Card deal()
    {
        pointer++;
        return deck.get(pointer - 1);
    }


//	public int getPointer() {
//		return pointer;
//	}
//
//
//	public void setPointer(int pointer) {
//		this.pointer = pointer;
//	}


}