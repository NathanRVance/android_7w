package net.dumtoad.srednow7.cards;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;

public class Database {

    private ArrayList<Wonder> wonders;
    private ArrayList<Deck> decks;
    private int numPlayers;

    public Database(int numPlayers) {
        this.numPlayers = numPlayers;
        Generate.generateCards();

        wonders = Generate.getWonders();
        Collections.shuffle(wonders);

        decks = new ArrayList<>();
        decks.add(Generate.getEra0Deck(numPlayers));
        decks.add(Generate.getEra1Deck(numPlayers));
        decks.add(Generate.getEra2Cards(numPlayers)); //Contains numPlayers * 6 - 2 cards

        Deck guilds = Generate.getGuildCards();
        Collections.shuffle(guilds);
        //Add numPlayers + 2 guild cards to Era3 deck
        for(int i = 0; i < numPlayers + 2; i++) {
            decks.get(2).add(guilds.get(i));
        }

        for(int i = 0; i < 3; i++) {
            Collections.shuffle(decks.get(i));
        }
    }

    public ArrayList<Hand> dealHands(int era) {
        ArrayList<Hand> hands = new ArrayList<>();
        int index = 0;
        for(int i = 0; i < numPlayers; i++) {
            Hand hand = new Hand();
            for(int j = 0; j < 7; j++) {
                hand.add(decks.get(era).get(index++));
            }
            hand.sort();
            hands.add(hand);
        }
        return hands;
    }

    public Wonder drawWonder() {
        return(wonders.remove(0));
    }

    public Bundle getInstanceState() {
        Bundle outstate = new Bundle();
        ArrayList<String> order = new ArrayList<>();
        for(Deck deck : decks) {
            order.add(deck.getOrder());
        }
        outstate.putStringArrayList("deckOrder", order);
        return outstate;
    }

    public Deck getAllCards() {
        Deck deck = Generate.getEra0Deck(7);
        deck.addAll(Generate.getEra1Deck(7));
        deck.addAll(Generate.getEra2Cards(7));
        deck.addAll(Generate.getGuildCards());
        return deck;
    }

    public void onRestoreInstanceState (Bundle savedInstanceState) {
        numPlayers = savedInstanceState.getInt("numPlayers");
        ArrayList<String> order = savedInstanceState.getStringArrayList("deckOrder");
        if(order == null) {
            return;
        }
        decks = new ArrayList<>();
        decks.add(new Deck(Generate.getEra0Deck(numPlayers), order.get(0)));
        decks.add(new Deck(Generate.getEra1Deck(numPlayers), order.get(1)));

        Deck era2 = Generate.getEra2Cards(numPlayers);
        era2.addAll(Generate.getGuildCards());
        decks.add(new Deck(era2, order.get(2)));
    }

}
