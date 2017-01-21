package net.dumtoad.srednow7.backend.implementation;

import net.dumtoad.srednow7.MainActivity;
import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.CardList;
import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Wonder;
import net.dumtoad.srednow7.backend.implementation.action.Action;
import net.dumtoad.srednow7.backend.implementation.action.AddGoldAdjacent;
import net.dumtoad.srednow7.backend.implementation.action.LoseGold;
import net.dumtoad.srednow7.backend.implementation.specialValue.BestAdjacentGuildVps;
import net.dumtoad.srednow7.backend.implementation.specialValue.MilitaryLosses;
import net.dumtoad.srednow7.backend.implementation.specialValue.MilitaryVictories;
import net.dumtoad.srednow7.backend.implementation.specialValue.SpecialDependsPlayed;
import net.dumtoad.srednow7.backend.implementation.specialValue.SpecialValue;
import net.dumtoad.srednow7.backend.implementation.variableResource.ResourceStrategy;
import net.dumtoad.srednow7.backend.implementation.variableResource.SomethingAlreadyProduced;
import net.dumtoad.srednow7.backend.implementation.variableResource.SomethingNotProduced;
import net.dumtoad.srednow7.backend.implementation.variableResource.StandardResource;
import net.dumtoad.srednow7.backend.implementation.variableResource.StealScience;
import net.dumtoad.srednow7.bus.SaveUtil;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Generate {

    private static List<List<CardImpl>> eras;
    private static List<Wonder[]> wonders;
    private static int numPlayers;
    private static int cardsPerPlayer;
    private static Set<Expansion> expansions;
    private static List<CardImpl> allCards;

    static void initialize() {
        numPlayers = SaveUtil.getPlayerNames().length;
        expansions = SaveUtil.getExpansions();
        Document doc = null;
        try {
            doc = new SAXBuilder().build(MainActivity.getMainActivity().getAssets().open("cards.xml"));
        } catch (IOException | JDOMException e) {
            e.printStackTrace();
        }
        cardsPerPlayer = 7;
        for (Expansion expansion : expansions) {
            if (expansion.cardsPerPlayer > cardsPerPlayer)
                cardsPerPlayer = expansion.cardsPerPlayer;
        }
        System.out.println(cardsPerPlayer);
        eras = getCardsFromXML(doc, cardsPerPlayer * numPlayers);
        for (List<CardImpl> era : eras) {
            for (CardImpl card : era) {
                card.resolveCoupons();
            }
        }
        wonders = getWondersFromXML(doc);
    }

    static List<Wonder[]> getWonders() {
        return wonders;
    }

    static List<CardList> dealHands(int era, int numPlayers) {
        CardList deck = new CardListImpl();
        deck.addAll(eras.get(era));
        Collections.shuffle(deck);


        List<CardList> hands = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < numPlayers; i++) {
            CardList hand = new CardListImpl();
            for (int j = 0; j < cardsPerPlayer; j++) {
                hand.add(deck.get(index++));
            }
            hand.sort();
            hands.add(hand);
        }
        return hands;
    }

    static int getCardsPerPlayer() {
        return cardsPerPlayer;
    }

    public static CardList getAllCards() {
        CardList ret = new CardListImpl();
        ret.addAll(allCards);
        return ret;
    }

    static CardImpl findCardByName(String name) {
        for (CardImpl card : allCards)
            if (card.getName().equals(name))
                return card;
        throw new RuntimeException("Can't find card named " + name);
    }

    private static List<List<CardImpl>> getCardsFromXML(Document doc, int cardsPerEra) {
        allCards = new ArrayList<>();
        List<List<CardImpl>> eras = new ArrayList<>();
        List<List<CardImpl>> indeterminate = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            eras.add(new ArrayList<>());
            indeterminate.add(new ArrayList<>());
        }

        Element root = doc.getRootElement();
        for (Element card : root.getChildren("cards").get(0).getChildren()) {
            CardImpl c = getCard(card);
            boolean pass = true;
            for (Expansion expansion : c.getExpansions()) {
                pass &= expansions.contains(expansion);
            }
            if (!pass) continue;
            if (c.getPlayerCutoffs().length == 0) {
                indeterminate.get(c.getEra()).add(c);
            } else {
                for (int i = 0; i < c.getPlayerCutoffs().length; i++) {
                    if (c.getPlayerCutoffs()[i] <= numPlayers) {
                        eras.get(c.getEra()).add(c);
                    }
                }
            }
            allCards.add(c);
        }

        for (int era = 0; era < eras.size(); era++) {
            int defecit = cardsPerEra - eras.get(era).size();
            if (defecit < 0) throw new RuntimeException("Too many cards!");
            if (defecit > indeterminate.get(era).size())
                throw new RuntimeException("Too few indeterminates!");
            Collections.shuffle(indeterminate.get(era));
            while (defecit-- > 0) {
                eras.get(era).add(indeterminate.get(era).remove(0));
            }
        }
        return eras;
    }

    private static List<Wonder[]> getWondersFromXML(Document doc) {
        List<Wonder[]> wonders = new ArrayList<>();

        Element root = doc.getRootElement();
        for (Element wonder : root.getChildren("wonders").get(0).getChildren()) {

            if(wonder.getChild("expansions") != null) {
                boolean usingAllRequiredExpansions = true;
                for(String expansion : wonder.getChild("expansions").getTextNormalize().split(" ")) {
                    boolean usingExpansion = false;
                    for(Expansion exp : expansions) {
                        usingExpansion |= exp.name().equals(expansion);
                    }
                    usingAllRequiredExpansions &= usingExpansion;
                }
                if(! usingAllRequiredExpansions) continue; //This isn't the wonder we are looking for.
            }

            Wonder[] sides = new Wonder[2];
            for (int side = 0; side < sides.length; side++) {
                WonderImpl.Builder builder = new WonderImpl.Builder(
                        wonder.getAttributeValue("name"), Card.Resource.valueOf(wonder.getAttributeValue("resource")));
                for (Element stage : wonder.getChildren("side").get(side).getChildren()) {
                    builder.addStage(getCard(stage));
                }
                sides[side] = builder.build();
            }
            wonders.add(sides);
        }

        return wonders;
    }

    private static CardImpl getCard(Element card) {
        CardImpl.Builder builder = new CardImpl.Builder(
                Card.Type.valueOf(card.getAttributeValue("type")), card.getAttributeValue("name"));
        if (card.getAttributeValue("era") != null) {
            builder.setEra(Integer.parseInt(card.getAttributeValue("era")));
        }
        for (Element component : card.getChildren()) {
            switch (component.getName()) {
                case "expansions":
                    for (String expansion : component.getTextTrim().split("\\s+")) {
                        builder.addExpansion(Expansion.valueOf(expansion));
                    }
                    break;
                case "players":
                    String[] strings = component.getTextTrim().split("\\s+");
                    int[] players = new int[strings.length];
                    for (int i = 0; i < strings.length; i++) {
                        players[i] = Integer.parseInt(strings[i]);
                    }
                    builder.setPlayerCutoffs(players);
                    break;
                case "costs":
                    builder.setCosts(getResource(component));
                    break;
                case "products":
                    builder.setProducts(getResource(component));
                    break;
                case "trade":
                    builder.setTradeType(Card.TradeType.valueOf(component.getChildText("type")));
                    for (String direction : component.getChildTextTrim("direction").split("\\s+")) {
                        builder.addTradeDirection(Game.Direction.valueOf(direction.trim()));
                    }
                    break;
                case "attribute":
                    for (String attribute : component.getTextTrim().split("\\s+")) {
                        builder.addAttribute(Card.Attribute.valueOf(attribute.trim()));
                    }
                    break;
                case "makesFree":
                    for (String makesFree : component.getTextTrim().split("\\s+")) {
                        builder.setMakesFree(makesFree.trim());
                    }
                    break;
                case "message":
                    builder.setMessage(component.getTextNormalize());
                    break;
                case "action":
                    for (Element action : component.getChildren()) {
                        builder.setAction(getAction(action, builder));
                    }
                    break;
                default:
                    throw new RuntimeException("Can't handle case " + component.getName());
            }
        }
        return builder.build();
    }

    private static ResourceStrategy getResource(Element component) {
        switch (component.getChildren().get(0).getName()) {
            case "stealScience":
                return new StealScience();
            case "somethingIProduceAlready":
                return new SomethingAlreadyProduced();
            case "somethingIDontProduce":
                return new SomethingNotProduced();
            default:
                break;
        }
        StandardResource standardResource = new StandardResource();
        for (Element resource : component.getChildren()) {
            Card.Resource res = Card.Resource.valueOf(resource.getName());
            if (resource.getChildren().size() == 0) {
                standardResource.setResource(res, Integer.parseInt(resource.getTextNormalize()));
            } else {
                standardResource.setResource(res, getSpecialValue(resource.getChildren().get(0)));
            }
        }
        return standardResource;
    }

    private static SpecialValue getSpecialValue(Element special) {
        switch (special.getName()) {
            case "dependsPlayed":
                String count = special.getChildTextNormalize("count");
                return new SpecialDependsPlayed(
                        Card.Type.valueOf(special.getChildTextNormalize("type")),
                        Integer.parseInt(special.getChildTextNormalize("amntPer")),
                        count.contains("adjacent"),
                        count.contains("self"));
            case "militaryVictories":
                count = special.getChildTextNormalize("count");
                return new MilitaryVictories(
                        Integer.parseInt(special.getChildTextNormalize("amntPer")),
                        count.contains("adjacent"),
                        count.contains("self"));
            case "militaryLosses":
                count = special.getChildTextNormalize("count");
                return new MilitaryLosses(
                        Integer.parseInt(special.getChildTextNormalize("amntPer")),
                        count.contains("adjacent"),
                        count.contains("self"));
            case "bestAdjacentGuildVps":
                return new BestAdjacentGuildVps();
            default:
                throw new RuntimeException("Can't handle case " + special.getName());
        }
    }

    private static Action getAction(Element action, CardImpl.Builder builder) {
        switch (action.getName()) {
            case "loseGold":
                LoseGold loseGold;
                if (action.getChildren().size() == 1) {
                    loseGold = new LoseGold(getSpecialValue(action.getChildren().get(0)), builder.build().getName());
                } else {
                    loseGold = new LoseGold(Integer.parseInt(action.getTextNormalize()), builder.build().getName());
                }
                builder.setCallback(loseGold);
                return loseGold;
            case "addGoldAdjacent":
                return new AddGoldAdjacent(Integer.parseInt(action.getTextNormalize()));
            default:
                throw new RuntimeException("Can't handle case " + action.getName());
        }
    }

    public enum Expansion {
        Cities(8);

        int cardsPerPlayer;

        Expansion(int cardsPerPlayer) {
            this.cardsPerPlayer = cardsPerPlayer;
        }
    }
}
