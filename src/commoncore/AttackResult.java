package commoncore;

/**
 * Created by jdz on 8/29/16.
 */
public class AttackResult {
    Boolean result = false;
    Boolean sunkShip = false;
    //Constructor
    public AttackResult() {}
    public AttackResult(Boolean wasShipHit, Boolean wasShipSunk) {
        this.result = wasShipHit;
        this.sunkShip = wasShipSunk;
    }

    public Boolean getResult() {
        return result;
    }

    public Boolean sunkShip() {
        return sunkShip;
    }
}