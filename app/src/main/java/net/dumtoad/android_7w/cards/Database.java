package net.dumtoad.android_7w.cards;

import java.util.ArrayList;

/**
 * Created by nathav63 on 7/27/15.
 */
public class Database {

    private ArrayList<Deck> decks;
    private ArrayList<Hand> hands;
    private int numPlayers;

    public Database(int numPlayers) {
        this.numPlayers = numPlayers;
        Generate.generateCards();
        decks = new ArrayList<>();
        decks.add(Generate.getEra1Deck(numPlayers));
        decks.add(Generate.getEra2Deck(numPlayers));
        decks.add(Generate.getEra3Cards(numPlayers)); //Contains numPlayers * 6 - 2 cards

        Deck guilds = Generate.getGuildCards();
        guilds.shuffle();
        //Add numPlayers + 2 guild cards to Era3 deck
        for(int i = 0; i < numPlayers + 2; i++) {
            decks.get(2).add(guilds.get(i));
        }

        for(int i = 0; i < 3; i++) {
            decks.get(i).shuffle();
        }
    }

    public void dealHands(int era) {
        hands = new ArrayList<>();
        int index = 0;
        for(int i = 0; i < numPlayers; i++) {
            Hand hand = new Hand();
            for(int j = 0; j < 7; j++) {
                hand.add(decks.get(era).get(index++));
            }
            hand.sort();
            hands.add(hand);
        }
    }

    public ArrayList<Hand> getHands() {
        return hands;
    }

}
