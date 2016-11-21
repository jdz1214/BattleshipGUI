package commoncore;

import java.io.Serializable;

/**
 * Created by jdz on 11/21/16.
 */
public class GameOver implements Serializable {
    private String winnerUsername;
    private String loserUsername;

    public GameOver(String winnerUsername, String loserUsername) {
        this.winnerUsername = winnerUsername;
        this.loserUsername = loserUsername;
    }

    public String getWinnerUsername() {return winnerUsername;}

    public String getLoserUsername() {return loserUsername;}
}
