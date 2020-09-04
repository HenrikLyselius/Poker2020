package com.lyselius.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompareHandsTest {

    private CompareHands ch;
    private ArrayList<Card> hand;
    private ArrayList<Card> communityCards;
    private ArrayList<Card> playerHand;



    @BeforeEach
    public void init()
    {
        communityCards = new ArrayList<Card>();
        communityCards.add(new Card(Suit.HEARTS, 2));
        communityCards.add(new Card(Suit.DIAMONDS, 4));
        communityCards.add(new Card(Suit.DIAMONDS, 6));
        communityCards.add(new Card(Suit.DIAMONDS, 7));
        communityCards.add(new Card(Suit.DIAMONDS, 8));

        ch = new CompareHands(communityCards);
        hand = new ArrayList<Card>();
        playerHand = new ArrayList<Card>();
    }



    @Test
    public void testGetBestFiveCardHand1()
    {
        ArrayList<Card> expected = new ArrayList<Card>();

        expected.add(new Card(Suit.DIAMONDS, 14));
        expected.add(new Card(Suit.DIAMONDS, 10));
        expected.add(new Card(Suit.DIAMONDS, 8));
        expected.add(new Card(Suit.DIAMONDS, 7));
        expected.add(new Card(Suit.DIAMONDS, 6));
        Collections.sort(expected, new CompareCardsByValue());

        playerHand.add(new Card(Suit.DIAMONDS, 14));
        playerHand.add(new Card(Suit.DIAMONDS, 10));

        ArrayList<Card> result = ch.getBestFiveCardHand(playerHand);
        Collections.sort(result, new CompareCardsByValue());

        for(int i = 0; i < expected.size(); i++)
        {
            assertEquals(expected.get(i).toString(), result.get(i).toString());
        }
    }


    @Test
    public void testGetBestFiveCardHand2()
    {
        ArrayList<Card> expected = new ArrayList<Card>();

        expected.add(new Card(Suit.CLUBS, 5));
        expected.add(new Card(Suit.DIAMONDS, 4));
        expected.add(new Card(Suit.DIAMONDS, 8));
        expected.add(new Card(Suit.DIAMONDS, 7));
        expected.add(new Card(Suit.DIAMONDS, 6));
        Collections.sort(expected, new CompareCardsByValue());

        playerHand.add(new Card(Suit.CLUBS, 5));
        playerHand.add(new Card(Suit.CLUBS, 10));

        ArrayList<Card> result = ch.getBestFiveCardHand(playerHand);
        Collections.sort(result, new CompareCardsByValue());

        for(int i = 0; i < expected.size(); i++)
        {
            assertEquals(expected.get(i).toString(), result.get(i).toString());
        }
    }



    @Test
    public void testGetBestFiveCardHand3()
    {
        ArrayList<Card> expected = new ArrayList<Card>();

        expected.add(new Card(Suit.HEARTS, 9));
        expected.add(new Card(Suit.CLUBS, 9));
        expected.add(new Card(Suit.DIAMONDS, 8));
        expected.add(new Card(Suit.DIAMONDS, 7));
        expected.add(new Card(Suit.DIAMONDS, 6));
        Collections.sort(expected, new CompareCardsByValue());

        playerHand.add(new Card(Suit.HEARTS, 9));
        playerHand.add(new Card(Suit.CLUBS, 9));

        ArrayList<Card> result = ch.getBestFiveCardHand(playerHand);
        Collections.sort(result, new CompareCardsByValue());

        for(int i = 0; i < expected.size(); i++)
        {
            assertEquals(expected.get(i).toString(), result.get(i).toString());
        }
    }



    @Test
    public void testGetBestFiveCardHand4()
    {
        ArrayList<Card> expected = new ArrayList<Card>();

        expected.add(new Card(Suit.CLUBS, 2));
        expected.add(new Card(Suit.CLUBS, 4));
        expected.add(new Card(Suit.DIAMONDS, 8));
        expected.add(new Card(Suit.DIAMONDS, 4));
        expected.add(new Card(Suit.HEARTS, 2));
        Collections.sort(expected, new CompareCardsByValue());

        playerHand.add(new Card(Suit.CLUBS, 2));
        playerHand.add(new Card(Suit.CLUBS, 4));

        ArrayList<Card> result = ch.getBestFiveCardHand(playerHand);
        Collections.sort(result, new CompareCardsByValue());

        for(int i = 0; i < expected.size(); i++)
        {
            assertEquals(expected.get(i).toString(), result.get(i).toString());
        }
    }





    @Test
    public void testGetBestFiveCardHand5()
    {
        ArrayList<Card> expected = new ArrayList<Card>();

        expected.add(new Card(Suit.CLUBS, 2));
        expected.add(new Card(Suit.HEARTS, 2));
        expected.add(new Card(Suit.DIAMONDS, 8));
        expected.add(new Card(Suit.DIAMONDS, 7));
        expected.add(new Card(Suit.CLUBS, 14));
        Collections.sort(expected, new CompareCardsByValue());

        playerHand.add(new Card(Suit.CLUBS, 2));
        playerHand.add(new Card(Suit.CLUBS, 14));

        ArrayList<Card> result = ch.getBestFiveCardHand(playerHand);
        Collections.sort(result, new CompareCardsByValue());

        for(int i = 0; i < expected.size(); i++)
        {
            assertEquals(expected.get(i).toString(), result.get(i).toString());
        }
    }




    @Test
    public void testGetHandName1()
    {
        hand.add(new Card(Suit.HEARTS, 6));
        hand.add(new Card(Suit.HEARTS, 7));
        hand.add(new Card(Suit.HEARTS, 8));
        hand.add(new Card(Suit.HEARTS, 9));
        hand.add(new Card(Suit.HEARTS, 10));

        String result = ch.getHandName(hand);
        assertEquals(result, "a Straight flush");
    }

    @Test
    public void testGetHandName2()
    {
        hand.add(new Card(Suit.HEARTS, 6));
        hand.add(new Card(Suit.DIAMONDS, 6));
        hand.add(new Card(Suit.SPADES, 6));
        hand.add(new Card(Suit.CLUBS, 6));
        hand.add(new Card(Suit.HEARTS, 10));

        String result = ch.getHandName(hand);
        assertEquals(result, "Four of a kind");
    }

    @Test
    public void testGetHandName3()
    {
        hand.add(new Card(Suit.HEARTS, 6));
        hand.add(new Card(Suit.DIAMONDS, 6));
        hand.add(new Card(Suit.CLUBS, 6));
        hand.add(new Card(Suit.HEARTS, 9));
        hand.add(new Card(Suit.DIAMONDS, 9));

        String result = ch.getHandName(hand);
        assertEquals(result, "a Full house");
    }

    @Test
    public void testGetHandName4()
    {
        hand.add(new Card(Suit.HEARTS, 2));
        hand.add(new Card(Suit.HEARTS, 7));
        hand.add(new Card(Suit.HEARTS, 8));
        hand.add(new Card(Suit.HEARTS, 9));
        hand.add(new Card(Suit.HEARTS, 10));

        String result = ch.getHandName(hand);
        assertEquals(result, "a Flush");
    }

    @Test
    public void testGetHandName5()
    {
        hand.add(new Card(Suit.HEARTS, 6));
        hand.add(new Card(Suit.HEARTS, 7));
        hand.add(new Card(Suit.HEARTS, 8));
        hand.add(new Card(Suit.HEARTS, 9));
        hand.add(new Card(Suit.DIAMONDS, 10));

        String result = ch.getHandName(hand);
        assertEquals(result, "a Straight");
    }

    @Test
    public void testGetHandName6()
    {
        hand.add(new Card(Suit.HEARTS, 6));
        hand.add(new Card(Suit.DIAMONDS, 6));
        hand.add(new Card(Suit.CLUBS, 6));
        hand.add(new Card(Suit.HEARTS, 9));
        hand.add(new Card(Suit.HEARTS, 10));

        String result = ch.getHandName(hand);
        assertEquals(result, "Trips");
    }

    @Test
    public void testGetHandName7()
    {
        hand.add(new Card(Suit.HEARTS, 6));
        hand.add(new Card(Suit.DIAMONDS, 6));
        hand.add(new Card(Suit.DIAMONDS, 9));
        hand.add(new Card(Suit.HEARTS, 9));
        hand.add(new Card(Suit.HEARTS, 10));

        String result = ch.getHandName(hand);
        assertEquals(result, "Two pair");
    }

    @Test
    public void testGetHandName8()
    {
        hand.add(new Card(Suit.DIAMONDS, 7));
        hand.add(new Card(Suit.HEARTS, 7));
        hand.add(new Card(Suit.HEARTS, 8));
        hand.add(new Card(Suit.HEARTS, 9));
        hand.add(new Card(Suit.HEARTS, 10));

        String result = ch.getHandName(hand);
        assertEquals(result, "One pair");
    }


    @Test
    public void testGetHandName9()
    {
        hand.add(new Card(Suit.DIAMONDS, 2));
        hand.add(new Card(Suit.HEARTS, 7));
        hand.add(new Card(Suit.HEARTS, 8));
        hand.add(new Card(Suit.HEARTS, 9));
        hand.add(new Card(Suit.HEARTS, 10));

        String result = ch.getHandName(hand);
        assertEquals(result, "High card");
    }
}