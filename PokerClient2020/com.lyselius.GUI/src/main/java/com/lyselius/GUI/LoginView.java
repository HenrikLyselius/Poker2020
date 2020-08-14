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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

/**
 * An object of this class is used to display the login view to a player.
 *
 */

public class LoginView {


    private Scene logInScene;
    private Group root = new Group();
    private Group startPage = new Group();
    private WebConnection webConnection;
    private Button logInButton = new Button("Log in");
    private Button createUserButton = new Button("Create user");
    private Button createNewUserButton = new Button("Create new user");

    private Label welcome = new Label("WELCOME TO GOLDEN POKER!");
    private Label userNameLabel = new Label("username");
    private Label passwordLabel = new Label("password");
    private Label incorrectLoginLabel = new Label("Username or password is incorrect.");
    private Label createUserLabel = new Label("Choose a username and a password.");
    private Label newUserCreated = new Label("User is succesfully created, and you have been awarded 100 euro."
            + " Please login to start playing.");
    private Label userNameTakenLabel = new Label("Username already exists.");
    private TextField userName = new TextField();
    private PasswordField password = new PasswordField();
    private DoubleProperty fontSize;



    public LoginView(WebConnection webConnection)
    {
        this.webConnection = webConnection;
        Color background = Color.rgb(14, 0, 0);
        logInScene = new Scene(root, 1000, 600, background);

        fontSize = new SimpleDoubleProperty(10);
        fontSize.bind(logInScene.heightProperty().multiply(0.02));
        root.styleProperty().bind(Bindings.concat("-fx-font-size: ",
                fontSize.asString("%.0f")).concat("%;"));

        welcome.layoutYProperty().bind(logInScene.heightProperty().multiply(0.15));
        welcome.prefHeightProperty().bind(logInScene.heightProperty().multiply(0.15));
        welcome.layoutXProperty().bind(logInScene.widthProperty().multiply(0.20));
        welcome.prefWidthProperty().bind(logInScene.widthProperty().multiply(0.75));

        welcome.setTextFill(Color.web("#FFD700"));
        welcome.setStyle("-fx-font-family: Arial");
        welcome.setStyle("-fx-font-size: 29em");




        logInButton.layoutYProperty().bind(logInScene.heightProperty().multiply(0.45));
        logInButton.prefHeightProperty().bind(logInScene.heightProperty().multiply(0.06));
        logInButton.layoutXProperty().bind(logInScene.widthProperty().multiply(0.55));
        logInButton.prefWidthProperty().bind(logInScene.widthProperty().multiply(0.1));
        logInButton.setStyle("-fx-font-size: 10em");
        logInButton.setOnAction(new LogInButtonHandler());

        createUserButton.layoutYProperty().bind(logInScene.heightProperty().multiply(0.45));
        createUserButton.prefHeightProperty().bind(logInScene.heightProperty().multiply(0.06));
        createUserButton.layoutXProperty().bind(logInScene.widthProperty().multiply(0.75));
        createUserButton.prefWidthProperty().bind(logInScene.widthProperty().multiply(0.1));
        createUserButton.setStyle("-fx-font-size: 10em");
        createUserButton.setOnAction(new CreateUserButtonHandler());

        userName.layoutYProperty().bind(logInScene.heightProperty().multiply(0.45));
        userName.prefHeightProperty().bind(logInScene.heightProperty().multiply(0.06));
        userName.layoutXProperty().bind(logInScene.widthProperty().multiply(0.15));
        userName.prefWidthProperty().bind(logInScene.widthProperty().multiply(0.1));
        userName.setStyle("-fx-font-size: 1em");


        userNameLabel.layoutYProperty().bind(logInScene.heightProperty().multiply(0.40));
        userNameLabel.prefHeightProperty().bind(logInScene.heightProperty().multiply(0.04));
        userNameLabel.layoutXProperty().bind(logInScene.widthProperty().multiply(0.15));
        userNameLabel.prefWidthProperty().bind(logInScene.widthProperty().multiply(0.1));
        userNameLabel.setStyle("-fx-font-size: 8em");
        userNameLabel.setTextFill(Color.web("2338F3"));


        userNameTakenLabel.layoutYProperty().bind(logInScene.heightProperty().multiply(0.54));
        userNameTakenLabel.prefHeightProperty().bind(logInScene.heightProperty().multiply(0.07));
        userNameTakenLabel.layoutXProperty().bind(logInScene.widthProperty().multiply(0.15));
        userNameTakenLabel.prefWidthProperty().bind(logInScene.widthProperty().multiply(0.2));
        userNameTakenLabel.setStyle("-fx-font-size: 9em");
        userNameTakenLabel.setTextFill(Color.web("EF2C2C"));

        newUserCreated.layoutYProperty().bind(logInScene.heightProperty().multiply(0.54));
        newUserCreated.prefHeightProperty().bind(logInScene.heightProperty().multiply(0.07));
        newUserCreated.layoutXProperty().bind(logInScene.widthProperty().multiply(0.15));
        newUserCreated.prefWidthProperty().bind(logInScene.widthProperty().multiply(0.6));
        newUserCreated.setStyle("-fx-font-size: 9em");
        newUserCreated.setTextFill(Color.web("#FFD700"));

        incorrectLoginLabel.layoutYProperty().bind(logInScene.heightProperty().multiply(0.54));
        incorrectLoginLabel.prefHeightProperty().bind(logInScene.heightProperty().multiply(0.07));
        incorrectLoginLabel.layoutXProperty().bind(logInScene.widthProperty().multiply(0.15));
        incorrectLoginLabel.prefWidthProperty().bind(logInScene.widthProperty().multiply(0.4));
        incorrectLoginLabel.setStyle("-fx-font-size: 9em");
        incorrectLoginLabel.setTextFill(Color.web("EF2C2C"));

        passwordLabel.layoutYProperty().bind(logInScene.heightProperty().multiply(0.40));
        passwordLabel.prefHeightProperty().bind(logInScene.heightProperty().multiply(0.04));
        passwordLabel.layoutXProperty().bind(logInScene.widthProperty().multiply(0.35));
        passwordLabel.prefWidthProperty().bind(logInScene.widthProperty().multiply(0.1));
        passwordLabel.setStyle("-fx-font-size: 8em");
        passwordLabel.setTextFill(Color.web("2338F3"));


        password.layoutYProperty().bind(logInScene.heightProperty().multiply(0.45));
        password.prefHeightProperty().bind(logInScene.heightProperty().multiply(0.06));
        password.layoutXProperty().bind(logInScene.widthProperty().multiply(0.35));
        password.prefWidthProperty().bind(logInScene.widthProperty().multiply(0.1));
        password.setStyle("-fx-font-size: 1em");


        startPage.getChildren().addAll(userName, userNameLabel, password, passwordLabel,
                logInButton, createUserButton, welcome);

        root.getChildren().add(startPage);


        createUserLabel.layoutYProperty().bind(logInScene.heightProperty().multiply(0.25));
        createUserLabel.prefHeightProperty().bind(logInScene.heightProperty().multiply(0.15));
        createUserLabel.layoutXProperty().bind(logInScene.widthProperty().multiply(0.15));
        createUserLabel.prefWidthProperty().bind(logInScene.widthProperty().multiply(0.75));

        createUserLabel.setTextFill(Color.web("#FFD700"));
        createUserLabel.setStyle("-fx-font-size: 15em");


        createNewUserButton.layoutYProperty().bind(logInScene.heightProperty().multiply(0.45));
        createNewUserButton.prefHeightProperty().bind(logInScene.heightProperty().multiply(0.06));
        createNewUserButton.layoutXProperty().bind(logInScene.widthProperty().multiply(0.60));
        createNewUserButton.prefWidthProperty().bind(logInScene.widthProperty().multiply(0.2));
        createNewUserButton.setStyle("-fx-font-size: 10em");
        createNewUserButton.setOnAction(new CreateNewUserButtonHandler());




    }

    public Scene getLogInScene()
    {
        return logInScene;
    }


    public void showCreateUserPage()
    {
        root.getChildren().clear();
        root.getChildren().addAll(userName, userNameLabel, password, passwordLabel,
                createNewUserButton, createUserLabel);
    }


    public void showAlreadyExistsMessage()
    {
        showCreateUserPage();
        root.getChildren().add(userNameTakenLabel);
    }

    public void showStartPage()
    {
        root.getChildren().clear();
        root.getChildren().addAll(userName, userNameLabel, password, passwordLabel,
                logInButton, createUserButton, welcome);
    }

    public void showIncorrectLogin()
    {
        showStartPage();
        root.getChildren().add(incorrectLoginLabel);
    }

    public void showSuccesfullLogin()
    {
        showStartPage();
        root.getChildren().add(newUserCreated);
    }

    public Button getLoginButton()
    {
        return logInButton;
    }

    class LogInButtonHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent ae)
        {
            webConnection.sendToServer("login");
            webConnection.sendToServer(userName.getText());
            webConnection.sendToServer(password.getText());

        }
    }



    class CreateUserButtonHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent ae)
        {
            //webConnection.setShowLogin(false);
            showCreateUserPage();
        }
    }

    class CreateNewUserButtonHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent ae)
        {
            if(userName.getText().equals("") || password.getText().equals(""))
            {
                // Skriv att de måste innehålla symboler.
            }
            else
            {
                webConnection.sendToServer("createUser");
                webConnection.sendToServer(userName.getText());
                webConnection.sendToServer(password.getText());
            }

        }
    }
}

