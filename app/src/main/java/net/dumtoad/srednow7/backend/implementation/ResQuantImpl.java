package net.dumtoad.srednow7.backend.implementation;

import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.ResQuant;

import java.io.Serializable;
import java.util.TreeMap;

public class ResQuantImpl extends TreeMap<Card.Resource, Integer> implements ResQuant {

    public ResQuantImpl() {
        super();
        for(Card.Resource resource : Card.Resource.values()) {
            put(resource, 0);
        }
    }

    @Override
    public ResQuant addResources(ResQuant add) {
        for(Card.Resource resource : add.keySet()) {
            put(resource, get(resource) + add.get(resource));
        }
        return this;
    }

    @Override
    public ResQuant subtractResources(ResQuant sub) {
        for(Card.Resource resource : sub.keySet()) {
            put(resource, get(resource) - sub.get(resource));
        }
        return this;
    }

    @Override
    public boolean allZeroOrBelow() {
        for(Card.Resource resource : keySet()) {
            if(get(resource) > 0)
                return false;
        }
        return true;
    }

    @Override
    public boolean allZeroOrAbove() {
        for(Card.Resource resource : keySet()) {
            if(get(resource) < 0)
                return false;
        }
        return true;
    }

    @Override
    public Serializable getContents() {
        StringBuilder sb = new StringBuilder();
        for(Card.Resource res : keySet()) {
            sb.append(res.toString()).append(":").append(get(res)).append('\n');
        }
        return sb.toString();
    }

    @Override
    public void restoreContents(Serializable contents) {
        String[] pairs = ((String) contents).split("\n");
        for(String pair : pairs) {
            String[] parts = pair.split(":");
            for(Card.Resource res : Card.Resource.values()) {
                if(res.toString().equals(parts[0])) {
                    put(res, Integer.valueOf(parts[1]));
                }
            }
        }
    }
}
