package commoncore;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by jdz on 8/29/16.
 */
public class Ship {
    int shipLength; //Ship length, in number of Spots. Must be greater than 1 && less than 5.
    int hitsRemaining;
    ArrayList<Spot> locations; //Each ship has the board coordinates it occupies as its location.
    //Constructor
    public Ship (int shipLength) {
        this.shipLength = shipLength;
        this.hitsRemaining = shipLength;
        locations = new ArrayList<Spot>(shipLength);
    }

    //Methods
    public int getShipLength() {
        return this.shipLength;
    }

    int getHitsRemaining() {
        return this.hitsRemaining;
    }

    ArrayList<Spot> placeShip(ArrayList<Spot> spotPool) {
        ArrayList<Spot> updatedSpotPool = spotPool;
        String alignment;
        ArrayList<Spot> tempLocations = new ArrayList<>();
        ArrayList<Spot> rowSpots = new ArrayList<>();
        ArrayList<Spot> rowGroup = new ArrayList<>();
        ArrayList<Spot> colSpots = new ArrayList<>();
        ArrayList<Spot> colGroup = new ArrayList<>();
        ArrayList<ArrayList<Spot>> Groups = new ArrayList<>(2);
        Boolean cannotFitInColumn = true;
        Boolean cannotFitInRow = true;
        Random rand = new Random();
        int alignmentRand = rand.nextInt(2);
        if (alignmentRand == 0) {
            alignment = "h";
        } else {alignment = "v";}


        //Choose ship spots
        if (alignment.equals("h")) {
            int leftMostColumn = -1;
            while (cannotFitInRow) {
                int nextRow = rand.nextInt(5);
                for (int i = 0; i < spotPool.size(); i++) { //Put all row spots into single arraylist
                    Spot spot = spotPool.get(i);
                    if (spot.getY() == nextRow) {
                        rowSpots.add(spot);
                    }
                }
                //Break rowSpots into contiguous Groups
                for (int i = 0; i < rowSpots.size(); i++) {
                    Spot rowSpot = rowSpots.get(i);

                    if (rowGroup.size() == 0) {
                        rowGroup.add(rowSpot);
                        Groups.add(rowGroup);
                    }
                    else {
                        for (int j = 0; j < Groups.size(); j++) {
                            ArrayList<Spot> thisGroup = Groups.get(i);
                            for (int k = 0; k < thisGroup.size(); k++) {
                                Spot thisGroupSpot = thisGroup.get(k);
                                if (Math.abs(rowSpot.getX() - thisGroupSpot.getX()) == 1) {
                                    thisGroup.add(rowSpot);
                                }
                                else { //If no existing group has any x-coordinate contiguous with the current rowSpot, put rowSpot in a new group.
                                    ArrayList<Spot> newGroup = new ArrayList<>();
                                    newGroup.add(rowSpot);
                                    Groups.add(newGroup);
                                }
                            }
                        }
                    }
                } //Now, Groups contains all groups of contiguous spots. Time to eliminate those less than shipLength in size.

                for (int i = 0; i < Groups.size(); i++) {
                    ArrayList<Spot> thisGroup = Groups.get(i);
                    if (thisGroup.size() < shipLength) {
                        Groups.remove(i);
                        i--;
                    }
                }

                if (Groups.size() == 0) {continue;} //This row does not have a contiguous group of sufficient size for this ship.

                if (Groups.size() > 1) { //Now, Groups contains only contiguous groups of spots that could fit this ship. If more than one, time to choose randomly.
                    int pickGroup = rand.nextInt(Groups.size()); //will return an index, because it will only return up to Groups.size()-1.
                    ArrayList<Spot> chosenGroup = Groups.get(pickGroup);
                    for (int i = 0; i < chosenGroup.size(); i++) {
                        Spot pickedSpot = chosenGroup.get(i);
                        tempLocations.add(pickedSpot);
                    }
                } else if (Groups.size() == 1) { //This means there was only one contiguous group remaining.
                    ArrayList<Spot> chosenGroup = Groups.get(0);
                    for (int j = 0; j < chosenGroup.size(); j++) {
                        Spot pickedSpot = chosenGroup.get(j);
                        tempLocations.add(pickedSpot);
                    }
                }

                if (tempLocations.size() >= shipLength) {
                    cannotFitInRow = false;
                } else {System.out.println("Might want to check your looping and logic in placeShip for Horizontal Ship Alignment.");}
            }
            //Now we have the contiguous group of spots in this row. Must determine the left-most starting point for the column.
            if (tempLocations.size() == shipLength) { //No determining necessary
                this.locations = tempLocations;
                for (int i = 0; i < locations.size(); i++) {
                    Spot tempSpot = locations.get(i);
                    for (int j = 0; j < updatedSpotPool.size(); j++) {
                        Spot spotPoolSpot = updatedSpotPool.get(j);
                        if (spotPoolSpot == tempSpot) {
                            updatedSpotPool.remove(j);
                            j--;
                        }
                    }
                }
            }
            else {
                leftMostColumn = rand.nextInt(6 - shipLength); //6** required to get correct col positions b/c req at least 1 to get col position 0.
                for (int i = leftMostColumn; i < tempLocations.size(); i++) {
                    while (locations.size() < shipLength) {
                        Spot spotToAdd = tempLocations.get(i);
                        if (spotToAdd.getX() >= leftMostColumn && spotToAdd.getX() <= (leftMostColumn + shipLength-1)) { //accounts for size vs column position
                            locations.add(spotToAdd);
                            for (int j = 0; j < updatedSpotPool.size(); j++) {
                                Spot spotPoolSpot = updatedSpotPool.get(j);
                                if (spotPoolSpot == spotToAdd) {
                                    updatedSpotPool.remove(j);
                                    j--;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (alignment.equals("v")) {
            int bottomRow = -1;
            while (cannotFitInColumn) {
                int nextCol = rand.nextInt(5);
                for (int i = 0; i < spotPool.size(); i++) { //Put all row spots into single arraylist
                    Spot spot = spotPool.get(i);
                    if (spot.getX() == nextCol) {
                        colSpots.add(spot);
                    }
                }
                //Break colSpots into contiguous Groups
                for (int i = 0; i < colSpots.size(); i++) {
                    Spot colSpot = colSpots.get(i);

                    if (colGroup.size() == 0) {
                        colGroup.add(colSpot);
                        Groups.add(colGroup);
                    }
                    else {
                        for (int j = 0; j < Groups.size(); j++) {
                            ArrayList<Spot> thisGroup = Groups.get(i);
                            for (int k = 0; k < thisGroup.size(); k++) {
                                Spot thisGroupSpot = thisGroup.get(k);
                                if (Math.abs(colSpot.getY() - thisGroupSpot.getY()) == 1) {
                                    thisGroup.add(colSpot);
                                }
                                else { //If no existing group has any x-coordinate contiguous with the current colSpot, put colSpot in a new group.
                                    ArrayList<Spot> newGroup = new ArrayList<>();
                                    newGroup.add(colSpot);
                                    Groups.add(newGroup);
                                }
                            }
                        }
                    }
                } //Now, Groups contains all groups of contiguous spots. Time to eliminate those less than shipLength in size.
                for (int i = 0; i < Groups.size(); i++) {
                    ArrayList<Spot> thisGroup = Groups.get(i);
                    if (thisGroup.size() < shipLength) {
                        Groups.remove(i);
                        i--;
                    }
                }

                if (Groups.size() == 0) {continue;} //This row does not have a contiguous group of sufficient size for this ship.

                if (Groups.size() > 1) { //Now, Groups contains only contiguous groups of spots that could fit this ship. If more than one, time to choose randomly.
                    int pickGroup = rand.nextInt(Groups.size()); //will return an index, because it will only return up to Groups.size()-1.
                    ArrayList<Spot> chosenGroup = Groups.get(pickGroup);
                    for (int i = 0; i < chosenGroup.size(); i++) {
                        Spot pickedSpot = chosenGroup.get(i);
                        tempLocations.add(pickedSpot);
                    }
                } else if (Groups.size() == 1) { //This means there was only one contiguous group remaining.
                    ArrayList<Spot> chosenGroup = Groups.get(0);
                    for (int j = 0; j < chosenGroup.size(); j++) {
                        Spot pickedSpot = chosenGroup.get(j);
                        tempLocations.add(pickedSpot);
                    }
                }

                if (tempLocations.size() >= shipLength) {
                    cannotFitInColumn = false;
                } else {System.out.println("Might want to check your looping and logic in placeShip for Horizontal Ship Alignment.");}
            }
            //Now we have the contiguous group of spots in this row. Must determine the left-most starting point for the column.
            if (tempLocations.size() == shipLength) { //No determining necessary
                this.locations = tempLocations;
                for (int i = 0; i < locations.size(); i++) {
                    Spot tempSpot = locations.get(i);
                    for (int j = 0; j < updatedSpotPool.size(); j++) {
                        Spot spotPoolSpot = updatedSpotPool.get(j);
                        if (spotPoolSpot == tempSpot) {
                            updatedSpotPool.remove(j);
                            j--;
                        }
                    }
                }
            }
            else {
                bottomRow = rand.nextInt(6 - shipLength); //**6 required to get correct col positions b/c req at least 1 to get col position 0.
                for (int i = bottomRow; i < tempLocations.size(); i++) {
                    while (locations.size() < shipLength) {
                        Spot spotToAdd = tempLocations.get(i);
                        if (spotToAdd.getY() >= bottomRow && spotToAdd.getY() <= (bottomRow + shipLength-1)) { //accounts for size vs column position
                            locations.add(spotToAdd);
                            for (int j = 0; j < updatedSpotPool.size(); j++) {
                                Spot spotPoolSpot = updatedSpotPool.get(j);
                                if (spotPoolSpot == spotToAdd) {
                                    updatedSpotPool.remove(j);
                                    j--;
                                }
                            }
                        }
                    }
                }
            }
        }
        else {System.out.println("Error determining ship alignment (horizontal or vertical).");}

        return updatedSpotPool;
    }
}