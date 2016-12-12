package net.dumtoad.srednow7.backend.implementation;

import net.dumtoad.srednow7.MainActivity;
import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.CardList;
import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Wonder;
import net.dumtoad.srednow7.backend.implementation.specialValue.AdjacentMilitaryLosses;
import net.dumtoad.srednow7.backend.implementation.specialValue.BestAdjacentGuildVps;
import net.dumtoad.srednow7.backend.implementation.specialValue.SpecialDependsPlayed;
import net.dumtoad.srednow7.backend.implementation.variableResource.ResourceStrategy;
import net.dumtoad.srednow7.backend.implementation.variableResource.StandardResource;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Generate {

    private static final Enum[] era0deck = {Generate.Cards.Lumber_Yard, Generate.Cards.Stone_Pit, Generate.Cards.Clay_Pool, Generate.Cards.Ore_Vein, Generate.Cards.Clay_Pit, Generate.Cards.Timber_Yard, Generate.Cards.Loom,
            Generate.Cards.Glassworks, Generate.Cards.Press, Generate.Cards.Baths, Generate.Cards.Altar, Generate.Cards.Theater, Generate.Cards.East_Trading_Post, Generate.Cards.West_Trading_Post, Generate.Cards.Marketplace,
            Generate.Cards.Stockade, Generate.Cards.Barracks, Generate.Cards.Guard_Tower, Generate.Cards.Apothecary, Generate.Cards.Workshop, Generate.Cards.Scriptorium, Generate.Cards.Lumber_Yard, Generate.Cards.Ore_Vein, Generate.Cards.Excavation,
            Generate.Cards.Pawnshop, Generate.Cards.Tavern, Generate.Cards.Guard_Tower, Generate.Cards.Scriptorium, Generate.Cards.Stone_Pit, Generate.Cards.Clay_Pool, Generate.Cards.Forest_Cave, Generate.Cards.Altar, Generate.Cards.Tavern,
            Generate.Cards.Barracks, Generate.Cards.Apothecary, Generate.Cards.Tree_Farm, Generate.Cards.Mine, Generate.Cards.Loom, Generate.Cards.Glassworks, Generate.Cards.Press, Generate.Cards.Theater, Generate.Cards.Marketplace, Generate.Cards.Pawnshop,
            Generate.Cards.Baths, Generate.Cards.Tavern, Generate.Cards.East_Trading_Post, Generate.Cards.West_Trading_Post, Generate.Cards.Stockade, Generate.Cards.Workshop};
    private static final Enum[] era1deck = {Generate.Cards.Sawmill, Generate.Cards.Quarry, Generate.Cards.Brickyard, Generate.Cards.Foundry, Generate.Cards.Loom, Generate.Cards.Glassworks, Generate.Cards.Press, Generate.Cards.Aqueduct,
            Generate.Cards.Temple, Generate.Cards.Statue, Generate.Cards.Courthouse, Generate.Cards.Forum, Generate.Cards.Caravansery, Generate.Cards.Vineyard, Generate.Cards.Walls, Generate.Cards.Stables, Generate.Cards.Archery_Range,
            Generate.Cards.Dispensary, Generate.Cards.Laboratory, Generate.Cards.Library, Generate.Cards.School, Generate.Cards.Sawmill, Generate.Cards.Quarry, Generate.Cards.Brickyard, Generate.Cards.Foundry, Generate.Cards.Bazaar, Generate.Cards.Training_Ground,
            Generate.Cards.Dispensary, Generate.Cards.Loom, Generate.Cards.Glassworks, Generate.Cards.Press, Generate.Cards.Courthouse, Generate.Cards.Caravansery, Generate.Cards.Stables, Generate.Cards.Laboratory, Generate.Cards.Temple,
            Generate.Cards.Forum, Generate.Cards.Caravansery, Generate.Cards.Vineyard, Generate.Cards.Training_Ground, Generate.Cards.Archery_Range, Generate.Cards.Library, Generate.Cards.Aqueduct, Generate.Cards.Statue, Generate.Cards.Forum,
            Generate.Cards.Bazaar, Generate.Cards.Walls, Generate.Cards.Training_Ground, Generate.Cards.School};
    private static final Enum[] era2deck = {Generate.Cards.Pantheon, Generate.Cards.Gardens, Generate.Cards.Town_Hall, Generate.Cards.Palace, Generate.Cards.Senate, Generate.Cards.Haven, Generate.Cards.Lighthouse,
            Generate.Cards.Arena, Generate.Cards.Fortifications, Generate.Cards.Arsenal, Generate.Cards.Siege_Workshop, Generate.Cards.Lodge, Generate.Cards.Observatory, Generate.Cards.University, Generate.Cards.Academy, Generate.Cards.Study,
            Generate.Cards.Gardens, Generate.Cards.Haven, Generate.Cards.Chamber_Of_Commerce, Generate.Cards.Circus, Generate.Cards.Arsenal, Generate.Cards.University, Generate.Cards.Town_Hall, Generate.Cards.Senate, Generate.Cards.Arena,
            Generate.Cards.Circus, Generate.Cards.Siege_Workshop, Generate.Cards.Study, Generate.Cards.Pantheon, Generate.Cards.Town_Hall, Generate.Cards.Lighthouse, Generate.Cards.Chamber_Of_Commerce, Generate.Cards.Circus,
            Generate.Cards.Lodge, Generate.Cards.Palace, Generate.Cards.Arena, Generate.Cards.Fortifications, Generate.Cards.Arsenal, Generate.Cards.Observatory, Generate.Cards.Academy};
    private static final Enum[] guilds = {Generate.Cards.Workers_Guild, Generate.Cards.Craftmens_Guild, Generate.Cards.Traders_Guild, Generate.Cards.Philosophers_Guild, Generate.Cards.Spy_Guild,
            Generate.Cards.Strategy_Guild, Generate.Cards.Shipowners_Guild, Generate.Cards.Scientists_Guild, Generate.Cards.Magistrates_Guild, Generate.Cards.Builders_Guild};
    private static List<CardImpl> cards;
    private static List<Wonder> wondersA;
    private static List<Wonder> wondersB;
    private static CardList allCards;

    static {
        Document doc = null;
        try {
            doc = new SAXBuilder().build(MainActivity.getMainActivity().getAssets().open("cards.xml"));
        } catch (IOException | JDOMException e) {
            e.printStackTrace();
        }
        cards = getCardsFromXML(doc);
        for (CardImpl card : cards) {
            card.resolveCoupons();
        }
        wondersA = getWondersFromXML(doc, 0);
        wondersB = getWondersFromXML(doc, 1);
    }

    static List<Wonder[]> getWonders() {
        List<Wonder[]> wonders = new ArrayList<>();
        for (int i = 0; i < wondersA.size(); i++) {
            wonders.add(new Wonder[]{wondersA.get(i), wondersB.get(i)});
        }

        return wonders;
    }

    static List<CardList> dealHands(int era, int numPlayers) {
        CardList deck;
        switch (era) {
            case 0:
                deck = getEra0Deck(numPlayers);
                break;
            case 1:
                deck = getCardsDeck(numPlayers);
                break;
            case 2:
                deck = getCardsCards(numPlayers);
                CardList guilds = Generate.getGuildCards();
                Collections.shuffle(guilds);
                //Add numPlayers + 2 guild cards to the deck
                for (int i = 0; i < numPlayers + 2; i++) {
                    deck.add(guilds.get(i));
                }
                break;
            default:
                throw new IllegalArgumentException("Cannot get deck for era " + era);
        }
        Collections.shuffle(deck);


        List<CardList> hands = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < numPlayers; i++) {
            CardList hand = new CardListImpl();
            for (int j = 0; j < 7; j++) {
                hand.add(deck.get(index++));
            }
            hand.sort();
            hands.add(hand);
        }
        return hands;
    }

    static CardList getAllCards() {
        if (allCards == null) {
            allCards = getEra0Deck(7);
            allCards.addAll(getCardsDeck(7));
            allCards.addAll(getCardsCards(7));
            allCards.addAll(getGuildCards());
        }
        return allCards;
    }

    private static CardList getEra0Deck(int numPlayers) {
        return getDeck(era0deck, numPlayers * 7);
    }

    private static CardList getCardsDeck(int numPlayers) {
        return getDeck(era1deck, numPlayers * 7);
    }

    private static CardList getCardsCards(int numPlayers) {
        return getDeck(era2deck, numPlayers * 6 - 2);
    }

    private static CardList getGuildCards() {
        return getDeck(guilds, 10);
    }

    private static CardList getDeck(Enum[] cardNames, int numCards) {
        CardList deck = new CardListImpl();
        for (int i = 0; i < numCards; i++) {
            deck.add(findCardByName(cardNames[i]));
        }
        return deck;
    }

    static CardImpl findCardByName(Enum name) {
        for (CardImpl card : cards)
            if (card.getEnum() == name)
                return card;
        throw new RuntimeException("That shouldn't have happended!");
    }

    private static List<CardImpl> getCardsFromXML(Document doc) {
        List<CardImpl> cards = new ArrayList<>();

        Element root = doc.getRootElement();
        for (Element card : root.getChildren("cards").get(0).getChildren()) {
            cards.add(getCard(card));
        }
        return cards;
    }

    private static List<Wonder> getWondersFromXML(Document doc, int side) {
        List<Wonder> wonders = new ArrayList<>();

        Element root = doc.getRootElement();
        for (Element wonder : root.getChildren("wonders").get(0).getChildren()) {
            System.out.println("Processing wonder " + wonder.getAttributeValue("name"));
            WonderImpl.Builder builder = new WonderImpl.Builder(
                    Wonders.valueOf(wonder.getAttributeValue("name")), Card.Resource.valueOf(wonder.getAttributeValue("resource")));
            for (Element stage : wonder.getChildren().get(side).getChildren()) {
                builder.addStage(getCard(stage));
            }
            wonders.add(builder.build());
        }

        return wonders;
    }

    private static CardImpl getCard(Element card) {
        CardImpl.Builder builder = new CardImpl.Builder(
                Card.Type.valueOf(card.getAttributeValue("type")), Cards.valueOf(card.getAttributeValue("name")));
        for (Element component : card.getChildren()) {
            switch (component.getName()) {
                case "costs":
                    builder.setCosts(getResource(component));
                    break;
                case "products":
                    builder.setProducts(getResource(component));
                    break;
                case "trade":
                    builder.setTradeType(Card.TradeType.valueOf(component.getChildText("type")));
                    for (String direction : component.getChildTextTrim("direction").split("\n")) {
                        builder.addTradeDirection(Game.Direction.valueOf(direction.trim()));
                    }
                    break;
                case "attribute":
                    for (String attribute : component.getTextTrim().split("\n")) {
                        builder.addAttribute(Card.Attribute.valueOf(attribute.trim()));
                    }
                    break;
                case "makesFree":
                    for (String makesFree : component.getTextTrim().split("\n")) {
                        builder.setMakesFree(Cards.valueOf(makesFree.trim()));
                    }
                    break;
                case "message":
                    builder.setMessage(component.getTextNormalize());
                    break;
                default:
                    throw new RuntimeException("Can't handle case " + component.getName());
            }
        }
        return builder.build();
    }

    private static ResourceStrategy getResource(Element component) {
        StandardResource standardResource = new StandardResource();
        for (Element resource : component.getChildren()) {
            Card.Resource res = Card.Resource.valueOf(resource.getName());
            if (resource.getChildren().size() == 0) {
                standardResource.setResource(res, Integer.parseInt(resource.getTextNormalize()));
            } else {
                for (Element special : resource.getChildren()) {
                    switch (special.getName()) {
                        case "dependsPlayed":
                            String count = special.getChildTextNormalize("count");
                            standardResource.setResource(res, new SpecialDependsPlayed(
                                    Card.Type.valueOf(special.getChildTextNormalize("type")),
                                    Integer.parseInt(special.getChildTextNormalize("goldPer")),
                                    count.contains("adjacent"),
                                    count.contains("self")));
                            break;
                        case "militaryLosses":
                            standardResource.setResource(res, new AdjacentMilitaryLosses());
                            break;
                        case "bestAdjacentGuildVps":
                            standardResource.setResource(res, new BestAdjacentGuildVps());
                            break;
                        default:
                            throw new RuntimeException("Can't handle case " + special.getName());
                    }
                }
            }
        }
        return standardResource;
    }

    private enum Cards {
        Lumber_Yard,
        Stone_Pit,
        Clay_Pool,
        Ore_Vein,
        Tree_Farm,
        Excavation,
        Clay_Pit,
        Timber_Yard,
        Forest_Cave,
        Mine,
        Loom,
        Glassworks,
        Press,
        Pawnshop,
        Baths,
        Altar,
        Theater,
        Tavern,
        East_Trading_Post,
        West_Trading_Post,
        Marketplace,
        Stockade,
        Barracks,
        Guard_Tower,
        Apothecary,
        Workshop,
        Scriptorium,
        Sawmill,
        Quarry,
        Brickyard,
        Foundry,
        Aqueduct,
        Temple,
        Statue,
        Courthouse,
        Forum,
        Caravansery,
        Vineyard,
        Bazaar,
        Walls,
        Training_Ground,
        Stables,
        Archery_Range,
        Dispensary,
        Laboratory,
        Library,
        School,
        Pantheon,
        Gardens,
        Town_Hall,
        Palace,
        Senate,
        Haven,
        Lighthouse,
        Chamber_Of_Commerce,
        Arena,
        Fortifications,
        Circus,
        Arsenal,
        Siege_Workshop,
        Lodge,
        Observatory,
        University,
        Academy,
        Study,
        Workers_Guild,
        Craftmens_Guild,
        Traders_Guild,
        Philosophers_Guild,
        Spy_Guild,
        Strategy_Guild,
        Shipowners_Guild,
        Scientists_Guild,
        Magistrates_Guild,
        Builders_Guild,
        Stage_1,
        Stage_2,
        Stage_3,
        Stage_4
    }

    private enum Wonders {
        The_Colossus_of_Rhodes,
        The_Lighthouse_of_Alexandria,
        The_Temple_of_Artemis_in_Ephesus,
        The_Hanging_Gardens_of_Babylon,
        The_Statue_of_Zeus_in_Olympia,
        The_Mausoleum_of_Halicarnassus,
        The_Pyramids_of_Giza
    }

    private enum CitiesExpansionCards {
        Hideout,
        Gambling_Den,
        Residence,
        Clandestine_Dock_East,
        Clandestine_Dock_West,
        Pigeon_Loft,
        Militia,
        Secret_Warehouse,
        Gates_Of_The_City,
        Lair,
        Architect_Cabinet,
        Mercenaries,
        Sepulcher,
        Consulate,
        Black_Market,
        Tabularium,
        Gambling_House,
        Spy_Ring,
        Slave_Market,
        Brotherhood,
        Secret_Society,
        Builders_Union,
        Capitol,
        Torture_Chamber,
        Cenotaph,
        Embassy,
        Contingent,
        Mourners_Guild,
        Counterfeiters_Guild,
        Guild_Of_Shadows
    }
}
