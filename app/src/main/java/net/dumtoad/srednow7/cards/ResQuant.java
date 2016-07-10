package net.dumtoad.srednow7.cards;

import java.util.TreeMap;

public class ResQuant extends TreeMap<Card.Resource, Integer> {

    public ResQuant() {
        super();
        for(Card.Resource resource : Card.Resource.values()) {
            put(resource, 0);
        }
    }

    public ResQuant(String save) {
        String[] pairs = save.split("\n");
        for(String pair : pairs) {
            String[] parts = pair.split(":");
            for(Card.Resource res : Card.Resource.values()) {
                if(res.toString().equals(parts[0])) {
                    put(res, Integer.valueOf(parts[1]));
                }
            }
        }
    }

    public String getString() {
        StringBuilder sb = new StringBuilder();
        for(Card.Resource res : keySet()) {
            sb.append(res.toString()).append(":").append(get(res)).append('\n');
        }
        return sb.toString();
    }

    public ResQuant addResources(ResQuant add) {
        for(Card.Resource resource : add.keySet()) {
            put(resource, get(resource) + add.get(resource));
        }
        return this;
    }

    public ResQuant subtractResources(ResQuant sub) {
        for(Card.Resource resource : sub.keySet()) {
            put(resource, get(resource) - sub.get(resource));
        }
        return this;
    }

    public boolean allZeroOrBelow() {
        for(Card.Resource resource : keySet()) {
            if(get(resource) > 0)
                return false;
        }
        return true;
    }

    public boolean allZeroOrAbove() {
        for(Card.Resource resource : keySet()) {
            if(get(resource) < 0)
                return false;
        }
        return true;
    }

}
