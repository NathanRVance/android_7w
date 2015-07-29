package net.dumtoad.android_7w.cards;

import java.util.ArrayList;

/**
 * Created by nathav63 on 7/28/15.
 */
public class Hand extends ArrayList<Card> {

    //Sorts by type
    public void sort() {
        ArrayList<Card> sorted = new ArrayList<>();
        for(Card.Type type : Card.Type.values()) {
            for(Card card : this) {
                if(card.getType().equals(type)) {
                    sorted.add(card);
                }
            }
        }
        this.clear();
        this.addAll(sorted);
    }

}
