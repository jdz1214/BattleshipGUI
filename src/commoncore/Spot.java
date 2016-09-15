package commoncore;

import javafx.scene.text.Text;

import java.io.Serializable;

public class Spot implements Serializable {
    private int row;
    private int col;
    private String text;
    //Constructors

    public Spot(){}

    public Spot(Text txt) {
        this.row = Integer.parseInt(txt.getId().substring(9,10));
        this.col = Integer.parseInt(txt.getId().substring(10,11));
        this.text = txt.getText();
    }


    public Spot (int row, int col, String text) {
        this.row = row;
        this.col = col;
        this.text = text;
    }

    //Methods
    public int getRow() {
        return row;
    }
    public int getCol() {
        return col;
    }
    public String getRowColStr() {return String.valueOf(row) + String.valueOf(col);}
    public void printMe() {
        System.out.println("row: " + this.row + ", col: " + this.col + ", text: " + this.text);
    }
    public void setRow(int row) { this.row = row; }
    public void setCol(int col) { this.col = col; }
    public void setText(String txt) {this.text = txt; }
}