package net.dumtoad.android_7w.cards;

import net.dumtoad.android_7w.controller.MasterViewController;
import net.dumtoad.android_7w.controller.TableController;

import java.util.ArrayList;

/**
 * Created by nathav63 on 7/29/15.
 */
public class Player {

    private Wonder wonder;
    private Hand hand;
    private ArrayList<Card> playedCards;
    private MasterViewController mvc;
    private TableController tc;

    public Player(MasterViewController mvc, TableController tc) {
        this.mvc = mvc;
        this.tc = tc;
        playedCards = new ArrayList<>();
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

    }

}
