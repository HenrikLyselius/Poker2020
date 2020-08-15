package com.lyselius.database;

import com.lyselius.logic.Player;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.w3c.dom.Document;
import javax.naming.Referenceable;


/**
 *
 * This class provides static methods for communication with the database.
 *
 */


public class Services {

    private static SessionFactory sessionFactory = null;


    private static void loadSessionFactory(){

        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        configuration.addAnnotatedClass(Player.class);
        sessionFactory = configuration.buildSessionFactory();
    }




    private static Session getSession() throws HibernateException {

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
        }
        if(session == null) {
            System.err.println("session is discovered null");
        }

        return session;
    }





    /**
     * Puts a Player object in the database.
     * @param player A Player object.
     */

    public static void putInDatabase(Player player)
    {
        Session session = getSession();

        try
        {
            session.beginTransaction();

            session.save(player);

            session.getTransaction().commit();
        }

        finally
        {
            session.close();
        }
    }




    /**
     * Gets a Player object from the database.
     * @param username The username for a Player object.
     * @return A Player object if the username provided corresponds to one, null otherwise.
     */

    public static Player getFromDatabase(String username)
    {
        Session session = getSession();

        try
        {
            session.beginTransaction();

            Player player = session.get(Player.class, username);

            session.getTransaction().commit();

            return player;
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


    public static void updateInDatabase(Player player)
    {
        Session session = getSession();

        try
        {
            session.beginTransaction();

            Player playerToUpdate = session.get(Player.class, player.getUsername());
            playerToUpdate.setCash(player.getCash());

            session.getTransaction().commit();
        }
        finally
        {
            session.close();
        }
    }



}