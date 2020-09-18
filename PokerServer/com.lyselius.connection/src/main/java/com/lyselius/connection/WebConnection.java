package com.lyselius.connection;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Objects of this class are supposed to be given to playerobjects (initially to a handleLogin object), to simplify
 * the information exchange with the clients.
 */


public class WebConnection extends Thread{


    private DataInputStream  in = null;
    private DataOutputStream out = null;
    private Socket socket = null;
    private ArrayBlockingQueue<String> fromClient;
    private static final Logger logger = Logging.getLogger(WebConnection.class.getName());



    public WebConnection(Socket socket)
    {
        this.socket = socket;
        fromClient = new ArrayBlockingQueue<String>(60);

        try
        {
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        }
        catch(IOException i)
        {
            logger.log(Level.SEVERE, "In- and/or outputstream was not opened correctly", i);
        }
    }



    /**
     * Listens to the client, and puts incoming strings in the fromClient list.
     */

    public void run()
    {

        String string = "";

        // Continuously listen to the client.
        while (!socket.isClosed())
        {
            try
            {
                string = in.readUTF();
                fromClient.add(string);
            }
            catch(IOException i)
            {
                logger.log(Level.SEVERE, i.getMessage(), i);
                fromClient.add("connectionFail");
                closeConnection();
            }
        }
    }




    /**
     * Gets the next string that was sent from the server.
     */

    public String getFromClientLog() {

        String string = "";

        try { string = fromClient.take(); }
        catch (InterruptedException e)
        { logger.log(Level.SEVERE, e.getMessage(), e); }

        return string;
    }



    /**
     * Sends and flushes the incoming string to the client.
     * @param string A string that is sent to the client.
     */

    public void sendToPlayer(String string)
    {
        try
        {
            out.writeUTF(string);
            out.flush();
        }
        catch(IOException e)
        {
            logger.log(Level.SEVERE, e.getMessage(), e);
            fromClient.add("connectionFail");
        }
    }


    /**
     * Closes the socket and the streams.
     */

    public void closeConnection()
    {
        try { in.close(); }
        catch(IOException i)
        { logger.log(Level.WARNING, i.getMessage(), i); }

        try { out.close(); }
        catch(IOException e)
        { logger.log(Level.WARNING, e.getMessage(), e); }

        try{ socket.close(); }
        catch(IOException e)
        { logger.log(Level.WARNING, e.getMessage(), e); }
    }
}