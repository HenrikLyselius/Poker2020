package com.lyselius.GUI;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class BettingHBox extends HBox {

    private ImageView chipStack;
    private Label betAmount;



    public BettingHBox(Scene scene)
    {
        chipStack = new ImageView(new Image("chipStack.png"));
        betAmount = new Label("");
        betAmount.setStyle("-fx-font-size: 10em");
        this.setSpacing(10);

        chipStack.fitHeightProperty().bind(scene.heightProperty().multiply(0.04));
        chipStack.setPreserveRatio(true);
        this.setAlignment(Pos.CENTER);
    }


    /**
     * Fills the box with a string representing the bet, and a chip stack picture.
     * @param string The new bet size.
     */

    public void setBetAmount(String string) {

        if(string.equals("0") == false)
        {
            this.getChildren().clear();

            betAmount.setText(string + "€");
            this.getChildren().addAll(chipStack, betAmount);
        }

    }


    /**
     * Empties the box.
     */

    public void ClearTheBox()
    {
        this.getChildren().clear();
    }




}
