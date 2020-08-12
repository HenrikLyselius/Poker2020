package logic;


import java.util.Comparator;


/**
 *
 * Objects of this class are used to sort players by the total amount they have put in the pot.
 *
 */


public class ComparePlayersByAmountPutInPot implements Comparator<Player>{


    public int compare(Player player1, Player player2) {

        if(player1.getTotalAmountPutInPot() == player2.getTotalAmountPutInPot())
        {
            return 0;
        }
        else
        {
            return (player1.getTotalAmountPutInPot() - player2.getTotalAmountPutInPot());
        }
    }
}
