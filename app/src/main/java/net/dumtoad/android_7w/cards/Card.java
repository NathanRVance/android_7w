package net.dumtoad.android_7w.cards;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;

import net.dumtoad.android_7w.MainActivity;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

public class Card {

    private Type type;
    private Enum name;
    private String message = "";
    private SortedMap<Resource, Integer> cost;
    private SortedMap<Product, Integer> products;
    public ArrayList<Card> couponsFor;
    public ArrayList<Card> couponedBy;

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

    boolean isBaseResource(String name) {
        switch(name.toLowerCase()) {
            case "wood":
            case "stone":
            case "clay":
            case "ore":
            case "cloth":
            case "glass":
            case "paper":
                return true;
            default: return false;
        }
    }

    public static int getColorId(String id) {
        Context context = MainActivity.getMainActivity();
        return ContextCompat.getColor(context, context.getResources().getIdentifier(id, "color", context.getPackageName()));
    }

    public Card(Type type, Enum name) {
        this.type = type;
        this.name = name;
        this.message = "";

        //Instantiate both to zero
        cost = new TreeMap<>();
        for(Resource res : Resource.values()) {
            cost.put(res, 0);
        }

        products = new TreeMap<>();
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

    public void setCost(Resource resource, int num) {
        cost.put(resource, num);
    }

    public void setProducts(Product product, int num) {
        products.put(product, num);
    }

    public void couponFor(Card card) {
        couponsFor.add(card);
        card.couponedBy(this);
    }

    public void couponedBy(Card card) {
        couponedBy.add(card);
    }

    public static void appendSb(SpannableStringBuilder sb, String text, CharacterStyle style)
    {
        sb.append(text);
        sb.setSpan(style, sb.length()-text.length(), sb.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
    }

    public SpannableStringBuilder getSummary()
    {

        SpannableStringBuilder sb = new SpannableStringBuilder();
        ForegroundColorSpan fcs = new ForegroundColorSpan(getColorId(type.toString()));
        appendSb(sb, type.toString(), fcs);
        sb.append('\n');
        int numNonZero;

        numNonZero=0;
        for(Integer i : cost.values()) {
            if(! i.equals(0))
                numNonZero++;
        }
        if(numNonZero != 0) {
            sb.append("Costs:\n");
            for(Resource resource : cost.keySet()) {
                if(cost.get(resource).equals(0))
                    continue;
                sb.append(" ");
                fcs = new ForegroundColorSpan(getColorId(resource.toString()));
                appendSb(sb, resource.toString().toLowerCase(), fcs);
                sb.append(": ");
                sb.append(cost.get(resource).toString());
                sb.append("\n");
            }
        }

        numNonZero=0;
        for(Integer i : products.values()) {
            if(! i.equals(0))
                numNonZero++;
        }
        if(numNonZero != 0) {
            sb.append("Produces:\n");
            int i = 1;
            for(Product product : products.keySet()) {
                if(products.get(product).equals(0))
                    continue;
                sb.append(" ");
                fcs = new ForegroundColorSpan(getColorId(product.toString()));
                appendSb(sb, product.toString().toLowerCase(), fcs);
                sb.append(": ");
                sb.append(products.get(product).toString());
                if(i < numNonZero && isBaseResource(product.toString())) {
                    sb.append("\tor");
                }
                sb.append("\n");
                i++;
            }
        }

        if(! message.isEmpty()) {
            sb.append(message);
            sb.append('\n');
        }

        if(! couponedBy.isEmpty()) {
            sb.append("Free if owned:\n");
            for(Card card : couponedBy) {
                sb.append(" ");
                fcs = new ForegroundColorSpan(getColorId(card.getType().toString()));
                appendSb(sb, card.getNameString(), fcs);
                sb.append("\n");
            }
        }

        if(! couponsFor.isEmpty()) {
            sb.append("Free if owned:\n");
            for(Card card : couponsFor) {
                sb.append(" ");
                fcs = new ForegroundColorSpan(getColorId(card.getType().toString()));
                appendSb(sb, card.getNameString(), fcs);
                sb.append("\n");
            }
        }

        return sb;
    }
}
