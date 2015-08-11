package net.dumtoad.android_7w.cards;

import net.dumtoad.android_7w.ai.AI;
import net.dumtoad.android_7w.controller.MasterViewController;
import net.dumtoad.android_7w.controller.TableController;

import java.util.ArrayList;

/**
 * Created by nathav63 on 7/29/15.
 */
public class Player {

    private Hand hand;
    private ArrayList<Card> playedCards;
    private MasterViewController mvc;
    private final String name;
    private Wonder wonder;
    private final boolean isAI;
    private AI ai;

    public Player(MasterViewController mvc, boolean isAI, String name) {
        this.mvc = mvc;
        playedCards = new ArrayList<>();
        this.isAI = isAI;
        this.name = name;
    }

    public void setWonder(Wonder wonder) {
        this.wonder = wonder;
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
    }

}
