package com.lyselius.database;

import com.lyselius.connection.Logging;
import com.lyselius.logic.Player;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;


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
    private BackupDatabase backUpDB = new BackupDatabase();
    private int counter = 0;
    private static final Logger logger = Logging.getLogger(Services.class.getName());




    private Services()
    {

        if(backUpDB.backUpFileLoggedOutPlayersExists())
        {
            readInBackedUpData();
        }
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







    private void readInBackedUpData()
    {
        ArrayList<Player> backedUpPlayers = backUpDB.getBackedUpPlayerDataLoggedOutPlayers();
        ArrayList<Player> backedUpActivePlayers = backUpDB.getBackedUpPlayerDataActivePlayers();

        backedUpPlayers.stream()
                .forEach(player -> updateInDatabase(player, false));

        backedUpActivePlayers.stream()
                .forEach(player -> updateInDatabase(player, true));

        if(!databaseError)
        {
            backUpDB.clearLoggedOutPlayersBackUp();
            backUpDB.clearActivePlayersBackUp();
        }
    }



    private void loadSessionFactory(){

        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        configuration.addAnnotatedClass(Player.class);
        sessionFactory = configuration.buildSessionFactory();
    }




    private Session getSession() throws HibernateException {

        counter++;
        System.out.println("Counter är: " + counter);

        if(sessionFactory == null)
        {
            loadSessionFactory();
        }

        Session session = null;
        try
        {
            if(counter >= 10 && counter <= 20)
            {
                throw new IllegalArgumentException("This is just for testing purposes, to make sure the " +
                        " backup database is working.");
            }

            session = sessionFactory.openSession();
        }
        catch(Throwable t)
        {

            logger.log(Level.SEVERE, "Exception while getting session.", t);
            t.printStackTrace();
            databaseError = true;
        }

        if(session == null) {
            logger.log(Level.SEVERE, "Session is discovered null.");
            databaseError = true;
        }



        /*If a session is collected correctly, then the database is obviously up and running. The check below is to see
        if it has been out of service earlier. If that is the case, backed up data should should be transferred from the
        backup files, to the database before doing any new operations.  */

        if(Objects.nonNull(session) && databaseError)
        {
            databaseError = false;
            logger.log(Level.INFO, "The database is working again, and backed up data will be read from the back" +
                    " up files.");
            readInBackedUpData();
        }

        return session;
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
            catch(Exception e)
            {
                logger.log(Level.SEVERE, "Exception while putting player in database.", e);
                e.printStackTrace();
                databaseError = true;
            }
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
            logger.log(Level.SEVERE, "Exception while getting player from database.", e);
            e.printStackTrace();
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


    public void updateInDatabase(Player player, boolean playerIsActive)
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
            catch(Exception e)
            {
                logger.log(Level.SEVERE, "Exception while updating player in database.", e);
                e.printStackTrace();
                databaseError = true;
            }
            finally {session.close();}
        }

        if(databaseError)
        {
            backUpDB.backupPlayerObject(player, playerIsActive);
        }
    }



    public void prepareBackUpDB()
    {
        backUpDB.clearActivePlayersBackUp();
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





    public String encodeHexString(byte[] byteArray)
    {
        StringBuffer hexStringBuffer = new StringBuffer();

        for (int i = 0; i < byteArray.length; i++) {
            hexStringBuffer.append(byteToHex(byteArray[i]));
        }
        return hexStringBuffer.toString();
    }


    public String byteToHex(byte num)
    {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }




    public boolean dataBaseError()
    {
        return databaseError;
    }
}