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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main extends Application {
    private Stage stage;
    private LoginController lc;
    private GUIController gc;
    private GameController gameController;
    private ExecutorService ex;
    private String address;
    private int port;
    private Socket s;
    private ObjectOutputStream os;
    private ObjectInputStream is;
    private Boolean ready; // Used for kick action to chain the disabling of login until GUI has restarted.
    private Boolean connected;
    private Boolean loggedIn;
    private Boolean inGame;
    private int attempts;
    private String username;
    private Scene loginScene;
    private ObservableList<String> usernameList;
    public SimpleListProperty<String> slp;

    public Main() {
        address = "localhost";
        port = 1500;
        ex = Executors.newFixedThreadPool(10);
        connected = false;
        loggedIn = false;
        inGame = false;
        attempts = 0;
        usernameList = FXCollections.observableArrayList();
        slp = new SimpleListProperty<>(usernameList);
        ready = false;
    }

	@Override
	public void start(Stage primaryStage) throws Exception {
        ex = Executors.newFixedThreadPool(10);
        stage = primaryStage;
        handleShutdown();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/Login.fxml"));
        Parent root = loader.load();
        lc = loader.getController();
        lc.init(this);
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
            System.out.println("Connected to Watchtower.");
        } catch (Exception e) {connected = false; System.out.println("Error establishing initial server connection.");}
        if (connected) {
            Platform.runLater(() -> lc.enableLogin());
        } else {
            lc.setLblMessage("Unable to reach the server");
        }
    }

    private void handleShutdown() {
        stage.setOnCloseRequest(t -> {
            ex.shutdownNow();
            Platform.exit();
        });
    }

    private void startLogin() throws Exception {
        stage.getScene().getWindow().hide();
        //getConnection();
        stage.setScene(loginScene);
        stage.show();
        //receive();
        ready = true;
    }
	
	public void startGUI() {
        Runnable stgui = () -> {
            if (connected && loggedIn) {
                try {
                stage.getScene().getWindow().hide();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/gui.fxml"));
                Parent parent = loader.load();
                gc = null;
                gc = loader.getController();
                gc.init(this);
                Scene scene = new Scene(parent);
                stage.setScene(scene);
                stage.show();
                } catch (IOException e) {e.printStackTrace();}
            }
        };
        Platform.runLater(stgui);
	}

	private void startGame(String opponentUsername) {
        inGame = true;
	    Runnable sg = () -> {
	        if (connected && loggedIn) {
	            try {
	                stage.getScene().getWindow().hide();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/game.fxml"));
                    Parent parent = loader.load();
                    gameController = loader.getController();
                    gameController.init(this, opponentUsername);
                    Scene scene = new Scene(parent);
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {System.out.println("Error setting up game GUI.");}
            }
        };
        Platform.runLater(sg);
    }

    void login(String user, String pass) {
        Runnable lgin = () -> {
            System.out.println("Received call to log in. Attempts = " + attempts);
            if (attempts < 5) {
                LoginObject lo = new LoginObject(true, user, pass);
                Transmission t = new Transmission(lo);
                try {
                    os.writeObject(t);
                    os.flush();
                } catch (Exception e) {
                    System.out.println("Error sending login attempt to server.");
                }
                attempts++;
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
                    System.out.println("Error in shutdown after too many incorrect attempts.");
                }
            }
        };
        Platform.runLater(lgin);
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
                                Boolean successful = lo.getLoginSuccess();
                                if (successful) {
                                    loggedIn = true;
                                    username = lo.getUsername();

                                            try {
                                                startGUI();
                                            } catch (Exception e) {System.out.println("Error starting client GUI.");}
                                } else {
                                    Platform.runLater(() -> lc.setLblMessage("Login attempt " + attempts + "/5 unsuccessful."));
                                }
                            }
                    } else if(inGame) {
                        switch (t.getTransmissionType()) {
                            case CHATMESSAGE:
                                String msg = t.getChatMessage();
                                gc.updateChat(msg);
                            case GAMEOBJECT:
                                GameObject go = t.getGameObject();
                                switch (go.getGameObjectType()) {
                                    case GAMESTATE:
                                        Game.Gamestate gs = go.getGamestate();
                                        switch (gs) {
                                            case preGame:
                                                startGame(go.getOpponentUsername());
                                                break;

                                            case youAreUp:
                                                //TODO set attack board editable
                                                break;

                                            case youAreNotUp:
                                                //TODO set attack board uneditable
                                                break;
                                        }
                                        break;
                                }
                            }
                        } else { // Lobby activity
                            switch (t.getTransmissionType()) {
                                case CHATMESSAGE:
                                    String msg = t.getChatMessage();
                                    if (msg.length() > 0) {
                                        gc.updateChat(msg);
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
                                    }
                                    break;
                            }
                        }
                    } catch (SocketException soe) {System.out.println("Client disconnected via closing socket.");}
                      catch (EOFException eof) {System.out.println("Disconnected from the server."); logout(); break;}
                      catch (Exception e) {System.out.println("Error receiving transmission."); e.printStackTrace();}
            }
        };

        Thread r = new Thread(receive);
        r.setDaemon(true);
        r.start();
        System.out.println("Started receive thread.");
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

    public void listPlayers() {
        send(new Transmission(new ServerRequestObject(ServerRequestObject.ServerRequestObjectType.CLIENTREQUEST)));
    }

    public void logout() {
        try {
            Platform.runLater(() -> {
                stage.getScene().getWindow().hide();
                gc.updateChat("[" + username + " logged out]");
            });
            LoginObject lo = new LoginObject();
            lo.setType(LoginObject.Type.LOGOUT);

            os.writeObject(new Transmission(lo));
            os.flush();
        } catch (IOException e) {e.printStackTrace();}
        //ex.shutdownNow();
        loggedIn = false;
        username = "";
        attempts = 0;
        Platform.runLater(() -> {
            try {
                startLogin();
            } catch (Exception e) {System.out.println("Error restarting login gui");}
        });
    }

    public void gameRequest(String otherPlayerUsername) {
        try {
            os.writeObject(new Transmission(new GameObject(new GameRequest(otherPlayerUsername))));
            os.flush();
        } catch (IOException e) {e.printStackTrace();}
        System.out.println("Sent gameRequest.");
    }

    public void newGame(String opponentUsername) {
        try {
            os.writeObject(new Transmission(new GameObject(opponentUsername)));
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Client Main initiated Watchtower newGame.");
    }

    public void confirmRequest(String opponentUsername) {
        //TODO
        //Create dialog box that asks for user confirmation of new game, and includes opponent's username.
        Platform.runLater(() -> gc.confirmRequest(opponentUsername));

    }

    public String getUsername() {
        return username;
    }
	
	public static void main(String[] args) {
		launch(args);
	}
}