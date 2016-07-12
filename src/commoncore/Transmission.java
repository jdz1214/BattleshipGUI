package commoncore;

import server.ClientRunnable;

import java.io.Serializable;


public class Transmission implements Serializable{
	
	private static final long serialVersionUID = 4852737544034013158L;
    private TransmissionType transmissionType;
    private String chatMessage;
    private LoginObject loginObject;
	public Boolean iAmUp;
    private GameObject gameObject;
	private ServerRequestObject serverRequestObject;
	public enum TransmissionType {
		CHATMESSAGE, 
		LOGINOBJECT, 
		SERVERREQUESTOBJECT,
		GAMEOBJECT }

	//Constructors
	public Transmission () {}
	
	public Transmission (String chatMessage) {
		this.chatMessage = chatMessage;
		this.transmissionType = TransmissionType.CHATMESSAGE;
	}
	
	public Transmission (GameObject gameObject) {
		this.gameObject = gameObject;
		this.transmissionType = TransmissionType.GAMEOBJECT;
	}

	public Transmission (LoginObject loginObject) {
		this.loginObject = loginObject;
		this.transmissionType = TransmissionType.LOGINOBJECT;
	}
	
	public Transmission (ServerRequestObject serverRequestObject) {
		this.serverRequestObject = serverRequestObject;
		this.transmissionType = TransmissionType.SERVERREQUESTOBJECT;
	}
	
	//Methods
	public TransmissionType getTransmissionType() {
		return transmissionType;
	}
	
	public String getChatMessage() {
		String chatMessage = null;
		if (this.transmissionType == TransmissionType.CHATMESSAGE) {
			chatMessage = this.chatMessage;
		}
		else {System.out.println("Transmission type was incorrect for " + this.toString());}
		return chatMessage;
	}

	public GameObject getGameObject () {
		return gameObject;
	}
	
	public ServerRequestObject getServerRequestObject () {
		return serverRequestObject;
	}
	
	public LoginObject getLoginObject() {
		LoginObject loginObject = null;
		if (this.transmissionType == TransmissionType.LOGINOBJECT) {
			loginObject = this.loginObject;
		}
		else {System.out.println("Transmission type was incorrect for " + this.toString());}
		return loginObject;
	}

	public Game requestGame(ClientRunnable clientRunnable1, ClientRunnable clientRunnable2) throws Exception {
		Game game = new Game (clientRunnable1, clientRunnable2);
		return game;
	}
	
	public void setTransmissionType(TransmissionType transmissionType) {
		this.transmissionType = transmissionType;
	}
	
	public void setChatMessage(String chatMessage) {
		this.chatMessage = chatMessage;
		this.transmissionType = TransmissionType.CHATMESSAGE;
	}

	public void setLoginObject(LoginObject loginObject) {
		this.loginObject = loginObject;
	}
}