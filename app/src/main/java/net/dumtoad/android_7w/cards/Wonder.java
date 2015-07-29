package net.dumtoad.android_7w.cards;

import java.util.ArrayList;

/**
 * Created by nathav63 on 7/28/15.
 */
public class Wonder {

    private Enum name;
    private ArrayList<Card> stagesA;
    private ArrayList<Card> stagesB;
    private boolean side;

    public Wonder(Enum name, ArrayList<Card> stagesA, ArrayList<Card> stagesB) {
        this.name = name;
        this.stagesA = stagesA;
        this.stagesB = stagesB;
    }

    public String getNameString() {
        return name.name().replace("_", " ");
    }

    public Enum getName() {
        return name;
    }

    public ArrayList<Card> getStagesA() {
        return stagesA;
    }

    public ArrayList<Card> getStagesB() {
        return stagesB;
    }

    public void setSide(boolean isSideA) {
        side = isSideA;
    }

    public boolean getSide() {
        return side;
    }

    public ArrayList<Card> getStages() {
        if(side)
            return stagesA;
        else
            return stagesB;
    }

}
