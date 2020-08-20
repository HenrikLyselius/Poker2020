package com.lyselius.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;



/**
 * An object of this class handles the actual play at a poker table for Texas Hold'Em.
 *
 */

public class Gameplay {

    private Deck deck;
    private int fullPot;
    private ArrayList<Player> playersAtTable;
    private ArrayList<Player> playersInPot;
    private ArrayList<Card> communityCards;



    public Gameplay()
    {
        communityCards = new ArrayList<Card>();
    }


    /**
     * The central method of the class, which is called when a new hand should be played.
     * @param dealer The position of the player that is the dealer in the hand.
     */

    public void playHand(int dealer)
    {

        // A pause between hands, so everyone can take in the result of the previous hand and prepare for the next.
        sleep(6000);

        setDealer(dealer);

        cleanUpForNewHand();

        sendToAllPlayers("newHand");

        // Lets all clients catch up and be ready for a new hand.
        sleep(1000);

        deck = new Deck();
        deck.shuffle();

        Betting betting = new Betting(playersAtTable, playersInPot);

        deal();
        sendInfoToClients();

        betting.postBlinds();

        // Start preflop betting.
        betting.startBettingRound(2);


        // Flop
        if(playersInPot.size() > 1)
        {
            dealCommunityCards(3);
            sendCommunityCardsToPlayers("flop", 0, 2);
        }

        if(thereShouldBeMoreBetting())
        {
            betting.startBettingRound(0);
        }


        // Turn
        if(playersInPot.size() > 1)
        {
            dealCommunityCards(1);
            sendCommunityCardsToPlayers("turn", 3, 3);
        }

        if(thereShouldBeMoreBetting())
        {
            betting.startBettingRound(0);
        }


        // River
        if(playersInPot.size() > 1)
        {
            dealCommunityCards(1);
            sendCommunityCardsToPlayers("river", 4, 4);
        }

        if(thereShouldBeMoreBetting())
        {
            betting.startBettingRound(0);
        }


        // Results
        if(playersInPot.size() > 1)
        {
            //sendFinalResultToPlayers(betting.getPot());

            showCardsOfRemainingPlayers();
            calculateResults(betting.getPot());
        }

    }




    private void setDealer(int dealer)
    {
        Collections.rotate(playersAtTable, -dealer);
    }



    // Clearing of old variables and general preparation.
    private void cleanUpForNewHand()
    {
        playersInPot = new ArrayList<Player>(playersAtTable);
        resetPlayerHands(playersAtTable);
        communityCards.clear();
    }




    private void showCardsOfRemainingPlayers()
    {
        for(Player player : playersInPot)
        {
            sendToAllPlayers("updatePlayer");
            sendToAllPlayers(player.getUsername());
            sendToAllPlayers("showCards");
        }
    }



    private void calculateResults(int pot)
    {
        fullPot = pot;

        sortPlayersByAmountTheyHavePutInThePot();

        // Check if someone has put in a bet that puts all other players all in. And if that is the case, remove the
        // overshooting part of the bet from the pot, and give it back to the player.

        int difference = playersInPot.get(playersInPot.size() - 1).getTotalAmountPutInPot() -
                playersInPot.get(playersInPot.size() - 2).getTotalAmountPutInPot();

        if(difference > 0)
        {
            playersInPot.get(playersInPot.size() - 1).setCash(playersInPot.get(playersInPot.size() - 1).getCash() + difference);
            playersInPot.get(playersInPot.size() - 1).increaseTotalAmountPutInPot(-difference);
            fullPot = fullPot - difference;

        }

        // Check if there are side pots.

        while(playersInPot.size() > 2 &&
                playersInPot.get(playersInPot.size() - 2).getTotalAmountPutInPot() > playersInPot.get(0).getTotalAmountPutInPot())
        {
            calculateSidePot();
        }

        sendResultToPlayers(fullPot, "pot of ");
    }




    private void calculateSidePot()
    {
        int sidePot = playersInPot.get(0).getTotalAmountPutInPot() * playersInPot.size();
        sendResultToPlayers(sidePot, "side pot of ");
        fullPot = fullPot - sidePot;
        reduceTotalAmountInPotVariables(playersInPot.get(0).getTotalAmountPutInPot());
        removePlayersNotInTheRestOfThePot();
        sleep(2000);
    }


    private void reduceTotalAmountInPotVariables(int reduction)
    {
        for(Player player : playersInPot)
        {
            player.increaseTotalAmountPutInPot(-reduction);
        }
    }

    private void removePlayersNotInTheRestOfThePot()
    {
        Iterator<Player> it = playersInPot.iterator();
        while(it.hasNext())
        {
            Player player = it.next();
            if(player.getTotalAmountPutInPot() == 0)
            {
                it.remove();
            }
        }
    }



    private void sortPlayersByAmountTheyHavePutInThePot()
    {
        Collections.sort(playersInPot, new ComparePlayersByAmountPutInPot());
    }




    private void sendResultToPlayers(int pot, String string)
    {
        ArrayList<Player> winners = findWinners();

        CompareHands ch = new CompareHands(communityCards);


        // Only one winner, normal case.

        if(winners.size() == 1)
        {
            // Send information about the winner.
            sendToAllPlayers("winnerMessage");
            sendToAllPlayers(winners.get(0).getUsername() + " wins the " + string + pot
                    + " with " + ch.getHandName(ch.getBestFiveCardHand(winners.get(0).getCards())) + ".");

            // Award the pot to the winnning player.
            winners.get(0).setCash(winners.get(0).getCash() + pot);
        }



        // More than one winner.

        else
        {
            // Split the pot between the winning players.
            for(int i = 0; i < winners.size(); i++)
            {
                winners.get(i).setCash(winners.get(i).getCash() + (pot / winners.size()));
            }



            // Compose the winning message.
            String theWinners = "";

            for(int i = 0; i < winners.size(); i++)
            {
                theWinners = theWinners + (winners.get(i).getUsername());

                if(winners.size() - 1 > i)
                {
                    theWinners = theWinners + " and ";
                }
            }

            theWinners = theWinners + " wins the pot of " + pot + " with:" +
                    ch.getHandName(ch.getBestFiveCardHand(winners.get(0).getCards())) + ".";


            // Send information about the winners.
            sendToAllPlayers("winnerMessage");
            sendToAllPlayers(theWinners);
        }

    }


    private ArrayList<Player> findWinners()
    {
        ArrayList<Player> winners = new ArrayList<Player>();

        CompareHands ch = new CompareHands(communityCards);

        winners.add(playersInPot.get(0));

        for(int i = 1; i < playersInPot.size(); i++)
        {
            if(ch.whichHandIsStrongest(ch.getBestFiveCardHand(winners.get(0).getCards()),
                    ch.getBestFiveCardHand(playersInPot.get(i).getCards())) == 2)
            {
                winners.clear();
                winners.add(playersInPot.get(i));
            }

            else if(ch.whichHandIsStrongest(ch.getBestFiveCardHand(winners.get(0).getCards()),
                    ch.getBestFiveCardHand(playersInPot.get(i).getCards())) == 3)
            {
                winners.add(playersInPot.get(i));
            }
        }

        return winners;
    }




    private void deal()
    {
        for(Player player : playersInPot)
        {
            player.setCard1(deck.deal());
            player.setCard2(deck.deal());
        }
    }


    private void dealCommunityCards(int times)
    {
        for(int i = 0; i < times; i++)
        {
            communityCards.add(deck.deal());
        }
    }



    /**
     * Updates the playersAtTable variable, and is supposed to be called before each call to
     * {@link #playHand(int) playHand}.
     * @param list An ArrayList containing the players seated at the table.
     */
    public void setPlayersAtTable(ArrayList<Player> list)
    {
        playersAtTable = new ArrayList<Player>(list);
    }






    private boolean thereShouldBeMoreBetting()
    {
        int numberOfPlayersNotAllIn = 0;

        for(Player player : playersInPot)
        {
            if(player.isAllIn() != true)
            {
                numberOfPlayersNotAllIn = numberOfPlayersNotAllIn + 1;
            }
        }

        if(numberOfPlayersNotAllIn > 1)
        {
            return true;
        }

        return false;
    }




    private void sendInfoToClients()
    {
        for(Player player : playersAtTable)
        {
            player.getWebConnection().sendToPlayer("playerInfo");
            player.getWebConnection().sendToPlayer("" + playersAtTable.size());

            for(Player player2 : playersAtTable)
            {
                player.getWebConnection().sendToPlayer(player2.getUsername());
                player.getWebConnection().sendToPlayer("" + player2.getCash());
                player.getWebConnection().sendToPlayer(player2.getCard1().toString());
                player.getWebConnection().sendToPlayer(player2.getCard2().toString());
            }
        }
    }




    private void sendCommunityCardsToPlayers(String string, int from, int to)
    {
        for(Player player : playersAtTable)
        {
            player.getWebConnection().sendToPlayer(string);

            for(int j = from; j < to + 1; j++)
            {
                player.getWebConnection().sendToPlayer(communityCards.get(j).toString());
            }
        }
    }


    private void resetPlayerHands(ArrayList<Player> playersInPot)
    {
        for(Player player : playersInPot)
        {
            player.resetPlayVariablesForNewHand();
        }
    }




    private void sleep(long milliseconds)
    {
        try
        {
            Thread.sleep(milliseconds);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }




    private void sendToAllPlayers(String string)
    {
        for(Player player : playersAtTable)
        {
            player.getWebConnection().sendToPlayer(string);
        }
    }
}
