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
    private List<Set<Spot>> fleetLocations;

    //Constructors
    public Fleet() {
        System.out.println("Adding ships to Fleet in Fleet class.");
        shipName.stream().forEach(s -> this.add(new Ship(s, this)));
        System.out.println("Fleet size: " + this.size());
        fleetLocations = new ArrayList<>();
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

    public Integer getTotalRemainingHits() {
        return this.stream()
                .collect(Collectors.summingInt(Ship::getHitsRemaining));
    }
}