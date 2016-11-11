package net.dumtoad.srednow7.backend.implementation;

import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.CardList;
import net.dumtoad.srednow7.bus.Bus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CardListImpl extends ArrayList<Card> implements CardList {

    //Sorts by type
    @Override
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

    @Override
    public Card get(Enum name) {
        for (Card card : this) {
            if(card.getEnum() == name) {
                return card;
            }
        }
        return null;
    }

    @Override
    public Serializable getContents() {
        StringBuilder sb = new StringBuilder();
        for(Card card : this) {
            sb.append(card.getEnum().toString()).append('\n');
        }
        return sb.toString();
    }

    @Override
    public void restoreContents(Serializable contents) {
        List<Card> cards = Bus.bus.getGame().getCardCreator().getAllCards();
        String[] names = ((String) contents).split("\n");
        for(String name : names) {
            for(Card card : cards) {
                if(card.getEnum().toString().equals(name)) {
                    add(card);
                    break;
                }
            }
        }
    }
}
