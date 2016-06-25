package net.dumtoad.android_7w.cards;

import android.os.Bundle;

import net.dumtoad.android_7w.ai.AI;
import net.dumtoad.android_7w.controller.MasterViewController;

import java.util.ArrayList;

public class Player {

    private Hand hand;
    private ArrayList<Card> playedCards;
    private MasterViewController mvc;
    private final String name;
    private Wonder wonder;
    private boolean wonderSide;
    private final boolean isAI;
    private AI ai;
    private int gold;

    public Player(MasterViewController mvc, boolean isAI, String name) {
        this.mvc = mvc;
        playedCards = new ArrayList<>();
        this.isAI = isAI;
        this.name = name;
        hand = new Hand();
    }

    public Player(MasterViewController mvc, Bundle savedInstanceState) {
        this.isAI = savedInstanceState.getBoolean("isAI");
        this.name = savedInstanceState.getString("name");
        this.hand = new Hand(mvc.getDatabase().getAllCards(), savedInstanceState.getString("hand"));
        String wonderName = savedInstanceState.getString("wonder");
        for(Wonder wonder : Generate.getWonders()) {
            if(wonder.getName().toString().equals(wonderName)) {
                this.wonder = wonder;
                break;
            }
        }
        this.wonderSide = savedInstanceState.getBoolean("player" + name + "wonderSide");
        this.gold = savedInstanceState.getInt("player" + name + "gold");
    }

    public Bundle getInstanceState() {
        Bundle outstate = new Bundle();
        outstate.putBoolean("isAI", isAI);
        outstate.putString("name", name);
        outstate.putString("hand", hand.getOrder());
        outstate.putString("wonder", wonder.getName().toString());
        outstate.putBoolean("wonderSide", wonderSide);
        outstate.putInt("gold", gold);
        return outstate;
    }

    public void setWonder(Wonder wonder) {
        this.wonder = wonder;
    }

    public Wonder getWonder() {
        return wonder;
    }

    public boolean isAI() {
        return isAI;
    }

    public void setAI(AI ai) {
        this.ai = ai;
    }

    public AI getAI() {
        return ai;
    }

    public void setWonderSide(boolean wonderSide) {
        this.wonderSide = wonderSide;
    }

    public boolean getWonderSide() {
        return wonderSide;
    }

    public String getName() {
        return name;
    }

    public void setHand(Hand hand) {
        this.hand = hand;
    }

    public Hand getHand() {
        return hand;
    }

    public ArrayList<Card> getPlayedCards() {
        return playedCards;
    }

    public void buildCard(Card card) {
        hand.remove(card); //if exists in hand, remove it.
        playedCards.add(card);
    }

    public void discardCard(Card card) {
        hand.remove(card);
        mvc.getTableController().discard(card);
        gold += 3;
    }

}
