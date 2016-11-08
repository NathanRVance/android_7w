package net.dumtoad.srednow7.backend;

import java.util.List;

public interface CardCreator extends Savable {

    List<CardList> dealHands(int era, int numPlayers);

    List<Wonder[]> getWonders();

    CardList getAllCards();
}
