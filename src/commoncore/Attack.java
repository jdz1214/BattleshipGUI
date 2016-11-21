package commoncore;

import java.io.Serializable;

/**
 * Created by jdz on 8/29/16.
 */
public class Attack implements Serializable {
    private String attackSpotRowColStr;
    //Constructor

    public Attack (String attackSpotRowColStr) {
        this.attackSpotRowColStr = attackSpotRowColStr;
    }

    public String getAttackSpotRowColStr() {
        return attackSpotRowColStr;
    }
}