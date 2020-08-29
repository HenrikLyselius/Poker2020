package com.lyselius.connection;


import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Objects of this class are used to simplify the exchange of information between the client
 * and the server.
 *
 */

public class WebConnection extends Thread{

    private ArrayBlockingQueue<String> fromServer = new ArrayBlockingQueue<String>(60);
    // private ArrayList<String> fromServer;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;



    public WebConnection(String address, int port)
    {
        try
        {
            socket = new Socket(address, port);
            System.out.println("Connected");

            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            //fromServer = new ArrayList<String>();
        }

        catch(UnknownHostException u)
        {
            System.out.println(u);
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
    }




    /**
     * Listens to the server and adds (through calls to the changeFromServer method) incoming
     * strings to the fromServer list.
     */

    public void run()
    {
        String string = "";

        // Continuously listen to the server.
        while (!socket.isClosed())
        {
            try
            {
                string = in.readUTF();
                fromServer.add(string);
                //changeFromServer(string);
            }
            catch(IOException i)
            {
                System.out.println(i);
            }
        }
    }


    /**
     * Writes and flushes a string to the server.
     * @param string The string to be sent to the server.
     */

    public void sendToServer(String string)
    {
        try
        {
            out.writeUTF(string);
            out.flush();
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
    }




    public String getFromServer()
    {
        String string = "";

        try{ fromServer.take(); }
        catch(InterruptedException e) { }

        return string;
    }


    /**
     * Closes the socket and the data streams.
     */

    public void closeConnection()
    {
        sendToServer("closeConnection");

        try {in.close();
            System.out.println("DataInputStream closed.");}
        catch(IOException i)
        {
            System.out.println("Closing DataInputStream failed.");
            System.out.println(i);
        }

        try {out.close();
            System.out.println("DataOutputStream closed.");}
        catch(IOException e)
        {
            System.out.println("Closing DataOutputStream failed.");
            System.out.println(e);
        }

        try{socket.close();
            System.out.println("Socket closed.");}
        catch(IOException e)
        {
            System.out.println("Closing socket failed.");
            System.out.println(e);
        }
    }


    /**
     * Returns the first string in the ArrayList fromServer.
     * @return The first string in the fromServer list.
     */

  /*  public String getFromServerLog()
    {
        while(true)
        {
            if(fromServer.size() > 0)
            {
                String string = fromServer.get(0);
                changeFromServer("remove");

                return string;
            }
            else
            {
                try { Thread.sleep(60); }
                catch(InterruptedException e) {}
            }
        }


    }*/


    /**
     * Returns the ArrayList fromServer.
     * @return The ArrayList fromServer.
     */




    /**
     * Method for both adding to, and removing from, the fromServer list. If the incoming string is
     * "remove", the first string in the list is removed. Otherwise the incoming string is added to
     * the list. The method is synchronized to avoid any potential race conditions.
     * @param string The string to be added, or the signal to remove the first string in fromServer.
     */



    /*public synchronized void changeFromServer(String string)
    {

        if(string.equals("remove"))
        {
            fromServer.remove(0);
            //System.out.println("Removed one item. Number of items are now " + fromServer.size() + ".");
        }
        else
        {
            fromServer.add(string);
            //System.out.println("Added " + string + ". Number of items are now " + fromServer.size() + ".");
        }
    }*/
}

