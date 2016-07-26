package commoncore;

import server.ClientRunnable;

/**
 * Created by jdz on 7/26/16.
 */
public class GameRequest {
    Boolean gameOn;
    ClientRunnable client1;
    ClientRunnable client2;
    String opponentUsername;
    public GameRequest(ClientRunnable you, ClientRunnable opponent) { //For server side of request
        this.client1 = you;
        this.client2 = opponent;
    }
    public GameRequest(String opponentUsername) { //For client side of request
        this.opponentUsername = opponentUsername;
    }
}