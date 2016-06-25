package net.dumtoad.android_7w.cards;

import java.util.ArrayList;

public class CardCollection extends ArrayList<Card> {

    public CardCollection(ArrayList<Card> cards, String order) {
        String[] names = order.split("\n");
        for(String name : names) {
            for(Card card : cards) {
                if(card.getName().toString().equals(name)) {
                    add(card);
                }
            }
        }
    }

    public CardCollection() {
        super();
    }

    public String getOrder() {
        StringBuilder sb = new StringBuilder();
        for(Card card : this) {
            sb.append(card.getName().toString()).append('\n');
        }
        return sb.toString();
    }

}
