package commoncore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by jdz on 8/29/16.
 */
public class Ship implements Serializable {
    private int shipLength; //Ship length, in number of Spots. Must be greater than 1 && less than 5.
    private int hitsRemaining;
    private Boolean sunk;
    private Fleet fleet;
    private ArrayList<Spot> locations; //Each ship has the board coordinates it occupies as its location.
    public enum shipName { //Names must be consistent.
        BIGFIVER,
        FOUR,
        THREEONE,
        THREETWO,
        THREETHREE,
        TWOONE,
        TWOTWO,
        TWOTHREE;
        public static Stream<shipName> stream() {
            return Arrays.stream(shipName.values());
        }
    }

    //Constructor
    public Ship(shipName name, Fleet shipsFleet) {
        this.sunk = false;
        this.shipLength = inferShipLength(name);
        this.hitsRemaining = shipLength;
        locations = new ArrayList<>(shipLength);
        this.fleet=shipsFleet;
    }

    //Methods
    private int inferShipLength(shipName name) {
        int digit = 0;
        switch (name) {
            case BIGFIVER:
                digit = 5;
                break;
            case FOUR:
                digit = 4;
                break;
            case THREEONE:
            case THREETWO:
            case THREETHREE:
                digit = 3;
                break;
            case TWOONE:
            case TWOTWO:
            case TWOTHREE:
                digit = 2;
                break;
        }
        return digit;
    }

    public AttackResult evaluateAttack(Attack attackRecieved) {
        Boolean wasHit = locations.stream()
                .anyMatch(l -> l.getRowColStr().equals(attackRecieved.getAttackSpotRowColStr()));
        if (wasHit) {

            for (Spot spot : locations) {
                if (attackRecieved.getAttackSpotRowColStr().equals(spot.getRowColStr())) {
                    spot.becomesHit();
                    hitsRemaining--;
                    sunk = hitsRemaining == 0;
                    break;
                }
            }
        }
        return new AttackResult(wasHit, sunk, attackRecieved.getAttackSpotRowColStr());
    }

    public int getShipLength() {
        return this.shipLength;
    }

    public int getHitsRemaining() {
        return this.hitsRemaining;
    }

    public ArrayList<Spot> getShipLocations() { return this.locations; }

    public void setLocations (ArrayList<Spot> shipLocations) {
        this.locations = shipLocations;
        fleet.getFleetLocations().add(shipLocations.stream().collect(Collectors.toSet()));
    }
}