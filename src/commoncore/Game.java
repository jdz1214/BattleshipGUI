package commoncore;

import server.ClientRunnable;
import server.Watchtower;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import static commoncore.Game.Gamestate.gameOn;


public class Game implements Runnable {
	private Watchtower w;
	private ClientRunnable p1;
	private ClientRunnable p2;
	private ClientRunnable playerUp;
    private ClientRunnable winner;
    private ClientRunnable loser;
	private Gamestate gamestate;
	public enum Gamestate { gameOn, gameOver, youAreUp, youAreNotUp, preGame, youWon, youLost }
	
	//Constructors
	public Game (ClientRunnable p1, ClientRunnable p2, Watchtower w) {
	    this.w = w;
		this.p1 = p1;
		this.p2 = p2;
        init();
        //TODO gameboard development in progress.
		//gameboard = new Gameboard();
        determineFirst();
		gamestate = gameOn;
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
        if (this.gamestate == Gamestate.gameOn) {
            ClientRunnable otherPlayer = (playerUp.equals(p1)) ? p2 : p1;
            playerUp.notifyUp(true);
            otherPlayer.notifyUp(false);
        } else if (this.gamestate == Gamestate.gameOver) {
            p1.notifyGameOver(winner==p1?Gamestate.youWon : Gamestate.youLost);
            p2.notifyGameOver(winner==p2?Gamestate.youWon : Gamestate.youLost);
        }
    }

    public void setGamestate (Game.Gamestate gamestate) {
        this.gamestate = gamestate;
    }

    public void setWinnerAndLoser (String winnerUsername) {
        if (p1.getUsername().equals(winnerUsername)) {
            winner = p1;
            loser = p2;
        } else {
            winner = p2;
            loser = p1;
        }
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