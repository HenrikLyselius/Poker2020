package com.lyselius.logic;

import java.util.ArrayList;


/**
 * This class is used to compare the hand strengths of different hands in Texas Hold'Em.
 *
 */


public class CompareHands {

    private ArrayList<Card> communityCards;



    /**
     * @param cards An ArrayList of cards, representing the community cards on the table.
     */
    public CompareHands(ArrayList<Card> cards)
    {
        communityCards = cards;
    }






    /** The method takes all 21 (7 over 5) combinations of five card hands that you can create
     *  from the seven total cards (five community cards, and two from the players hand), and
     *  returns them in a list.
     *  @param playerHand A two card player hand.
     *  @return A list of all possible five card combinations.
     */

    private ArrayList<ArrayList<Card>> getListOfPossibleFiveCardHands(ArrayList<Card> playerHand)
    {
        ArrayList<ArrayList<Card>> listOfPossibleFiveCardHands = new ArrayList<ArrayList<Card>>();
        ArrayList<Card> help = new ArrayList<Card>();

        for(Card card : communityCards)
        {
            help.add(card);
        }

        for(Card card : playerHand)
        {
            help.add(card);
        }

        for(int i = 0; i < help.size() - 1; i++)
        {
            for(int j = i + 1; j < help.size(); j++)
            {
                ArrayList<Card> help2 = new ArrayList<Card>(help);
                help2.remove(j);
                help2.remove(i);
                listOfPossibleFiveCardHands.add((help2));
            }
        }

        return listOfPossibleFiveCardHands;
    }



    /**
     * Takes a player hand as input and returns the best possible five card hand.
     * @param playerHand A two card player hand.
     * @return The best five card hand that can be formed by combining the player's cards and the community cards.
     */

    public ArrayList<Card> getBestFiveCardHand(ArrayList<Card> playerHand)
    {
        ArrayList<ArrayList<Card>> listOfPossibleFiveCardHands = getListOfPossibleFiveCardHands(playerHand);

        ArrayList<Card> bestHand = listOfPossibleFiveCardHands.get(0);

        for(ArrayList<Card> hand : listOfPossibleFiveCardHands)
        {
            if(whichHandIsStrongest(bestHand, hand) == 2)
            {
                bestHand = hand;
            }
        }

        return bestHand;
    }


    /**
     * Compares the strength between two five card hands.
     * @param hand1 The first five card hand.
     * @param hand2 The second five card hand.
     * @return 1 if the first hand is strongest, 2 if the second hand is strongest, and 3 if the hands are
     * of equal strength.
     */

    public int whichHandIsStrongest(ArrayList<Card> hand1, ArrayList<Card> hand2)
    {
        if(getHandStrength(hand1) > getHandStrength(hand2))
        {
            return 1;
        }

        if(getHandStrength(hand1) < getHandStrength(hand2))
        {
            return 2;
        }

        return tieBreaker(getHandStrength(hand1), hand1, hand2);
    }



    /**
     * Compares two hands that has the same basic hand strength, and looks at which of them is ultimately stronger.
     * @param handStrength The basic hand strength of the hands (high card, one pair, two pair etc.), represented by
     * ints.
     * @param hand1 The first five card hand.
     * @param hand2 The second five card hand.
     * @return 1 if the first hand is strongest, 2 if the second hand is strongest, and 3 if the hands are
     * of equal strength.
     */

    private int tieBreaker(int handStrength, ArrayList<Card> hand1, ArrayList<Card> hand2)
    {

        switch(handStrength)
        {

            case 8:
                return tiebreakerStraightFlush(hand1, hand2);

            case 7:
                return tiebreakerFourOfAKind(hand1, hand2);

            case 6:
                // A full house has the same tiebreaker as four of a kind.
                return tiebreakerFourOfAKind(hand1, hand2);

            case 5:
                return tiebreakerFlush(hand1, hand2);

            case 4:
                // A straight has the same tiebreaker as a straight flush.
                return tiebreakerStraightFlush(hand1, hand2);

            case 3:
                return tiebreakerTrips(hand1, hand2);

            case 2:
                return tiebreakerTwoPair(hand1, hand2);

            case 1:
                return tiebreakerOnePair(hand1, hand2);

            case 0:
                // A high card hand has the same tiebreaker as a flush.
                return tiebreakerFlush(hand1, hand2);

            default:
                return 0; // This code is unreachable.

        }


    }




    /** Computes the basic hand strength of a five card hand.
     * @param hand A five card hand.
     * @return The basic hand strength of the hand (high card, one pair, two pair etc.) in the form of an int.
     */

    private int getHandStrength(ArrayList<Card> hand)
    {

        if(CompareHandsHelper.isAFlush(hand) && CompareHandsHelper.isAStraight(hand))
        {
            return 8;
        }

        if(CompareHandsHelper.isFourOfAKind(hand))
        {
            return 7;
        }

        if(CompareHandsHelper.isAFullHouse(hand))
        {
            return 6;
        }

        if(CompareHandsHelper.isAFlush(hand))
        {
            return 5;
        }

        if(CompareHandsHelper.isAStraight(hand))
        {
            return 4;
        }

        if(CompareHandsHelper.isTrips(hand))
        {
            return 3;
        }

        if(CompareHandsHelper.isTwoPair(hand))
        {
            return 2;
        }

        if(CompareHandsHelper.isAPair(hand))
        {
            return 1;
        }

        return 0;
    }






    private int tiebreakerStraightFlush(ArrayList<Card> hand1, ArrayList<Card> hand2)
    {
        // Check if it is a 6-5-4-3-2 vs 5-4-3-2-A situation.

        if(hand1.get(4).getValue() == 6 && hand2.get(4).getValue() == 14)
        {
            return 1;
        }

        if(hand1.get(4).getValue() == 14 && hand2.get(4).getValue() == 6)
        {
            return 2;
        }


        // If not, check if one straight is higher than the other.

        if(hand1.get(4).getValue() > hand2.get(4).getValue())
        {
            return 1;
        }

        if(hand1.get(4).getValue() < hand2.get(4).getValue())
        {
            return 2;
        }

        return 3;
    }



    private int tiebreakerFlush(ArrayList<Card> hand1, ArrayList<Card> hand2)
    {

        for(int i = 4; i >= 0; i--)
        {
            if(hand1.get(i).getValue() > hand2.get(i).getValue())
            {
                return 1;
            }

            if(hand1.get(i).getValue() < hand2.get(i).getValue())
            {
                return 2;
            }
        }

        return 3;
    }


    private int tiebreakerFourOfAKind(ArrayList<Card> hand1, ArrayList<Card> hand2)
    {
		/* Do the hands have different four of a kind or different trips in a full house?
		 Note that the middle card must be part of the four of a kind or of the trips, since
		 the hands are sorted by value. */

        if(hand1.get(2).getValue() > hand2.get(2).getValue())
        {
            return 1;
        }

        if(hand1.get(2).getValue() < hand2.get(2).getValue())
        {
            return 2;
        }


        // If not, check eventual difference of last card/last cards.

        int sum1 = 0;
        int sum2 = 0;

        for(Card card : hand1)
        {
            sum1 = sum1 + card.getValue();
        }

        for(Card card : hand2)
        {
            sum2 = sum2 + card.getValue();
        }

        if(sum1 > sum2)
        {
            return 1;
        }

        if(sum1 < sum2)
        {
            return 2;
        }

        return 3;
    }


    private int tiebreakerTrips(ArrayList<Card> hand1, ArrayList<Card> hand2)
    {
        // Do the hands have different trips?
        // Note that the middle card must be part of the trips, since the hands are sorted by value.

        if(hand1.get(2).getValue() > hand2.get(2).getValue())
        {
            return 1;
        }

        if(hand1.get(2).getValue() < hand2.get(2).getValue())
        {
            return 2;
        }


        // If not, check the highest card besides the trips, which is equivalent to
        // calling tiebreakerFlush.

        return tiebreakerFlush(hand1, hand2);
    }



    private int tiebreakerTwoPair(ArrayList<Card> hand1, ArrayList<Card> hand2)
    {
        // Compare the highest pair value.

        if(findTheHighestPair(hand1) > findTheHighestPair(hand2))
        {
            return 1;
        }

        if(findTheHighestPair(hand1) < findTheHighestPair(hand2))
        {
            return 2;
        }

        // If the highest pair is the same, compare the lowest pair value.

        if(findTheLowestPair(hand1) > findTheLowestPair(hand2))
        {
            return 1;
        }

        if(findTheLowestPair(hand1) < findTheLowestPair(hand2))
        {
            return 2;
        }

        // If both pairs are the same, find the hand with the highest fifth card, which is
        // equivalent to calling tiebreakerFlush.

        return tiebreakerFlush(hand1, hand2);
    }



    private int findTheHighestPair(ArrayList<Card> hand)
    {
        for(int i = 4; i > 0; i--)
        {
            if(hand.get(i).getValue() == hand.get(i - 1).getValue())
            {
                return hand.get(i).getValue();
            }
        }

        return 0; // This is unreachable if the hand actually contains a pair.
    }



    private int findTheLowestPair(ArrayList<Card> hand)
    {
        for(int i = 0; i < 4; i++)
        {
            if(hand.get(i).getValue() == hand.get(i + 1).getValue())
            {
                return hand.get(i).getValue();
            }
        }

        return 0; // This is unreachable if the hand actually contains a pair.
    }




    private int tiebreakerOnePair(ArrayList<Card> hand1, ArrayList<Card> hand2)
    {
        // Find the pair value.

        if(findTheHighestPair(hand1) > findTheHighestPair(hand2))
        {
            return 1;
        }

        if(findTheHighestPair(hand1) < findTheHighestPair(hand2))
        {
            return 2;
        }

        // If the pair is the same, find the highest of the remaining cards, which is
        // equivalent to calling tiebreakerFlush.

        return tiebreakerFlush(hand1, hand2);
    }


    /**
     * Takes a five card hand as input and returns the basic strength of the hand as a string.
     * @param hand A five card hand.
     * @return A string that describes the hand's basic strength.
     */

    public String getHandName(ArrayList<Card> hand)
    {

        switch(getHandStrength(hand))
        {
            case 0:
                return "High card";

            case 1:
                return "One pair";

            case 2:
                return "Two pair";

            case 3:
                return "Trips";

            case 4:
                return "a Straight";

            case 5:
                return "a Flush";

            case 6:
                return "a Full house";

            case 7:
                return "Four of a kind";

            case 8:
                return "a Straight flush";

            default:
                return null;
        }
    }
}