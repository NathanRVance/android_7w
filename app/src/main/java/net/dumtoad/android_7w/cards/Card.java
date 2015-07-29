package net.dumtoad.android_7w.cards;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nathav63 on 7/27/15.
 */
public class Card {

    private Type type;
    private Enum name;
    private String message;
    private HashMap<Resource, Integer> cost;
    private HashMap<Product, Integer> products;
    public ArrayList<Enum> couponsFor;
    public ArrayList<Enum> couponedBy;

    public enum Type {
        RESOURCE,
        INDUSTRY,
        STRUCTURE,
        COMMERCIAL,
        MILITARY,
        SCIENCE,
        GUILD,
        STAGE
    }

    public enum Resource {
        WOOD,
        STONE,
        CLAY,
        ORE,
        CLOTH,
        GLASS,
        PAPER,
        GOLD
    }

    public enum Product {
        WOOD,
        STONE,
        CLAY,
        ORE,
        CLOTH,
        GLASS,
        PAPER,
        GOLD,
        COMPASS,
        GEAR,
        TABLET,
        SHIELD,
        VP
    }

    public Card(Type type, Enum name) {
        this.type = type;
        this.name = name;
        this.message = "";

        //Instantiate both to zero
        cost = new HashMap<>();
        for(Resource res : Resource.values()) {
            cost.put(res, 0);
        }

        products = new HashMap<>();
        for(Product prod : Product.values()) {
            products.put(prod, 0);
        }

        couponedBy = new ArrayList<>();
        couponsFor = new ArrayList<>();
    }

    public String getNameString() {
        return name.name().replace("_", " ");
    }

    public Enum getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setCost(Resource resource, int num) {
        cost.put(resource, num);
    }

    public void setProducts(Product product, int num) {
        products.put(product, num);
    }

    public HashMap<Resource, Integer> getCost() {
        return cost;
    }

    public HashMap<Product, Integer> getProducts() {
        return products;
    }

    public void couponFor(Card card) {
        couponsFor.add(card.getName());
        card.couponedBy(this);
    }

    public void couponedBy(Card card) {
        couponedBy.add(card.getName());
    }

    //Makes a new card that looks exactly the same as this one
    public Card getCopy() {
        Card card = new Card(type, name);
        card.setMessage(message);
        for(Resource res : Resource.values()) {
            card.setCost(res, cost.get(res));
        }
        for(Product prod : Product.values()) {
            card.setProducts(prod, products.get(prod));
        }
        for(Enum e : couponsFor) {
            card.couponsFor.add(e);
        }
        for(Enum e : couponedBy) {
            card.couponedBy.add(e);
        }
        return card;
    }
}
