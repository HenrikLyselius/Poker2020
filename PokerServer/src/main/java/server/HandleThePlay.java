package server;

import java.util.ArrayList;
import java.util.Iterator;

import database.Services;
import logic.Gameplay;
import logic.Player;



/**
 * An object of this class is meant to handle the play on a table from a high level perspective. It monitors the players
 * that are seated at the table at a given moment, and checks if they have enough many to take part in the play.
 * The handling of the actual play is then delegated to a {@link logic.Gameplay Gameplay object}.
 */

public class HandleThePlay extends Thread{

    private Gameplay gameplay = new Gameplay();
    private ArrayList<Player> playersOnServer = new ArrayList<Player>();
    private int dealerNumber = 0;


    public HandleThePlay()
    {

    }

    public void run()
    {
        while(true)
        {

            // If there are several players on the server, open a table and start a hand.
            if(getPlayersOnServer().size() > 1)
            {
                checkIfPlayersAreStillConnected();
                checkIfPlayersHaveEnoughMoney();


                if(playersOnServer.size() > 1)
                {
                    dealerNumber++;

                    sendToAllPlayers("sitDownAtTable");

                    gameplay.setPlayersAtTable(playersOnServer);
                    gameplay.playHand(dealerNumber % playersOnServer.size());

                    checkIfPlayersHaveEnoughMoney();
                    checkIfPlayersAreStillConnected();
                    checkIfOnlyOnePlayerLeftAtTable();
                }
            }
        }
    }





    private void checkIfPlayersAreStillConnected()
    {
        Iterator<Player> it = playersOnServer.iterator();
        while(it.hasNext())
        {
            Player player = it.next();
            player.getWebConnection().sendToPlayer("stillWannaPlay");


            String string = player.getWebConnection().getFromClientLog();

            if(string.equals("closeConnection") || string.equals("connectionFail"))
            {
                player.getWebConnection().closeConnection();
                it.remove();
                Services.updateInDatabase(player);
            }
        }

    }



    private void checkIfPlayersHaveEnoughMoney()
    {
        Iterator<Player> it = playersOnServer.iterator();
        while(it.hasNext())
        {
            Player player = it.next();

            if(player.getCash() <= 1)
            {
                player.getWebConnection().sendToPlayer("noMoney");
                Services.updateInDatabase(player);
                it.remove();

            }
        }
    }




    private void checkIfOnlyOnePlayerLeftAtTable()
    {
        if(playersOnServer.size() == 1)
        {
            Player player = playersOnServer.get(0);

            player.getWebConnection().sendToPlayer("correctLogin");
            player.getWebConnection().sendToPlayer(player.getUsername());
        }
    }





    private void sendToAllPlayers(String string)
    {
        for(Player player : playersOnServer)
        {
            player.getWebConnection().sendToPlayer(string);
        }
    }



    /**
     * Returns a list with the players that are logged in on the server.
     * @return An ArrayList of Player objects.
     */

    public synchronized ArrayList<Player> getPlayersOnServer()
    {
        return playersOnServer;
    }
}