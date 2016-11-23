package commoncore;

import java.io.Serializable;

/**
 * Created by jdz on 11/21/16.
 */
public class GameOver implements Serializable {
    private String winnerUsername;

    public GameOver(String winnerUsername) {
        this.winnerUsername = winnerUsername;
    }

    public String getWinnerUsername() {return winnerUsername;}

}
