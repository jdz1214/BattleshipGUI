package commoncore;

import java.util.ArrayList;

/**
 * Created by jdz on 8/29/16.
 */
public class Gameboard extends ArrayList<String> {
    private static final long serialVersionUID = -543995990855427415L;
    private int defaultCols = 5;
    private int defaultRows = 5;
    private String defaultRow = "~~~~~";

    //Constructor
    public Gameboard() {}

    //Methods
    public Gameboard buildClientBoard(int playerNumber, Gameboard serverboard) {
        //TODO needs rebuilding.
        Gameboard clientBoard = new Gameboard();
        clientBoard.resetClientBoard(clientBoard);
        if (playerNumber == 1) {
            for (int i = 0; i < 4; i++) {
                clientBoard.add(i, serverboard.get(i));
            }
        }
        else if (playerNumber == 2) {
            for (int i = 0; i < 4; i++) {
                clientBoard.add(i, serverboard.get(i + 5));
            }
        }
        return clientBoard;
    }

    public void resetClientBoard(Gameboard clientBoard) {
        for (int i = 0; i < 4; i++) {
            clientBoard.add(i, defaultRow);
        }
    }
}