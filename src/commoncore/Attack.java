package commoncore;

/**
 * Created by jdz on 8/29/16.
 */
public class Attack {
    private Spot attackSpot;
    //Constructor

    public Attack (Spot spotToAttack) {
        this.attackSpot = spotToAttack;
    }

    public Spot getSpot() {
        return attackSpot;
    }
}