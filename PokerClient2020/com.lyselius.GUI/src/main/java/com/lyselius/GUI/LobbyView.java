package com.lyselius.GUI;


import com.lyselius.connection.WebConnection;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

/**
 * An object of this class is used to display a view of the lobby to a player.
 *
 */
public class LobbyView {


    private Scene lobbyScene;
    private Group root = new Group();
    private WebConnection webConnection;
    private Label welcome = new Label("You will be seated at a table as soon as possible.");
    private Label noMoney = new Label("You have run out of money, please deposit again.");
    private Button logOutButton = new Button("Log out");
    private boolean stillWannaPlay = true;
    private DoubleProperty fontSize;


    public LobbyView(WebConnection webConnection)
    {
        this.webConnection = webConnection;

        Color background = Color.rgb(255, 215, 0);
        lobbyScene = new Scene(root, 1000, 600, background);

        fontSize = new SimpleDoubleProperty(10);
        fontSize.bind(lobbyScene.heightProperty().multiply(0.02));
        root.styleProperty().bind(Bindings.concat("-fx-font-size: ",
                fontSize.asString("%.0f")).concat("%;"));

        welcome.layoutYProperty().bind(lobbyScene.heightProperty().multiply(0.40));
        welcome.prefHeightProperty().bind(lobbyScene.heightProperty().multiply(0.20));
        welcome.layoutXProperty().bind(lobbyScene.widthProperty().multiply(0.05));
        welcome.prefWidthProperty().bind(lobbyScene.widthProperty().multiply(0.92));

        welcome.setTextFill(Color.web("#FFFFFF"));
        welcome.setStyle("-fx-font-size: 29em");

        noMoney.layoutYProperty().bind(lobbyScene.heightProperty().multiply(0.40));
        noMoney.prefHeightProperty().bind(lobbyScene.heightProperty().multiply(0.20));
        noMoney.layoutXProperty().bind(lobbyScene.widthProperty().multiply(0.05));
        noMoney.prefWidthProperty().bind(lobbyScene.widthProperty().multiply(0.95));

        noMoney.setTextFill(Color.web("#FFFFFF"));
        noMoney.setStyle("-fx-font-size: 29em");


        logOutButton.layoutXProperty().bind(lobbyScene.widthProperty().multiply(0.02));
        logOutButton.prefWidthProperty().bind(lobbyScene.widthProperty().multiply(0.10));
        logOutButton.layoutYProperty().bind(lobbyScene.heightProperty().multiply(0.90));
        logOutButton.prefHeightProperty().bind(lobbyScene.heightProperty().multiply(0.05));
        logOutButton.setStyle("-fx-font-size: 10em");
        logOutButton.setOnAction(new LogOutButtonHandler());

        root.getChildren().addAll(welcome, logOutButton);
    }


    /**
     * Returns the lobby scene object.
     * @return The lobby scene object.
     */

    public Scene getLobbyScene()
    {
        return lobbyScene;
    }


    /**
     * Returns the boolean stillWannaPlay.
     * @return The stillWannaPlay boolean.
     */

    public boolean stillWannaPlay()
    {
        return stillWannaPlay;
    }

    /**
     * Displays a message that informs the player that the account does not have enough money.
     */
    public void changeToNoMoneyMessage()
    {
        root.getChildren().remove(welcome);
        root.getChildren().add(noMoney);
    }



    class LogOutButtonHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent ae)
        {
            webConnection.closeConnection();
            System.exit(2);
        }
    }
}

