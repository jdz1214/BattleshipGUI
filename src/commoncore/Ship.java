package commoncore;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by jdz on 8/29/16.
 */
public class Ship implements Serializable {
    int shipLength; //Ship length, in number of Spots. Must be greater than 1 && less than 5.
    int hitsRemaining;
    int orientation; // 0 == Horizontal, 1 == Vertical.
    shipName name;
    ArrayList<Spot> locations; //Each ship has the board coordinates it occupies as its location.
    public enum shipName { //Names must be consistent.
        BIGFIVER,
        FOUR,
        THREEONE,
        THREETWO,
        THREETHREE,
        TWOONE,
        TWOTWO,
        TWOTHREE
    }

    //Constructor
    public Ship (shipName name, int shipLength) {
        this.name = name;
        this.shipLength = shipLength;
        this.hitsRemaining = shipLength;
        locations = new ArrayList<Spot>(shipLength);
    }

    //Methods
    public int getShipLength() {
        return this.shipLength;
    }

    public int getHitsRemaining() {
        return this.hitsRemaining;
    }

    public int getOrientation() { return this.orientation; }

    public void setName (shipName name) { this.name = name; }

    public ArrayList<Spot> getShipLocations() { return this.locations; }

    public shipName getNameEnum() { return this.name; }

    public String getNameString() { return this.name.toString(); }

    public void setOrientation(int orientation) { this.orientation = orientation; }

    public void setLocations (ArrayList<Spot> shipLocations) {
        this.locations = shipLocations;
    }

    public void updateGrid() {
        locations.forEach(Spot::updateGrid);
    }
}