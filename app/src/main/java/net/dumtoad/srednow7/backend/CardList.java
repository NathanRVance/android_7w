package net.dumtoad.srednow7.backend;

import java.util.List;

public interface CardList extends List<Card>, Savable {

    void sort();

    Card get(Enum name);
}
