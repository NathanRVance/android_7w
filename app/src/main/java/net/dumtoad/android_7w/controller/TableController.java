package net.dumtoad.android_7w.controller;

import net.dumtoad.android_7w.cards.Card;

import java.util.ArrayList;

/**
 * Created by nathav63 on 7/29/15.
 */
public class TableController {

    private MasterViewController mvc;
    private ArrayList<Card> discards;

    public TableController(MasterViewController mvc) {
        this.mvc = mvc;
        discards = new ArrayList<>();
    }

    public void discard(Card card) {
        discards.add(card);
    }

}
