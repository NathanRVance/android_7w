package net.dumtoad.srednow7.backend;

import java.io.Serializable;
import java.util.Map;

public interface ResQuant extends Map<Card.Resource, Integer>, Serializable {

    ResQuant addResources(ResQuant add);

    ResQuant subtractResources(ResQuant sub);

    ResQuant add(Card.Resource resource, int num);

    boolean allZeroOrBelow();

    boolean allZeroOrAbove();
}
