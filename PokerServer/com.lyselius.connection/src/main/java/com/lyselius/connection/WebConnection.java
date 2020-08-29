package com.lyselius.connection;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;


/**
 * Objects of this class are supposed to be given to playerobjects (initially to a handleLogin object), to simplify the code when
 * the server needs to talk to the clients.
 *
 */


public class WebConnection extends Thread{


    private DataInputStream  in = null;
    private DataOutputStream out = null;
    private Socket socket = null;
    //private ArrayList<String> fromClient;
    private ArrayBlockingQueue<String> fromClient;
    //private WaitAndNotify waitAndNotify = new WaitAndNotify();



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
            System.out.println(i);
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
                //changeFromClient(string);
            }
            catch(IOException i)
            {
                System.out.println(i);
                fromClient.add("connectionFail");
                //changeFromClient("connectionFail");
                closeConnection();
            }

        }
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
            System.out.println(e);
            fromClient.add("connectionFail");
            //changeFromClient("connectionFail");
        }
    }


    /**
     * Closes the socket and the streams.
     */

    public void closeConnection()
    {
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



    public String getFromClientLog() {

        String string = "";

        try { string = fromClient.take(); }
        catch (InterruptedException e) { }

        return string;
    }

      /*  while(true)
        {
            if(!fromClient.isEmpty())
            {
                String string = fromClient.get(0);
                changeFromClient("remove");

                //System.out.println(string);
                return string;
            }

            waitAndNotify.doWait();
        }*/




  /*      public synchronized void changeFromClient(String string)
    {
        if(string.equals("remove"))
        {
            fromClient.remove(0);
            //System.out.println(string);
        }
        else
        {
            fromClient.add(string);
            waitAndNotify.doNotify();
        }
    }*/



}