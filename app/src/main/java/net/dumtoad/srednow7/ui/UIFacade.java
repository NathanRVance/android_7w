package net.dumtoad.srednow7.ui;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class UIFacade implements UI {

    private static final long serialVersionUID = -2416669026990337029L;

    private Map<Enum, Queue<Display>> displayQueues = new HashMap<>();
    private Map<Enum, Display> defaultDisplays = new HashMap<>();
    private Map<Enum, Display> currentDisplays = new HashMap<>();
    private Map<Enum, Boolean> invalid = new HashMap<>();

    @Override
    public void initDisplayQueue(Enum queueID, Display defaultDisplay) {
        displayQueues.put(queueID, new LinkedList<>());
        defaultDisplays.put(queueID, defaultDisplay);
        invalid.put(queueID, true);
    }

    @Override
    public void display(Enum queueID, Display display) {
        displayQueues.get(queueID).add(display);
        if (invalid.get(queueID))
            invalidateView(queueID);
    }

    @Override
    public void invalidateView(Enum queueID) {
        if (displayQueues.get(queueID).isEmpty()) {
            currentDisplays.put(queueID, defaultDisplays.get(queueID));
            currentDisplays.get(queueID).show(queueID);
            invalid.put(queueID, true);
        } else {
            currentDisplays.put(queueID, displayQueues.get(queueID).remove());
            currentDisplays.get(queueID).show(queueID);
            invalid.put(queueID, false);
        }
    }

    @Override
    public void reset() {
        for (Enum queueID : displayQueues.keySet()) {
            displayQueues.put(queueID, new LinkedList<>());
            currentDisplays.clear();
            invalid.put(queueID, true);
        }
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        for (Enum queueID : currentDisplays.keySet()) {
            currentDisplays.get(queueID).show(queueID);
        }
    }
}
