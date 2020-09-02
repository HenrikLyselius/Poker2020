package com.lyselius.database;

import com.lyselius.logic.Player;
import org.hibernate.HibernateError;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.w3c.dom.Document;
import javax.naming.Referenceable;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Objects;


/**
 *
 * This class provides static methods for communication with the database. If the database goes down, a backup database is
 * simulated, in a somewhat cumbersome way, by writing to and reading from a file.
 *
 */


public class Services {

    private SessionFactory sessionFactory = null;
    private static volatile Services instance = null;
    private boolean databaseError = false;
    private boolean databaseIsUpToDate = false;
    private ObjectOutputStream outBackupActive;
    private ObjectOutputStream outBackupLoggedOut;
    private File backupFileActivePlayers = new File("com.lyselius.database/src/main/resources/backupFileActivePlayers.txt");
    private File backupFileLoggedOutPlayers = new File ("com.lyselius.database/src/resources/backupFileLoggedOutPlayers.txt");


    private Services()
    {
        // If the database was out of sync, i.e. the database was offline for some time before the server was turned off,
        // there will be data written to file as a backup that should be transfered to the database before any other
        // operations take place.
        readInBackedUpDataToDatabase();

        try
        {
            outBackupActive = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(backupFileActivePlayers)));
            outBackupLoggedOut = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(backupFileLoggedOutPlayers)));
        }
        catch (FileNotFoundException e) {e.printStackTrace();}
        catch (IOException e) {e.printStackTrace();}
    }




    public static Services getInstance()
    {
        if(instance == null)
        {
            synchronized (Services.class)
            {
                if(instance == null)
                {
                    instance = new Services();
                }
            }
        }
        return instance;
    }




    private void loadSessionFactory(){

        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        configuration.addAnnotatedClass(Player.class);
        sessionFactory = configuration.buildSessionFactory();
    }




    private Session getSession() throws HibernateException {

        if(sessionFactory == null)
        {
            loadSessionFactory();
        }

        Session session = null;
        try {
            session = sessionFactory.openSession();
        }catch(Throwable t){
            System.err.println("Exception while getting session.. ");
            t.printStackTrace();
            databaseError = true;
        }
        if(session == null) {
            System.err.println("session is discovered null");
            databaseError = true;
        }

        /*If a session is collected correctly, then the database is obviously up and running. The check below is to see
        if it is working now, but has been out of service earlier. If that is the case, backed up data should should be
        transfered to the database before doing any new operations.  */
        if(Objects.nonNull(session) && databaseError)
        {
            readInBackedUpDataToDatabase();
            databaseError = false;
        }

        return session;
    }


    public String hashPassword(String password, String salt)
    {
        MessageDigest md = null;
        try { md = MessageDigest.getInstance("SHA-256"); }
        catch (NoSuchAlgorithmException e) { e.printStackTrace(); }

        md.update(salt.getBytes());
        byte[] bytes = md.digest(password.getBytes());

        return encodeHexString(bytes);

    }

    public String getSalt()
    {
        SecureRandom sr = null;

        try { sr = SecureRandom.getInstance("SHA1PRNG", "SUN"); }
        catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
        catch (NoSuchProviderException e) { e.printStackTrace(); }

        byte[] salt = new byte[32];
        sr.nextBytes(salt);

        return encodeHexString(salt);
    }


    /**
     * Puts a Player object in the database.
     * @param player A Player object.
     */

    public void putInDatabase(Player player)
    {
        Session session = getSession();

        if(!databaseError)
        {
            try
            {
                session.beginTransaction();
                session.save(player);
                session.getTransaction().commit();
            }
            catch(Exception e){databaseError = true;}
            finally {session.close();}
        }
    }




    /**
     * Gets a Player object from the database.
     * @param username The username for a Player object.
     * @return A Player object if the username provided corresponds to one, null otherwise.
     */

    public Player getFromDatabase(String username)
    {
        Session session = getSession();

        try
        {
            session.beginTransaction();
            Player player = session.get(Player.class, username);
            session.getTransaction().commit();
            return player;
        }
        catch(Exception e)
        {
            databaseError = true;
            return null;
        }
        finally
        {
            session.close();
        }

    }




    /**
     * Updates a Player object's cash variable in the database.
     * @param player A Player object.
     */


    public void updateInDatabase(Player player, boolean active)
    {
        Session session = getSession();

        if(!databaseError)
        {
            try
            {
                session.beginTransaction();
                Player playerToUpdate = session.get(Player.class, player.getUsername());
                playerToUpdate.setCash(player.getCash());
                session.getTransaction().commit();
            }
            catch(Exception e) {databaseError = true;}
            finally {session.close();}
        }

        if(databaseError)
        {
            backupPlayerObject(player, active);
        }

        backupPlayerObject(player, active);






        //backupPlayerObject(player);
    }



    private void backupPlayerObject(Player player, boolean playerIsActive)
    {
        try
        {
            // Write the player object to a file, as a backup, until the database has started working again.
            if(playerIsActive)
            {
                outBackupActive.writeObject(player);
                outBackupActive.flush();
            }
            else
            {
                outBackupLoggedOut.writeObject(player);
                outBackupLoggedOut.flush();
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }





    private void readInBackedUpDataToDatabase()
    {
        ObjectInputStream in = null;
        ArrayList<Player> list = new ArrayList<Player>();

        if(backupFileLoggedOutPlayers.length() != 0)
        {
            try
            {
                in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(backupFileLoggedOutPlayers)));
            }
            catch (IOException e) {e.printStackTrace();}

            try
            {
                while(true)
                {list.add((Player) in.readObject());}
            }
            catch(EOFException e){System.out.println("All data was read correctly.");}
            catch(Exception e){}

            closeStream(in);

            list.stream()
                    .forEach(player -> updateInDatabase(player, false));

            list.clear();
            backupFileLoggedOutPlayers.delete();
        }

        if(backupFileActivePlayers.length() != 0)
        {
            try
            {
                in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(backupFileActivePlayers)));
            }
            catch (IOException e) {e.printStackTrace();}

            try
            {
                while(true)
                {list.add((Player) in.readObject());}
            }
            catch(EOFException e){System.out.println("Allt är inläst.");}
            catch(Exception e){}

            closeStream(in);

            list.stream()
                    .forEach(player -> updateInDatabase(player, true));

            backupFileActivePlayers.delete();
        }

        databaseIsUpToDate = true;
    }



    private void closeStream(ObjectInputStream in)
    {
        try
        {
            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

/*
    private boolean databaseIsUpToDate()
    {
        if(backupFile.length() == 0)
        {
            return true;
        }

        // If the backup file is nonempty, it means the server went down without being in sync with the database,
        // and that we have to move data from the backup file to the database. No player should be able to log in,
        // until this is done.

        try
        {
            backupIn = new ObjectInputStream(new BufferedInputStream(new FileInputStream(backupFile)));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }

        backup.clear();

        try{
            while(true)
            {
                backup.add((Player) backupIn.readObject());
            }
        }
        catch(EOFException e){
            System.out.println("Everything from the backup file, is now read to the backup list.");
        }
        catch(Exception e){ return false;}

        backup.stream()
                .forEach(player -> updateInDatabase(player));

        if(!databaseError)
        {
            clearBackups();
            return true;
        }

        return false;

    }

    private void clearBackups()
    {
        backup.clear();
        backupFile.delete();
    }*/


    public String encodeHexString(byte[] byteArray) {
        StringBuffer hexStringBuffer = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            hexStringBuffer.append(byteToHex(byteArray[i]));
        }
        return hexStringBuffer.toString();
    }


    public String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }

}