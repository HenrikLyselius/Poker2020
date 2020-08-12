package logic;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * An object of this class handles the betting rounds in a hand. A new object is supposed to be created for every new hand.
 *
 */




public class Betting {

    private ArrayList<Player> playersAtTable;
    private ArrayList<Player> playersInPot;
    private int pot = 0;
    private int actualBet = 0;
    private int bigBlind = 2;


    public Betting(ArrayList<Player> playersAtTable, ArrayList<Player> playersInPot)
    {
        this.playersAtTable = playersAtTable;
        this.playersInPot = playersInPot;

        for(Player player : playersAtTable)
        {
            player.setActualBet(0);
        }
    }


    /**
     * The central method for the class, that loops through the players actions until a betting round is completed.
     * @param firstPlayerToAct The first player to act in the betting round.
     */


    public void startBettingRound(int firstPlayerToAct)
    {
        // Clear bets except for when the blinds are posted.
        if(firstPlayerToAct == 0)
        {
            sendToAllPlayers("clearBets");
        }

        // Takes care of the case when there are only two players.
        firstPlayerToAct = firstPlayerToAct % playersInPot.size();


        int actionsMade = 0;
        int initialNumberOfPlayers = playersInPot.size();
        int i = firstPlayerToAct;

        while(true)
        {
            actionsMade++;

            Player player = playersInPot.get(i);

            if(!player.isAllIn())
            {
                // Update the GUI, so that the player to act is lit up.
                sendToAllPlayers("changeActivePlayer");
                sendToAllPlayers(player.getUsername());
                //sendToAllPlayers("setActiveBackground");

                choiceOfAction(player);

                // Update the GUI, so that the player is no longer lit up after the action.
                sendToAllPlayers("changeActivePlayer");
                sendToAllPlayers(player.getUsername());
                //sendToAllPlayers("setOffActiveBackground");
            }



            // Check if the player is still in the pot, and remove him if he folded.

            if(player.isStillInPot() == false)
            {
                playersInPot.remove(i);
                i--;
            }

            // Break loop if there is only one player left,

            if(playersInPot.size() == 1)
            {
                sendToAllPlayers("winnerMessage");
                sendToAllPlayers(playersInPot.get(0).getUsername() + " wins the pot of " + pot + ".");


                playersInPot.get(0).setCash(playersInPot.get(0).getCash() + pot);
                pot = 0;

                break;
            }

            // or if everyone has acted and all the players that are left have called (or if
            // it is checked through).

            if(actionsMade >= initialNumberOfPlayers && playersMatchActualBetOrIsAllIn())
            {
                break;
            }


            // Fixes continuous looping through the list.
            if(playersInPot.size() - 1 == i)
            {
                i = 0;
            }
            else
            {
                i++;
            }

        }

        // Resets "actualBet"-variables for the next betting round.

        actualBet = 0;
        for(Player player : playersInPot)
        {
            player.setActualBet(0);
        }


    }




    private void choiceOfAction(Player player)
    {

        if(actualBet == 0 || actualBet == player.getActualBet())
        {
            choiceOfActionIfNoprecedingBets(player);
        }

        else
        {
            choiceIfFacingBet(player);
        }
    }



    private void choiceOfActionIfNoprecedingBets(Player player)
    {
        player.getWebConnection().sendToPlayer("betLimits");
        player.getWebConnection().sendToPlayer("" + (actualBet + 2));
        player.getWebConnection().sendToPlayer("" + (player.getCash() + player.getActualBet()));

        player.getWebConnection().sendToPlayer("menu");
        player.getWebConnection().sendToPlayer("checkOrBet");

        Timer timer = new Timer();
        timer.schedule(new TimeToActBeforeDecision(player, "check"), 10 * 1000);

        String choice = player.getWebConnection().getFromClientLog();

        timer.cancel();

        switch (choice)
        {
            case "check":
                break;

            case "bet":
                bet(player);
                break;

            default:
                break;
        }

    }



    private void choiceIfFacingBet(Player player)
    {
        // If facing a bet that puts the player all in. Or if all other players are all in.

        if(actualBet >= player.getActualBet() + player.getCash() ||
                allOtherPlayersAreAllIn(player))
        {
            player.getWebConnection().sendToPlayer("menu");
            player.getWebConnection().sendToPlayer("foldOrCall");

            Timer timer = new Timer();
            timer.schedule(new TimeToActBeforeDecision(player, "fold"), 10 *1000);

            String choice = player.getWebConnection().getFromClientLog();
            timer.cancel();


            switch(choice)
            {
                case "fold":
                    fold(player);
                    break;

                case "call":
                    call(player);
                    break;

                default:
                    fold(player);
                    break;
            }
        }



        // If facing a smaller bet.

        else
        {
            player.getWebConnection().sendToPlayer("betLimits");
            player.getWebConnection().sendToPlayer("" + (actualBet + 2));
            player.getWebConnection().sendToPlayer("" + (player.getCash() + player.getActualBet()));

            player.getWebConnection().sendToPlayer("menu");
            player.getWebConnection().sendToPlayer("foldCallOrRaise");

            Timer timer = new Timer();
            timer.schedule(new TimeToActBeforeDecision(player, "fold"), 9750);

            String choice = player.getWebConnection().getFromClientLog();
            timer.cancel();

            switch(choice)
            {
                case "fold":
                    fold(player);
                    break;

                case "call":
                    call(player);
                    break;

                case "raise":
                    raise(player);
                    break;

                default:
                    fold(player);
                    break;
            }

        }
    }




    private void call(Player player)
    {

        // If the bet is bigger than the amount the player has left.

        if(actualBet - player.getActualBet() >= player.getCash())
        {
            pot = pot + player.getCash();
            player.increaseTotalAmountPutInPot(player.getCash());
            player.setActualBet(player.getActualBet() + player.getCash());
            player.setCash(0);
        }


        // Normal call.

        else
        {
            pot = pot + (actualBet - player.getActualBet());
            player.increaseTotalAmountPutInPot(actualBet - player.getActualBet());
            player.setCash(player.getCash() - (actualBet - player.getActualBet()));
            player.setActualBet(actualBet);
        }

        sendToAllPlayers("newBet");
        sendToAllPlayers(player.getUsername());
        sendToAllPlayers("" + player.getActualBet());

        sendToAllPlayers("pot");
        sendToAllPlayers("" + pot);

        sendToAllPlayers("updatePlayer");
        sendToAllPlayers(player.getUsername());
        sendToAllPlayers("cash");
        sendToAllPlayers("" + player.getCash());
    }




    private void fold(Player player)
    {
        player.setStillInPot(false);

        sendToAllPlayers("updatePlayer");
        sendToAllPlayers(player.getUsername());
        sendToAllPlayers("fold");
    }




    private void bet(Player player)
    {
        int bet = Integer.parseInt(player.getWebConnection().getFromClientLog());

        player.setActualBet(bet);
        player.setCash(player.getCash() - bet);
        player.increaseTotalAmountPutInPot(bet);
        actualBet = bet;
        pot = pot + bet;

        sendToAllPlayers("pot");
        sendToAllPlayers("" + pot);

        sendToAllPlayers("newBet");
        sendToAllPlayers(player.getUsername());
        sendToAllPlayers("" + bet);

        sendToAllPlayers("updatePlayer");
        sendToAllPlayers(player.getUsername());
        sendToAllPlayers("cash");
        sendToAllPlayers("" + player.getCash());
    }







    private void raise(Player player)
    {
        int raise = Integer.parseInt(player.getWebConnection().getFromClientLog());

        pot = pot + (raise - player.getActualBet());
        player.setCash(player.getCash() - (raise - player.getActualBet()));
        player.increaseTotalAmountPutInPot(raise);
        player.setActualBet(raise);
        actualBet = raise;

        sendToAllPlayers("pot");
        sendToAllPlayers("" + pot);

        sendToAllPlayers("newBet");
        sendToAllPlayers(player.getUsername());
        sendToAllPlayers("" + raise);

        sendToAllPlayers("updatePlayer");
        sendToAllPlayers(player.getUsername());
        sendToAllPlayers("cash");
        sendToAllPlayers("" + player.getCash());
    }



    /**
     * Makes normal "small blind/big blind"-bets for the first two players in the pot. It
     * is supposed to be called before the {@link startBettingRound startBettingRound method} in a preflop situation.
     */

    public void postBlinds()
    {
        // Post small blind

        Player player1 = playersInPot.get(0);
        Player player2 = playersInPot.get(1);

        player1.setActualBet(bigBlind / 2);
        player1.setCash(player1.getCash() - bigBlind / 2);
        player1.increaseTotalAmountPutInPot(bigBlind / 2);

        sendToAllPlayers("newBet");
        sendToAllPlayers(player1.getUsername());
        sendToAllPlayers("" + (bigBlind / 2));

        sendToAllPlayers("updatePlayer");
        sendToAllPlayers(player1.getUsername());
        sendToAllPlayers("cash");
        sendToAllPlayers("" + player1.getCash());


        // Post big blind

        player2.setActualBet(bigBlind);
        player2.setCash(player2.getCash() - bigBlind);
        player2.increaseTotalAmountPutInPot(bigBlind);

        sendToAllPlayers("newBet");
        sendToAllPlayers(player2.getUsername());
        sendToAllPlayers("" + bigBlind);

        sendToAllPlayers("updatePlayer");
        sendToAllPlayers(player2.getUsername());
        sendToAllPlayers("cash");
        sendToAllPlayers("" + player2.getCash());


        actualBet = bigBlind;
        pot = bigBlind + bigBlind / 2;

        sendToAllPlayers("pot");
        sendToAllPlayers("" + pot);
    }




    private boolean allOtherPlayersAreAllIn(Player player)
    {
        for (Player otherPlayer : playersInPot)
        {
            if(otherPlayer != player && !otherPlayer.isAllIn())
            {
                return false;
            }
        }

        return true;
    }




    private boolean playersMatchActualBetOrIsAllIn()
    {
        for(Player player : playersInPot)
        {
            if(actualBet != player.getActualBet() && player.isAllIn() != true)
            {
                return false;
            }
        }

        return true;
    }


    /**
     * Returns the actual value of the pot.
     * @return The actual value of the pot.
     */

    public int getPot()
    {
        return pot;
    }



    private void sendToAllPlayers(String string)
    {
        for(Player player : playersAtTable)
        {
            player.getWebConnection().sendToPlayer(string);
        }
    }



    public class TimeToActBeforeDecision extends TimerTask
    {
        private Player player;
        private String defaultDecision;





        public TimeToActBeforeDecision(Player player, String defaultDecision)
        {
            this.player = player;
            this.defaultDecision = defaultDecision;
        }


        /**
         * Makes the client automatically take the default decision.
         */

        public void run()
        {
            player.getWebConnection().sendToPlayer(defaultDecision);

        }
    };



}