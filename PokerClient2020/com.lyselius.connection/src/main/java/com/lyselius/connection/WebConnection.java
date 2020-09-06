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
     * Listens to the server and adds incoming strings to the fromServer list.
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

        try{ string = fromServer.take(); }
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




    public boolean serverLogIsEmpty()
    {
        return fromServer.isEmpty();
    }


}

