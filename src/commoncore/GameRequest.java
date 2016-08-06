package commoncore;

import server.ClientRunnable;

import java.io.Serializable;


public class GameRequest implements Serializable {
    private static final long serialVersionUID = -1L;
    ClientRunnable client1;
    ClientRunnable client2;
    String opponentUsername;

    public GameRequest (String opponentUsername) { //For client side of request
        this.opponentUsername = opponentUsername;
    } // Initiation -- Client Main 1 to ClientRunnable 1

    // If ClientRunnable receives this, then it is the initial invitation from Client Main 1.
    // If Main receives this, then it is the asking for confirmation received by Client Main 2 from CR 1.

    public GameRequest (ClientRunnable you, ClientRunnable opponent) { // For server side of request
        this.client1 = you;
        this.client2 = opponent;

    }

    public String getUsername() {
        return opponentUsername;
    }

    public ClientRunnable getClient1() {
        return client1;
    }

    public ClientRunnable getClient2() {
        return client2;
    }

}