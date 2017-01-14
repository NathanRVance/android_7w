package net.dumtoad.srednow7.backend;

import java.io.Serializable;
import java.util.List;

public interface CardList extends List<Card>, Serializable {

    void sort();

    Card get(String name);
}
