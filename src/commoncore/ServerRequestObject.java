package commoncore;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class ServerRequestObject implements Serializable {
	private GameRequest gameRequest;
	private String serverRequest;
	private ArrayList<String> lobbyList;
	private ServerRequestObjectType serverRequestObjectType;
	public enum ServerRequestObjectType {
		CLIENTREQUEST,
		LOBBYLIST
	}

	public ServerRequestObject() {
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
