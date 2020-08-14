package com.lyselius.GUI;


import com.lyselius.connection.WebConnection;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;

/**
 * An object of this class is used to display the gameplay view to a player.
 *
 */

public class GameView {

    private Scene scene;
    private Group root = new Group();
    private WebConnection webConnection;


    private ArrayList<PlayerVBox> playerBoxes;
    private ArrayList<BettingHBox> bettingBoxes;


    private Button checkButton = new Button("Check");
    private Button betButton = new Button("Bet");
    private Button foldButton = new Button("Fold");
    private Button callButton = new Button("Call");
    private Button raiseButton = new Button("Raise");
    private Button leaveTable = new Button("Leave table after this hand.");
    private Button stayAtTable = new Button("Changed my mind, I am staying.");
    private ImageView table = new ImageView(new Image("table2.png"));
    //private ImageView table = new ImageView(new Image("/images/table2.png"));

    private VBox menu = new VBox();
    private HBox menuTop;
    private HBox communityCards;
    private Label pot;
    private Label winner;
    private Slider slider;
    private HBox leaveButtons;
    public boolean stillWannaPlay = true;
    private DoubleProperty fontSize;
    //private Sound sound;
    private int dealHelp = 0;


    private ImageView dealerButton = new ImageView(new Image("dealerButton2.png"));
    private String username;



    public GameView(WebConnection webConnection)
    {
        this.webConnection = webConnection;
        scene = new Scene(root, 1000, 600, Color.rgb(15, 76, 102));
        //sound = new Sound();

        playerBoxes = new ArrayList<PlayerVBox>();
        bettingBoxes = new ArrayList<BettingHBox>();
        createPlayerBoxes();
        createBettingBoxes();

        organizeGraphics();
        organizeButtons();
    }



    /**
     * Returns the scene object.
     * @return The scene object.
     */

    public Scene getscene()
    {
        return scene;
    }


    /**
     * Sets the username.
     * @param string The new username.
     */
    public void setUsername(String string)
    {
        username = string;
    }




    /**
     * Prepares for a new hand by clearing various variables.
     */

    public void newHand()
    {
        pot.setText("");
        winner.setText("");
        menu.getChildren().clear();
        communityCards.getChildren().clear();
        clearBets();

        if(root.getChildren().contains(dealerButton))
        {
            root.getChildren().remove(dealerButton);
        }


        for(PlayerVBox playerVBox : playerBoxes)
        {
            playerVBox.clearForNewHand();
        }

    }


    /**
     * Updates information about a player.
     */

    public void updatePlayer()
    {
        String username = webConnection.getFromServerLog();
        String update = webConnection.getFromServerLog();

        int index = getIndexOfPlayer(username);

        if(update.equals("fold"))
        {
            playerBoxes.get(index).foldCards();
        }

        if(update.equals("cash"))
        {
            playerBoxes.get(index).setCash(webConnection.getFromServerLog());
        }

        if(update.equals("showCards"))
        {
            playerBoxes.get(index).showCards();
        }
    }


    /**
     * Sets info about all players.
     */

    public void setPlayerInfo()
    {
        int numberOfPlayers = Integer.parseInt(webConnection.getFromServerLog());

        for(int i = 0; i < numberOfPlayers; i++)
        {
            setPlayerInfoHelp(i);
        }

        sortPlayerBoxes(numberOfPlayers);
        positionThePlayerBoxes();
        positionTheBettingBoxes();
        showCards(numberOfPlayers);
    }



    /**
     * Removes the menu from the screen.
     *
     */

    public void clearMenu()
    {
        menu.getChildren().clear();
    }



    private void setPlayerInfoHelp(int i)
    {
        playerBoxes.get(i).setUsername(webConnection.getFromServerLog());
        playerBoxes.get(i).setCash(webConnection.getFromServerLog());

        String string1 = webConnection.getFromServerLog();
        //ImageView card1 = new ImageView(new Image("/images/" + string1 + ".png"));
        ImageView card1 = getImageView(string1);
        card1.fitHeightProperty().bind(scene.heightProperty().multiply(0.15));
        card1.setPreserveRatio(true);
        playerBoxes.get(i).setCard1(card1);

        String string2 = webConnection.getFromServerLog();
        //ImageView card2 = new ImageView(new Image("/images/" + string2 + ".png"));
        ImageView card2 = getImageView(string2);
        card2.fitHeightProperty().bind(scene.heightProperty().multiply(0.15));
        card2.setPreserveRatio(true);
        playerBoxes.get(i).setCard2(card2);

        playerBoxes.get(i).setBlackBackground();
    }


    private ImageView getImageView(String string)
    {
        return new ImageView(new Image(string + ".png"));
    }



    private void sortPlayerBoxes(int numberOfPlayers)
    {
        int rotations = 0;

        while(true)
        {
            if(playerBoxes.get(0).getUsername().equals(username) == false)
            {
                Collections.rotate(playerBoxes.subList(0, numberOfPlayers), 1);
                rotations++;
            }
            else
            {
                break;
            }
        }

        positionTheDealerButton((rotations + (numberOfPlayers - 1)) % numberOfPlayers);
    }


    /**
     * Updates the screen with a new bet.
     */

    public void setBet()
    {
        String bettingPlayer = webConnection.getFromServerLog();
        String betAmount = webConnection.getFromServerLog();

        int index = getIndexOfPlayer(bettingPlayer);

        bettingBoxes.get(index).setBetAmount(betAmount);
    }



    /**
     * Clears the screen from bets.
     */

    public void clearBets()
    {
        for(BettingHBox bettingHBox : bettingBoxes)
            bettingHBox.ClearTheBox();
    }


    /**
     * Changes the view of the player from inactive to active and vice versa.
     */

    public void changeActivePlayer()
    {
        String username = webConnection.getFromServerLog();
        int index = getIndexOfPlayer(username);

        playerBoxes.get(index).changeBackground();

//		String change = webConnection.getFromServerLog();
//
//		if(change.equals("setActiveBackground"))
//		{
//			playerBoxes.get(index).setActiveBackground();
//		}
//
//		if(change.equals("setOffActiveBackground"))
//		{
//			playerBoxes.get(index).setOffActiveBackground();
//		}
    }


    private int getIndexOfPlayer(String username)
    {
        for(int i = 0; i < playerBoxes.size(); i++)
        {
            if(playerBoxes.get(i).getUsername().equals(username))
            {
                return i;
            }
        }

        return -1;
    }



    private void positionThePlayerBoxes()
    {
        System.out.println("Här är vi i playerBoxes.");
        int numberOfPlayers = playerBoxes.size();

        if(numberOfPlayers > 0)
        {
            playerBoxes.get(0).layoutXProperty().bind(scene.widthProperty().multiply(0.44));
            playerBoxes.get(0).prefWidthProperty().bind(scene.widthProperty().multiply(0.1));
            playerBoxes.get(0).layoutYProperty().bind(scene.heightProperty().multiply(0.77));
            playerBoxes.get(0).prefHeightProperty().bind(scene.heightProperty().multiply(0.17));
        }

        if(numberOfPlayers > 1)
        {
            playerBoxes.get(1).layoutXProperty().bind(scene.widthProperty().multiply(0.11));
            playerBoxes.get(1).prefWidthProperty().bind(scene.widthProperty().multiply(0.1));
            playerBoxes.get(1).layoutYProperty().bind(scene.heightProperty().multiply(0.64));
            playerBoxes.get(1).prefHeightProperty().bind(scene.heightProperty().multiply(0.15));
        }

        if(numberOfPlayers > 2)
        {
            playerBoxes.get(2).layoutXProperty().bind(scene.widthProperty().multiply(0.11));
            playerBoxes.get(2).prefWidthProperty().bind(scene.widthProperty().multiply(0.1));
            playerBoxes.get(2).layoutYProperty().bind(scene.heightProperty().multiply(0.14));
            playerBoxes.get(2).prefHeightProperty().bind(scene.heightProperty().multiply(0.15));
        }

        if(numberOfPlayers > 3)
        {
            playerBoxes.get(3).layoutXProperty().bind(scene.widthProperty().multiply(0.44));
            playerBoxes.get(3).prefWidthProperty().bind(scene.widthProperty().multiply(0.1));
            playerBoxes.get(3).layoutYProperty().bind(scene.heightProperty().multiply(0.01));
            playerBoxes.get(3).prefHeightProperty().bind(scene.heightProperty().multiply(0.15));
        }

        if(numberOfPlayers > 4)
        {
            playerBoxes.get(4).layoutXProperty().bind(scene.widthProperty().multiply(0.81));
            playerBoxes.get(4).prefWidthProperty().bind(scene.widthProperty().multiply(0.1));
            playerBoxes.get(4).layoutYProperty().bind(scene.heightProperty().multiply(0.14));
            playerBoxes.get(4).prefHeightProperty().bind(scene.heightProperty().multiply(0.15));
        }

        if(numberOfPlayers > 5)
        {
            playerBoxes.get(5).layoutXProperty().bind(scene.widthProperty().multiply(0.81));
            playerBoxes.get(5).prefWidthProperty().bind(scene.widthProperty().multiply(0.1));
            playerBoxes.get(5).layoutYProperty().bind(scene.heightProperty().multiply(0.64));
            playerBoxes.get(5).prefHeightProperty().bind(scene.heightProperty().multiply(0.15));
        }
    }





    private void positionTheBettingBoxes()
    {
        System.out.println("Här är vi i bettingboxes.");
        int numberOfPlayers = playerBoxes.size();

        if(numberOfPlayers > 0)
        {
            bettingBoxes.get(0).layoutXProperty().bind(scene.widthProperty().multiply(0.46));
            bettingBoxes.get(0).prefWidthProperty().bind(scene.widthProperty().multiply(0.1));
            bettingBoxes.get(0).layoutYProperty().bind(scene.heightProperty().multiply(0.70));
            bettingBoxes.get(0).prefHeightProperty().bind(scene.heightProperty().multiply(0.05));
        }


        if(numberOfPlayers > 1)
        {
            bettingBoxes.get(1).layoutXProperty().bind(scene.widthProperty().multiply(0.22));
            bettingBoxes.get(1).prefWidthProperty().bind(scene.widthProperty().multiply(0.1));
            bettingBoxes.get(1).layoutYProperty().bind(scene.heightProperty().multiply(0.60));
            bettingBoxes.get(1).prefHeightProperty().bind(scene.heightProperty().multiply(0.05));
        }

        if(numberOfPlayers > 2)
        {
            bettingBoxes.get(2).layoutXProperty().bind(scene.widthProperty().multiply(0.22));
            bettingBoxes.get(2).prefWidthProperty().bind(scene.widthProperty().multiply(0.1));
            bettingBoxes.get(2).layoutYProperty().bind(scene.heightProperty().multiply(0.36));
            bettingBoxes.get(2).prefHeightProperty().bind(scene.heightProperty().multiply(0.05));
        }

        if(numberOfPlayers > 3)
        {
            bettingBoxes.get(3).layoutXProperty().bind(scene.widthProperty().multiply(0.46));
            bettingBoxes.get(3).prefWidthProperty().bind(scene.widthProperty().multiply(0.1));
            bettingBoxes.get(3).layoutYProperty().bind(scene.heightProperty().multiply(0.25));
            bettingBoxes.get(3).prefHeightProperty().bind(scene.heightProperty().multiply(0.05));
        }

        if(numberOfPlayers > 4)
        {
            bettingBoxes.get(4).layoutXProperty().bind(scene.widthProperty().multiply(0.74));
            bettingBoxes.get(4).prefWidthProperty().bind(scene.widthProperty().multiply(0.1));
            bettingBoxes.get(4).layoutYProperty().bind(scene.heightProperty().multiply(0.38));
            bettingBoxes.get(4).prefHeightProperty().bind(scene.heightProperty().multiply(0.05));
        }

        if(numberOfPlayers > 5)
        {
            bettingBoxes.get(5).layoutXProperty().bind(scene.widthProperty().multiply(0.74));
            bettingBoxes.get(5).prefWidthProperty().bind(scene.widthProperty().multiply(0.1));
            bettingBoxes.get(5).layoutYProperty().bind(scene.heightProperty().multiply(0.60));
            bettingBoxes.get(5).prefHeightProperty().bind(scene.heightProperty().multiply(0.05));
        }

    }



    private void positionTheDealerButton(int dealerNumber)
    {
        dealerButton.fitHeightProperty().bind(scene.heightProperty().multiply(0.04));
        dealerButton.setPreserveRatio(true);
        root.getChildren().add(dealerButton);

        switch(dealerNumber)
        {

            case 0:
                dealerButton.layoutXProperty().bind(scene.widthProperty().multiply(0.44));
                dealerButton.layoutYProperty().bind(scene.heightProperty().multiply(0.77));
                break;

            case 1:
                dealerButton.layoutXProperty().bind(scene.widthProperty().multiply(0.11));
                dealerButton.layoutYProperty().bind(scene.heightProperty().multiply(0.64));
                break;

            case 2:
                dealerButton.layoutXProperty().bind(scene.widthProperty().multiply(0.11));
                dealerButton.layoutYProperty().bind(scene.heightProperty().multiply(0.14));
                break;

            case 3:
                dealerButton.layoutXProperty().bind(scene.widthProperty().multiply(0.44));
                dealerButton.layoutYProperty().bind(scene.heightProperty().multiply(0.01));
                break;

            case 4:
                dealerButton.layoutXProperty().bind(scene.widthProperty().multiply(0.81));
                dealerButton.layoutYProperty().bind(scene.heightProperty().multiply(0.14));
                break;

            case 5:
                dealerButton.layoutXProperty().bind(scene.widthProperty().multiply(0.81));
                dealerButton.layoutYProperty().bind(scene.heightProperty().multiply(0.64));
                break;

            default:
                break;
        }
    }



    // This outcommented method is a sceleton to make a deal animation that looks good. Some other
    // things have to be set up before it is working though.

    //	private void showCards(int numberOfPlayers)
//	{
//		playerBoxes.get(0).showCard1();
//		sound.playDealSound();
//
//		for(int i = 1; i < numberOfPlayers; i++)
//		{
//			playerBoxes.get(i).showBackOfCard1();
//			sound.playDealSound();
//		}
//
//		playerBoxes.get(0).showCard2();
//		sound.playDealSound();
//
//		for(int i = 1; i < numberOfPlayers; i++)
//		{
//			playerBoxes.get(i).showBackOfCard2();
//			sound.playDealSound();
//		}
//	}




    private void showCards(int numberOfPlayers)
    {
        playerBoxes.get(0).showCards();

        for(int i = 1; i < numberOfPlayers; i++)
        {
            playerBoxes.get(i).showBackOfCards();
        }
    }



    private void addPlayerBoxes()
    {
        for(int i = 0; i < playerBoxes.size(); i++)
        {
            root.getChildren().add(playerBoxes.get(i));
        }
    }




    private void addBettingBoxes()
    {
        for(int i = 0; i < bettingBoxes.size(); i++)
        {
            root.getChildren().add(bettingBoxes.get(i));
        }
    }


    /**
     * Updates the pot on the screen.
     */

    public void updatePot()
    {
        pot.setText("Pot: " + webConnection.getFromServerLog() + "€");
    }


    /**
     * Updates the slider with the actual bet limits.
     */

    public void updateBetLimits()
    {
        slider.setMin(Double.parseDouble(webConnection.getFromServerLog()));
        slider.setMax(Double.parseDouble(webConnection.getFromServerLog()));
    }


    /**
     * Sets the text of the winner Label.
     */

    public void changeWinnerMessage()
    {
        winner.setText(webConnection.getFromServerLog());
    }


    /**
     * Sets which menu that is to be shown on the screen.
     */
    public void changeMenu()
    {
        String whichMenu = webConnection.getFromServerLog();

        System.out.println("Här är vi.");
        switch(whichMenu)
        {
            case "checkOrBet":
                checkOrBetMenu();
                break;

            case "foldCallOrRaise":
                foldCallOrRaiseMenu();
                break;

            case "foldOrCall":
                foldOrCallMenu();
                break;

            default:
                break;

        }

    }




    /**
     * Shows the flop cards on the screen.
     */

    public void showFlop()
    {
        String card1 = webConnection.getFromServerLog();
        ImageView flop1 = new ImageView(new Image(card1 + ".png"));
        flop1.fitHeightProperty().bind(scene.heightProperty().multiply(0.15));
        flop1.setPreserveRatio(true);

        String card2 = webConnection.getFromServerLog();
        ImageView flop2 = new ImageView(new Image(card2 + ".png"));
        flop2.fitHeightProperty().bind(scene.heightProperty().multiply(0.15));
        flop2.setPreserveRatio(true);

        String card3 = webConnection.getFromServerLog();
        ImageView flop3 = new ImageView(new Image(card3 + ".png"));
        flop3.fitHeightProperty().bind(scene.heightProperty().multiply(0.15));
        flop3.setPreserveRatio(true);

        communityCards.getChildren().addAll(flop1, flop2, flop3);
    }


    /**
     * Shows the turn card on the screen.
     */

    public void showTurn()
    {
        String card = webConnection.getFromServerLog();
        ImageView turnCard = new ImageView(new Image(card + ".png"));
        turnCard.fitHeightProperty().bind(scene.heightProperty().multiply(0.15));
        turnCard.setPreserveRatio(true);

        communityCards.getChildren().add(turnCard);
    }


    /**
     * Shows the river card on the screen.
     */

    public void showRiver()
    {
        String card = webConnection.getFromServerLog();
        ImageView riverCard = new ImageView(new Image(card + ".png"));
        riverCard.fitHeightProperty().bind(scene.heightProperty().multiply(0.15));
        riverCard.setPreserveRatio(true);

        communityCards.getChildren().add(riverCard);
    }


    private void sleep(long milliseconds)
    {
        try
        {
            Thread.sleep(milliseconds);
        }
        catch(InterruptedException e)
        {

        }
    }


    private void organizeGraphics()
    {
        fontSize = new SimpleDoubleProperty(10);
        fontSize.bind(scene.heightProperty().multiply(0.02));
        root.styleProperty().bind(Bindings.concat("-fx-font-size: ",
                fontSize.asString("%.0f")).concat("%;"));


        menu = new VBox();
        menu.layoutXProperty().bind(scene.widthProperty().multiply(0.58));
        menu.prefWidthProperty().bind(scene.widthProperty().multiply(0.20));
        menu.layoutYProperty().bind(scene.heightProperty().multiply(0.79));
        menu.prefHeightProperty().bind(scene.heightProperty().multiply(0.21));


        menuTop = new HBox();
        menuTop.setPrefHeight(50);
        menuTop.setPrefWidth(600);

        communityCards = new HBox(6);
        communityCards.layoutXProperty().bind(scene.widthProperty().multiply(0.35));
        communityCards.prefWidthProperty().bind(scene.widthProperty().multiply(0.3));
        communityCards.layoutYProperty().bind(scene.heightProperty().multiply(0.42));
        communityCards.prefHeightProperty().bind(scene.heightProperty().multiply(0.16));
        communityCards.setAlignment(Pos.CENTER);

        slider = new Slider(0, 100, 0);
        slider.setShowTickLabels(true);

        slider.setLayoutY(100 - slider.getLayoutBounds().getMinY());
        slider.setPrefHeight(50);
        slider.setPrefWidth(100);

        pot = new Label();
        pot.layoutXProperty().bind(scene.widthProperty().multiply(0.48));
        pot.prefWidthProperty().bind(scene.widthProperty().multiply(0.3));
        pot.layoutYProperty().bind(scene.heightProperty().multiply(0.60));
        pot.prefHeightProperty().bind(scene.heightProperty().multiply(0.04));
        pot.setStyle("-fx-font-size: 10em");

        winner = new Label();
        winner.layoutXProperty().bind(scene.widthProperty().multiply(0.36));
        winner.prefWidthProperty().bind(scene.widthProperty().multiply(0.3));
        winner.layoutYProperty().bind(scene.heightProperty().multiply(0.37));
        winner.prefHeightProperty().bind(scene.heightProperty().multiply(0.04));
        winner.setStyle("-fx-font-size: 10em");

        leaveButtons = new HBox();
        leaveButtons.layoutXProperty().bind(scene.widthProperty().multiply(0.02));
        leaveButtons.prefWidthProperty().bind(scene.widthProperty().multiply(0.20));
        leaveButtons.layoutYProperty().bind(scene.heightProperty().multiply(0.90));
        leaveButtons.prefHeightProperty().bind(scene.heightProperty().multiply(0.05));

        leaveButtons.getChildren().add(leaveTable);


        leaveTable.setPrefHeight(40);
        leaveTable.setPrefWidth(400);
        leaveTable.setStyle("-fx-font-size: 10em");
        stayAtTable.setPrefHeight(40);
        stayAtTable.setPrefWidth(400);
        stayAtTable.setStyle("-fx-font-size: 10em");

        checkButton.setPrefWidth(400);
        checkButton.setStyle("-fx-font-size: 10em");
        betButton.setPrefWidth(400);
        betButton.setStyle("-fx-font-size: 10em");
        foldButton.setPrefWidth(400);
        foldButton.setStyle("-fx-font-size: 10em");
        callButton.setPrefWidth(400);
        callButton.setStyle("-fx-font-size: 10em");
        raiseButton.setPrefWidth(400);
        raiseButton.setStyle("-fx-font-size: 10em");

        table.layoutXProperty().bind(scene.widthProperty().multiply(0.15));
        table.layoutYProperty().bind(scene.heightProperty().multiply(0.20));
        table.fitWidthProperty().bind(scene.widthProperty().multiply(0.70));
        table.fitHeightProperty().bind(scene.heightProperty().multiply(0.6));

        root.getChildren().add(table);
        root.getChildren().add(menu);
        root.getChildren().add(communityCards);
        root.getChildren().add(pot);
        root.getChildren().add(winner);
        root.getChildren().add(leaveButtons);
        addPlayerBoxes();
        addBettingBoxes();

    }



    /**
     * Checks if the player wants to stay at the table, and disconnects if the answer is no.
     */
    public void stillWannaPlay()
    {

        if(stillWannaPlay == false)
        {
            webConnection.closeConnection();
            System.exit(2);
        }

        else
        {
            webConnection.sendToServer("continue");
        }
    }



    private void checkOrBetMenu()
    {
        menuTop.getChildren().clear();
        menuTop.getChildren().addAll(checkButton, betButton);

        menu.getChildren().addAll(menuTop, slider);
    }


    private void foldOrCallMenu()
    {
        menuTop.getChildren().clear();
        menuTop.getChildren().addAll(foldButton, callButton);

        menu.getChildren().add(menuTop);
    }

    private void foldCallOrRaiseMenu()
    {
        menuTop.getChildren().clear();
        menuTop.getChildren().addAll(foldButton, callButton, raiseButton);

        menu.getChildren().addAll(menuTop, slider);
    }

    private void createPlayerBoxes()
    {
        for(int i = 0; i < 6; i++)
        {
            playerBoxes.add(new PlayerVBox(scene));
        }
    }


    private void createBettingBoxes()
    {
        for(int i = 0; i < 6; i++)
        {
            bettingBoxes.add(new BettingHBox(scene));
        }
    }


    private void organizeButtons()
    {
        callButton.setOnAction(new CallButtonHandler());
        checkButton.setOnAction(new CheckButtonHandler());
        foldButton.setOnAction(new FoldButtonHandler());
        betButton.setOnAction(new BetButtonHandler());
        raiseButton.setOnAction(new RaiseButtonHandler());
        leaveTable.setOnAction(new LeaveTableHandler());
        stayAtTable.setOnAction(new StayAtTableHandler());
    }

    class RaiseButtonHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent ae)
        {
            webConnection.sendToServer("raise");
            webConnection.sendToServer("" + ((int) slider.getValue()));

            menu.getChildren().clear();

        }
    }


    class CallButtonHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent ae)
        {
            webConnection.sendToServer("call");
            menu.getChildren().clear();
        }
    }

    class CheckButtonHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent ae)
        {
            webConnection.sendToServer("check");
            menu.getChildren().clear();
        }
    }


    class FoldButtonHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent ae)
        {
            webConnection.sendToServer("fold");
            menu.getChildren().clear();
        }
    }



    class BetButtonHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent ae)
        {
            webConnection.sendToServer("bet");
            webConnection.sendToServer("" + ((int) slider.getValue()));

            menu.getChildren().clear();
        }
    }

    class LeaveTableHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent ae)
        {
            stillWannaPlay = false;
            leaveButtons.getChildren().clear();
            leaveButtons.getChildren().add(stayAtTable);
        }
    }


    class StayAtTableHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent ae)
        {
            stillWannaPlay = true;
            leaveButtons.getChildren().clear();
            leaveButtons.getChildren().add(leaveTable);
        }
    }
}
