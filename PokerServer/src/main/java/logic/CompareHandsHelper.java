package logic;

import java.util.ArrayList;
import java.util.Collections;


/**
 * This class provides static methods that is used by {@link CompareHands CompareHands}.
 */

public class CompareHandsHelper {




    public static boolean isFourOfAKind(ArrayList<Card> hand)
    {
        Collections.sort(hand, new CompareCardsByValue());

        if(hand.get(0).getValue() == hand.get(1).getValue() &&
                hand.get(1).getValue() == hand.get(2).getValue() &&
                hand.get(2).getValue() == hand.get(3).getValue()  )
        {
            return true;
        }

        if(hand.get(1).getValue() == hand.get(2).getValue() &&
                hand.get(2).getValue() == hand.get(3).getValue() &&
                hand.get(3).getValue() == hand.get(4).getValue()  )
        {
            return true;
        }

        return false;

    }



    public static boolean isAFullHouse(ArrayList<Card> hand)
    {
        Collections.sort(hand, new CompareCardsByValue());

        if(hand.get(0).getValue() == hand.get(1).getValue() &&
                hand.get(1).getValue() == hand.get(2).getValue() &&
                hand.get(3).getValue() == hand.get(4).getValue()   )
        {
            return true;
        }

        if(hand.get(0).getValue() == hand.get(1).getValue() &&
                hand.get(2).getValue() == hand.get(3).getValue() &&
                hand.get(3).getValue() == hand.get(4).getValue()   )
        {
            return true;
        }

        return false;
    }


    public static boolean isAFlush(ArrayList<Card> hand)
    {
        if(hand.get(0).getSuit() == hand.get(1).getSuit() &&
                hand.get(0).getSuit() == hand.get(2).getSuit() &&
                hand.get(0).getSuit() == hand.get(3).getSuit() &&
                hand.get(0).getSuit() == hand.get(4).getSuit())
        {
            return true;
        }

        return false;
    }


    public static boolean isAStraight(ArrayList<Card> hand)
    {
        Collections.sort(hand, new CompareCardsByValue());


        for(int i = 1; i < 5; i++)
        {
            if(hand.get(i).getValue() - hand.get(i - 1).getValue() != 1 &&
                    !(hand.get(i).getValue() == 14 && hand.get(i - 1).getValue() == 5)) // 1-5 straight
            {
                return false;
            }

        }

        return true;
    }


    public static boolean isTrips(ArrayList<Card> hand)
    {
        Collections.sort(hand, new CompareCardsByValue());

        if(hand.get(0).getValue() == hand.get(1).getValue() &&
                hand.get(1).getValue() == hand.get(2).getValue() &&
                hand.get(2).getValue() != hand.get(3).getValue() &&
                hand.get(3).getValue() != hand.get(4).getValue()  )
        {
            return true;
        }

        if(hand.get(0).getValue() != hand.get(1).getValue() &&
                hand.get(1).getValue() == hand.get(2).getValue() &&
                hand.get(2).getValue() == hand.get(3).getValue() &&
                hand.get(3).getValue() != hand.get(4).getValue()  )
        {
            return true;
        }

        if(hand.get(0).getValue() != hand.get(1).getValue() &&
                hand.get(1).getValue() != hand.get(2).getValue() &&
                hand.get(2).getValue() == hand.get(3).getValue() &&
                hand.get(3).getValue() == hand.get(4).getValue()  )
        {
            return true;
        }

        return false;
    }


    public static boolean isTwoPair(ArrayList<Card> hand)
    {
        Collections.sort(hand, new CompareCardsByValue());

        if(hand.get(0).getValue() == hand.get(1).getValue() &&
                hand.get(1).getValue() != hand.get(2).getValue()	&&
                hand.get(2).getValue() == hand.get(3).getValue() &&
                hand.get(3).getValue() != hand.get(4).getValue()  )
        {
            return true;
        }

        if(hand.get(0).getValue() == hand.get(1).getValue() &&
                hand.get(1).getValue() != hand.get(2).getValue() &&
                hand.get(2).getValue() != hand.get(3).getValue() &&
                hand.get(3).getValue() == hand.get(4).getValue()  )
        {
            return true;
        }

        if(hand.get(0).getValue() != hand.get(1).getValue() &&
                hand.get(1).getValue() == hand.get(2).getValue() &&
                hand.get(2).getValue() != hand.get(3).getValue() &&
                hand.get(3).getValue() == hand.get(4).getValue()  )
        {
            return true;
        }

        return false;
    }


    public static boolean isAPair(ArrayList<Card> hand)
    {
        Collections.sort(hand, new CompareCardsByValue());

        if(hand.get(0).getValue() == hand.get(1).getValue() &&
                hand.get(1).getValue() != hand.get(2).getValue() &&
                hand.get(2).getValue() != hand.get(3).getValue() &&
                hand.get(3).getValue() != hand.get(4).getValue()  )
        {
            return true;
        }

        if(hand.get(0).getValue() != hand.get(1).getValue() &&
                hand.get(1).getValue() == hand.get(2).getValue() &&
                hand.get(2).getValue() != hand.get(3).getValue() &&
                hand.get(3).getValue() != hand.get(4).getValue()  )
        {
            return true;
        }

        if(hand.get(0).getValue() != hand.get(1).getValue() &&
                hand.get(1).getValue() != hand.get(2).getValue() &&
                hand.get(2).getValue() == hand.get(3).getValue() &&
                hand.get(3).getValue() != hand.get(4).getValue()  )
        {
            return true;
        }

        if(hand.get(0).getValue() != hand.get(1).getValue() &&
                hand.get(1).getValue() != hand.get(2).getValue() &&
                hand.get(2).getValue() != hand.get(3).getValue() &&
                hand.get(3).getValue() == hand.get(4).getValue()  )
        {
            return true;
        }

        return false;
    }



}
