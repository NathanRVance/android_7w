package net.dumtoad.srednow7.cards;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;

public class Database {

    private ArrayList<Wonder> wonders;
    private Deck decks[];
    private int numPlayers;

    public Database(int numPlayers) {
        this.numPlayers = numPlayers;
        Generate.generateCards();

        wonders = Generate.getWonders();
        Collections.shuffle(wonders);

        decks = new Deck[3];
        decks[0] = Generate.getEra0Deck(numPlayers);
        decks[1] = Generate.getEra1Deck(numPlayers);
        decks[2] = Generate.getEra2Cards(numPlayers); //Contains numPlayers * 6 - 2 cards

        Deck guilds = Generate.getGuildCards();
        Collections.shuffle(guilds);
        //Add numPlayers + 2 guild cards to Era3 deck
        for(int i = 0; i < numPlayers + 2; i++) {
            decks[2].add(guilds.get(i));
        }

        for(int i = 0; i < 3; i++) {
            Collections.shuffle(decks[i]);
        }
    }

    public ArrayList<Hand> dealHands(int era) {
        ArrayList<Hand> hands = new ArrayList<>();
        int index = 0;
        for(int i = 0; i < numPlayers; i++) {
            Hand hand = new Hand();
            for(int j = 0; j < 7; j++) {
                hand.add(decks[era].get(index++));
            }
            hand.sort();
            hands.add(hand);
        }
        return hands;
    }

    public Wonder drawWonder() {
        return(wonders.remove(0));
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
        decks = new Deck[3];
        decks[0] = new Deck(Generate.getEra0Deck(numPlayers), order.get(0));
        decks[1] = new Deck(Generate.getEra1Deck(numPlayers), order.get(1));

        Deck era2 = Generate.getEra2Cards(numPlayers);
        era2.addAll(Generate.getGuildCards());
        decks[2] = new Deck(era2, order.get(2));
    }

    public Bundle getInstanceState() {
        Bundle outstate = new Bundle();
        String order[] = new String[decks.length];
        for(int i = 0; i < decks.length; i++) {
            order[i] = decks[i].getOrder();
        }
        outstate.putStringArray("deckOrder", order);
        return outstate;
    }

}
