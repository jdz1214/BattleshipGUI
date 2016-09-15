package commoncore;

import commoncore.Ship.shipName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by jdz on 8/30/16.
 */
public class Fleet extends ArrayList<Ship> implements Serializable {
    private static final long serialVersionUID = 5959358336090731180L;
    //one fleet per player
    int numberOfShips = 5; //number of ships in fleet
    Fleet fleet;

    //Constructors
    public Fleet () {}

    //Methods
    public ArrayList<String> getUnplaceableLocationsString() {
        ArrayList<String> taken = new ArrayList<>();
        for (Ship s : this) {
            taken.addAll(s.locations.stream().map(Spot::getRowColStr).collect(Collectors.toList()));
        }
        return taken;
    }

    public ArrayList<Spot> getUnplaceableLocations() {
        ArrayList<Spot> taken = new ArrayList<>();
        for (Ship s : this) {
            taken.addAll(s.locations);
        }
        return taken;
    }

    public Ship getShipByName(shipName name) {
        Ship ship = null;
        for (Ship s : this) {
            if (s.getName().equals(name)) {
                ship = s;
            }
        }
        assert(ship!=null);
        return ship;
    }
}