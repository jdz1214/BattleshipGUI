package client;

import commoncore.LoginObject;
import commoncore.ServerRequestObject;
import commoncore.Transmission;
import gui.GUIController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main extends Application {
    private Stage stage;
    private LoginController lc;
    private GUIController gc;
    private ExecutorService ex;
    private String address;
    private int port;
    private Socket s;
    private ObjectOutputStream os;
    private ObjectInputStream is;
    private Boolean connected;
    private Boolean loggedIn;
    private int attempts;
    private String username;
    private Scene loginScene;

    public Main() {
        address = "localhost";
        port = 1500;
        ex = Executors.newFixedThreadPool(10);
        connected = false;
        loggedIn = false;
        attempts = 0;
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
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    lc.enableLogin();
                }
            });
        } else {
            lc.setLblMessage("Unable to reach the server");
        }
    }

    private void handleShutdown() {
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                ex.shutdownNow();
                Platform.exit();
            }
        });
    }

    private void startLogin() throws Exception {
        stage.getScene().getWindow().hide();
        getConnection();
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/Login.fxml"));
        //Parent root = loader.load();
        //lc = loader.getController();
        //lc.init(this);
        stage.setScene(loginScene);
        stage.show();
        receive();
    }
	
	private void startGUI() {
        Runnable stgui = () -> {
            if (connected && loggedIn) {
                try {
                stage.getScene().getWindow().hide();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI.fxml"));
                Parent parent = loader.load();
                gc = loader.getController();
                gc.init(this);
                Scene scene = new Scene(parent);
                stage.setScene(scene);
                stage.show();
                } catch (IOException e) {System.out.println("Error loading GUI parent.");}
            }
        };
        Platform.runLater(stgui);
	}

    protected void login(String user, String pass) {
        Runnable lgin = () -> {
            System.out.println("Received call to log in. Attempts = " + attempts);
            if (attempts < 5) {
                String u = user;
                String p = pass;
                LoginObject lo = new LoginObject(true, u, p);
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
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            lc.setLblMessage("Login attempt " + attempts + "/5 unsuccessful.");
                                        }
                                    });
                                }
                            }
                    } else {
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
                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                lc.setLblMessage("You were kicked.");
                                                gc.updateChat("[You have been kicked]");
                                            }
                                        });
                                        System.out.println("[Kicked from server]");
                                        logout();
                                }
                                break;

                            case SERVERREQUESTOBJECT:
                                ServerRequestObject sro = t.getServerRequestObject();
                                switch (sro.getServerRequestObjectType()) {
                                    case LOBBYLIST:
                                    ArrayList<String> lobbyList = sro.getLobbyList();
                                    gc.updateChat("Users currently connected to Watchtower: ");
                                    for (int i = 0; i < lobbyList.size(); i++) {
                                        gc.updateChat("<< " + lobbyList.get(i) + " >>");
                                    }
                                }
                                break;

                            case GAMEOBJECT:
                                //TODO
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
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        lc.setLblMessage("Error sending message.");
                    }
                });}
        }
    }

    public void send(Transmission t) {
        try {
            os.writeObject(t);
            os.flush();
        } catch (Exception e) {System.out.println("Error sending transmission object");}
    }

    public void listPlayers() {
        send(new Transmission(new ServerRequestObject(ServerRequestObject.ServerRequestObjectType.CLIENTREQUEST)));
    }

    public void logout() {

        Runnable lgout = () -> {
            try {
                stage.getScene().getWindow().hide();
                gc.updateChat("[" + username + " logged out]");
                LoginObject lo = new LoginObject();
                lo.setType(LoginObject.Type.LOGOUT);

                os.writeObject(new Transmission(lo));
                os.flush();
                is.close();
                os.close();
                s.close();
                ex.shutdownNow();
                connected = false;
                loggedIn = false;
                username = "";
                attempts = 0;
                try {
                    startLogin();
                } catch (Exception e) {System.out.println("Error restarting login gui");}
            } catch (Exception e) {
                System.out.println("Error closing connections during logout.");
            }


        };
        Platform.runLater(lgout);
    }

    public void gameRequest() {
        //TODO
    }

    public String getUsername() {
        return username;
    }
	
	public static void main(String[] args) {
		launch(args);
	}
}