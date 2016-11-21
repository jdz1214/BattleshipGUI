package commoncore;

import java.io.Serializable;

/**
 * Created by jdz on 8/29/16.
 */
public class AttackResult implements Serializable {
    private Boolean result;
    private Boolean sunkShip;
    private String spotRowCol;
    //Constructor
    public AttackResult(Boolean wasShipHit, Boolean wasShipSunk, String spotRowCol) {
        this.result = wasShipHit;
        this.sunkShip = wasShipSunk;
        this.spotRowCol = spotRowCol;
    }

    public Boolean wasHit() {
        return result;
    }

    public Boolean sunkShip() {
        return sunkShip;
    }

    public String getSpotRowCol() {return spotRowCol;}
}