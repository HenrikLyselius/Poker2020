package com.lyselius.database;


import com.lyselius.connection.Logging;
import com.lyselius.logic.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  This class is used to simulate a backup database, and does that by serializing player objects, and writing them to
 *  file.
 */
public class BackupDatabase {


    private ObjectOutputStream outBackupActive;
    private ObjectOutputStream outBackupLoggedOut;
    private File backupFileActivePlayers = new File("com.lyselius.database/src/main/resources/backupFileActivePlayers.txt");
    private File backupFileLoggedOutPlayers = new File ("com.lyselius.database/src/main/resources/backupFileLoggedOutPlayers.txt");
    private static final Logger logger = Logging.getLogger(BackupDatabase.class.getName());


    protected BackupDatabase()
    {

    }




    private void openOutputStreamActivePlayers()
    {
        try
        {
            outBackupActive = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(backupFileActivePlayers)));
        }
        catch (FileNotFoundException e)
        {
            logger.log(Level.SEVERE, "FileNotFound", e);
            e.printStackTrace();
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, "IOException", e);
            e.printStackTrace();
        }
    }


    private void openOutputStreamLoggedOutPlayers()
    {
        try
        {
            outBackupLoggedOut = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(backupFileLoggedOutPlayers)));
        }
        catch (FileNotFoundException e)
        {
            logger.log(Level.SEVERE, "FileNotFound", e);
            e.printStackTrace();
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, "IOException", e);
            e.printStackTrace();
        }
    }


    public void prepare()
    {
        openOutputStreamActivePlayers();
        openOutputStreamLoggedOutPlayers();
    }


    public void backupPlayerObject(Player player, boolean playerIsActive)
    {
        try
        {
            if(playerIsActive)
            {
                outBackupActive.writeObject(player);
                outBackupActive.flush();
                logger.log(Level.INFO, "Player " + player.getUsername() + " is written to backup file.");
            }
            else
            {
                outBackupLoggedOut.writeObject(player);
                outBackupLoggedOut.flush();
                logger.log(Level.INFO, "Player " + player.getUsername() + " is written to backup file for logged out players.");
            }

        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, "IOException, Player object could not be written to back up file.", e);
            e.printStackTrace();
        }
    }



    public void clearActivePlayersBackUp()
    {
        closeStream(outBackupActive);
        boolean deleted = backupFileActivePlayers.delete();
        System.out.println(deleted);
        openOutputStreamActivePlayers();
    }


    public void clearLoggedOutPlayersBackUp()
    {
        closeStream(outBackupLoggedOut);
        backupFileLoggedOutPlayers.delete();
        openOutputStreamLoggedOutPlayers();
    }



    public ArrayList<Player> getBackedUpPlayerDataLoggedOutPlayers()
    {
        ObjectInputStream in = null;
        ArrayList<Player> list = new ArrayList<Player>();

        if(backupFileLoggedOutPlayers.length() != 0)
        {
            try
            {
                in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(backupFileLoggedOutPlayers)));
            }
            catch (IOException e)
            {
                logger.log(Level.SEVERE, "Input stream from back up file could not be opened.", e);
                e.printStackTrace();
            }

            try
            {
                while (true)
                {
                    list.add((Player) in.readObject());
                }
            }
            catch (EOFException e) { logger.log(Level.INFO, "All data was read correctly.");}
            catch (Exception e) {logger.log(Level.SEVERE, "Player objects could not be read from back up file.", e); }

            closeStream(in);
        }

        return list;
    }



    public ArrayList<Player> getBackedUpPlayerDataActivePlayers()
    {
        ObjectInputStream in = null;
        ArrayList<Player> list = new ArrayList<Player>();

        if(backupFileActivePlayers.length() != 0)
        {
            try
            {
                in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(backupFileActivePlayers)));
            }
            catch (IOException e)
            {
                logger.log(Level.SEVERE, "Input stream from from back up file could not be opened.", e);
                e.printStackTrace();
            }

            try
            {
                while(true)
                {list.add((Player) in.readObject());}
            }
            catch(EOFException e){logger.log(Level.INFO, "All data was read correctly.");}
            catch(Exception e) {logger.log(Level.SEVERE, "Player objects could not be read from back up file.", e); }

            closeStream(in);
        }

        for(Player player : list)
        {
            logger.log(Level.INFO, "Player info for " + player.getUsername() + " has been read from the backup " +
                "database, and will be transferred to the main database.");
        }

        return list;
    }






    private void closeStream(Closeable stream)
    {
        if(stream != null)
        {
            try
            {
                stream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }


    public boolean backUpFileLoggedOutPlayersExists()
    {
        return backupFileLoggedOutPlayers.exists();
    }
}
