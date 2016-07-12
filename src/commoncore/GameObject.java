package commoncore;

import commoncore.Game.*;

import java.io.Serializable;

public class GameObject implements Serializable {
	
	private static final long serialVersionUID = -4391807776691961968L;
	private Attack attack;
	private AttackResult attackResult;
	private Gamestate gamestate;
	private Gameboard gameboard;
	private History history;
	private GameRequest gameRequest;
	private GameObjectType gameObjectType;
	enum GameObjectType {
		ATTACK, 
		ATTACKRESULT, 
		BOARD, 
		HISTORY, 
		GAMESTATE,
		GAMEREQUEST,
		GAMEBOARD
	}
	
	// Constructors
	GameObject(Attack attack) {
		this.attack = attack;
		this.gameObjectType = GameObjectType.ATTACK;
	}
	
	GameObject(AttackResult attackResult) {
		this.attackResult = attackResult;
		this.gameObjectType = GameObjectType.ATTACKRESULT;
	}
	
	GameObject(Gamestate gamestate) {
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
	
	// Getters
	Attack getAttack() {
		return attack;
	}

	AttackResult getAttackResult() {
		return attackResult;
	}

	Gamestate getGamestate() {
		return gamestate;
	}

	History getHistory() {
		return history;
	}

	public GameRequest getGameRequest() {
		return gameRequest;
	}

	Gameboard getGameboard() {
		return gameboard;
	}
	
	public GameObjectType getGameObjectType() {
		return gameObjectType;
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