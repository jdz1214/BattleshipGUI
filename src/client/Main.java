package client;

import commoncore.*;
import gui.GUIController;
import gui.GameController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import static commoncore.GameObject.GameObjectType.QUIT;


public class Main extends Application {
    private Stage stage;
    private Scene guiScene;
    private Scene loginScene;
    private Scene gameScene;
    private LoginController lc;
    private GUIController guiController;
    private GameController gameController;
    private String address;
    private int port;
    private Socket s;
    private ObjectOutputStream os;
    private ObjectInputStream is;
    private Boolean ready; // Used for kick action to chain the disabling of login until GUI has restarted.
    private Boolean connected;
    private Boolean loggedIn;
    private Boolean inGame;
    private int loginAttempts;
    private String username;
    private ObservableList<String> usernameList;
    SimpleListProperty<String> slp;

    public Main() {
        address = "localhost";
        port = 1500;
        connected = false;
        loggedIn = false;
        inGame = false;
        loginAttempts = 0;
        usernameList = FXCollections.observableArrayList();
        slp = new SimpleListProperty<>(usernameList);
        ready = false;
    }

	@Override
	public void start(Stage primaryStage) {
        stage = primaryStage;
        handleShutdown();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/Login.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {e.printStackTrace();}
        lc = loader.getController();
        lc.init(this);
        assert root != null;
        loginScene = new Scene(root);
        loginScene.getStylesheets().add("/client/application.css");
        stage.setScene(loginScene);
        stage.show();
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
                        if (t.getTransmissionType() == Transmission.TransmissionType.LOGINOBJECT) {
                            LoginObject lo = t.getLoginObject();
                            if (lo.getLoginSuccess()) {
                                loggedIn = true;
                                username = lo.getUsername();
                                startGUI();
                            } else {
                                Platform.runLater(() -> lc.setLblMessage("Login attempt " + loginAttempts + "/5 was unsuccessful."));
                            }
                        }
                    } else if (inGame) {
                        switch (t.getTransmissionType()) {
                            case CHATMESSAGE:
                                String msg = t.getChatMessage();
                                Platform.runLater(() -> gameController.updateChat(msg));
                                break;
                            case GAMEOBJECT:
                                GameObject go = t.getGameObject();
                                switch (go.getGameObjectType()) {
                                    case QUIT:
                                        System.out.println("Received QUIT object.");
                                        if (inGame) {
                                            Platform.runLater(() -> gameController.disableChat());
                                            Platform.runLater(() -> gameController.updateChat("[" + go.getUserWhoQuit() + " has quit.]"));
                                        }
                                        endGame();
                                        break;
                                    case GAMESTATE:
                                        Game.Gamestate gs = go.getGamestate();
                                        switch (gs) {
                                            case youAreUp:
                                                //TODO set attack board editable
                                                break;

                                            case youAreNotUp:
                                                //TODO set attack board uneditable
                                                break;
                                        }
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
                                    case LOGOUT:
                                        logout();
                                        break;
                                    case LOGIN:
                                        System.out.println("Error: Received login obj but already logged in.");
                                        break;
                                    case KICK:
                                        System.out.println("[Kicked from server]");
                                        logout();
                                        while(!ready) {
                                            Thread.sleep(10);
                                        }
                                        Platform.runLater(() -> lc.disableLogin());
                                        for (int i = 30; i > 0; i--) {
                                            final int iFin = i;
                                            Platform.runLater(() -> lc.lblMessage.setText("You were kicked. [" + iFin + "]"));
                                            try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
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
                } catch (SocketException soe) {System.out.println("Client disconnected via closing socket.");}
                catch (EOFException eof) {System.out.println("Disconnected from the server."); logout(); break;}
                catch (Exception e) {System.out.println("Error receiving transmission."); e.printStackTrace();}
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
            if (connected && loggedIn) {
                stage.getScene().getWindow().hide();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/gui.fxml"));
                Parent root = null;
                try {
                    root = loader.load();
                } catch (IOException e) {e.printStackTrace();}
                guiController = loader.getController();
                guiController.init(this);
                assert root != null;
                guiScene = new Scene(root);
                stage.setScene(guiScene);
                stage.show();
            }
        };
        Platform.runLater(stgui);
    }

    public void showGUI() {
        Runnable stgui = () -> {
            guiController.updateInfoLabel("");
            inGame = false;
            try {
                os.writeObject(new Transmission(new GameObject(QUIT, username)));
                os.flush();
            } catch (IOException e) {e.printStackTrace();}
            stage.getScene().getWindow().hide();

            if (connected && loggedIn) {
                stage.setScene(guiScene);
                stage.show();
            } else {
                showLogin();
            }
        };
        Platform.runLater(stgui);
    }

    private void showLogin() {
        Runnable shLogin = () -> {
            stage.getScene().getWindow().hide();
            loggedIn = false;
            if (connected) {
                lc.enableLogin();
            }
            stage.setScene(loginScene);
            stage.show();
        };
        Platform.runLater(shLogin);
    }

    void logout() {
        try {
            Platform.runLater(() -> {
                stage.getScene().getWindow().hide();
                guiController.updateChat("[" + username + " logged out]");
            });
            LoginObject lo = new LoginObject();
            lo.setType(LoginObject.Type.LOGOUT);

            os.writeObject(new Transmission(lo));
            os.flush();
        } catch (IOException e) {e.printStackTrace();}
        loggedIn = false;
        username = "";
        loginAttempts = 0;
        Platform.runLater(() -> {
            try {
                showLogin();
            } catch (Exception e) {System.out.println("Error restarting login gui");}
        });
    }

    private void handleShutdown() {
        stage.setOnCloseRequest(t -> Platform.exit());
    }

	private void startGame(String opponentUsername) {
        inGame = true;
	    Runnable sg = () -> {
	        if (connected && loggedIn) {
                stage.getScene().getWindow().hide();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/game.fxml"));
                Parent root = null;
                try {
                    root = loader.load();
                    gameScene = new Scene(root);
                } catch (IOException e) {e.printStackTrace();}
                gameController = loader.getController();
                gameController.init(this, opponentUsername);
                stage.setScene(gameScene);
                stage.show();
            }
        };
        Platform.runLater(sg);
    }

    void login(String user, String pass) {
        Runnable lgin = () -> {
            System.out.println("Received call to log in. Attempts = " + loginAttempts);
            if (loginAttempts < 5) {
                LoginObject lo = new LoginObject(true, user, pass);
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
                lc.disableLogin();
                connected = false;
                try {
                    is.close();
                    os.close();
                    s.shutdownInput();
                    s.shutdownOutput();
                    s.close();
                } catch (Exception e) {
                    System.out.println("Error in shutdown after too many incorrect loginAttempts.");
                }
            }
        };
        Platform.runLater(lgin);
    }

    public void send(String msg) {
        if (msg.length() > 0) {
            try {
                os.writeObject(new Transmission(username + ": " + msg));
                os.flush();
            } catch (Exception e) {
                Platform.runLater(() -> lc.setLblMessage("Error sending message."));}
        }
    }

    private void send(Transmission t) {
        try {
            os.writeObject(t);
            os.flush();
        } catch (Exception e) {System.out.println("Error sending transmission object");}
    }

    void listPlayers() {
        send(new Transmission(new ServerRequestObject(ServerRequestObject.ServerRequestObjectType.CLIENTREQUEST)));
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
            os.writeObject(new Transmission(new GameObject(opponentUsername)));
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Client Main initiated Watchtower newGame.");
        startGame(opponentUsername);

    }

    private void confirmRequest(String opponentUsername) {
        //TODO
        //Create dialog box that asks for user confirmation of new game, and includes opponent's username.
        Platform.runLater(() -> guiController.confirmRequest(opponentUsername));

    }

    private void endGame() {
        Platform.runLater(() -> gameController.disableChat());
        inGame = false;
    }

    public String getUsername() {
        return username;
    }
	
	public static void main(String[] args) {
		launch(args);
	}
}