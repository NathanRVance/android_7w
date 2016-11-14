package net.dumtoad.srednow7.backend.implementation;

import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.CardCreator;
import net.dumtoad.srednow7.backend.CardList;
import net.dumtoad.srednow7.backend.Wonder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class CardCreatorImpl implements CardCreator {

    private static final long serialVersionUID = 881644334039888008L;
    private transient List<Wonder[]> wonders;
    private List<CardList> decks;
    private int numPlayers;

    CardCreatorImpl(int numPlayers) {
        this.numPlayers = numPlayers;
        Generate.generateBuilders();
        initializeWonders();
        initializeDecks();
    }

    private void initializeWonders() {
        wonders = new ArrayList<>();
        List<Wonder> wondersA = Generate.getWonders(true);
        List<Wonder> wondersB = Generate.getWonders(false);
        for (int i = 0; i < wondersA.size(); i++) {
            wonders.add(new Wonder[]{wondersA.get(i), wondersB.get(i)});
        }
    }

    private void initializeDecks() {
        decks = new ArrayList<>();
        decks.add(Generate.getEra0Deck(numPlayers));
        decks.add(Generate.getEra1Deck(numPlayers));
        decks.add(Generate.getEra2Cards(numPlayers)); //Contains numPlayers * 6 - 2 cards

        List<Card> guilds = Generate.getGuildCards();
        Collections.shuffle(guilds);
        //Add numPlayers + 2 guild cards to Era3 deck
        for (int i = 0; i < numPlayers + 2; i++) {
            decks.get(2).add(guilds.get(i));
        }

        for (int i = 0; i < 3; i++) {
            Collections.shuffle(decks.get(i));
        }
    }

    @Override
    public List<CardList> dealHands(int era, int numPlayers) {
        List<CardList> hands = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < numPlayers; i++) {
            CardList hand = new CardListImpl();
            for (int j = 0; j < 7; j++) {
                hand.add(decks.get(era).get(index++));
            }
            hand.sort();
            hands.add(hand);
        }
        return hands;
    }

    @Override
    public List<Wonder[]> getWonders() {
        return new ArrayList<>(wonders);
    }

    @Override
    public CardList getAllCards() {
        CardList cards = Generate.getEra0Deck(7);
        cards.addAll(Generate.getEra1Deck(7));
        cards.addAll(Generate.getEra2Cards(7));
        cards.addAll(Generate.getGuildCards());
        return cards;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        Generate.generateBuilders();
        initializeWonders();
    }
}
