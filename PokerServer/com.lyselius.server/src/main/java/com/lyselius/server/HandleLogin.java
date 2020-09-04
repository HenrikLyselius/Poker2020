package com.lyselius.server;

import com.lyselius.connection.WaitAndNotify;
import com.lyselius.connection.WebConnection;
import com.lyselius.database.Services;
import com.lyselius.logic.Player;

import java.net.Socket;
import java.util.Objects;

/**
 * This class is used to create objects that handle the login process of a client. If a client is successfully logged in,
 * it is sent to a {@link HandleThePlay HandleThePlay object}.
 *
 */

public class HandleLogin extends Thread{

    private HandleThePlay handleThePlay;
    private Socket socket;
    private WebConnection webConnection;
    private Services services;
    private boolean isRunning = true;


    public HandleLogin(HandleThePlay handleThePlay, Socket socket)
    {
        this.handleThePlay = handleThePlay;
        this.socket = socket;
        services = Services.getInstance();
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

        Player player = returnPlayerObjectIfUserExistsAndPasswordIsCorrect(username, password);

        if(Objects.nonNull(player))
        {
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
            if(services.dataBaseError())
            {
                webConnection.sendToPlayer("loginMessage");
                webConnection.sendToPlayer("databaseError");
            }
            else
            {
                webConnection.sendToPlayer("loginMessage");
                webConnection.sendToPlayer("incorrectLogin");
            }

        }

    }


    private Player returnPlayerObjectIfUserExistsAndPasswordIsCorrect(String username, String password)
    {
        Player player = services.getFromDatabase(username);

        if(player != null)
        {
            if(player.getPassword().equals(services.hashPassword(password, player.getSalt())))
            {
                return player;
            }
        }

        return null;
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
            String salt = services.getSalt();

            Player player = new Player(username, services.hashPassword(password, salt), salt);
            player.setCash(100);

            services.putInDatabase(player);

            webConnection.sendToPlayer("loginMessage");
            webConnection.sendToPlayer("newUserCreated");
        }
    }


    private boolean userAlreadyExists(String username)
    {

        if(services.getFromDatabase(username) != null)
        {
            return true;

        }

        return false;
    }
}
