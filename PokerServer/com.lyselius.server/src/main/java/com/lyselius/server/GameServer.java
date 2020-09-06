package com.lyselius.server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.*;


/**
 * An object of this class acts as a server that just listens for new clients. It sends new clients on to
 * a {@link HandleLogin HandleLogin object}.
 */


public class GameServer {

    private Socket          socket   = null;
    private ServerSocket    server   = null;
    private HandleThePlay handleThePlay = new HandleThePlay();
    private static final Logger logger = Logger.getLogger(GameServer.class.getName());



    public GameServer(int port)
    {
        Handler fileHandler = null;
        try { fileHandler = new FileHandler("./logs/" + logger.getName() + ".log"); }
        catch (IOException e) { e.printStackTrace(); }
        logger.addHandler(fileHandler);


        // Starts server and waits for a connection
        try
        {
            server = new ServerSocket(port);
            logger.log(Level.INFO, "Server started");
            logger.log(Level.INFO, "Waiting for a client ...");


            /*System.out.println("Server started");

            System.out.println("Waiting for a client ...");*/
        }
        catch(IOException i)
        {
            logger.log(Level.SEVERE, "IOException", i);
        }

        handleThePlay.start();



        // Listens for clients,
        while(true)
        {
            try
            {
                socket = server.accept();
                logger.log(Level.INFO, "Client accepted");

                HandleLogin handleLogin = new HandleLogin(handleThePlay, socket);
                handleLogin.start();
            }
            catch(Exception e)
            {
                logger.log(Level.SEVERE, "Connection Error", e);
            }
        }
    }
}