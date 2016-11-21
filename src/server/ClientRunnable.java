package server;

import commoncore.*;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static commoncore.GameObject.GameObjectType.QUIT;


public class ClientRunnable implements Runnable {
    private Socket s;
    private String username;
    private Boolean connected;
    private SimpleDateFormat sdf;
    private Watchtower.Status status;
    private	Boolean loggedIn;
    private ObjectInputStream is;
    private ObjectOutputStream os;
    private int attempts;
    private Watchtower w;
    private ClientRunnable opponent;
    //Game variables
    private Game game;
    private Gameboard board;
    private Fleet fleet;
    private int playerNumber;
    private AttackResult attackResult;
    private Boolean myTurn = false;
    private Transmission t;



    //Constructor
    public ClientRunnable (Socket sock) throws Exception {
        loggedIn = false;
        attempts = 0;
        status = Watchtower.Status.UNAVAILABLE;
        username = "";
        sdf = new SimpleDateFormat("hh:mm:ss a MMM/dd/yyyy");
        s = sock;
        connected = true;
    }

    //Methods
    void init(Watchtower w) {
        this.w = w;
        w.usernameList.addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                ArrayList<String> temp = new ArrayList<>(w.usernameList);
                temp.forEach(System.out::println);
                Transmission t = new Transmission(new ServerRequestObject(temp));
                try {
                    os.writeObject(t);
                    os.flush();
                } catch (SocketException ignored) {}
                  catch (Exception e) {e.printStackTrace();}
            }
        });
    }

    @Override
    public void run() {
        receive();
    }

    private void receive() {
        Runnable r = () -> {
            try {
                os = new ObjectOutputStream (s.getOutputStream());
                is = new ObjectInputStream (s.getInputStream());
            } catch (IOException ignored) {}

            while(connected) {
                try {
                    t = (Transmission) is.readObject();
                    if (loggedIn) {
                        if (status == Watchtower.Status.INGAME) {
                            switch (t.getTransmissionType()) {
                                case CHATMESSAGE:
                                    assert t.getChatMessage().length() > 0;
                                    opponent.getObjectOutputStream().writeObject(new Transmission(t.getChatMessage()));
                                    opponent.getObjectOutputStream().flush();
                                    break;
                                case GAMEOBJECT:
                                    System.out.println("Received GameObject @ ClientRunnable.");
                                    GameObject go = t.getGameObject();
                                    switch (go.getGameObjectType()) {
                                        case ATTACK:
                                                Attack attack = go.getAttack();
                                                game.validateAttack(attack, this);
                                            break;
                                        case ATTACKRESULT:
                                            opponent.getObjectOutputStream().writeObject(new Transmission(new GameObject(go.getAttackResult())));
                                            opponent.getObjectOutputStream().flush();
                                            break;
                                        case BOARD:
                                            break;
                                        case HISTORY:
                                            break;
                                        case GAMEOVER:
                                            opponent.getObjectOutputStream().writeObject(new Transmission(new GameObject(go.getGameOver())));
                                            opponent.getObjectOutputStream().flush();
                                            game.setGamestate(Game.Gamestate.gameOver);
                                            break;
                                        case GAMESTATE:
                                            Game.Gamestate gs = go.getGamestate();
                                            switch (gs) {
                                                case youAreUp:
                                                    break;

                                                case youAreNotUp:
                                                    break;

                                                case gameOver:
                                                    break;

                                            }
                                            break;
                                        case QUIT:
                                            if (opponent != null) {
                                                opponent.endGame();
                                            }
                                            endGame();
                                            break;
                                    }
                            }
                        } else { // Not in game
                        switch (t.getTransmissionType()) {
                            case CHATMESSAGE:
                                String msg = t.getChatMessage();
                                if (msg.length() > 0) {
                                    w.broadcast(msg);
                                }
                                break;
                            case LOGINOBJECT:
                                LoginObject lo = t.getLoginObject();
                                switch (lo.getType()) {
                                    case LOGIN:
                                        break;
                                    case LOGOUT:
                                        logout();
                                        break;
                                }
                                break;
                            case GAMEOBJECT:
                                System.out.println("Received GameObject @ ClientRunnable.");
                                GameObject go = t.getGameObject();
                                switch (go.getGameObjectType()) {
                                    case GAMEREQUEST:
                                        System.out.println("Received gameRequest from Main.");
                                        String opponentUsername = go.getGameRequest().getUsername();
                                        ClientRunnable cr = w.getClientRunnable(opponentUsername);
                                        if (cr != null) {
                                            try {
                                                cr.getObjectOutputStream().writeObject(new Transmission(new GameObject((new GameRequest(username)))));
                                                cr.getObjectOutputStream().flush();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            System.out.println("Wrote opponent's Main a new gameRequest.");
                                            //Because it is from this username (and not from 'opponentUsername')
                                        } else {
                                            System.out.println("Error handling gamerequest transaction.");
                                        }
                                        break;
                                    case GAMEBOARD:
                                        break;
                                    case NEWGAME:
                                        ClientRunnable player1 = w.getClientRunnable(go.getOpponentUsername());
                                        w.newGame(player1, this);
                                        opponentUsername = player1.getUsername();
                                        break;
                                }
                                break;
                            case SERVERREQUESTOBJECT:
                                ServerRequestObject sro = t.getServerRequestObject();
                                ServerRequestObject.ServerRequestObjectType sroType = sro.getServerRequestObjectType();
                                switch (sroType) {
                                    case CLIENTREQUEST:
                                        ServerRequestObject listServerObject = new ServerRequestObject(new ArrayList<>(w.usernameList));
                                        try {
                                            os.writeObject(new Transmission(listServerObject));
                                            os.flush();
                                            System.out.println("Wrote server list to client.");
                                        } catch (IOException e1) {
                                            System.out.println("Error sending client list.");
                                        }
                                        break;
                                }
                            }
                        }
                    }
                    //// Login Section
                    if (!loggedIn && attempts >= 5) {
                        logout();
                    } else if (!loggedIn && attempts < 5) {
                        LoginObject lo = t.getLoginObject();
                        if (lo.getType() == LoginObject.Type.LOGIN) {
                            String username = lo.getUsername();
                            String password = lo.getPassword();
                            Boolean success = w.trySql(username, password);
                            Boolean unique = true;
                            if (w.clientList.size() > 0) {
                                for (int i = 0; i < w.clientList.size(); i++) {
                                    if (w.clientList.get(i).getUsername().equals(username)) {
                                        unique = false;
                                    }
                                }
                            }
                            if (success && unique) {
                                status = Watchtower.Status.AVAILABLE;
                                loggedIn = true;
                                this.username = username;
                                Thread.currentThread().setName(username);
                                lo.setLoginSuccess(true);
                                os.writeObject(new Transmission(lo));
                                os.flush();
                                System.out.println("Wrote new success object @ line 153.");
                                Platform.runLater(
                                        () -> {
                                            w.clientList.add(ClientRunnable.this);
                                            w.usernameList.add(username);
                                            w.clientOs.add(os);
                                        }
                                );
                            } else {
                                attempts++;
                                lo = new LoginObject();
                                lo.setType(LoginObject.Type.LOGIN);
                                lo.setLoginSuccess(false);
                                os.writeObject(new Transmission(lo));
                                os.flush();
                                System.out.println("Wrote 'unsuccessful login' object @ line 180.");
                            }
                        }
                    }
                }   catch (SocketException se) {System.out.println("Socket closed."); connected = false; logout(); break; }
                    catch (EOFException a) {System.out.println("Client disconnnected.");
                        logout();
                        connected = false; break;}
                        catch (Exception e) {
                            e.printStackTrace();
                        }
            }
        };

        Thread rt = new Thread(r);
        rt.setDaemon(true);
        rt.start();
        System.out.println("ClientRunnable Started receive thread.");
    }

    void logout() {
        Platform.runLater(() -> {
            w.clientOs.remove(os);
            w.usernameList.remove(username);
            w.clientList.remove(this);
        });
        status = Watchtower.Status.UNAVAILABLE;
        System.out.println("Client logging out");
        loggedIn = false;
        try {
            os.writeObject(new Transmission(new LoginObject(LoginObject.Type.LOGOUT)));
            os.flush();
        } catch (Exception ignored) {}
        System.out.println("Executed logout transmission.");
        System.out.println(username + " logged out at " + sdf.format(new Date()));
        Thread.currentThread().interrupt();
    }

    public String getUsername() {
        return username;
    }

    public ObjectOutputStream getObjectOutputStream () {
        return os;
    }

    //Game methods
    public void enterGameMode(Game game, String opponentUsername, int playerNumber) {
        System.out.println(opponentUsername + " ClientRunnable line 283");
        w.clientOs.remove(os);
        this.game = game;
        this.playerNumber = playerNumber;
        this.status = Watchtower.Status.INGAME;
        opponent = game.getClientRunnable(opponentUsername);
        try {
            GameObject go = new GameObject(Game.Gamestate.preGame);
            go.setOpponentUsername(opponentUsername);
            os.writeObject(new Transmission(go));
            os.flush();
        } catch (IOException e) {System.out.println("Error sending client's preGame transmission.");}
    }

    public Fleet getFleet() {
        return fleet;
    }

    public void setMyTurn(Boolean up) {
        myTurn = up;
    }

    public void notifyUp(Boolean isUp) {
        myTurn = isUp;
        if (myTurn) {
            try {
                os.writeObject(new Transmission(new GameObject(Game.Gamestate.youAreUp)));
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                os.writeObject(new Transmission(new GameObject(Game.Gamestate.youAreNotUp)));
                os.flush();
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    public void notifyGameOver(Game.Gamestate gamestate) {
        try {
            os.writeObject(new Transmission(new GameObject(gamestate)));
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void endGame() {
        try {
            os.writeObject(new Transmission(new GameObject(QUIT, username)));
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        w.clientOs.add(os);
        status = Watchtower.Status.AVAILABLE;
        game = null;
        opponent = null;
    }
}