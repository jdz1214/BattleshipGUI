package commoncore;

import commoncore.Game.Gamestate;

import java.io.Serializable;

public class GameObject implements Serializable {
	private Attack attack;
	private AttackResult attackResult;
	private Gamestate gamestate;
	private Gameboard gameboard;
	private GameRequest gameRequest;
	private GameOver gameOver;
	private GameObjectType gameObjectType;
    private String opponentUsername;
    private String username;
    private String userWhoQuit;
	public enum GameObjectType {
		ATTACK, 
		ATTACKRESULT, 
		BOARD, 
		HISTORY, 
		GAMESTATE,
		GAMEREQUEST,
		GAMEBOARD,
		GAMEOVER,
		NEWGAME,
		QUIT
	}
	
	// Constructors

    public GameObject () {}

	public GameObject(Attack attack) {
		this.attack = attack;
		this.gameObjectType = GameObjectType.ATTACK;
	}
	
	public GameObject (AttackResult attackResult) {
		this.attackResult = attackResult;
		this.gameObjectType = GameObjectType.ATTACKRESULT;
	}
	
	public GameObject (Gamestate gamestate) {
		this.gamestate = gamestate;
		this.gameObjectType = GameObjectType.GAMESTATE;
	}
	
	public GameObject (GameRequest gameRequest) {
		this.gameRequest = gameRequest;
		this.gameObjectType = GameObjectType.GAMEREQUEST;
	}

	public GameObject (GameOver gameOver) {
		this.gameOver = gameOver;
		this.gameObjectType = GameObjectType.GAMEOVER;
	}
	
	public GameObject (String opponentUsername) {
        this.opponentUsername = opponentUsername;
	    this.gameObjectType = GameObjectType.NEWGAME;
    }

    public GameObject (GameObjectType QUIT, String userWhoQuit) {
        this.gameObjectType = QUIT;
        this.userWhoQuit = userWhoQuit;
    }
	// Getters
	public Attack getAttack() {
		return attack;
	}

	public AttackResult getAttackResult() {
		return attackResult;
	}

	public Gamestate getGamestate() {
		return gamestate;
	}

	public GameRequest getGameRequest() {
		return gameRequest;
	}

	public GameOver getGameOver() { return gameOver; }

	public Gameboard getGameboard() {
		return gameboard;
	}
	
	public GameObjectType getGameObjectType() {
		return gameObjectType;
	}

	public String getOpponentUsername() {
	    return opponentUsername;
    }

    public String getUsername() {
        return username;
    }

    public String getUserWhoQuit() { return userWhoQuit; }

	//Setters

	public void setOpponentUsername (String opponentUsername) {this.opponentUsername = opponentUsername;}
}