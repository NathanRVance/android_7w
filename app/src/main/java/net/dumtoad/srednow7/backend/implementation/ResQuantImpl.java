package net.dumtoad.srednow7.backend.implementation;

import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.ResQuant;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.TreeMap;

public class ResQuantImpl extends TreeMap<Card.Resource, Integer> implements ResQuant {

    private static final long serialVersionUID = 5950476747377001627L;

    public ResQuantImpl() {
        super();
        for (Card.Resource resource : Card.Resource.values()) {
            put(resource, 0);
        }
    }

    @Override
    public ResQuant addResources(ResQuant add) {
        for (Card.Resource resource : add.keySet()) {
            put(resource, get(resource) + add.get(resource));
        }
        return this;
    }

    @Override
    public ResQuant subtractResources(ResQuant sub) {
        for (Card.Resource resource : sub.keySet()) {
            put(resource, get(resource) - sub.get(resource));
        }
        return this;
    }

    @Override
    public ResQuant add(Card.Resource resource, int num) {
        put(resource, get(resource) + num);
        return this;
    }

    @Override
    public boolean allZeroOrBelow() {
        for (Card.Resource resource : keySet()) {
            if (get(resource) > 0)
                return false;
        }
        return true;
    }

    @Override
    public boolean allZeroOrAbove() {
        for (Card.Resource resource : keySet()) {
            if (get(resource) < 0)
                return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object other) {
        if(! (other instanceof ResQuantImpl)) return false;
        boolean equal = true;
        for(Card.Resource resource : Card.Resource.values()) {
            equal &= get(resource) == ((ResQuantImpl) other).get(resource);
        }
        return equal;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
    }
}
