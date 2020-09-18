package com.lyselius.server;

import com.lyselius.connection.Logging;
import com.lyselius.connection.WaitAndNotify;
import com.lyselius.database.Services;
import com.lyselius.logic.Gameplay;
import com.lyselius.logic.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * An object of this class is meant to handle the play on a table from a high level perspective. It monitors the players
 * that are seated at the table at a given moment, and checks if they have enough money to take part in the play.
 * The handling of the actual play is then delegated to a {@link com.lyselius.logic.Gameplay Gameplay object}.
 */

public class HandleThePlay extends Thread{

    private Gameplay gameplay = new Gameplay();
    private ArrayBlockingQueue<Player> newPlayers = new ArrayBlockingQueue<Player>(10);
    private ArrayList<Player> playersOnServer = new ArrayList<Player>();
    private int dealerNumber = 0;
    private WaitAndNotify waitAndNotify = new WaitAndNotify();
    private Services services = Services.getInstance();
    private static final Logger logger = Logging.getLogger(HandleThePlay.class.getName());


    public HandleThePlay()
    {

    }

    public void run()
    {
        while(true)
        {
            checkIfNewPlayersHaveJoined();
            checkIfPlayersHaveEnoughMoney();
            checkIfPlayersAreStillConnected();
            checkIfOnlyOnePlayerLeftAtTable();

            // If there are several players on the server, open a table and start a hand.
            if(playersOnServer.size() > 1)
            {
                dealerNumber++;

                sendToAllPlayers("sitDownAtTable");

                gameplay.setPlayersAtTable(playersOnServer);
                gameplay.playHand(dealerNumber % playersOnServer.size());
                updatePlayersInDatabase();
            }
            else
            {
                waitAndNotify.doWait();
            }
        }
    }



    private void checkIfNewPlayersHaveJoined()
    {
        while(newPlayers.size() > 0)
        {
            try {playersOnServer.add(newPlayers.take());}
            catch(InterruptedException e){}
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
                logger.log(Level.INFO, "Player " + player.getUsername() + " has logged out, and is removed from the table.");
                services.updateInDatabase(player, false);
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
                services.updateInDatabase(player, true);
                it.remove();
                logger.log(Level.INFO, "Player " + player.getUsername() + " has no money left, and is removed from the table.");

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



    private void updatePlayersInDatabase()
    {
        if(services.dataBaseError())
        {
            services.prepareBackUpDB();
        }

        playersOnServer.stream()
                .forEach(player -> services.updateInDatabase(player, true));
    }



    public void addNewPlayer(Player player)
    {
        newPlayers.add(player);
        logger.log(Level.INFO, "Player " + player.getUsername() + " has logged in, and will be added to a table.");
        doNotify();
    }

    /**
     * Returns a list with the players that are logged in on the server.
     * @return An ArrayList of Player objects.
     */
    public synchronized ArrayList<Player> getPlayersOnServer()
    {
        return playersOnServer;
    }

    public void doNotify()
    {
        waitAndNotify.doNotify();
    }
}