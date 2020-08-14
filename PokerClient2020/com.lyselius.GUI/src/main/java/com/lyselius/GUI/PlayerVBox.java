package com.lyselius.GUI;


import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Objects of this class are used to display information about a player in the GameView.
 *
 */

public class PlayerVBox extends VBox {

    private Scene scene;
    private Label username;
    private Label cash;
    private Label time;
    private HBox cards;
    private ImageView card1;
    private ImageView card2;
    private ImageView cardBack1;
    private ImageView cardBack2;
    private String standardBackground;
    private String activeBackground;
    private String background;
    private IntegerProperty seconds;
    private Timeline timeline;




    public PlayerVBox(Scene scene)
    {
        standardBackground = "-fx-background-color: rgb(6, 0, 0);";
        activeBackground = "-fx-background-color: rgb(255, 255, 100);";

        background = standardBackground;

        this.scene = scene;

        username = new Label("");
        username.setTextFill(Color.web("#FFD700"));

        cash = new Label("");
        cash.setTextFill(Color.web("#FFD700"));

        time = new Label("");
        time.setTextFill(Color.RED);
        seconds = new SimpleIntegerProperty(10);
        time.textProperty().bind(seconds.asString());

        timeline = new Timeline();
        timeline.setCycleCount(11);
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(10),
                new KeyValue(seconds, 0)));

        cards = new HBox(2);
        cards.setAlignment(Pos.CENTER);

        /*cardBack1 = new ImageView(new Image("/images/" + "BLUE BACK.png"));
        cardBack2 = new ImageView(new Image("/images/" + "BLUE BACK.png"));*/
        cardBack1 = new ImageView(new Image("BLUE BACK.png"));
        cardBack2 = new ImageView(new Image("BLUE BACK.png"));
        setComponentSizes();

        this.getChildren().addAll(username, cash, cards);
        this.setAlignment(Pos.CENTER);
        this.setStyle(standardBackground);
    }




    /**
     * Returns the value of the username label.
     * @return The username label.
     */
    public String getUsername() {
        return username.getText();
    }

    /**
     * Sets the value of the username label.
     * @param username The new username.
     */
    public void setUsername(String username) {
        this.username.setText(username);
    }

    /**
     * Returns the cash Label.
     * @return The cash Label.
     */
    public Label getCash() {
        return cash;
    }

    /**
     * Sets the value of the cash Label.
     * @param cash The new cash value.
     */
    public void setCash(String cash) {
        this.cash.setText(cash + "€");
    }

    /**
     * Returns the HBox containing the players cards.
     * @return The HBox containing the cards.
     */
    public HBox getCards() {
        return cards;
    }


    private void setCards(ImageView card) {
        this.cards.getChildren().add(card);
    }

    /**
     * Sets the value of the first card.
     * @param card1 The new card.
     */
    public void setCard1(ImageView card1) {
        this.card1 = card1;

        this.card1.fitHeightProperty().bind(scene.heightProperty().multiply(0.12));
        this.card1.setPreserveRatio(true);
    }

    /**
     * Sets the value of the second card.
     * @param card2 The new card.
     */
    public void setCard2(ImageView card2) {
        this.card2 = card2;

        this.card2.fitHeightProperty().bind(scene.heightProperty().multiply(0.12));
        this.card2.setPreserveRatio(true);
    }

    /**
     * Changes the view so that two turned down cards are shown.
     */
    public void showBackOfCards()
    {
        cards.getChildren().clear();
        cards.getChildren().addAll(cardBack1, cardBack2);
    }



    /**
     * Removes the cards from the view.
     */
    public void foldCards()
    {
        cards.getChildren().clear();
    }


    /**
     * Shows the players cards.
     */
    public void showCards()
    {
        cards.getChildren().clear();
        setCards(card1);
        setCards(card2);
    }





    /**
     * Clears the Box's variables for the a new Hand.
     */
    public void clearForNewHand()
    {
        username.setText("");
        cash.setText("");
        cards.getChildren().clear();
        //this.setBackground(Background.EMPTY);
        this.setStyle("");

        if(this.getChildren().contains(time))
        {
            this.getChildren().remove(time);
        }
    }



    private void setComponentSizes()
    {
        cardBack1.fitHeightProperty().bind(scene.heightProperty().multiply(0.12));
        cardBack1.setPreserveRatio(true);

        cardBack2.fitHeightProperty().bind(scene.heightProperty().multiply(0.12));
        cardBack2.setPreserveRatio(true);

        cash.prefHeightProperty().bind(scene.heightProperty().multiply(0.02));
        cash.setStyle("-fx-font-size: 10em");

        username.prefHeightProperty().bind(scene.heightProperty().multiply(0.02));
        username.setStyle("-fx-font-size: 10em");

        time.prefHeightProperty().bind(scene.heightProperty().multiply(0.02));
        time.setStyle("-fx-font-size: 10em");
    }


    /**
     * Sets the background to black.
     */

    public void setBlackBackground()
    {
        this.setStyle(standardBackground);
    }

    /**
     * Changes the background and the text to show that this player is the next to act.
     */

    public void changeBackground()
    {
        if(this.getStyle().equals(standardBackground))
        {
            this.setStyle(activeBackground);
            username.setTextFill(Color.web("#060000"));
            cash.setTextFill(Color.web("#060000"));
            startTimer();
        }
        else
        {
            this.setStyle(standardBackground);
            username.setTextFill(Color.web("#FFD700"));
            cash.setTextFill(Color.web("#FFD700"));
            stopTimer();
        }
    }


//	public void setActiveBackground()
//	{
//		this.setStyle(activeBackground);
//		username.setTextFill(Color.web("#060000"));
//		cash.setTextFill(Color.web("#060000"));
//		startTimer();
//	}
//
//	public void setOffActiveBackground()
//	{
//		this.setStyle(standardBackground);
//		username.setTextFill(Color.web("#FFD700"));
//		cash.setTextFill(Color.web("#FFD700"));
//		stopTimer();
//	}

    private void startTimer()
    {
        seconds.set(10);
        timeline.playFromStart();
        this.getChildren().add(time);
    }

    private void stopTimer()
    {
        timeline.stop();
        this.getChildren().remove(time);
    }
}

