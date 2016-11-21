package commoncore;

import commoncore.Ship.shipName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by jdz on 8/30/16.
 */
public class Fleet extends ArrayList<Ship> implements Serializable {
    private static final long serialVersionUID = 5959358336090731180L;
    private List<Set<Spot>> fleetLocations;

    //Constructors
    public Fleet() {
        System.out.println("Adding ships to Fleet in Fleet class.");
        shipName.stream().forEach(s -> this.add(new Ship(s, this)));
        System.out.println("Fleet size: " + this.size());
        fleetLocations = new ArrayList<Set<Spot>>();
    }

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
            taken.addAll(s.getShipLocations());
        }
        return taken;
    }

    public Ship getShip(shipName name) { // Overloaded. See String variant.
        Ship ship = null;
        for (Ship s : this) {
            if (s.getNameEnum().equals(name)) {
                ship = s;
                break;
            }
        }
        assert (ship != null);
        return ship;
    }

    public Ship getShip(String name) { // Overloaded. See Enum variant.
        Ship ship = null;
        for (Ship s : this) {
            if (s.getNameString().equals(name)) {
                ship = s;
                break;
            }
        }
        assert (ship != null);
        return ship;
    }

    public List<Set<Spot>> getFleetLocations() {
        return fleetLocations;
    }

    public AttackResult evaluateAttackReceived(Attack attackReceived) {
        for (Ship s : this) {
            AttackResult ar = s.evaluateAttack(attackReceived);
            if (ar.wasHit()) {
                return ar;
            }
        }
        return new AttackResult(false, false, attackReceived.getAttackSpotRowColStr());
    }
}