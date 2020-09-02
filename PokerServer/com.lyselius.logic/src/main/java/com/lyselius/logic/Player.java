package com.lyselius.logic;

import com.lyselius.connection.WebConnection;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;




/**
 * This class represents a player. The object variables username, password, and cash, can be put in, or be
 * retrieved from, a database.
 *
 */


@Entity
@Table(name = "player_data")


public class Player implements Serializable {


    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name ="salt")
    private String salt;

    @Column(name = "cash")
    private int cash;

    @Transient
    private transient int actualBet = 0;
    @Transient
    private transient int totalAmountPutInPot = 0;
    @Transient
    private transient boolean stillInPot = true;
    @Transient
    private transient boolean isAllIn = false;
    @Transient
    private transient ArrayList<Card> cards = new ArrayList<Card>();
    @Transient
    private transient WebConnection webConnection;
    @Transient
    private transient boolean connected = true;




    public Player(String name, String password, String salt)
    {
        this.username = name;
        this.password = password;
        this.salt = salt;
        this.cash = 100;
    }

    public Player()
    {

    }


    /**
     * Returns the player's WebConnection object.
     * @return A WebConnection object.
     */

    public WebConnection getWebConnection()
    {
        return webConnection;
    }


    /**
     * Sets the player's WebConnection object.
     * @param webConnection A WebConnection object.
     */
    public void setWebConnection(WebConnection webConnection)
    {
        this.webConnection = webConnection;
    }



    /**
     * Returns the value of the player's cash variable.
     * @return The value of the cash variable.
     */
    public int getCash() {
        return cash;
    }


    /**
     * Sets the value of the player's cash variable.
     * @param cash The new cash value.
     */

    public void setCash(int cash) {
        this.cash = cash;

        if(cash == 0)
        {
            setIsAllIn(true);
        }
    }


    /**
     * Returns the value of the player's actualBet variable.
     * @return The value of the actualBet variable.
     */
    public int getActualBet() {
        return actualBet;
    }



    /**
     * Sets the value of the player's actualBet variable.
     * @param actualBet The new actualBet value.
     */
    public void setActualBet(int actualBet) {
        this.actualBet = actualBet;
    }


    /**
     * Returns the player's password.
     * @return A string representing the player's password.
     */

    public String getPassword()
    {
        return password;
    }



    /**
     * Returns a string with the player's username.
     * @return A string representing the player's username.
     */
    public String getUsername() {
        return username;
    }




    public String getSalt() { return salt;}


    /**
     * Returns the player's cards.
     * @return An ArrayList containing Card objects.
     */
    public ArrayList<Card> getCards()
    {
        return cards;
    }


    public String cardsToString()
    {
        return getCard1().toString() + ", " + getCard2().toString();
    }


    /**
     * Returns a boolean that shows if the player is all in.
     * @return The value of the isAllin variable.
     */
    public boolean isAllIn()
    {
        return isAllIn;
    }


    /**
     * Sets the player's isAllin variable.
     * @param b The new value of the isAllIn variable.
     */
    public void setIsAllIn(boolean b)
    {
        isAllIn = b;
    }

    /**
     * Returns the value of the player's isStillInPot variable.
     * @return The value of the stillInPot variable.
     */
    public boolean isStillInPot() {
        return stillInPot;
    }


    /**
     * Sets the value of the player's stillInPot variable.
     * @param b The new value of the stillInPot variable.
     */
    public void setStillInPot(boolean b) {
        stillInPot = b;
    }


    /**
     * Returns the Card object representing the player's first card.
     * @return A Card object.
     */
    public Card getCard1() {
        return cards.get(0);
    }

    /**
     * Sets the player's first Card object.
     * @param card1 The new Card object.
     */
    public void setCard1(Card card1) {
        cards.add(card1);
    }

    /**
     * Returns the Card object representing the player's second card.
     * @return A Card object.
     */
    public Card getCard2() {
        return cards.get(1);
    }


    /**
     * Sets the player's second Card object.
     * @param card2 The new Card object.
     */
    public void setCard2(Card card2) {
        cards.add(card2);
    }

    /**
     * Returns the value of the boolean connected.
     * @return The value of the boolean connected.
     */
    public boolean isConnected()
    {
        return connected;
    }


    /**
     * Returns the totalAmountPutInPot variable.
     * @return The totalAmountPutInPot variable.
     */
    public int getTotalAmountPutInPot()
    {
        return totalAmountPutInPot;
    }

    /**
     * Increases the totalAmountPutInPot with the incoming value.
     * @param amount The incoming bet size.
     */

    public void increaseTotalAmountPutInPot(int amount)
    {
        totalAmountPutInPot = totalAmountPutInPot + amount;
    }


    /**
     * Sets the value of the boolean connected.
     * @param b The new boolean value.
     */
    public void setConnected(boolean b)
    {
        connected = b;
    }


    /**
     * Resets the variables that are relevant for a specific hand.
     */
    public void resetPlayVariablesForNewHand()
    {
        stillInPot = true;
        isAllIn = false;
        cards.clear();
        totalAmountPutInPot = 0;
    }

    @Override
    public boolean equals(Object object)
    {
        if(object == null || object.getClass() != this.getClass())
        {
            return false;
        }

        Player otherPlayer = (Player)object;

        if(!this.getUsername().equals(otherPlayer.getUsername()))
        {
            return false;
        }

        return true;
    }
}