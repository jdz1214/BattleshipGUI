package commoncore;

import commoncore.Game.GameRequest;

import java.io.Serializable;
import java.util.ArrayList;

public class ServerRequestObject implements Serializable {

	private static final long serialVersionUID = -3360166450589642180L;
	private GameRequest gameRequest;
	private String serverRequest;
	private ArrayList<String> lobbyList;
	private ServerRequestObjectType serverRequestObjectType;
	public enum ServerRequestObjectType {
		GAMEREQUEST,
		CLIENTREQUEST,
		LOBBYLIST
	}
	
	public ServerRequestObject (GameRequest gameRequest) {
		this.gameRequest = gameRequest;
		serverRequestObjectType = ServerRequestObjectType.GAMEREQUEST;
	}
	
	public ServerRequestObject (ServerRequestObjectType type) {
		// Pass string "LOBBYLIST" to request a client list.
		serverRequestObjectType = ServerRequestObjectType.CLIENTREQUEST;
	}
	
	public ServerRequestObject (ArrayList<String> lobbyList ) {
	    this.lobbyList = new ArrayList<>(lobbyList);
		serverRequestObjectType = ServerRequestObjectType.LOBBYLIST;
	}
	
	// Getters
	public GameRequest getGameRequest () {
		return gameRequest;
	}
	
	public String getServerRequest () {
		return serverRequest;
	}
	
	public ServerRequestObjectType getServerRequestObjectType () {
		return serverRequestObjectType;
	}
	
	public ArrayList<String> getLobbyList() {
		return lobbyList;
	}
	
	//Setters
	public void setLobbyList (ArrayList<String> lobbyList) {
		this.lobbyList = lobbyList;
	}
	
	public void setServerRequestObjectType (ServerRequestObjectType serverRequestObjectType) {
		this.serverRequestObjectType = serverRequestObjectType;
	}
	
	public void setServerRequest (String serverRequest) {
		this.serverRequest = serverRequest;
	}
	
	public void setGameRequest (GameRequest gameRequest) {
		this.gameRequest = gameRequest;
	}
}
