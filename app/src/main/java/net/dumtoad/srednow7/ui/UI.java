package net.dumtoad.srednow7.ui;

import java.io.Serializable;

public interface UI extends Serializable {

    void initDisplayQueue(Enum queueID, Display defaultDisplay);

    void display(Enum queueID, Display display);

    void invalidateView(Enum queueID);

    void reset();

    interface Display extends Serializable {
        void show(Enum queueID);
    }

}
