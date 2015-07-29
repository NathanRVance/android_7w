package net.dumtoad.android_7w.cards;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by nathav63 on 7/27/15.
 */
public class Deck extends ArrayList<Card> {

    public void shuffle() {
        Collections.shuffle(this);
    }

}
