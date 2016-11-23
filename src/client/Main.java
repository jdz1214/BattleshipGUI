package client;

import commoncore.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;


public class Main extends Application {
    private Stage stage;
    @Nullable
    private Scene guiScene;
    @Nullable
    private Scene loginScene;
    @Nullable
    private Scene gameScene;
    private LoginController lc;
    private GUIController guiController;
    private GameController gameController;
    private final String address;
    private final int port;
    private Socket s;
    private ObjectOutputStream os;
    private ObjectInputStream is;
    private Boolean ready; // Used for kick action to chain the disabling of login until GUI has restarted.
    private Boolean connected;
    private Boolean loggedIn;
    private Boolean inGame;
    private Boolean gameOver;
    private int loginAttempts;
    private String username;
    private ObservableList<String> usernameList;
    SimpleListProperty<String> slp;
    private Fleet fleet;

    public Main() {
        address = "battleship.jdzcode.com";
        port = 1500;
        connected = false;
        loggedIn = false;
        inGame = false;
        gameOver = false;
        loginAttempts = 0;
        usernameList = FXCollections.observableArrayList();
        slp = new SimpleListProperty<>(usernameList);
        ready = false;
    }

	@Override
	public void start(Stage primaryStage) {
        try {
            stage = primaryStage;
            stage.setTitle("Battleship");
            handleShutdown();
    //        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/Login.fxml"));
    //        Parent root = null;
    //        loader.setRoot(root);
    //        try {
    //            root = loader.load();
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //        }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/Login.fxml"));
            Parent root = loader.load();
            lc = loader.getController();
            lc.init(this);
            assert root != null;
            loginScene = new Scene(root);
            loginScene.getStylesheets().add("/client/application.css");
            stage.setScene(loginScene);
            stage.setTitle("Login:Battleship");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.fleet = new Fleet();
        getConnection();
        receive();
	}

    private void getConnection() {
        try {
            s = new Socket(address, port);
            os = new ObjectOutputStream(s.getOutputStream());
            is = new ObjectInputStream(s.getInputStream());
            connected = true;
        } catch (Exception e) {connected = false; System.out.println("Error establishing initial server connection.");}
        if (connected) {
            System.out.println("Connected to Watchtower.");
            lc.setLblMessage("Connected to Watchtower. Please log in.");
            lc.enableLogin();
        } else {
            lc.setLblMessage("Unable to reach the server.");
        }
    }

    private void receive() {

        Runnable receive = () -> {
            Transmission t = new Transmission();
            while (connected) {
                try {
                    t = (Transmission) is.readObject();
                    if (!loggedIn) {
                        if (t.getTransmissionType().equals(Transmission.TransmissionType.LOGINOBJECT)) {
                            LoginObject lo = t.getLoginObject();
                            switch (lo.getType()) {
                                case LOGINREQUEST:
                                    System.out.println("Invalid 'LOGINREQUEST' received in Main's !loggedin area. Expected 'LOGINRESULT'.");
                                    break;
                                case LOGINRESULT:
                                    Boolean loginSuccess = lo.getLoginSuccess();
                                    assert loginSuccess != null;
                                    System.out.println(loginSuccess);
                                    if (loginSuccess) {
                                        loggedIn = true;
                                        username = lo.getUsername();
                                        System.out.println(username + " successfully logged in.");
                                        startGUI();
                                    } else {
                                        Platform.runLater(() -> lc.setLblMessage("Login attempt " + loginAttempts + "/5 was unsuccessful."));
                                    }
                                    break;
                                case LOGOUTREQUEST:
                                    System.out.println("Invalid logout request received in Main's !loggedIn area.");
                                    break;
                                case KICK:
                                    System.out.println("Invalid kick requeste received in Main's !loggedIn area.");
                                    break;
                            }
                        }
                    } else {
                        if (inGame) {
                            switch (t.getTransmissionType()) {
                                case CHATMESSAGE:
                                    String msg = t.getChatMessage();
                                    Platform.runLater(() -> gameController.updateChat(msg));
                                    break;
                                case LOGINOBJECT:
                                    break;
                                case SERVERREQUESTOBJECT:
                                    break;
                                case GAMEOBJECT:
                                    GameObject go = t.getGameObject();
                                    assert go != null;
                                    switch (go.getGameObjectType()) {
                                        case QUIT:
                                            System.out.println("Received QUIT object.");
                                            if (inGame) {
                                                Platform.runLater(() -> {
                                                    gameController.disableChat();
                                                    gameController.disableGrid();
                                                    gameController.updateChat("[" + go.getUserWhoQuit() + " has quit.]");
                                                });
                                            }
                                            endGame();
                                            break;
                                        case ATTACK:
                                            System.out.println("Received Attack and transmitted AttackResult.");
                                            os.writeObject(new Transmission(new GameObject(gameController.evaluateAttackReceived(go.getAttack()))));
                                            os.flush();
                                            break;
                                        case ATTACKRESULT:
                                            System.out.println("Received AttackResult.");
                                            Platform.runLater(() -> gameController.processAttackResult(go.getAttackResult()));
                                            break;
                                        case BOARD:
                                            break;
                                        case HISTORY:
                                            break;
                                        case GAMEOVER:
                                            GameOver gameOver = go.getGameOver();
                                            assert gameOver != null;
                                            String winnerUserName = gameOver.getWinnerUsername();
                                            assert winnerUserName != null;
                                            assert username != null;
                                            assert username.length() > 0;
                                            System.out.println(username);
                                            System.out.println(winnerUserName);
                                            if (winnerUserName.equals(getUsername())) {
                                                Platform.runLater(() -> gameController.iWon());
                                            } else {
                                                Platform.runLater(() -> gameController.iLost());
                                            }
                                            break;
                                        case GAMESTATE:
                                            Game.Gamestate gs = go.getGamestate();
                                            assert gs != null;
                                            switch (gs) {
                                                case youAreUp:
                                                    if (!this.gameOver) {
                                                        Platform.runLater(() -> gameController.youAreUp());
                                                    }
                                                    break;

                                                case youAreNotUp:
                                                    if (!this.gameOver) {
                                                        Platform.runLater(() -> gameController.youAreNotUp());
                                                    }
                                                    break;

                                                case youWon:
                                                    Platform.runLater(() -> gameController.iWon());
                                                    break;

                                                case youLost:
                                                    Platform.runLater(() -> gameController.iLost());
                                                    break;
                                            }
                                            break;
                                        case GAMEREQUEST:
                                            break;
                                        case GAMEBOARD:
                                            break;
                                        case NEWGAME:
                                            break;
                                    }
                                    break;
                            }
                        } else { // Lobby activity
                            switch (t.getTransmissionType()) {
                                case CHATMESSAGE:
                                    String msg = t.getChatMessage();
                                    if (msg.length() > 0) {
                                        Platform.runLater(() -> guiController.updateChat(msg));
                                    }
                                    break;
                                case LOGINOBJECT:
                                    LoginObject lo = t.getLoginObject();
                                    switch (lo.getType()) {
                                        case LOGOUTREQUEST:
                                            logout();
                                            break;
                                        case LOGINRESULT:
                                            System.out.println("Error: Received 'LOGINRESULT' object, but already logged in.");
                                            break;
                                        case KICK:
                                            System.out.println("[Kicked from server]");
                                            logout();
                                            while (!ready) {
                                                Thread.sleep(10);
                                            }
                                            Platform.runLater(() -> lc.disableLogin());
                                            for (int i = 30; i > 0; i--) {
                                                final int iFin = i;
                                                Platform.runLater(() -> lc.lblMessage.setText("You were kicked. [" + iFin + "]"));
                                                try {
                                                    Thread.sleep(1000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            Platform.runLater(() -> {
                                                lc.setLblMessage("");
                                                lc.enableLogin();
                                            });
                                            break;
                                    }
                                    break;
                                case SERVERREQUESTOBJECT:
                                    ServerRequestObject sro = t.getServerRequestObject();
                                    switch (sro.getServerRequestObjectType()) {
                                        case LOBBYLIST:
                                            usernameList = FXCollections.observableArrayList(sro.getLobbyList());
                                            Platform.runLater(() -> slp.set(usernameList));
                                    }
                                    break;
                                case GAMEOBJECT:
                                    System.out.println("Main received GameObject.");
                                    GameObject go = t.getGameObject();
                                    GameObject.GameObjectType gto = go.getGameObjectType();
                                    switch (gto) {
                                        case GAMEREQUEST:
                                            confirmRequest(go.getGameRequest().getUsername());
                                            break;
                                        case GAMESTATE:
                                            if (go.getGamestate() == Game.Gamestate.preGame) {
                                                startGame(go.getOpponentUsername());
                                            }
                                            break;
                                    }
                                    break;
                            }
                        }
                    }
                } catch (SocketException soe) {System.out.println("Client disconnected via closing socket."); break;}
                catch (EOFException eof) {System.out.println("Disconnected from the server."); logout(); break;}
                catch (Exception e) {System.out.println("Error receiving transmission."); e.printStackTrace(); break;}
            }
        };

        Thread t = new Thread(receive);
        t.setDaemon(true);
        t.start();
        //
        System.out.println("Started receive thread.");
    }

    private void startGUI() {
        Runnable stgui = () -> {
            if (connected && loggedIn) { //Possible issue with connected variable on subsequent login attempts in same session.
                System.out.println("Starting GUI");
                stage.getScene().getWindow().hide();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/gui.fxml"));
                Parent root = null;
                try {
                    root = loader.load();
                } catch (IOException e) {e.printStackTrace();}
                guiController = loader.getController();
                guiController.init(this);
                assert root != null;
                guiScene = new Scene(root);
                stage.setScene(guiScene);
                stage.setTitle("Game:Battleship");
                stage.show();
            }
        };
        Platform.runLater(stgui);
    }

    void showGUI() {
        Runnable shGUI = () -> {
            guiController.updateInfoLabel("");
            inGame = false;
            try {
                os.writeObject(new Transmission(new GameObject(getUsername())));
                os.flush();
            } catch (IOException e) {e.printStackTrace();}
            stage.getScene().getWindow().hide();

            if (connected && loggedIn) {
                listPlayers();
                stage.setScene(guiScene);
                stage.setTitle("Game:Battleship");
                stage.show();
            } else {
                showLogin();
            }
        };
        Platform.runLater(shGUI);
    }

    private void showLogin() {
        Runnable shLogin = () -> {
            stage.getScene().getWindow().hide();
            loggedIn = false;
            if (connected) {
                lc.enableLogin();
            }
            stage.setScene(loginScene);
            stage.setTitle("Login:Battleship");
            stage.show();
        };
        Platform.runLater(shLogin);
    }

    void logout() {
        Platform.runLater(() -> {
            stage.getScene().getWindow().hide();
            guiController.updateChat("[" + username + " logged out]");
        });
        LoginObject lo = new LoginObject();
        lo.setType();

        try {
            os.writeObject(new Transmission(lo));
            os.flush();
        } catch (IOException ignored) {}
        username = "";
        loginAttempts = 0;
        showLogin();
    }

    private void handleShutdown() {
        stage.setOnCloseRequest(t -> Platform.exit());
    }

	private void startGame(String opponentUsername) {
        inGame = true;
        gameOver = false;
	    Runnable sg = () -> {
	        if (connected && loggedIn) {
                stage.getScene().getWindow().hide();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/game.fxml"));
                Parent root = null;
                try {
                    root = loader.load();
                    gameScene = new Scene(root);
                } catch (IOException e) {e.printStackTrace();}
                gameController = loader.getController();
                gameController.init(this, opponentUsername, this.fleet);
                stage.setScene(gameScene);
                stage.show();
            }
        };
        Platform.runLater(sg);
    }

    void login(String user, String pass) {
        System.out.println("Received call to log in. Attempts = " + loginAttempts);
        if (loginAttempts < 5) {
            LoginObject lo = new LoginObject(user, pass);
            Transmission t = new Transmission(lo);
            try {
                os.writeObject(t);
                os.flush();
            } catch (Exception e) {
                System.out.println("Error sending login attempt to server.");
            }
            loginAttempts++;
        } else {
            System.out.println("You have failed to log in too many times.");
            Platform.runLater(() -> lc.disableLogin());
            connected = false;
            try {
                is.close();
                os.close();
                s.shutdownInput();
                s.shutdownOutput();
                s.close();
            } catch (Exception ignored) {
                System.out.println("Error in shutdown after too many incorrect loginAttempts.");
            }
        }
    }

    void send(@NotNull String msg) {
        if (msg.length() > 0) {
            try {
                os.writeObject(new Transmission(username + ": " + msg));
                os.flush();
            } catch (Exception e) {
                Platform.runLater(() -> lc.setLblMessage("Error sending message."));}
        }
    }

    void send(Transmission t) {
        assert t != null;
        System.out.println("Sending transmission through Main's 'send' method.");
        try {
            os.writeObject(t);
            os.flush();
        } catch (Exception e) {System.out.println("Error sending transmission object");}
    }

    private void listPlayers() {
        send(new Transmission(new ServerRequestObject()));
    }

    void gameRequest(String otherPlayerUsername) {
        try {
            os.writeObject(new Transmission(new GameObject(new GameRequest(otherPlayerUsername))));
            os.flush();
            Platform.runLater( () -> guiController.updateInfoLabel("Game request sent."));
        } catch (IOException e) {e.printStackTrace();}
        System.out.println("Sent gameRequest.");
    }

    void newGame(String opponentUsername) {
        try {
            os.writeObject(new Transmission(new GameObject(true, opponentUsername)));
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Client Main initiated Watchtower newGame.");
        startGame(opponentUsername);
    }

    Stage getStage() {
        return stage;
    }

    private void confirmRequest(String opponentUsername) {
        Platform.runLater(() -> guiController.confirmRequest(opponentUsername));

    }

    private void endGame() {
        Platform.runLater(() -> gameController.disableChat());
        inGame = false;
    }

    Boolean getGameOver() {
        return gameOver;
    }

    String getUsername() {
        return username;
    }
	
	public static void main(String[] args) {
		launch(args);
	}

    void setGameOver() {
        this.gameOver = true;
    }
}