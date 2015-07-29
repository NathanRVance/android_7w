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
    private Card.Resource resource;

    public Wonder(Enum name) {
        this.name = name;
    }

    public void setStagesA(ArrayList<Card> stagesA) {
        this.stagesA = stagesA;
    }

    public void setStagesB(ArrayList<Card> stagesB) {
        this.stagesB = stagesB;
    }

    public void setResource(Card.Resource resource) {
        this.resource = resource;
    }

    public Card.Resource getResource(){
        return resource;
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
