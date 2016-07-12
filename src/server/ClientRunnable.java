package server;

import commoncore.GameObject;
import commoncore.LoginObject;
import commoncore.ServerRequestObject;
import commoncore.Transmission;
import javafx.application.Platform;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by jdz on 7/8/16.
 */
public class ClientRunnable implements Runnable {
    private Socket s;
    private String username;
    private	String chatMessage;
    private Boolean connected;
    private SimpleDateFormat sdf;
    private Watchtower.Status status;
    private	Boolean loggedIn;
    protected ObjectInputStream is;
    private ObjectOutputStream os;
    private int attempts;
    private Watchtower w;



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
    public void init(Watchtower w) {
        this.w = w;
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
            } catch (IOException e) {
                e.printStackTrace();
            }

            while(connected) {
                try {
                    Transmission t = (Transmission) is.readObject();
                    if (loggedIn) {
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
                                GameObject go = t.getGameObject();
                                switch (go.getGameObjectType()) {
                                    case ATTACK:
                                        // TODO
                                        break;
                                    case ATTACKRESULT:
                                        // TODO
                                        break;
                                    case BOARD:
                                        // TODO
                                        break;
                                    case HISTORY:
                                        // TODO
                                        break;
                                    case GAMESTATE:
                                        // TODO
                                        break;
                                    case GAMEREQUEST:
                                        // TODO
                                        break;
                                }

                            case SERVERREQUESTOBJECT:
                                ServerRequestObject sro = t.getServerRequestObject();
                                ServerRequestObject.ServerRequestObjectType sroType = sro.getServerRequestObjectType();
                                switch (sroType) {
                                    case CLIENTREQUEST:
                                        ArrayList<String> lobbyList = new ArrayList<>();
                                        for (int i = 0; i < w.clientList.size(); i++) {
                                            String uname = ((ClientRunnable) w.clientList.get(i)).getUsername();
                                            lobbyList.add(uname);
                                            System.out.println("connected client added: " + uname);
                                        }
                                        ServerRequestObject listServerObject = new ServerRequestObject(lobbyList);
                                        listServerObject.setServerRequestObjectType(ServerRequestObject.ServerRequestObjectType.LOBBYLIST);
                                        t = new Transmission(listServerObject);
                                        try {
                                            os.writeObject(t);
                                            os.flush();
                                            System.out.println("Wrote server list to client.");
                                        } catch (IOException e1) {
                                            System.out.println("Error sending client list");
                                        }
                                        break;
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
                                    ClientRunnable cr = w.clientList.get(i);
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
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                w.clientList.add(ClientRunnable.this);
                                                w.usernameList.add(username);
                                                w.clientOs.add(os);
                                            }
                                        }
                                );
                            } else {
                                attempts++;
                                lo = new LoginObject();
                                lo.setType(LoginObject.Type.LOGIN);
                                lo.setLoginSuccess(false);
                                os.writeObject(new Transmission(lo));
                                os.flush();
                                System.out.println("Wrote new failed login object @ line 180.");
                            }
                        }
                    }
                } catch (EOFException a) {System.out.println("Client disconnnected.");
                    try {
                        logout();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    connected = false; break;}
                catch (Exception e) {
                    System.out.println("Error in ClientHandler reading input stream.");
                    e.printStackTrace();
                }
            }
        };

        Thread rt = new Thread(r);
        rt.setDaemon(true);
        rt.start();
        System.out.println("ClientRunnable Started receive thread.");
    }

    void logout() throws IOException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                w.clientList.remove(ClientRunnable.this);
                w.usernameList.remove(username);
                w.clientOs.remove(os);
            }
        });
        status = Watchtower.Status.UNAVAILABLE;
        System.out.println("Client logging out");
        loggedIn = false;
        try {
            s.shutdownInput();
        } catch (Exception e) {}
        LoginObject lo = new LoginObject();
        Transmission t;
        lo.setType(LoginObject.Type.LOGIN);
        t = new Transmission(lo);
        try {
            os.writeObject(t);
            os.flush();
        } catch (Exception e) {}
        System.out.println("Executed logout transmission.");
        is.close();
        os.close();
        s.close();
        System.out.println(username + " logged out at " + sdf.format(new Date()));
        loggedIn = false;
        connected = false;
    }

    public String getUsername() {
        return username;
    }

    public ObjectOutputStream getObjectOutputStream () {
        return os;
    }

    public ObjectInputStream getObjectInputStream () {
        return is;
    }

    public Socket getSocket() {
        return s;
    }
}