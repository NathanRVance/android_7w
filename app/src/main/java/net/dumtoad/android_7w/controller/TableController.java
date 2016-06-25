package net.dumtoad.android_7w.controller;

import android.os.Bundle;

import net.dumtoad.android_7w.cards.Card;
import net.dumtoad.android_7w.cards.CardCollection;

public class TableController {

    private MasterViewController mvc;
    private CardCollection discards;
    private int era;

    public TableController(MasterViewController mvc) {
        this.mvc = mvc;
        discards = new CardCollection();
        era = 0;
    }

    public TableController(MasterViewController mvc, Bundle savedInstanceState) {
        this.mvc = mvc;
        discards = new CardCollection(mvc.getDatabase().getAllCards(), savedInstanceState.getString("discards"));
        era = savedInstanceState.getInt("era");
    }

    public void discard(Card card) {
        discards.add(card);
    }

    public int getEra() {
        return era;
    }

    public void nextEra() {
        era++;
    }

    public Bundle getInstanceState() {
        Bundle outstate = new Bundle();
        outstate.putString("discards", discards.getOrder());
        outstate.putInt("era", era);
        return outstate;
    }

}
