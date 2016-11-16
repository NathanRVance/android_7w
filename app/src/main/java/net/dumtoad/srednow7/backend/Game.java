package net.dumtoad.srednow7.backend;

import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.List;

public interface Game {

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

    Serializable getContents();

    void restoreContents(@Nullable Serializable contents) throws Exception;

    void reset();

    enum Direction {
        WEST,
        EAST
    }
}