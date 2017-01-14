package net.dumtoad.srednow7.backend.implementation;

import android.support.annotation.NonNull;

import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.CardList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class CardListImpl implements CardList {

    private static final long serialVersionUID = 2694500649855686851L;
    private transient List<Card> cards = new ArrayList<>();

    //Sorts by type
    @Override
    public void sort() {
        ArrayList<Card> sorted = new ArrayList<>();
        for (Card.Type type : Card.Type.values()) {
            for (Card card : this) {
                if (card.getType().equals(type)) {
                    sorted.add(card);
                }
            }
        }
        clear();
        addAll(sorted);
    }

    @Override
    public Card get(String name) {
        for (Card card : this) {
            if (card.getName().equals(name)) {
                return card;
            }
        }
        return null;
    }

    private synchronized void writeObject(ObjectOutputStream s) throws IOException {
        String[] names = new String[size()];
        for (int i = 0; i < size(); i++) {
            names[i] = get(i).getName();
        }
        s.writeObject(names);
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        cards = new ArrayList<>();
        String[] names = (String[]) s.readObject();
        List<Card> cards = Generate.getAllCards();
        for (String name : names) {
            for (Card card : cards) {
                if (card.getName().equals(name)) {
                    add(card);
                    break;
                }
            }
        }
    }

    @Override
    public synchronized int size() {
        return cards.size();
    }

    @Override
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return cards.contains(o);
    }

    @NonNull
    @Override
    public Iterator<Card> iterator() {
        return cards.iterator();
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return cards.toArray();
    }

    @NonNull
    @Override
    public <T> T[] toArray(@NonNull T[] ts) {
        //noinspection SuspiciousToArrayCall
        return cards.toArray(ts);
    }

    @Override
    public synchronized boolean add(Card card) {
        return cards.add(card);
    }

    @Override
    public synchronized boolean remove(Object o) {
        return cards.remove(o);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> collection) {
        return cards.containsAll(collection);
    }

    @Override
    public synchronized boolean addAll(@NonNull Collection<? extends Card> collection) {
        return cards.addAll(collection);
    }

    @Override
    public synchronized boolean addAll(int i, @NonNull Collection<? extends Card> collection) {
        return cards.addAll(i, collection);
    }

    @Override
    public synchronized boolean removeAll(@NonNull Collection<?> collection) {
        return cards.removeAll(collection);
    }

    @Override
    public synchronized boolean retainAll(@NonNull Collection<?> collection) {
        return cards.retainAll(collection);
    }

    @Override
    public synchronized void clear() {
        cards.clear();
    }

    @Override
    public Card get(int i) {
        return cards.get(i);
    }

    @Override
    public synchronized Card set(int i, Card card) {
        return cards.set(i, card);
    }

    @Override
    public synchronized void add(int i, Card card) {
        cards.add(i, card);
    }

    @Override
    public synchronized Card remove(int i) {
        return cards.remove(i);
    }

    @Override
    public int indexOf(Object o) {
        return cards.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return cards.lastIndexOf(o);
    }

    @Override
    public ListIterator<Card> listIterator() {
        return cards.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<Card> listIterator(int i) {
        return cards.listIterator(i);
    }

    @NonNull
    @Override
    public List<Card> subList(int i, int i1) {
        return cards.subList(i, i1);
    }
}
