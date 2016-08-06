package commoncore;

import commoncore.Game.*;

import java.io.Serializable;

public class GameObject implements Serializable {
	private static final long serialVersionUID = -4391807776691961968L;
	Attack attack;
	AttackResult attackResult;
	Gamestate gamestate;
	Gameboard gameboard;
	History history;
	GameRequest gameRequest;
	GameObjectType gameObjectType;
    String opponentUsername;
    String username;
	public enum GameObjectType {
		ATTACK, 
		ATTACKRESULT, 
		BOARD, 
		HISTORY, 
		GAMESTATE,
		GAMEREQUEST,
		GAMEBOARD,
		NEWGAME
	}
	
	// Constructors
	public GameObject (Attack attack) {
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
	
	public GameObject (History history) {
		this.history = history;
		this.gameObjectType = GameObjectType.HISTORY;
	}
	
	public GameObject (Gameboard gameboard) {
		this.gameboard = gameboard;
		this.gameObjectType = GameObjectType.GAMEBOARD;
	}

	public GameObject (String opponentUsername) {
	    this.gameObjectType = GameObjectType.NEWGAME;
        this.opponentUsername = opponentUsername;
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

	public History getHistory() {
		return history;
	}

	public GameRequest getGameRequest() {
		return gameRequest;
	}

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

	//Setters
	public void setAttack(Attack attack) {
		this.attack = attack;
	}

	public void setAttackResult(AttackResult attackResult) {
		this.attackResult = attackResult;
	}

	public void setGamestate(Gamestate gamestate) {
		this.gamestate = gamestate;
	}

	public void setHistory(History history) {
		this.history = history;
	}

	public void setGameRequest(GameRequest gameRequest) {
		this.gameRequest = gameRequest;
	}

	public void setGameBoard (Gameboard gameboard) {
		this.gameboard = gameboard;
	}
	
	public void setGameObjectType(GameObjectType gameObjectType) {
		this.gameObjectType = gameObjectType;
	}
}