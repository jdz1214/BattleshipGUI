package commoncore;

import java.util.ArrayList;

/**
 * Created by jdz on 8/30/16.
 */
public class Fleet extends ArrayList<Ship> {
    private static final long serialVersionUID = 5959358336090731180L;
    //one fleet per player
    int numberOfShips = 4; //number of ships in fleet
    ArrayList<Spot> fleetLocations = new ArrayList<>();
    Fleet fleet;

    //Constructors
    public Fleet () {}
    public Fleet (int playerNumber) {
        generateShips();
        generateLocations(fleet, playerNumber);
    }

    //Methods
    public Fleet generateShips() {
        Fleet fleet = new Fleet();
        Ship ship1 = new Ship(2);
        Ship ship2 = new Ship(3);
        Ship ship3 = new Ship(4);
        Ship ship4 = new Ship(5);
        fleet.add(ship1);
        fleet.add(ship2);
        fleet.add(ship3);
        fleet.add(ship4);
        return fleet;
    }

    public void generateLocations(Fleet fleet, int playerNumber) {
        ArrayList<Spot> spotPool = new ArrayList<>();
        ArrayList<Spot> fleetLocations = new ArrayList<>();

        for (int i = 0; i < 5; i++) { //Initialize spotPool
            for (int j = 0; j < 5; j++) {
                Spot newSpot = new Spot(i, j, "~");
                spotPool.add(newSpot);
            }
        }
        for (int i = 0; i < fleet.size(); i++) { //Place each ship on the board
            Ship ship = fleet.get(i);
            spotPool = ship.placeShip(spotPool);
            if (playerNumber == 2) {
                for (int j = 0; j < ship.locations.size(); j++) {
                    Spot spot = ship.locations.get(j);
                    // TODO Deleted a line here, this probably doesn't work.
                }
            }
        }
        for (int i = 0; i < fleet.size(); i++) { //Aggregate all ship spots into fleetLocations
            Ship ship = fleet.get(i);
            for (int j = 0; j < ship.locations.size(); j++) {
                Spot spot = ship.locations.get(j);
                fleetLocations.add(spot);
            }
        }
    }
}