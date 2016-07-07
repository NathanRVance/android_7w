package net.dumtoad.android_7w.cards;

import java.util.ArrayList;

public class CardCollection extends ArrayList<Card> {

    public CardCollection(ArrayList<Card> cards, String order) {
        String[] names = order.split("\n");
        for(String name : names) {
            for(Card card : cards) {
                if(card.getName().toString().equals(name)) {
                    add(card);
                    break;
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

    public boolean contains(Enum name) {
        for(Card card : this) {
            if(card.getName().toString().equals(name.toString())) {
                return true;
            }
        } return false;
    }

}
