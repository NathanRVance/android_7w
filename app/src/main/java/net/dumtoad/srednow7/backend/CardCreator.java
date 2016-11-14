package net.dumtoad.srednow7.backend;

import java.io.Serializable;
import java.util.List;

public interface CardCreator extends Serializable {

    List<CardList> dealHands(int era, int numPlayers);

    List<Wonder[]> getWonders();

    CardList getAllCards();
}
