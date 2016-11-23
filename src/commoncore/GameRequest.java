package commoncore;

import java.io.Serializable;


public class GameRequest implements Serializable {
    private String opponentUsername;

    public GameRequest (String opponentUsername) { //For client side of request
        this.opponentUsername = opponentUsername;
    } // Initiation -- Client Main 1 to ClientRunnable 1

    // If ClientRunnable receives this, then it is the initial invitation from Client Main 1.
    // If Main receives this, then it is the asking for confirmation received by Client Main 2 from CR 1.

    public String getUsername() {
        return opponentUsername;
    }

}