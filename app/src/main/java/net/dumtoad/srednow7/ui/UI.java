package net.dumtoad.srednow7.ui;

import java.io.Serializable;

public interface UI extends Serializable {

    void display(DisplayType type, int playerID);

    void invalidateView();

    void reset();

    enum DisplayType {
        Setup,
        WonderSideSelect,
        Turn,
        EndOfGame
    }

}
