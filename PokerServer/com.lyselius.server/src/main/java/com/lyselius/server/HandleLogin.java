package com.lyselius.server;

import com.lyselius.connection.WaitAndNotify;
import com.lyselius.connection.WebConnection;
import com.lyselius.database.Services;
import com.lyselius.logic.Player;

import java.net.Socket;

/**
 * This class is used to create objects that handle the login process of a client. If a client is successfully logged in,
 * it is sent to a {@link HandleThePlay HandleThePlay object}.
 *
 */

public class HandleLogin extends Thread{

    private HandleThePlay handleThePlay;
    private Socket socket;
    private WebConnection webConnection;
    private boolean isRunning = true;
    private WaitAndNotify waitAndNotify = new WaitAndNotify();


    public HandleLogin(HandleThePlay handleThePlay, Socket socket)
    {
        this.handleThePlay = handleThePlay;
        this.socket = socket;
    }

    public void run()
    {
        webConnection = new WebConnection(socket);
        webConnection.start();

        while(isRunning)
        {
            checkPlayerChoice();
        }
    }



    private void checkPlayerChoice()
    {
        String string = webConnection.getFromClientLog();

        if(string.equals("login"))
        {
            checkLogin();
        }
        if(string.equals("createUser"))
        {
            tryToCreateUser();
        }
    }



    private void checkLogin()
    {
        String username = webConnection.getFromClientLog();
        String password = webConnection.getFromClientLog();

        System.out.println(username + " " + password);


        if(userExistsAndPasswordIsCorrect(username, password))
        {
            Player player = Services.getFromDatabase(username);

            if(!handleThePlay.getPlayersOnServer().contains(player))
            {
                player.setWebConnection(webConnection);

                webConnection.sendToPlayer("correctLogin");
                webConnection.sendToPlayer(player.getUsername());

                handleThePlay.addNewPlayer(player);

                isRunning = false;
            }
            else
            {
                handleThePlay.doNotify();
            }
        }
        else
        {
            webConnection.sendToPlayer("loginMessage");
            webConnection.sendToPlayer("incorrectLogin");
        }

    }




    private boolean userExistsAndPasswordIsCorrect(String username, String password)
    {
        if(Services.getFromDatabase(username) != null)
        {
            Player player = Services.getFromDatabase(username);

            if(player.getPassword().equals(password))
            {
                return true;
            }
        }

        return false;
    }



    private void tryToCreateUser()
    {
        String username = webConnection.getFromClientLog();
        String password = webConnection.getFromClientLog();

        if(userAlreadyExists(username))
        {
            webConnection.sendToPlayer("loginMessage");
            webConnection.sendToPlayer("userAlreadyExists");
        }
        else
        {
            Player player = new Player(username, password);
            player.setCash(100);

            Services.putInDatabase(player);

            webConnection.sendToPlayer("loginMessage");
            webConnection.sendToPlayer("newUserCreated");
        }
    }


    private boolean userAlreadyExists(String username)
    {

        if(Services.getFromDatabase(username) != null)
        {
            return true;

        }

        return false;
    }
}
