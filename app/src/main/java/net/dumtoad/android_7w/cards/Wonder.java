package net.dumtoad.android_7w.cards;

import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import java.util.ArrayList;

public class Wonder {

    private Enum name;
    private ArrayList<Card> stagesA;
    private ArrayList<Card> stagesB;
    private Card.Resource product;

    public Wonder(Enum name) {
        this.name = name;
    }

    public void setStagesA(ArrayList<Card> stagesA) {
        this.stagesA = stagesA;
    }

    public void setStagesB(ArrayList<Card> stagesB) {
        this.stagesB = stagesB;
    }

    public void setResource(Card.Resource product) {
        this.product = product;
    }

    public Card.Resource getResource(){
        return product;
    }

    public String getNameString() {
        return name.name().replace("_", " ");
    }

    public Enum getName() {
        return name;
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
        ForegroundColorSpan fcs = new ForegroundColorSpan(Card.getColorId(product.toString()));
        Card.appendSb(sb, product.toString().toLowerCase(), fcs);
        sb.append('\n');
        ArrayList<Card> stages = getStages(side);
        int i = 1;
        for(Card card : stages) {
            sb.append("\nStage " + i++);
            sb.append('\n');
            sb.append(card.getSummary());
            sb.append("\n--------\n");
        }

        return sb;
    }

}
