package net.dumtoad.srednow7.backend;

import net.dumtoad.srednow7.backend.implementation.Generate;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public interface Game extends Serializable {

    void initialize(CharSequence[] playerNames, boolean[] ais, Set<Generate.Expansion> expansions);

    Setup getSetup(int playerID);

    /**
     * Returns a list of all players in the game
     *
     * @return list of players, sorted by position, west to east
     */
    List<? extends Player> getPlayers();

    Player getPlayerDirection(Player player, Direction direction);

    Direction getPassingDirection();

    /**
     * @return era number, between 0 and 2 inclusive
     */
    int getEra();

    /**
     * @return round number, between 0 and 5 inclusive (6 if playing an extra card)
     */
    int getRound();

    void discard(Card card);

    void startNewGame();

    void reset();

    enum Direction {
        WEST,
        EAST
    }
}