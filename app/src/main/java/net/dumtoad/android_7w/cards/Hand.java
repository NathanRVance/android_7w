package net.dumtoad.android_7w.cards;

import java.util.ArrayList;

public class Hand extends CardCollection {

    public Hand(ArrayList<Card> cards, String order) {
        super(cards, order);
    }

    public Hand() {
        super();
    }

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
