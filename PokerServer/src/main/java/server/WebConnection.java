package server;

import logic.Player;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;




/**
 * Objects of this class are supposed to be given to {@link logic.Player playerobjects}, to simplify the code when
 * the server needs to talk to the clients.
 *
 */


public class WebConnection extends Thread{


    private DataInputStream  in = null;
    private DataOutputStream out = null;
    private Socket socket = null;
    private ArrayList<String> fromClient;



    public WebConnection(Socket socket, Integer number)
    {
        this.socket = socket;
        fromClient = new ArrayList<String>();

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
     * Listens to the client, and puts incoming strings in fromClient list.
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
                changeFromClient(string);
                System.out.println(string);
            }
            catch(IOException i)
            {
                System.out.println(i);
            }
        }
    }


//    public String getInputFromPlayer()
//    {
//    	String string = "";
//
//    	try
//    	{
//    		string = in.readUTF();
//    	}
//    	catch(IOException e)
//    	{
//    		System.out.println(e);
//    	}
//    }



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
            changeFromClient("connectionFail");
        }
    }


    /**
     * Closes the socket and the streams.
     */

    public void closeConnection()
    {
        try
        {
            in.close();
            out.close();
            socket.close();
        }

        catch(IOException i)
        {
            System.out.println(i);
        }
    }



    /**
     * Waits until there is a string in the fromClient list. Then clears the string from the list and returns it.
     * @return The first string in the fromClient list.
     */

    public String getFromClientLog()
    {
        while(true)
        {
            System.out.println("");

            if(!fromClient.isEmpty())
            {
                String string = fromClient.get(0);
                changeFromClient("remove");

                System.out.println(string);
                return string;
            }
        }

    }


    /**
     * Method for both adding to, and removing from, the fromClient list. If the incoming string is
     * "remove", the first string in the list is removed. Otherwise the incoming string is added to
     * the list. The method is synchronized to avoid any potential race conditions.
     * @param string The string to be added, or the signal to remove the first string in fromServer.
     */
    public synchronized void changeFromClient(String string)
    {
        if(string.equals("remove"))
        {
            fromClient.remove(0);
            System.out.println(string);
        }
        else
        {
            fromClient.add(string);
        }
    }

}