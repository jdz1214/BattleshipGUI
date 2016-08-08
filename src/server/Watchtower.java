package server;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import commoncore.Game;
import commoncore.LoginObject;
import commoncore.Transmission;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Watchtower extends Application {
    private ExecutorService lobby;
	private ExecutorService gamePool;
	private ServerSocket ss;
	private Boolean atWar;
    ObservableList<String> usernameList;
	ObservableList<ClientRunnable> clientList;
    ArrayList<ObjectOutputStream> clientOs;
	private SimpleDateFormat sdf;
    private Stage stage;
    private ServerController sc;
    private SimpleListProperty<String> lp;
    private Connection connect;

	//Constructor
	public Watchtower () {
        int port = 1500;
		clientList = FXCollections.observableList(new ArrayList<ClientRunnable>());
        usernameList = FXCollections.observableList(new ArrayList<String>());
        clientOs = new ArrayList<>();
        lp = new SimpleListProperty<>(usernameList);
        try {
            ss = new ServerSocket(port);
        } catch (IOException e) {e.printStackTrace();}
        lobby = Executors.newFixedThreadPool(50);
		gamePool = Executors.newFixedThreadPool(10);
        atWar = true;
        sdf = new SimpleDateFormat("hh:mm:ss a MMM/dd/yyyy");
	}

    public static void main(String[] args) {launch(args);}

    @Override
    public void start(Stage primaryStage) {
        boot();
        stage = primaryStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/server/watchtower.fxml"));
        Parent parent = null;
        try {
            parent = loader.load();
        } catch (IOException e) {System.out.println("FXMLLoader failed to load initial parent.");}
        sc = loader.getController();
        sc.init(this);
        sc.clientList.itemsProperty().bind(lp);
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.show();
        handleShutdown();
    }

	//Methods
	private void boot() {
        Runnable Server = () -> {
            mySQLSetup();
            while (atWar) {
                try {
                    ClientRunnable cr = new ClientRunnable(ss.accept());
                    cr.init(this);
                    lobby.execute(cr);
                } catch (Exception e) {System.out.println("Error accepting new clients.");}
            }
        };
        Thread st = new Thread(Server);
        st.setDaemon(true);
        st.start();
        System.out.println("Now accepting clients.");
	}

    private void handleShutdown() {
        stage.setOnCloseRequest(t -> {
            lobby.shutdownNow();
            gamePool.shutdownNow();
            Platform.exit();
        });
    }

    private void mySQLSetup() {
        try {
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setUrl("jdbc:mysql://battleshipdb.ci19hdnrsizi.us-east-1.rds.amazonaws.com:3306");
            String sqlUser = "battleship";
            String sqlPass = "Mercedes789!";
            connect = dataSource.getConnection(sqlUser, sqlPass);

            System.out.println("Connected to battleshipDB.");
        } catch (SQLException e1) {e1.printStackTrace();}
    }

    Boolean trySql(String username, String password) {
        Boolean success = false;
        try {
            Statement stmt = connect.createStatement();
            String query = "SELECT * from Battleship.login WHERE username = '" + username + "';";

            ResultSet result = stmt.executeQuery(query);

            while (result.next()) {
                String queryUsername = result.getString("username");
                String queryPassword = result.getString("password");

                if (username.equals(queryUsername) && password.equals(queryPassword)) {
                    System.out.println(username + " logged in at " + sdf.format(new Date()));
                    success = true;
                    break;
                }
            }
        } catch (SQLException e) {e.printStackTrace(); System.out.println("Error in SQL querying.");} //Put SQL reconnection script in catch block
        return success;
    }

    void broadcast(String msg) {
        sc.txtAreaConsole.appendText(msg + "\n");
        if (clientOs.size() > 0) {
            for (ObjectOutputStream clientO : clientOs) {
                try {
                    clientO.writeObject(new Transmission(msg + "\n"));
                } catch (IOException e) {
                    System.out.println("Error broadcasting message");
                }
            }
        }
    }

	public void stop() {
		atWar = false;
	}

    void kick(String uname) {
        for (int i = clientList.size(); --i >= 0;) {
            if (clientList.get(i).getUsername().equals(uname)) {
                try {
                    LoginObject lo = new LoginObject();
                    lo.setType(LoginObject.Type.KICK);
                    clientList.get(i).getObjectOutputStream().writeObject(new Transmission(lo));
                    clientList.get(i).getObjectOutputStream().flush();
                    clientList.get(i).logout();
                    broadcast("[" + clientList.get(i).getUsername() + " was kicked]");
                } catch (IOException e) { System.out.println("Problem handling kick logout action near line 169."); }
            }
        }
    }
	
	public void newGame (ClientRunnable player1, ClientRunnable player2) {
        Game newGame = new Game(player1, player2, this);
        Thread gameThread = new Thread(newGame);
        gameThread.setDaemon(true);
        gameThread.start();
        System.out.println("Watchtower started newGame.");
    }

    public ClientRunnable getClientRunnable(String username) {
        ClientRunnable retCr = null;
        for (ClientRunnable cr : clientList) {
            if (cr.getUsername().equals(username)) {
                retCr = cr;
            }
        }
        return retCr;
    }


    enum Status {INGAME, SPECTATING, AVAILABLE, UNAVAILABLE}
}