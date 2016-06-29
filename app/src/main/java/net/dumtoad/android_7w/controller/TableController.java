package net.dumtoad.android_7w.controller;

import android.os.Bundle;

import net.dumtoad.android_7w.cards.Card;
import net.dumtoad.android_7w.cards.CardCollection;

public class TableController {

    private MasterViewController mvc;
    private TurnController tc;
    private CardCollection discards;
    private int era;
    private int playerTurn = 0;

    public TableController(MasterViewController mvc) {
        this.mvc = mvc;
        this.tc = new TurnController(mvc);
        discards = new CardCollection();
        era = 0;
    }

    public TableController(MasterViewController mvc, Bundle savedInstanceState) {
        this.mvc = mvc;
        discards = new CardCollection(mvc.getDatabase().getAllCards(), savedInstanceState.getString("discards"));
        era = savedInstanceState.getInt("era");
        tc = new TurnController(mvc, savedInstanceState.getBundle("tc"));
    }

    public void discard(Card card) {
        discards.add(card);
    }

    public TurnController getTurnController() {
        return tc;
    }

    public int getEra() {
        return era;
    }

    public void nextEra() {
        era++;
    }

    public void passTheHand() {

    }

    public void nextPlayerStart() {
        if(playerTurn < mvc.getNumPlayers()) {
            tc.startTurn(playerTurn++);
        } else {
            playerTurn = 0;
            passTheHand();
            nextPlayerStart();
        }
    }

    public Bundle getInstanceState() {
        Bundle outstate = new Bundle();
        outstate.putString("discards", discards.getOrder());
        outstate.putInt("era", era);
        outstate.putBundle("tc", tc.getInstanceState());
        return outstate;
    }

}
