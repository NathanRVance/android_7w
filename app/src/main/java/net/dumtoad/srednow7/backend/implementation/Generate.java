package net.dumtoad.srednow7.backend.implementation;

import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.CardList;
import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Wonder;
import net.dumtoad.srednow7.backend.implementation.specialValue.AdjacentMilitaryLosses;
import net.dumtoad.srednow7.backend.implementation.specialValue.BestAdjacentGuildVps;
import net.dumtoad.srednow7.backend.implementation.specialValue.MultiSpecial;
import net.dumtoad.srednow7.backend.implementation.specialValue.SpecialDependsPlayed;
import net.dumtoad.srednow7.backend.implementation.variableResource.StaticResource;

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
        cards = getCards();
        resolveCoupons();

        wondersA = getWondersA();
        wondersB = getWondersB();
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

    private static void resolveCoupons() {
        for (CardImpl card : cards) {
            card.resolveCoupons();
        }
    }

    static Card findCardByName(Enum name) {
        for (Card card : cards)
            if (card.getEnum() == name)
                return card;
        throw new RuntimeException("That shouldn't have happended!");
    }

    private static List<CardImpl> getCards() {
        List<CardImpl> cards = new ArrayList<>();

        cards.add(new CardImpl.Builder(Card.Type.RESOURCE, Generate.Cards.Lumber_Yard)
                .setProducts(new StaticResource().setResource(Card.Resource.WOOD, 1)).build());

        cards.add(new CardImpl.Builder(Card.Type.RESOURCE, Generate.Cards.Stone_Pit)
                .setProducts(new StaticResource().setResource(Card.Resource.STONE, 1)).build());

        cards.add(new CardImpl.Builder(Card.Type.RESOURCE, Generate.Cards.Clay_Pool)
                .setProducts(new StaticResource().setResource(Card.Resource.CLAY, 1)).build());

        cards.add(new CardImpl.Builder(Card.Type.RESOURCE, Generate.Cards.Ore_Vein)
                .setProducts(new StaticResource().setResource(Card.Resource.ORE, 1)).build());

        cards.add(new CardImpl.Builder(Card.Type.RESOURCE, Generate.Cards.Tree_Farm)
                .setCosts(new StaticResource().setResource(Card.Resource.GOLD, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.WOOD, 1)
                        .setResource(Card.Resource.CLAY, 1)).build());

        cards.add(new CardImpl.Builder(Card.Type.RESOURCE, Generate.Cards.Excavation)
                .setCosts(new StaticResource().setResource(Card.Resource.GOLD, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.STONE, 1)
                        .setResource(Card.Resource.CLAY, 1)).build());

        cards.add(new CardImpl.Builder(Card.Type.RESOURCE, Generate.Cards.Clay_Pit)
                .setCosts(new StaticResource().setResource(Card.Resource.GOLD, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.CLAY, 1)
                        .setResource(Card.Resource.ORE, 1)).build());

        cards.add(new CardImpl.Builder(Card.Type.RESOURCE, Generate.Cards.Timber_Yard)
                .setCosts(new StaticResource().setResource(Card.Resource.GOLD, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.WOOD, 1)
                        .setResource(Card.Resource.STONE, 1)).build());

        cards.add(new CardImpl.Builder(Card.Type.RESOURCE, Generate.Cards.Forest_Cave)
                .setCosts(new StaticResource().setResource(Card.Resource.GOLD, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.WOOD, 1)
                        .setResource(Card.Resource.ORE, 1)).build());

        cards.add(new CardImpl.Builder(Card.Type.RESOURCE, Generate.Cards.Mine)
                .setCosts(new StaticResource().setResource(Card.Resource.GOLD, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.ORE, 1)
                        .setResource(Card.Resource.STONE, 1)).build());

        cards.add(new CardImpl.Builder(Card.Type.INDUSTRY, Generate.Cards.Loom)
                .setProducts(new StaticResource().setResource(Card.Resource.CLOTH, 1)).build());

        cards.add(new CardImpl.Builder(Card.Type.INDUSTRY, Generate.Cards.Glassworks)
                .setProducts(new StaticResource().setResource(Card.Resource.GLASS, 1)).build());

        cards.add(new CardImpl.Builder(Card.Type.INDUSTRY, Generate.Cards.Press)
                .setProducts(new StaticResource().setResource(Card.Resource.PAPER, 1)).build());

        cards.add(new CardImpl.Builder(Card.Type.STRUCTURE, Generate.Cards.Pawnshop)
                .setProducts(new StaticResource().setResource(Card.Resource.VP, 3)).build());

        cards.add(new CardImpl.Builder(Card.Type.STRUCTURE, Generate.Cards.Baths)
                .setCosts(new StaticResource().setResource(Card.Resource.STONE, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.VP, 3))
                .setMakesFree(Generate.Cards.Aqueduct).build());

        cards.add(new CardImpl.Builder(Card.Type.STRUCTURE, Generate.Cards.Altar)
                .setProducts(new StaticResource().setResource(Card.Resource.VP, 2))
                .setMakesFree(Generate.Cards.Temple).build());

        cards.add(new CardImpl.Builder(Card.Type.STRUCTURE, Generate.Cards.Theater)
                .setProducts(new StaticResource().setResource(Card.Resource.VP, 2))
                .setMakesFree(Generate.Cards.Statue).build());

        cards.add(new CardImpl.Builder(Card.Type.COMMERCIAL, Generate.Cards.Tavern)
                .setProducts(new StaticResource().setResource(Card.Resource.GOLD, 5)).build());

        cards.add(new CardImpl.Builder(Card.Type.COMMERCIAL, Generate.Cards.East_Trading_Post)
                .setMessage("Can trade 1 coin for resources with player to the east.")
                .setTradeType(Card.TradeType.RESOURCE)
                .addTradeDirection(Game.Direction.EAST)
                .setMakesFree(Generate.Cards.Forum).build());

        cards.add(new CardImpl.Builder(Card.Type.COMMERCIAL, Generate.Cards.West_Trading_Post)
                .setMessage("Can trade 1 coin for resources with player to the west.")
                .setTradeType(Card.TradeType.RESOURCE)
                .addTradeDirection(Game.Direction.WEST)
                .setMakesFree(Generate.Cards.Forum).build());

        cards.add(new CardImpl.Builder(Card.Type.COMMERCIAL, Generate.Cards.Marketplace)
                .setMessage("Can trade 1 coin for INDUSTRY products with adjacent players.")
                .setTradeType(Card.TradeType.INDUSTRY)
                .addTradeDirection(Game.Direction.EAST)
                .addTradeDirection(Game.Direction.WEST)
                .setMakesFree(Generate.Cards.Caravansery).build());

        cards.add(new CardImpl.Builder(Card.Type.MILITARY, Generate.Cards.Stockade)
                .setCosts(new StaticResource().setResource(Card.Resource.WOOD, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.SHIELD, 1)).build());

        cards.add(new CardImpl.Builder(Card.Type.MILITARY, Generate.Cards.Barracks)
                .setCosts(new StaticResource().setResource(Card.Resource.ORE, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.SHIELD, 1)).build());

        cards.add(new CardImpl.Builder(Card.Type.MILITARY, Generate.Cards.Guard_Tower)
                .setCosts(new StaticResource().setResource(Card.Resource.CLAY, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.SHIELD, 1)).build());

        cards.add(new CardImpl.Builder(Card.Type.SCIENCE, Generate.Cards.Apothecary)
                .setCosts(new StaticResource().setResource(Card.Resource.CLOTH, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.COMPASS, 1))
                .setMakesFree(Generate.Cards.Stables)
                .setMakesFree(Generate.Cards.Dispensary).build());

        cards.add(new CardImpl.Builder(Card.Type.SCIENCE, Generate.Cards.Workshop)
                .setCosts(new StaticResource().setResource(Card.Resource.GLASS, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.GEAR, 1))
                .setMakesFree(Generate.Cards.Archery_Range)
                .setMakesFree(Generate.Cards.Laboratory).build());

        cards.add(new CardImpl.Builder(Card.Type.SCIENCE, Generate.Cards.Scriptorium)
                .setCosts(new StaticResource().setResource(Card.Resource.PAPER, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.TABLET, 1))
                .setMakesFree(Generate.Cards.Courthouse)
                .setMakesFree(Generate.Cards.Library).build());

        cards.add(new CardImpl.Builder(Card.Type.RESOURCE, Generate.Cards.Sawmill)
                .setCosts(new StaticResource().setResource(Card.Resource.GOLD, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.WOOD, 2)).build());

        cards.add(new CardImpl.Builder(Card.Type.RESOURCE, Generate.Cards.Quarry)
                .setCosts(new StaticResource().setResource(Card.Resource.GOLD, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.STONE, 2)).build());

        cards.add(new CardImpl.Builder(Card.Type.RESOURCE, Generate.Cards.Brickyard)
                .setCosts(new StaticResource().setResource(Card.Resource.GOLD, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.CLAY, 2)).build());

        cards.add(new CardImpl.Builder(Card.Type.RESOURCE, Generate.Cards.Foundry)
                .setCosts(new StaticResource().setResource(Card.Resource.GOLD, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.ORE, 2)).build());

        cards.add(new CardImpl.Builder(Card.Type.INDUSTRY, Generate.Cards.Loom)
                .setProducts(new StaticResource().setResource(Card.Resource.CLOTH, 1)).build());

        cards.add(new CardImpl.Builder(Card.Type.INDUSTRY, Generate.Cards.Glassworks)
                .setProducts(new StaticResource().setResource(Card.Resource.GLASS, 1)).build());

        cards.add(new CardImpl.Builder(Card.Type.INDUSTRY, Generate.Cards.Press)
                .setProducts(new StaticResource().setResource(Card.Resource.PAPER, 1)).build());

        cards.add(new CardImpl.Builder(Card.Type.STRUCTURE, Generate.Cards.Aqueduct)
                .setCosts(new StaticResource().setResource(Card.Resource.STONE, 3))
                .setProducts(new StaticResource().setResource(Card.Resource.VP, 5))
                .setMakesThisFree(Generate.Cards.Baths).build());

        cards.add(new CardImpl.Builder(Card.Type.STRUCTURE, Generate.Cards.Temple)
                .setCosts(new StaticResource().setResource(Card.Resource.WOOD, 1)
                        .setResource(Card.Resource.CLAY, 1)
                        .setResource(Card.Resource.GLASS, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.VP, 3))
                .setMakesThisFree(Generate.Cards.Altar)
                .setMakesFree(Generate.Cards.Pantheon).build());

        cards.add(new CardImpl.Builder(Card.Type.STRUCTURE, Generate.Cards.Statue)
                .setCosts(new StaticResource().setResource(Card.Resource.ORE, 2)
                        .setResource(Card.Resource.WOOD, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.VP, 4))
                .setMakesThisFree(Generate.Cards.Theater)
                .setMakesFree(Generate.Cards.Gardens).build());

        cards.add(new CardImpl.Builder(Card.Type.STRUCTURE, Generate.Cards.Courthouse)
                .setCosts(new StaticResource().setResource(Card.Resource.CLAY, 2)
                        .setResource(Card.Resource.CLOTH, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.VP, 4))
                .setMakesThisFree(Generate.Cards.Scriptorium).build());

        cards.add(new CardImpl.Builder(Card.Type.COMMERCIAL, Generate.Cards.Forum)
                .setCosts(new StaticResource().setResource(Card.Resource.CLAY, 2))
                .setProducts(new StaticResource().setResource(Card.Resource.CLOTH, 1)
                        .setResource(Card.Resource.GLASS, 1)
                        .setResource(Card.Resource.PAPER, 1))
                .setMakesThisFree(Generate.Cards.East_Trading_Post)
                .setMakesThisFree(Generate.Cards.West_Trading_Post)
                .setMakesFree(Generate.Cards.Haven).build());

        cards.add(new CardImpl.Builder(Card.Type.COMMERCIAL, Generate.Cards.Caravansery)
                .setCosts(new StaticResource().setResource(Card.Resource.WOOD, 2))
                .setProducts(new StaticResource().setResource(Card.Resource.WOOD, 1)
                        .setResource(Card.Resource.CLAY, 1)
                        .setResource(Card.Resource.STONE, 1)
                        .setResource(Card.Resource.ORE, 1))
                .setMakesThisFree(Generate.Cards.Marketplace)
                .setMakesFree(Generate.Cards.Lighthouse).build());

        cards.add(new CardImpl.Builder(Card.Type.COMMERCIAL, Generate.Cards.Vineyard)
                .setMessage("1 coin for each RESOURCE card of adjacent players or your own.")
                .setSpecialGold(new SpecialDependsPlayed(Card.Type.RESOURCE, 1, true, true)).build());

        cards.add(new CardImpl.Builder(Card.Type.COMMERCIAL, Generate.Cards.Bazaar)
                .setMessage("2 coins for each INDUSTRY card of adjacent players or your own.")
                .setSpecialGold(new SpecialDependsPlayed(Card.Type.INDUSTRY, 2, true, true)).build());

        cards.add(new CardImpl.Builder(Card.Type.MILITARY, Generate.Cards.Walls)
                .setCosts(new StaticResource().setResource(Card.Resource.STONE, 3))
                .setProducts(new StaticResource().setResource(Card.Resource.SHIELD, 2))
                .setMakesFree(Generate.Cards.Fortifications).build());

        cards.add(new CardImpl.Builder(Card.Type.MILITARY, Generate.Cards.Training_Ground)
                .setCosts(new StaticResource().setResource(Card.Resource.ORE, 2)
                        .setResource(Card.Resource.WOOD, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.SHIELD, 2))
                .setMakesFree(Generate.Cards.Circus).build());

        cards.add(new CardImpl.Builder(Card.Type.MILITARY, Generate.Cards.Stables)
                .setCosts(new StaticResource().setResource(Card.Resource.ORE, 1)
                        .setResource(Card.Resource.CLAY, 1)
                        .setResource(Card.Resource.WOOD, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.SHIELD, 2))
                .setMakesThisFree(Generate.Cards.Apothecary).build());

        cards.add(new CardImpl.Builder(Card.Type.MILITARY, Generate.Cards.Archery_Range)
                .setCosts(new StaticResource().setResource(Card.Resource.ORE, 1)
                        .setResource(Card.Resource.WOOD, 2))
                .setProducts(new StaticResource().setResource(Card.Resource.SHIELD, 2))
                .setMakesThisFree(Generate.Cards.Workshop).build());

        cards.add(new CardImpl.Builder(Card.Type.SCIENCE, Generate.Cards.Dispensary)
                .setCosts(new StaticResource().setResource(Card.Resource.GLASS, 1)
                        .setResource(Card.Resource.ORE, 2))
                .setProducts(new StaticResource().setResource(Card.Resource.COMPASS, 1))
                .setMakesThisFree(Generate.Cards.Apothecary)
                .setMakesFree(Generate.Cards.Lodge)
                .setMakesFree(Generate.Cards.Arena).build());

        cards.add(new CardImpl.Builder(Card.Type.SCIENCE, Generate.Cards.Laboratory)
                .setCosts(new StaticResource().setResource(Card.Resource.PAPER, 1)
                        .setResource(Card.Resource.CLAY, 2))
                .setProducts(new StaticResource().setResource(Card.Resource.GEAR, 1))
                .setMakesThisFree(Generate.Cards.Workshop)
                .setMakesFree(Generate.Cards.Siege_Workshop)
                .setMakesFree(Generate.Cards.Observatory).build());

        cards.add(new CardImpl.Builder(Card.Type.SCIENCE, Generate.Cards.Library)
                .setCosts(new StaticResource().setResource(Card.Resource.CLOTH, 1)
                        .setResource(Card.Resource.STONE, 2))
                .setProducts(new StaticResource().setResource(Card.Resource.TABLET, 1))
                .setMakesThisFree(Generate.Cards.Scriptorium)
                .setMakesFree(Generate.Cards.Senate)
                .setMakesFree(Generate.Cards.University).build());

        cards.add(new CardImpl.Builder(Card.Type.SCIENCE, Generate.Cards.School)
                .setCosts(new StaticResource().setResource(Card.Resource.PAPER, 1)
                        .setResource(Card.Resource.WOOD, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.TABLET, 1))
                .setMakesFree(Generate.Cards.Academy)
                .setMakesFree(Generate.Cards.Study).build());

        cards.add(new CardImpl.Builder(Card.Type.STRUCTURE, Generate.Cards.Pantheon)
                .setCosts(new StaticResource().setResource(Card.Resource.CLAY, 2)
                        .setResource(Card.Resource.ORE, 1)
                        .setResource(Card.Resource.PAPER, 1)
                        .setResource(Card.Resource.CLOTH, 1)
                        .setResource(Card.Resource.GLASS, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.VP, 7))
                .setMakesThisFree(Generate.Cards.Temple).build());

        cards.add(new CardImpl.Builder(Card.Type.STRUCTURE, Generate.Cards.Gardens)
                .setCosts(new StaticResource().setResource(Card.Resource.CLAY, 2)
                        .setResource(Card.Resource.WOOD, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.VP, 5))
                .setMakesThisFree(Generate.Cards.Statue).build());

        cards.add(new CardImpl.Builder(Card.Type.STRUCTURE, Generate.Cards.Town_Hall)
                .setCosts(new StaticResource().setResource(Card.Resource.STONE, 2)
                        .setResource(Card.Resource.ORE, 1)
                        .setResource(Card.Resource.GLASS, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.VP, 6)).build());

        cards.add(new CardImpl.Builder(Card.Type.STRUCTURE, Generate.Cards.Palace)
                .setCosts(new StaticResource().setResource(Card.Resource.WOOD, 1)
                        .setResource(Card.Resource.CLAY, 1)
                        .setResource(Card.Resource.ORE, 1)
                        .setResource(Card.Resource.STONE, 1)
                        .setResource(Card.Resource.PAPER, 1)
                        .setResource(Card.Resource.CLOTH, 1)
                        .setResource(Card.Resource.GLASS, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.VP, 8)).build());

        cards.add(new CardImpl.Builder(Card.Type.STRUCTURE, Generate.Cards.Senate)
                .setCosts(new StaticResource().setResource(Card.Resource.WOOD, 2)
                        .setResource(Card.Resource.ORE, 1)
                        .setResource(Card.Resource.STONE, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.VP, 6))
                .setMakesThisFree(Generate.Cards.Library).build());

        cards.add(new CardImpl.Builder(Card.Type.COMMERCIAL, Generate.Cards.Haven)
                .setCosts(new StaticResource().setResource(Card.Resource.ORE, 1)
                        .setResource(Card.Resource.WOOD, 1)
                        .setResource(Card.Resource.CLOTH, 1))
                .setMessage("1 coin and 1 vp for each RESOURCE card.")
                .setSpecialGold(new SpecialDependsPlayed(Card.Type.RESOURCE, 1, false, true))
                .setSpecialVps(new SpecialDependsPlayed(Card.Type.RESOURCE, 1, false, true))
                .setMakesThisFree(Generate.Cards.Forum).build());

        cards.add(new CardImpl.Builder(Card.Type.COMMERCIAL, Generate.Cards.Lighthouse)
                .setCosts(new StaticResource().setResource(Card.Resource.STONE, 1)
                        .setResource(Card.Resource.GLASS, 1))
                .setMessage("1 coin and 1 vp for each commercial card.")
                .setSpecialGold(new SpecialDependsPlayed(Card.Type.COMMERCIAL, 1, false, true))
                .setSpecialVps(new SpecialDependsPlayed(Card.Type.COMMERCIAL, 1, false, true))
                .setMakesThisFree(Generate.Cards.Caravansery).build());

        cards.add(new CardImpl.Builder(Card.Type.COMMERCIAL, Generate.Cards.Chamber_Of_Commerce)
                .setCosts(new StaticResource().setResource(Card.Resource.CLAY, 2)
                        .setResource(Card.Resource.PAPER, 1))
                .setMessage("2 coins and 2 vps for each industrial card.")
                .setSpecialGold(new SpecialDependsPlayed(Card.Type.INDUSTRY, 2, false, true))
                .setSpecialVps(new SpecialDependsPlayed(Card.Type.INDUSTRY, 2, false, true)).build());

        cards.add(new CardImpl.Builder(Card.Type.COMMERCIAL, Generate.Cards.Arena)
                .setCosts(new StaticResource().setResource(Card.Resource.STONE, 2)
                        .setResource(Card.Resource.ORE, 1))
                .setMessage("3 coins and 1 vp for each completed wonder stage")
                .setSpecialGold(new SpecialDependsPlayed(Card.Type.STAGE, 3, false, true))
                .setSpecialVps(new SpecialDependsPlayed(Card.Type.STAGE, 1, false, true))
                .setMakesThisFree(Generate.Cards.Dispensary).build());

        cards.add(new CardImpl.Builder(Card.Type.MILITARY, Generate.Cards.Fortifications)
                .setCosts(new StaticResource().setResource(Card.Resource.ORE, 3)
                        .setResource(Card.Resource.STONE, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.SHIELD, 3))
                .setMakesThisFree(Generate.Cards.Walls).build());

        cards.add(new CardImpl.Builder(Card.Type.MILITARY, Generate.Cards.Circus)
                .setCosts(new StaticResource().setResource(Card.Resource.STONE, 3)
                        .setResource(Card.Resource.ORE, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.SHIELD, 3))
                .setMakesThisFree(Generate.Cards.Training_Ground).build());

        cards.add(new CardImpl.Builder(Card.Type.MILITARY, Generate.Cards.Arsenal)
                .setCosts(new StaticResource().setResource(Card.Resource.ORE, 1)
                        .setResource(Card.Resource.WOOD, 2)
                        .setResource(Card.Resource.CLOTH, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.SHIELD, 3)).build());

        cards.add(new CardImpl.Builder(Card.Type.MILITARY, Generate.Cards.Siege_Workshop)
                .setCosts(new StaticResource().setResource(Card.Resource.CLAY, 3)
                        .setResource(Card.Resource.WOOD, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.SHIELD, 3))
                .setMakesThisFree(Generate.Cards.Laboratory).build());

        cards.add(new CardImpl.Builder(Card.Type.SCIENCE, Generate.Cards.Lodge)
                .setCosts(new StaticResource().setResource(Card.Resource.CLAY, 2)
                        .setResource(Card.Resource.CLOTH, 1)
                        .setResource(Card.Resource.PAPER, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.COMPASS, 1))
                .setMakesThisFree(Generate.Cards.Dispensary).build());

        cards.add(new CardImpl.Builder(Card.Type.SCIENCE, Generate.Cards.Observatory)
                .setCosts(new StaticResource().setResource(Card.Resource.ORE, 2)
                        .setResource(Card.Resource.GLASS, 1)
                        .setResource(Card.Resource.CLOTH, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.GEAR, 1))
                .setMakesThisFree(Generate.Cards.Laboratory).build());

        cards.add(new CardImpl.Builder(Card.Type.SCIENCE, Generate.Cards.University)
                .setCosts(new StaticResource().setResource(Card.Resource.WOOD, 2)
                        .setResource(Card.Resource.GLASS, 1)
                        .setResource(Card.Resource.PAPER, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.TABLET, 1))
                .setMakesThisFree(Generate.Cards.Library).build());

        cards.add(new CardImpl.Builder(Card.Type.SCIENCE, Generate.Cards.Academy)
                .setCosts(new StaticResource().setResource(Card.Resource.STONE, 3)
                        .setResource(Card.Resource.GLASS, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.COMPASS, 1))
                .setMakesThisFree(Generate.Cards.School).build());

        cards.add(new CardImpl.Builder(Card.Type.SCIENCE, Generate.Cards.Study)
                .setCosts(new StaticResource().setResource(Card.Resource.WOOD, 1)
                        .setResource(Card.Resource.CLOTH, 1)
                        .setResource(Card.Resource.PAPER, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.GEAR, 1))
                .setMakesThisFree(Generate.Cards.School).build());

        cards.add(new CardImpl.Builder(Card.Type.GUILD, Generate.Cards.Workers_Guild)
                .setCosts(new StaticResource().setResource(Card.Resource.ORE, 2)
                        .setResource(Card.Resource.CLAY, 1)
                        .setResource(Card.Resource.STONE, 1)
                        .setResource(Card.Resource.WOOD, 1))
                .setMessage("1 vp for each RESOURCE card owned by adjacent players.")
                .setSpecialVps(new SpecialDependsPlayed(Card.Type.RESOURCE, 1, true, false)).build());

        cards.add(new CardImpl.Builder(Card.Type.GUILD, Generate.Cards.Craftmens_Guild)
                .setCosts(new StaticResource().setResource(Card.Resource.ORE, 2)
                        .setResource(Card.Resource.STONE, 2))
                .setMessage("2 vps for each industrial card owned by adjacent players.")
                .setSpecialVps(new SpecialDependsPlayed(Card.Type.INDUSTRY, 2, true, false)).build());

        cards.add(new CardImpl.Builder(Card.Type.GUILD, Generate.Cards.Traders_Guild)
                .setCosts(new StaticResource().setResource(Card.Resource.CLOTH, 1)
                        .setResource(Card.Resource.PAPER, 1)
                        .setResource(Card.Resource.GLASS, 1))
                .setMessage("1 vp for each commercial card owned by adjacent players.")
                .setSpecialVps(new SpecialDependsPlayed(Card.Type.COMMERCIAL, 1, true, false)).build());

        cards.add(new CardImpl.Builder(Card.Type.GUILD, Generate.Cards.Philosophers_Guild)
                .setCosts(new StaticResource().setResource(Card.Resource.CLAY, 3)
                        .setResource(Card.Resource.CLOTH, 1)
                        .setResource(Card.Resource.PAPER, 1))
                .setMessage("1 vp for each scientific card owned by adjacent players.")
                .setSpecialVps(new SpecialDependsPlayed(Card.Type.SCIENCE, 1, true, false)).build());

        cards.add(new CardImpl.Builder(Card.Type.GUILD, Generate.Cards.Spy_Guild)
                .setCosts(new StaticResource().setResource(Card.Resource.CLAY, 3)
                        .setResource(Card.Resource.GLASS, 1))
                .setMessage("1 vp for each military card owned by adjacent players.")
                .setSpecialVps(new SpecialDependsPlayed(Card.Type.MILITARY, 1, true, false)).build());

        cards.add(new CardImpl.Builder(Card.Type.GUILD, Generate.Cards.Strategy_Guild)
                .setCosts(new StaticResource().setResource(Card.Resource.ORE, 2)
                        .setResource(Card.Resource.STONE, 1)
                        .setResource(Card.Resource.CLOTH, 1))
                .setMessage("1 vp for each military defeat by adjacent players.")
                .setSpecialVps(new AdjacentMilitaryLosses()).build());

        cards.add(new CardImpl.Builder(Card.Type.GUILD, Generate.Cards.Shipowners_Guild)
                .setCosts(new StaticResource().setResource(Card.Resource.WOOD, 3)
                        .setResource(Card.Resource.PAPER, 1)
                        .setResource(Card.Resource.GLASS, 1))
                .setMessage("1 vp for each RESOURCE, industrial, and guild card.")
                .setSpecialVps(new MultiSpecial(
                        new SpecialDependsPlayed(Card.Type.RESOURCE, 1, false, true),
                        new SpecialDependsPlayed(Card.Type.INDUSTRY, 1, false, true),
                        new SpecialDependsPlayed(Card.Type.GUILD, 1, false, true))).build());

        cards.add(new CardImpl.Builder(Card.Type.GUILD, Generate.Cards.Scientists_Guild)
                .setCosts(new StaticResource().setResource(Card.Resource.WOOD, 2)
                        .setResource(Card.Resource.ORE, 2)
                        .setResource(Card.Resource.CLOTH, 1))
                .setProducts(new StaticResource().setResource(Card.Resource.COMPASS, 1)
                        .setResource(Card.Resource.GEAR, 1)
                        .setResource(Card.Resource.TABLET, 1)).build());

        cards.add(new CardImpl.Builder(Card.Type.GUILD, Generate.Cards.Magistrates_Guild)
                .setCosts(new StaticResource().setResource(Card.Resource.WOOD, 3)
                        .setResource(Card.Resource.STONE, 1)
                        .setResource(Card.Resource.CLOTH, 1))
                .setMessage("1 vp for each structure card owned by adjacent players.")
                .setSpecialVps(new SpecialDependsPlayed(Card.Type.STRUCTURE, 1, true, false)).build());

        cards.add(new CardImpl.Builder(Card.Type.GUILD, Generate.Cards.Builders_Guild)
                .setCosts(new StaticResource().setResource(Card.Resource.STONE, 2)
                        .setResource(Card.Resource.CLAY, 2)
                        .setResource(Card.Resource.GLASS, 1))
                .setMessage("1 vp for each completed wonder stage by you or adjacent players.")
                .setSpecialVps(new SpecialDependsPlayed(Card.Type.STAGE, 1, true, false)).build());

        return cards;
    }

    private static List<Wonder> getWondersA() {
        List<Wonder> wonders = new ArrayList<>();

        //Rhodes
        wonders.add(new WonderImpl.Builder(Wonders.The_Colossus_of_Rhodes, Card.Resource.ORE)
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_1)
                        .setCosts(new StaticResource().setResource(Card.Resource.WOOD, 2))
                        .setProducts(new StaticResource().setResource(Card.Resource.VP, 3)).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_2)
                        .setCosts(new StaticResource().setResource(Card.Resource.CLAY, 3))
                        .setProducts(new StaticResource().setResource(Card.Resource.SHIELD, 2)).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_3)
                        .setCosts(new StaticResource().setResource(Card.Resource.ORE, 4))
                        .setProducts(new StaticResource().setResource(Card.Resource.VP, 7)).build())
                .build());

        //Alexandria
        wonders.add(new WonderImpl.Builder(Wonders.The_Lighthouse_of_Alexandria, Card.Resource.GLASS)
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_1)
                        .setCosts(new StaticResource().setResource(Card.Resource.STONE, 2))
                        .setProducts(new StaticResource().setResource(Card.Resource.VP, 3)).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_2)
                        .setCosts(new StaticResource().setResource(Card.Resource.ORE, 2))
                        .setProducts(new StaticResource().setResource(Card.Resource.CLAY, 1)
                                .setResource(Card.Resource.ORE, 1)
                                .setResource(Card.Resource.WOOD, 1)
                                .setResource(Card.Resource.STONE, 1)).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_3)
                        .setCosts(new StaticResource().setResource(Card.Resource.GLASS, 2))
                        .setProducts(new StaticResource().setResource(Card.Resource.VP, 7)).build())
                .build());

        //Ephesus
        wonders.add(new WonderImpl.Builder(Wonders.The_Temple_of_Artemis_in_Ephesus, Card.Resource.PAPER)
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_1)
                        .setCosts(new StaticResource().setResource(Card.Resource.STONE, 2))
                        .setProducts(new StaticResource().setResource(Card.Resource.VP, 3)).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_2)
                        .setCosts(new StaticResource().setResource(Card.Resource.WOOD, 2))
                        .setProducts(new StaticResource().setResource(Card.Resource.GOLD, 9)).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_3)
                        .setCosts(new StaticResource().setResource(Card.Resource.PAPER, 2))
                        .setProducts(new StaticResource().setResource(Card.Resource.VP, 7)).build())
                .build());

        //Babylon
        wonders.add(new WonderImpl.Builder(Wonders.The_Hanging_Gardens_of_Babylon, Card.Resource.CLAY)
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_1)
                        .setCosts(new StaticResource().setResource(Card.Resource.CLAY, 2))
                        .setProducts(new StaticResource().setResource(Card.Resource.VP, 3)).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_2)
                        .setCosts(new StaticResource().setResource(Card.Resource.WOOD, 3))
                        .setProducts(new StaticResource().setResource(Card.Resource.TABLET, 1)
                                .setResource(Card.Resource.COMPASS, 1)
                                .setResource(Card.Resource.GEAR, 1)).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_3)
                        .setCosts(new StaticResource().setResource(Card.Resource.CLAY, 4))
                        .setProducts(new StaticResource().setResource(Card.Resource.VP, 7)).build())
                .build());

        //Olympia
        wonders.add(new WonderImpl.Builder(Wonders.The_Statue_of_Zeus_in_Olympia, Card.Resource.WOOD)
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_1)
                        .setCosts(new StaticResource().setResource(Card.Resource.WOOD, 2))
                        .setProducts(new StaticResource().setResource(Card.Resource.VP, 3)).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_2)
                        .setCosts(new StaticResource().setResource(Card.Resource.STONE, 2))
                        .addAttribute(Card.Attribute.PLAY_1_FREE)
                        .setMessage("Once per age can build a card for free.").build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_3)
                        .setCosts(new StaticResource().setResource(Card.Resource.ORE, 2))
                        .setProducts(new StaticResource().setResource(Card.Resource.VP, 7)).build())
                .build());

        //Halicarnassus
        wonders.add(new WonderImpl.Builder(Wonders.The_Mausoleum_of_Halicarnassus, Card.Resource.CLOTH)
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_1)
                        .setCosts(new StaticResource().setResource(Card.Resource.CLAY, 2))
                        .setProducts(new StaticResource().setResource(Card.Resource.VP, 3)).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_2)
                        .setCosts(new StaticResource().setResource(Card.Resource.ORE, 3))
                        .addAttribute(Card.Attribute.FREE_BUILD)
                        .setMessage("Can look through all discards since the beginning of the game, pick one, and build it for free.").build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_3)
                        .setCosts(new StaticResource().setResource(Card.Resource.CLOTH, 2))
                        .setProducts(new StaticResource().setResource(Card.Resource.VP, 7)).build())
                .build());

        //Giza
        wonders.add(new WonderImpl.Builder(Wonders.The_Pyrimids_of_Giza, Card.Resource.STONE)
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_1)
                        .setCosts(new StaticResource().setResource(Card.Resource.STONE, 2))
                        .setProducts(new StaticResource().setResource(Card.Resource.VP, 3)).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_2)
                        .setCosts(new StaticResource().setResource(Card.Resource.WOOD, 3))
                        .setProducts(new StaticResource().setResource(Card.Resource.VP, 5)).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_3)
                        .setCosts(new StaticResource().setResource(Card.Resource.STONE, 4))
                        .setProducts(new StaticResource().setResource(Card.Resource.VP, 7)).build())
                .build());

        return wonders;
    }

    private static List<Wonder> getWondersB() {
        List<Wonder> wonders = new ArrayList<>();

        //Rhodes
        wonders.add(new WonderImpl.Builder(Wonders.The_Colossus_of_Rhodes, Card.Resource.ORE)
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_1)
                        .setCosts(new StaticResource().setResource(Card.Resource.STONE, 3))
                        .setProducts(new StaticResource().setResource(Card.Resource.SHIELD, 1)
                                .setResource(Card.Resource.VP, 3)
                                .setResource(Card.Resource.GOLD, 3)).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_2)
                        .setCosts(new StaticResource().setResource(Card.Resource.ORE, 4))
                        .setProducts(new StaticResource().setResource(Card.Resource.SHIELD, 1)
                                .setResource(Card.Resource.VP, 4)
                                .setResource(Card.Resource.GOLD, 4)).build())
                .build());

        //Alexandria
        wonders.add(new WonderImpl.Builder(Wonders.The_Lighthouse_of_Alexandria, Card.Resource.GLASS)
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_1)
                        .setCosts(new StaticResource().setResource(Card.Resource.CLAY, 2))
                        .setProducts(new StaticResource().setResource(Card.Resource.CLAY, 1)
                                .setResource(Card.Resource.ORE, 1)
                                .setResource(Card.Resource.WOOD, 1)
                                .setResource(Card.Resource.STONE, 1)).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_2)
                        .setCosts(new StaticResource().setResource(Card.Resource.WOOD, 2))
                        .setProducts(new StaticResource().setResource(Card.Resource.GLASS, 1)
                                .setResource(Card.Resource.CLOTH, 1)
                                .setResource(Card.Resource.PAPER, 1)).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_3)
                        .setCosts(new StaticResource().setResource(Card.Resource.STONE, 3))
                        .setProducts(new StaticResource().setResource(Card.Resource.VP, 7)).build())
                .build());

        //Ephesus
        wonders.add(new WonderImpl.Builder(Wonders.The_Temple_of_Artemis_in_Ephesus, Card.Resource.PAPER)
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_1)
                        .setCosts(new StaticResource().setResource(Card.Resource.STONE, 2))
                        .setProducts(new StaticResource().setResource(Card.Resource.VP, 2)
                                .setResource(Card.Resource.GOLD, 4)).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_2)
                        .setCosts(new StaticResource().setResource(Card.Resource.WOOD, 2))
                        .setProducts(new StaticResource().setResource(Card.Resource.VP, 3)
                                .setResource(Card.Resource.GOLD, 4)).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_3)
                        .setCosts(new StaticResource().setResource(Card.Resource.GLASS, 1)
                                .setResource(Card.Resource.CLOTH, 1)
                                .setResource(Card.Resource.PAPER, 1))
                        .setProducts(new StaticResource().setResource(Card.Resource.VP, 5)
                                .setResource(Card.Resource.GOLD, 4)).build())
                .build());

        //Babylon
        wonders.add(new WonderImpl.Builder(Wonders.The_Hanging_Gardens_of_Babylon, Card.Resource.CLAY)
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_1)
                        .setCosts(new StaticResource().setResource(Card.Resource.CLAY, 1)
                                .setResource(Card.Resource.CLOTH, 1))
                        .setProducts(new StaticResource().setResource(Card.Resource.VP, 3)).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_2)
                        .setCosts(new StaticResource().setResource(Card.Resource.WOOD, 2)
                                .setResource(Card.Resource.GLASS, 1))
                        .addAttribute(Card.Attribute.PLAY_7TH_CARD)
                        .setMessage("Can now play 7th age card rather than discarding it.").build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_3)
                        .setCosts(new StaticResource().setResource(Card.Resource.CLAY, 3)
                                .setResource(Card.Resource.PAPER, 1))
                        .setProducts(new StaticResource().setResource(Card.Resource.TABLET, 1)
                                .setResource(Card.Resource.COMPASS, 1)
                                .setResource(Card.Resource.GEAR, 1)).build())
                .build());

        //Olympia
        wonders.add(new WonderImpl.Builder(Wonders.The_Statue_of_Zeus_in_Olympia, Card.Resource.WOOD)
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_1)
                        .setCosts(new StaticResource().setResource(Card.Resource.WOOD, 2))
                        .setMessage("Can trade 1 coin for resources with adjacent players.")
                        .setTradeType(Card.TradeType.RESOURCE)
                        .addTradeDirection(Game.Direction.EAST)
                        .addTradeDirection(Game.Direction.WEST).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_2)
                        .setCosts(new StaticResource().setResource(Card.Resource.STONE, 2))
                        .setProducts(new StaticResource().setResource(Card.Resource.VP, 5)).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_3)
                        .setCosts(new StaticResource().setResource(Card.Resource.ORE, 2)
                                .setResource(Card.Resource.CLOTH, 1))
                        .setMessage("Can copy one Guild card built by an adjacent player.")
                        .setSpecialVps(new BestAdjacentGuildVps()).build())
                .build());

        //Halicarnassus
        wonders.add(new WonderImpl.Builder(Wonders.The_Mausoleum_of_Halicarnassus, Card.Resource.CLOTH)
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_1)
                        .setCosts(new StaticResource().setResource(Card.Resource.ORE, 2))
                        .setProducts(new StaticResource().setResource(Card.Resource.VP, 2))
                        .addAttribute(Card.Attribute.FREE_BUILD)
                        .setMessage("Can look through all discards since the beginning of the game, pick one, and build it for free.").build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_2)
                        .setCosts(new StaticResource().setResource(Card.Resource.CLAY, 3))
                        .setProducts(new StaticResource().setResource(Card.Resource.VP, 1))
                        .addAttribute(Card.Attribute.FREE_BUILD)
                        .setMessage("Can look through all discards since the beginning of the game, pick one, and build it for free.").build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_3)
                        .setCosts(new StaticResource().setResource(Card.Resource.CLOTH, 1)
                                .setResource(Card.Resource.PAPER, 1)
                                .setResource(Card.Resource.GLASS, 1))
                        .addAttribute(Card.Attribute.FREE_BUILD)
                        .setMessage("Can look through all discards since the beginning of the game, pick one, and build it for free.").build())
                .build());

        //Giza
        wonders.add(new WonderImpl.Builder(Wonders.The_Pyrimids_of_Giza, Card.Resource.STONE)
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_1)
                        .setCosts(new StaticResource().setResource(Card.Resource.WOOD, 2))
                        .setProducts(new StaticResource().setResource(Card.Resource.VP, 3)).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_2)
                        .setCosts(new StaticResource().setResource(Card.Resource.STONE, 3))
                        .setProducts(new StaticResource().setResource(Card.Resource.VP, 5)).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_3)
                        .setCosts(new StaticResource().setResource(Card.Resource.CLAY, 3))
                        .setProducts(new StaticResource().setResource(Card.Resource.VP, 5)).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, Cards.Stage_4)
                        .setCosts(new StaticResource().setResource(Card.Resource.STONE, 4)
                                .setResource(Card.Resource.PAPER, 1))
                        .setProducts(new StaticResource().setResource(Card.Resource.VP, 7)).build())
                .build());

        return wonders;
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
        The_Pyrimids_of_Giza
    }

    private enum CitiesExpansionCards {

    }
}
