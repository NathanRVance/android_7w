package net.dumtoad.srednow7.backend;

import java.util.Map;

public interface ResQuant extends Map<Card.Resource, Integer>, Savable {

    ResQuant addResources(ResQuant add);

    ResQuant subtractResources(ResQuant sub);

    boolean allZeroOrBelow();

    boolean allZeroOrAbove();
}
