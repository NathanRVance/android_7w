package net.dumtoad.android_7w.cards;

import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import java.util.ArrayList;

public class Wonder {

    private Enum name;
    private ArrayList<Card> stagesA;
    private ArrayList<Card> stagesB;
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

    public ArrayList<Card> getStages(boolean side) {
        if(side)
            return stagesA;
        else
            return stagesB;
    }

    public SpannableStringBuilder getSummary(boolean side) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append(getNameString());
        sb.append('\n');
        sb.append("produces: ");
        ForegroundColorSpan fcs = new ForegroundColorSpan(Card.getColorId(resource.toString()));
        Card.appendSb(sb, resource.toString().toLowerCase(), fcs);
        ArrayList<Card> stages = getStages(side);
        for(Card card : stages) {
            sb.append('\n');
            sb.append(card.getSummary());
            sb.append("\n--------\n");
        }

        return sb;
    }

}
