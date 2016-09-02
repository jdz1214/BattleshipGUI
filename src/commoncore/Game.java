package commoncore;

import server.ClientRunnable;
import server.Watchtower;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import static commoncore.Game.Gamestate.youWon;


public class Game implements Runnable {
	private Watchtower w;
	private ClientRunnable p1;
	private ClientRunnable p2;
	private ClientRunnable winner;
	private Boolean gameOver;
	private ClientRunnable playerUp;
	private Gameboard gameboard;
	private Gamestate gamestate;
	public enum Gamestate { gameOn, gameOver, youAreUp, youAreNotUp, preGame, youWon }
	
	//Constructors
	public Game (ClientRunnable p1, ClientRunnable p2, Watchtower w) {
	    this.w = w;
		this.p1 = p1;
		this.p2 = p2;
        init();
		gameOver = false;
        //TODO gameboard development in progress.
		//gameboard = new Gameboard();
        determineFirst();
		gamestate = Gamestate.gameOn;
		run();
	}
	
	//Methods
	@Override
	public void run() {
        notifyPlayers();
	}

	private void init() {
	    p1.enterGameMode(this, p2.getUsername(), 1);
        p2.enterGameMode(this, p1.getUsername(), 2);
    }

	private void determineFirst() {
		//Choose random player to go first
		int rand = ThreadLocalRandom.current().nextInt(0, 2);
		int first = rand + 1;
        if (first == 1) {
            playerUp = p1;
        } else {
            playerUp = p2;
        }
	}

	public void determineWinner() {
		winner = (playerUp.equals(p1)) ? p2 : p1;
        try {
            winner.getObjectOutputStream().writeObject(new Transmission(new GameObject(youWon)));
            winner.getObjectOutputStream().flush();
        } catch (IOException e) {e.printStackTrace();}

    }

	public void validateAttack (Attack attack, ClientRunnable attackingPlayer) {
		if (attackingPlayer == playerUp) {
			if (playerUp.equals(p1)) {
                try {
                    p2.getObjectOutputStream().writeObject(new Transmission(new GameObject(attack)));
                    p2.getObjectOutputStream().flush();
                } catch (IOException e) {e.printStackTrace();}
                playerUp = p2;
                notifyPlayers();
			} else if (playerUp.equals(p2)) {
                try {
                    p1.getObjectOutputStream().writeObject(new Transmission(new GameObject(attack)));
                    p1.getObjectOutputStream().flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                playerUp = p1;
                notifyPlayers();
            }
        }
    }

	private void notifyPlayers() {
	    ClientRunnable otherPlayer = (playerUp.equals(p1)) ? p2 : p1;
        playerUp.notifyUp(true);
        otherPlayer.notifyUp(false);
    }
	
	//Classes
    public ClientRunnable getClientRunnable (String crUsername) {
        ClientRunnable cr = null;
        if (crUsername.equals(p1.getUsername())) {
            cr = p1;
        } else if (crUsername.equals(p2.getUsername())) {
            cr = p2;
        }
        assert cr!= null;
        return cr;
    }


}