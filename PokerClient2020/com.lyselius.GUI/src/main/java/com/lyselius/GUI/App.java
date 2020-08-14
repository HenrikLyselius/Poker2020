package com.lyselius.GUI;

import com.lyselius.connection.WebConnection;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class App extends Application {

    private WebConnection webConnection;
    private Stage stage;
    private Scene logInScene;
    private Scene lobbyScene;

    public boolean stillWannaPlay = true;
    public boolean showLogin = true;

    private GameView gameView;
    private LoginView VH;
    private LobbyView VL;



    public App()
    {
        webConnection = new WebConnection("127.0.0.1", 6002);
        //webConnection = new WebConnection("192.168.0.10", 6000);
        webConnection.start();

        VH = new LoginView(webConnection);
        logInScene = VH.getLogInScene();
        VL = new LobbyView(webConnection);
        gameView = new GameView(webConnection);
        lobbyScene = VL.getLobbyScene();
        //sound = new Sound();
    }


    public static void main(String[] args) {
        launch();
    }




    @Override
    public void start(Stage stage) throws Exception {

        this.stage = stage;
        stage.setScene(logInScene);
        stage.show();

        stage.minWidthProperty().bind(stage.heightProperty().multiply(1.6));
        stage.maxWidthProperty().bind(stage.heightProperty().multiply(1.6));
        stage.setMinHeight(420);

        stage.setOnCloseRequest(new WindowsClose());




        AnimationTimer timer = new AnimationTimer() {

            private long latestUpdate = 0 ;

            @Override
            public void handle(long now) {

                if (now - latestUpdate >= 100_000_000) {
                    latestUpdate = now ;
                }

                checkServerLog();

            }
        };
        timer.start();

    }







    /**
     * Checks if there are any new messages from the server. Interprets the message if there is one,
     * and calls the appropriate method.
     */

    public void checkServerLog()
    {
        if(!webConnection.getFromServer().isEmpty())
        {
            String string = webConnection.getFromServerLog();

            switch (string)
            {

                case "pot":
                    gameView.updatePot();
                    break;

                case "betLimits":
                    gameView.updateBetLimits();
                    break;

                case "newHand":
                    gameView.newHand();
                    break;

                case "menu":
                    gameView.changeMenu();
                    break;

                case "stillWannaPlay":
                    gameView.stillWannaPlay();
                    break;

                case "winnerMessage":
                    gameView.changeWinnerMessage();
                    break;

                case "correctLogin":
                    login();
                    break;

                case "sitDownAtTable":
                    setTableScene();
                    break;

                case "loginMessage":
                    checkLoginMessages();
                    break;

                case "noMoney":
                    noMoney();
                    break;

                case "flop":
                    gameView.showFlop();
                    break;

                case "turn":
                    gameView.showTurn();
                    break;

                case "river":
                    gameView.showRiver();
                    break;

                case "playerInfo":
                    gameView.setPlayerInfo();
                    break;

                case "updatePlayer":
                    gameView.updatePlayer();
                    break;

                case "newBet":
                    gameView.setBet();
                    break;

                case "clearBets":
                    gameView.clearBets();
                    break;

                case "changeActivePlayer":
                    gameView.changeActivePlayer();
                    break;

                case "check":
                    timeOutCheck();
                    break;

                case "fold":
                    timeOutFold();
                    break;

            }
        }
    }


    /**
     * Returns the Stage object.
     * @return The Stage object.
     */

    public Stage getStage()
    {
        return stage;
    }


    private void login()
    {
        getStage().setScene(lobbyScene);
        String username = webConnection.getFromServerLog();
        gameView.setUsername(username);
    }


    private void setTableScene()
    {
        getStage().setScene(gameView.getscene());
    }




    private void noMoney()
    {
        VL.changeToNoMoneyMessage();
        getStage().setScene(lobbyScene);
    }

    /**
     * Interprets the latest login message and displays (through a call to a LoginView object)
     * the appropriate message on the screen.
     */

    public void checkLoginMessages()
    {
        String string = webConnection.getFromServerLog();

        if(string.equals("userAlreadyExists"))
        {
            VH.showAlreadyExistsMessage();
        }

        if(string.equals("newUserCreated"))
        {
            VH.showSuccesfullLogin();
        }

        if(string.equals("incorrectLogin"))
        {
            VH.showIncorrectLogin();
        }

    }


    private void timeOutCheck()
    {
        webConnection.sendToServer("check");
        gameView.clearMenu();
    }


    private void timeOutFold()
    {
        webConnection.sendToServer("fold");
        gameView.clearMenu();
    }



    /**
     * Checks if the player still wants to play. Sends "continue" to the server if yes, and closes
     * the connection if no.
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





    class WindowsClose implements EventHandler<WindowEvent> {

        @Override
        public void handle(WindowEvent e)
        {
            e.consume();
            System.exit(2);
        }

    }
}


