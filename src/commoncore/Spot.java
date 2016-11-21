package commoncore;

import javafx.application.Platform;
import javafx.scene.text.Text;

import java.io.Serializable;

public class Spot implements Serializable {
    private int row;
    private int col;
    private String text;
    private Text txt;
    //Constructors

    public Spot(){}

    public Spot(Text txt) {
        String id = txt.getId();
        this.row = Integer.parseInt(id.substring(9,10));
        this.col = Integer.parseInt(id.substring(10,11));
        this.text = txt.getText();
        this.txt = txt;
    }

    public Spot (int row, int col, String text) {
        assert row >= 0 && row < 6;
        this.row = row;
        this.col = col;
        this.text = text;
    }

    //Methods
    public Integer getRow() {
        return row;
    }
    public Integer getCol() {
        return col;
    }
    public String getRowColStr() {return String.valueOf(row) + String.valueOf(col);}
    public void printMe() {
        System.out.println("row: " + this.row + ", col: " + this.col + ", text: " + this.text);
    }
    public void updateGrid(String setGridToThis) {this.text = setGridToThis; Platform.runLater(() -> txt.setText(this.text));}
    public String getGridText() {
        this.text = txt.getText();
        return this.text; // Let us hope this works without Platform.runLater()...
    }
}