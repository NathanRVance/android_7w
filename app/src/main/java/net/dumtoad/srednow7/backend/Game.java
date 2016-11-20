package net.dumtoad.srednow7.backend;

import java.io.Serializable;
import java.util.List;

public interface Game extends Serializable {

    void initialize(CharSequence[] playerNames, boolean[] ais);

    void finishedTurn();

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

    void discard(Card card);

    void startNewGame();

    void reset();

    enum Direction {
        WEST,
        EAST
    }
}