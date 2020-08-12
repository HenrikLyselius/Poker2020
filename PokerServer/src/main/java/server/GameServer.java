package server;



import java.net.*;
import java.util.ArrayList;

import logic.Gameplay;
import logic.Player;

import java.io.*;


/**
 * An object of this class acts as a server that just listens for new clients. It sends new clients on to
 * a {@link HandleLogin HandleLogin object}.
 */


public class GameServer {

    private int 			number	 = 1;
    private Socket          socket   = null;
    private ServerSocket    server   = null;
    private HandleThePlay handleThePlay = new HandleThePlay();



    public GameServer(int port)
    {
        // Starts server and waits for a connection
        try
        {
            server = new ServerSocket(port);
            System.out.println("Server started");

            System.out.println("Waiting for a client ...");
        }
        catch(IOException i)
        {
            System.out.println(i);
        }

        handleThePlay.start();



        // Listens for clients,
        while(true)
        {
            try
            {
                socket = server.accept();
                System.out.println("Client accepted");

                HandleLogin handleLogin = new HandleLogin(handleThePlay, socket, number);
                handleLogin.start();
                number++;
            }
            catch(Exception e)
            {
                e.printStackTrace();
                System.out.println("Connection Error");
            }
        }
    }
}