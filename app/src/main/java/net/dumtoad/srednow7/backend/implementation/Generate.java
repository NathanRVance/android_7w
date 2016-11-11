package net.dumtoad.srednow7.backend.implementation;

import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.CardList;
import net.dumtoad.srednow7.backend.Wonder;
import net.dumtoad.srednow7.backend.implementation.Special.AdjacentMilitaryLosses;
import net.dumtoad.srednow7.backend.implementation.Special.BestAdjacentGuildVps;
import net.dumtoad.srednow7.backend.implementation.Special.MultiSpecial;
import net.dumtoad.srednow7.backend.implementation.Special.SpecialDependsPlayed;

import java.util.ArrayList;
import java.util.List;

class Generate {

    private static final Enum[] era0deck = {Era0.Lumber_Yard, Era0.Stone_Pit, Era0.Clay_Pool, Era0.Ore_Vein, Era0.Clay_Pit, Era0.Timber_Yard, Era0.Loom,
            Era0.Glassworks, Era0.Press, Era0.Baths, Era0.Altar, Era0.Theater, Era0.East_Trading_Post, Era0.West_Trading_Post, Era0.Marketplace,
            Era0.Stockade, Era0.Barracks, Era0.Guard_Tower, Era0.Apothecary, Era0.Workshop, Era0.Scriptorium, Era0.Lumber_Yard, Era0.Ore_Vein, Era0.Excavation,
            Era0.Pawnshop, Era0.Tavern, Era0.Guard_Tower, Era0.Scriptorium, Era0.Stone_Pit, Era0.Clay_Pool, Era0.Forest_Cave, Era0.Altar, Era0.Tavern,
            Era0.Barracks, Era0.Apothecary, Era0.Tree_Farm, Era0.Mine, Era0.Loom, Era0.Glassworks, Era0.Press, Era0.Theater, Era0.Marketplace, Era0.Pawnshop,
            Era0.Baths, Era0.Tavern, Era0.East_Trading_Post, Era0.West_Trading_Post, Era0.Stockade, Era0.Workshop};
    private static final Enum[] era1deck = {Era1.Sawmill, Era1.Quarry, Era1.Brickyard, Era1.Foundry, Era1.Loom, Era1.Glassworks, Era1.Press, Era1.Aqueduct,
            Era1.Temple, Era1.Statue, Era1.Courthouse, Era1.Forum, Era1.Caravansery, Era1.Vineyard, Era1.Walls, Era1.Stables, Era1.Archery_Range,
            Era1.Dispensary, Era1.Laboratory, Era1.Library, Era1.School, Era1.Sawmill, Era1.Quarry, Era1.Brickyard, Era1.Foundry, Era1.Bazaar, Era1.Training_Ground,
            Era1.Dispensary, Era1.Loom, Era1.Glassworks, Era1.Press, Era1.Courthouse, Era1.Caravansery, Era1.Stables, Era1.Laboratory, Era1.Temple,
            Era1.Forum, Era1.Caravansery, Era1.Vineyard, Era1.Training_Ground, Era1.Archery_Range, Era1.Library, Era1.Aqueduct, Era1.Statue, Era1.Forum,
            Era1.Bazaar, Era1.Walls, Era1.Training_Ground, Era1.School};
    private static final Enum[] era2deck = {Era2.Pantheon, Era2.Gardens, Era2.Town_Hall, Era2.Palace, Era2.Senate, Era2.Haven, Era2.Lighthouse,
            Era2.Arena, Era2.Fortifications, Era2.Arsenal, Era2.Siege_Workshop, Era2.Lodge, Era2.Observatory, Era2.University, Era2.Academy, Era2.Study,
            Era2.Gardens, Era2.Haven, Era2.Chamber_Of_Commerce, Era2.Circus, Era2.Arsenal, Era2.University, Era2.Town_Hall, Era2.Senate, Era2.Arena,
            Era2.Circus, Era2.Siege_Workshop, Era2.Study, Era2.Pantheon, Era2.Town_Hall, Era2.Lighthouse, Era2.Chamber_Of_Commerce, Era2.Circus,
            Era2.Lodge, Era2.Palace, Era2.Arena, Era2.Fortifications, Era2.Arsenal, Era2.Observatory, Era2.Academy};
    private static final Enum[] guilds = {Era2.Workers_Guild, Era2.Craftmens_Guild, Era2.Traders_Guild, Era2.Philosophers_Guild, Era2.Spy_Guild,
            Era2.Strategy_Guild, Era2.Shipowners_Guild, Era2.Scientists_Guild, Era2.Magistrates_Guild, Era2.Builders_Guild};
    private static List<CardImpl> era0;
    private static List<CardImpl> era1;
    private static List<CardImpl> era2;
    private static List<Wonder> wondersA;
    private static List<Wonder> wondersB;

    static void generateBuilders() {
        era0 = getEra0Cards();
        era1 = getEra1Cards();
        era2 = getEra2Cards();
        resolveCoupons();

        wondersA = getWondersA();
        wondersB = getWondersB();
    }

    static CardList getEra0Deck(int numPlayers) {
        return getDeck(era0deck, numPlayers * 7);
    }

    static CardList getEra1Deck(int numPlayers) {
        return getDeck(era1deck, numPlayers * 7);
    }

    static CardList getEra2Cards(int numPlayers) {
        return getDeck(era2deck, numPlayers * 6 - 2);
    }

    static CardList getGuildCards() {
        return getDeck(guilds, 10);
    }

    private static CardList getDeck(Enum[] cardNames, int numCards) {
        CardList deck = new CardListImpl();
        for (int i = 0; i < numCards; i++) {
            deck.add(findCardByName(cardNames[i]));
        }
        return deck;
    }

    static List<Wonder> getWonders(boolean side) {
        return (side) ? wondersA : wondersB;
    }

    private static void resolveCoupons() {
        for (CardImpl card : era0) {
            card.resolveCoupons();
        }
        for (CardImpl card : era1) {
            card.resolveCoupons();
        }
        for (CardImpl card : era2) {
            card.resolveCoupons();
        }
    }

    static Card findCardByName(Enum name) {
        for (Card card : era0)
            if (card.getEnum() == name)
                return card;
        for (Card card : era1)
            if (card.getEnum() == name)
                return card;
        for (Card card : era2)
            if (card.getEnum() == name)
                return card;
        throw new RuntimeException("That shouldn't have happended!");
    }

    private static List<CardImpl> getEra0Cards() {
        List<CardImpl> cards = new ArrayList<>();

        cards.add(new CardImpl.Builder(Card.Type.RESOURCE, Era0.Lumber_Yard)
                .setProduct(Card.Resource.WOOD, 1).build());

        cards.add(new CardImpl.Builder(Card.Type.RESOURCE, Era0.Stone_Pit)
                .setProduct(Card.Resource.STONE, 1).build());

        cards.add(new CardImpl.Builder(Card.Type.RESOURCE, Era0.Clay_Pool)
                .setProduct(Card.Resource.CLAY, 1).build());

        cards.add(new CardImpl.Builder(Card.Type.RESOURCE, Era0.Ore_Vein)
                .setProduct(Card.Resource.ORE, 1).build());

        cards.add(new CardImpl.Builder(Card.Type.RESOURCE, Era0.Tree_Farm)
                .setCost(Card.Resource.GOLD, 1)
                .setProduct(Card.Resource.WOOD, 1)
                .setProduct(Card.Resource.CLAY, 1).build());

        cards.add(new CardImpl.Builder(Card.Type.RESOURCE, Era0.Excavation)
                .setCost(Card.Resource.GOLD, 1)
                .setProduct(Card.Resource.STONE, 1)
                .setProduct(Card.Resource.CLAY, 1).build());

        cards.add(new CardImpl.Builder(Card.Type.RESOURCE, Era0.Clay_Pit)
                .setCost(Card.Resource.GOLD, 1)
                .setProduct(Card.Resource.CLAY, 1)
                .setProduct(Card.Resource.ORE, 1).build());

        cards.add(new CardImpl.Builder(Card.Type.RESOURCE, Era0.Timber_Yard)
                .setCost(Card.Resource.GOLD, 1)
                .setProduct(Card.Resource.WOOD, 1)
                .setProduct(Card.Resource.STONE, 1).build());

        cards.add(new CardImpl.Builder(Card.Type.RESOURCE, Era0.Forest_Cave)
                .setCost(Card.Resource.GOLD, 1)
                .setProduct(Card.Resource.WOOD, 1)
                .setProduct(Card.Resource.ORE, 1).build());

        cards.add(new CardImpl.Builder(Card.Type.RESOURCE, Era0.Mine)
                .setCost(Card.Resource.GOLD, 1)
                .setProduct(Card.Resource.ORE, 1)
                .setProduct(Card.Resource.STONE, 1).build());

        cards.add(new CardImpl.Builder(Card.Type.INDUSTRY, Era0.Loom)
                .setProduct(Card.Resource.CLOTH, 1).build());

        cards.add(new CardImpl.Builder(Card.Type.INDUSTRY, Era0.Glassworks)
                .setProduct(Card.Resource.GLASS, 1).build());

        cards.add(new CardImpl.Builder(Card.Type.INDUSTRY, Era0.Press)
                .setProduct(Card.Resource.PAPER, 1).build());

        cards.add(new CardImpl.Builder(Card.Type.STRUCTURE, Era0.Pawnshop)
                .setProduct(Card.Resource.VP, 3).build());

        cards.add(new CardImpl.Builder(Card.Type.STRUCTURE, Era0.Baths)
                .setCost(Card.Resource.STONE, 1)
                .setProduct(Card.Resource.VP, 3)
                .setMakesFree(Era1.Aqueduct).build());

        cards.add(new CardImpl.Builder(Card.Type.STRUCTURE, Era0.Altar)
                .setProduct(Card.Resource.VP, 2)
                .setMakesFree(Era1.Temple).build());

        cards.add(new CardImpl.Builder(Card.Type.STRUCTURE, Era0.Theater)
                .setProduct(Card.Resource.VP, 2)
                .setMakesFree(Era1.Statue).build());

        cards.add(new CardImpl.Builder(Card.Type.COMMERCIAL, Era0.Tavern)
                .setProduct(Card.Resource.GOLD, 5).build());

        cards.add(new CardImpl.Builder(Card.Type.COMMERCIAL, Era0.East_Trading_Post)
                .setMessage("Can trade 1 coin for resources with player to the east.")
                .setTradeType(Card.TradeType.resource)
                .addTradeDirection(Game.Direction.EAST)
                .setMakesFree(Era1.Forum).build());

        cards.add(new CardImpl.Builder(Card.Type.COMMERCIAL, Era0.West_Trading_Post)
                .setMessage("Can trade 1 coin for resources with player to the west.")
                .setTradeType(Card.TradeType.resource)
                .addTradeDirection(Game.Direction.WEST)
                .setMakesFree(Era1.Forum).build());

        cards.add(new CardImpl.Builder(Card.Type.COMMERCIAL, Era0.Marketplace)
                .setMessage("Can trade 1 coin for industry products with adjacent players.")
                .setTradeType(Card.TradeType.industry)
                .addTradeDirection(Game.Direction.EAST)
                .addTradeDirection(Game.Direction.WEST)
                .setMakesFree(Era1.Caravansery).build());

        cards.add(new CardImpl.Builder(Card.Type.MILITARY, Era0.Stockade)
                .setCost(Card.Resource.WOOD, 1)
                .setProduct(Card.Resource.SHIELD, 1).build());

        cards.add(new CardImpl.Builder(Card.Type.MILITARY, Era0.Barracks)
                .setCost(Card.Resource.ORE, 1)
                .setProduct(Card.Resource.SHIELD, 1).build());

        cards.add(new CardImpl.Builder(Card.Type.MILITARY, Era0.Guard_Tower)
                .setCost(Card.Resource.CLAY, 1)
                .setProduct(Card.Resource.SHIELD, 1).build());

        cards.add(new CardImpl.Builder(Card.Type.SCIENCE, Era0.Apothecary)
                .setCost(Card.Resource.CLOTH, 1)
                .setProduct(Card.Resource.COMPASS, 1)
                .setMakesFree(Era1.Stables)
                .setMakesFree(Era1.Dispensary).build());

        cards.add(new CardImpl.Builder(Card.Type.SCIENCE, Era0.Workshop)
                .setCost(Card.Resource.GLASS, 1)
                .setProduct(Card.Resource.GEAR, 1)
                .setMakesFree(Era1.Archery_Range)
                .setMakesFree(Era1.Laboratory).build());

        cards.add(new CardImpl.Builder(Card.Type.SCIENCE, Era0.Scriptorium)
                .setCost(Card.Resource.PAPER, 1)
                .setProduct(Card.Resource.TABLET, 1)
                .setMakesFree(Era1.Courthouse)
                .setMakesFree(Era1.Library).build());

        return cards;
    }

    private static List<CardImpl> getEra1Cards() {
        List<CardImpl> cards = new ArrayList<>();

        cards.add(new CardImpl.Builder(Card.Type.RESOURCE, Era1.Sawmill)
                .setCost(Card.Resource.GOLD, 1)
                .setProduct(Card.Resource.WOOD, 2).build());

        cards.add(new CardImpl.Builder(Card.Type.RESOURCE, Era1.Quarry)
                .setCost(Card.Resource.GOLD, 1)
                .setProduct(Card.Resource.STONE, 2).build());

        cards.add(new CardImpl.Builder(Card.Type.RESOURCE, Era1.Brickyard)
                .setCost(Card.Resource.GOLD, 1)
                .setProduct(Card.Resource.CLAY, 2).build());

        cards.add(new CardImpl.Builder(Card.Type.RESOURCE, Era1.Foundry)
                .setCost(Card.Resource.GOLD, 1)
                .setProduct(Card.Resource.ORE, 2).build());

        cards.add(new CardImpl.Builder(Card.Type.INDUSTRY, Era1.Loom)
                .setProduct(Card.Resource.CLOTH, 1).build());

        cards.add(new CardImpl.Builder(Card.Type.INDUSTRY, Era1.Glassworks)
                .setProduct(Card.Resource.GLASS, 1).build());

        cards.add(new CardImpl.Builder(Card.Type.INDUSTRY, Era1.Press)
                .setProduct(Card.Resource.PAPER, 1).build());

        cards.add(new CardImpl.Builder(Card.Type.STRUCTURE, Era1.Aqueduct)
                .setCost(Card.Resource.STONE, 3)
                .setProduct(Card.Resource.VP, 5)
                .setMakesThisFree(Era0.Baths).build());

        cards.add(new CardImpl.Builder(Card.Type.STRUCTURE, Era1.Temple)
                .setCost(Card.Resource.WOOD, 1)
                .setCost(Card.Resource.CLAY, 1)
                .setCost(Card.Resource.GLASS, 1)
                .setProduct(Card.Resource.VP, 3)
                .setMakesThisFree(Era0.Altar)
                .setMakesFree(Era2.Pantheon).build());

        cards.add(new CardImpl.Builder(Card.Type.STRUCTURE, Era1.Statue)
                .setCost(Card.Resource.ORE, 2)
                .setCost(Card.Resource.WOOD, 1)
                .setProduct(Card.Resource.VP, 4)
                .setMakesThisFree(Era0.Theater)
                .setMakesFree(Era2.Gardens).build());

        cards.add(new CardImpl.Builder(Card.Type.STRUCTURE, Era1.Courthouse)
                .setCost(Card.Resource.CLAY, 2)
                .setCost(Card.Resource.CLOTH, 1)
                .setProduct(Card.Resource.VP, 4)
                .setMakesThisFree(Era0.Scriptorium).build());

        cards.add(new CardImpl.Builder(Card.Type.COMMERCIAL, Era1.Forum)
                .setCost(Card.Resource.CLAY, 2)
                .setProduct(Card.Resource.CLOTH, 1)
                .setProduct(Card.Resource.GLASS, 1)
                .setProduct(Card.Resource.PAPER, 1)
                .setMakesThisFree(Era0.East_Trading_Post)
                .setMakesThisFree(Era0.West_Trading_Post)
                .setMakesFree(Era2.Haven).build());

        cards.add(new CardImpl.Builder(Card.Type.COMMERCIAL, Era1.Caravansery)
                .setCost(Card.Resource.WOOD, 2)
                .setProduct(Card.Resource.WOOD, 1)
                .setProduct(Card.Resource.CLAY, 1)
                .setProduct(Card.Resource.STONE, 1)
                .setProduct(Card.Resource.ORE, 1)
                .setMakesThisFree(Era0.Marketplace)
                .setMakesFree(Era2.Lighthouse).build());

        cards.add(new CardImpl.Builder(Card.Type.COMMERCIAL, Era1.Vineyard)
                .setMessage("1 coin for each resource card of adjacent players or your own.")
                .setSpecialGold(new SpecialDependsPlayed(Card.Type.RESOURCE, 1, true, true)).build());

        cards.add(new CardImpl.Builder(Card.Type.COMMERCIAL, Era1.Bazaar)
                .setMessage("2 coins for each industry card of adjacent players or your own.")
                .setSpecialGold(new SpecialDependsPlayed(Card.Type.INDUSTRY, 2, true, true)).build());

        cards.add(new CardImpl.Builder(Card.Type.MILITARY, Era1.Walls)
                .setCost(Card.Resource.STONE, 3)
                .setProduct(Card.Resource.SHIELD, 2)
                .setMakesFree(Era2.Fortifications).build());

        cards.add(new CardImpl.Builder(Card.Type.MILITARY, Era1.Training_Ground)
                .setCost(Card.Resource.ORE, 2)
                .setCost(Card.Resource.WOOD, 1)
                .setProduct(Card.Resource.SHIELD, 2)
                .setMakesFree(Era2.Circus).build());

        cards.add(new CardImpl.Builder(Card.Type.MILITARY, Era1.Stables)
                .setCost(Card.Resource.ORE, 1)
                .setCost(Card.Resource.CLAY, 1)
                .setCost(Card.Resource.WOOD, 1)
                .setProduct(Card.Resource.SHIELD, 2)
                .setMakesThisFree(Era0.Apothecary).build());

        cards.add(new CardImpl.Builder(Card.Type.MILITARY, Era1.Archery_Range)
                .setCost(Card.Resource.ORE, 1)
                .setCost(Card.Resource.WOOD, 2)
                .setProduct(Card.Resource.SHIELD, 2)
                .setMakesThisFree(Era0.Workshop).build());

        cards.add(new CardImpl.Builder(Card.Type.SCIENCE, Era1.Dispensary)
                .setCost(Card.Resource.GLASS, 1)
                .setCost(Card.Resource.ORE, 2)
                .setProduct(Card.Resource.COMPASS, 1)
                .setMakesThisFree(Era0.Apothecary)
                .setMakesFree(Era2.Lodge)
                .setMakesFree(Era2.Arena).build());

        cards.add(new CardImpl.Builder(Card.Type.SCIENCE, Era1.Laboratory)
                .setCost(Card.Resource.PAPER, 1)
                .setCost(Card.Resource.CLAY, 2)
                .setProduct(Card.Resource.GEAR, 1)
                .setMakesThisFree(Era0.Workshop)
                .setMakesFree(Era2.Siege_Workshop)
                .setMakesFree(Era2.Observatory).build());

        cards.add(new CardImpl.Builder(Card.Type.SCIENCE, Era1.Library)
                .setCost(Card.Resource.CLOTH, 1)
                .setCost(Card.Resource.STONE, 2)
                .setProduct(Card.Resource.TABLET, 1)
                .setMakesThisFree(Era0.Scriptorium)
                .setMakesFree(Era2.Senate)
                .setMakesFree(Era2.University).build());

        cards.add(new CardImpl.Builder(Card.Type.SCIENCE, Era1.School)
                .setCost(Card.Resource.PAPER, 1)
                .setCost(Card.Resource.WOOD, 1)
                .setProduct(Card.Resource.TABLET, 1)
                .setMakesFree(Era2.Academy)
                .setMakesFree(Era2.Study).build());

        return cards;
    }

    private static List<CardImpl> getEra2Cards() {
        List<CardImpl> cards = new ArrayList<>();

        cards.add(new CardImpl.Builder(Card.Type.STRUCTURE, Era2.Pantheon)
                .setCost(Card.Resource.CLAY, 2)
                .setCost(Card.Resource.ORE, 1)
                .setCost(Card.Resource.PAPER, 1)
                .setCost(Card.Resource.CLOTH, 1)
                .setCost(Card.Resource.GLASS, 1)
                .setProduct(Card.Resource.VP, 7)
                .setMakesThisFree(Era1.Temple).build());

        cards.add(new CardImpl.Builder(Card.Type.STRUCTURE, Era2.Gardens)
                .setCost(Card.Resource.CLAY, 2)
                .setCost(Card.Resource.WOOD, 1)
                .setProduct(Card.Resource.VP, 5)
                .setMakesThisFree(Era1.Statue).build());

        cards.add(new CardImpl.Builder(Card.Type.STRUCTURE, Era2.Town_Hall)
                .setCost(Card.Resource.STONE, 2)
                .setCost(Card.Resource.ORE, 1)
                .setCost(Card.Resource.GLASS, 1)
                .setProduct(Card.Resource.VP, 6).build());

        cards.add(new CardImpl.Builder(Card.Type.STRUCTURE, Era2.Palace)
                .setCost(Card.Resource.WOOD, 1)
                .setCost(Card.Resource.CLAY, 1)
                .setCost(Card.Resource.ORE, 1)
                .setCost(Card.Resource.STONE, 1)
                .setCost(Card.Resource.PAPER, 1)
                .setCost(Card.Resource.CLOTH, 1)
                .setCost(Card.Resource.GLASS, 1)
                .setProduct(Card.Resource.VP, 8).build());

        cards.add(new CardImpl.Builder(Card.Type.STRUCTURE, Era2.Senate)
                .setCost(Card.Resource.WOOD, 2)
                .setCost(Card.Resource.ORE, 1)
                .setCost(Card.Resource.STONE, 1)
                .setProduct(Card.Resource.VP, 6)
                .setMakesThisFree(Era1.Library).build());

        cards.add(new CardImpl.Builder(Card.Type.COMMERCIAL, Era2.Haven)
                .setCost(Card.Resource.ORE, 1)
                .setCost(Card.Resource.WOOD, 1)
                .setCost(Card.Resource.CLOTH, 1)
                .setMessage("1 coin and 1 vp for each resource card.")
                .setSpecialGold(new SpecialDependsPlayed(Card.Type.RESOURCE, 1, false, true))
                .setSpecialVps(new SpecialDependsPlayed(Card.Type.RESOURCE, 1, false, true))
                .setMakesThisFree(Era1.Forum).build());

        cards.add(new CardImpl.Builder(Card.Type.COMMERCIAL, Era2.Lighthouse)
                .setCost(Card.Resource.STONE, 1)
                .setCost(Card.Resource.GLASS, 1)
                .setMessage("1 coin and 1 vp for each commercial card.")
                .setSpecialGold(new SpecialDependsPlayed(Card.Type.COMMERCIAL, 1, false, true))
                .setSpecialVps(new SpecialDependsPlayed(Card.Type.COMMERCIAL, 1, false, true))
                .setMakesThisFree(Era1.Caravansery).build());

        cards.add(new CardImpl.Builder(Card.Type.COMMERCIAL, Era2.Chamber_Of_Commerce)
                .setCost(Card.Resource.CLAY, 2)
                .setCost(Card.Resource.PAPER, 1)
                .setMessage("2 coins and 2 vps for each industrial card.")
                .setSpecialGold(new SpecialDependsPlayed(Card.Type.INDUSTRY, 2, false, true))
                .setSpecialVps(new SpecialDependsPlayed(Card.Type.INDUSTRY, 2, false, true)).build());

        cards.add(new CardImpl.Builder(Card.Type.COMMERCIAL, Era2.Arena)
                .setCost(Card.Resource.STONE, 2)
                .setCost(Card.Resource.ORE, 1)
                .setMessage("3 coins and 1 vp for each completed wonder stage")
                .setSpecialGold(new SpecialDependsPlayed(Card.Type.STAGE, 3, false, true))
                .setSpecialVps(new SpecialDependsPlayed(Card.Type.STAGE, 1, false, true))
                .setMakesThisFree(Era1.Dispensary).build());

        cards.add(new CardImpl.Builder(Card.Type.MILITARY, Era2.Fortifications)
                .setCost(Card.Resource.ORE, 3)
                .setCost(Card.Resource.STONE, 1)
                .setProduct(Card.Resource.SHIELD, 3)
                .setMakesThisFree(Era1.Walls).build());

        cards.add(new CardImpl.Builder(Card.Type.MILITARY, Era2.Circus)
                .setCost(Card.Resource.STONE, 3)
                .setCost(Card.Resource.ORE, 1)
                .setProduct(Card.Resource.SHIELD, 3)
                .setMakesThisFree(Era1.Training_Ground).build());

        cards.add(new CardImpl.Builder(Card.Type.MILITARY, Era2.Arsenal)
                .setCost(Card.Resource.ORE, 1)
                .setCost(Card.Resource.WOOD, 2)
                .setCost(Card.Resource.CLOTH, 1)
                .setProduct(Card.Resource.SHIELD, 3).build());

        cards.add(new CardImpl.Builder(Card.Type.MILITARY, Era2.Siege_Workshop)
                .setCost(Card.Resource.CLAY, 3)
                .setCost(Card.Resource.WOOD, 1)
                .setProduct(Card.Resource.SHIELD, 3)
                .setMakesThisFree(Era1.Laboratory).build());

        cards.add(new CardImpl.Builder(Card.Type.SCIENCE, Era2.Lodge)
                .setCost(Card.Resource.CLAY, 2)
                .setCost(Card.Resource.CLOTH, 1)
                .setCost(Card.Resource.PAPER, 1)
                .setProduct(Card.Resource.COMPASS, 1)
                .setMakesThisFree(Era1.Dispensary).build());

        cards.add(new CardImpl.Builder(Card.Type.SCIENCE, Era2.Observatory)
                .setCost(Card.Resource.ORE, 2)
                .setCost(Card.Resource.GLASS, 1)
                .setCost(Card.Resource.CLOTH, 1)
                .setProduct(Card.Resource.GEAR, 1)
                .setMakesThisFree(Era1.Laboratory).build());

        cards.add(new CardImpl.Builder(Card.Type.SCIENCE, Era2.University)
                .setCost(Card.Resource.WOOD, 2)
                .setCost(Card.Resource.GLASS, 1)
                .setCost(Card.Resource.PAPER, 1)
                .setProduct(Card.Resource.TABLET, 1)
                .setMakesThisFree(Era1.Library).build());

        cards.add(new CardImpl.Builder(Card.Type.SCIENCE, Era2.Academy)
                .setCost(Card.Resource.STONE, 3)
                .setCost(Card.Resource.GLASS, 1)
                .setProduct(Card.Resource.COMPASS, 1)
                .setMakesThisFree(Era1.School).build());

        cards.add(new CardImpl.Builder(Card.Type.SCIENCE, Era2.Study)
                .setCost(Card.Resource.WOOD, 1)
                .setCost(Card.Resource.CLOTH, 1)
                .setCost(Card.Resource.PAPER, 1)
                .setProduct(Card.Resource.GEAR, 1)
                .setMakesThisFree(Era1.School).build());

        cards.add(new CardImpl.Builder(Card.Type.GUILD, Era2.Workers_Guild)
                .setCost(Card.Resource.ORE, 2)
                .setCost(Card.Resource.CLAY, 1)
                .setCost(Card.Resource.STONE, 1)
                .setCost(Card.Resource.WOOD, 1)
                .setMessage("1 vp for each resource card owned by adjacent players.")
                .setSpecialVps(new SpecialDependsPlayed(Card.Type.RESOURCE, 1, true, false)).build());

        cards.add(new CardImpl.Builder(Card.Type.GUILD, Era2.Craftmens_Guild)
                .setCost(Card.Resource.ORE, 2)
                .setCost(Card.Resource.STONE, 2)
                .setMessage("2 vps for each industrial card owned by adjacent players.")
                .setSpecialVps(new SpecialDependsPlayed(Card.Type.INDUSTRY, 2, true, false)).build());

        cards.add(new CardImpl.Builder(Card.Type.GUILD, Era2.Traders_Guild)
                .setCost(Card.Resource.CLOTH, 1)
                .setCost(Card.Resource.PAPER, 1)
                .setCost(Card.Resource.GLASS, 1)
                .setMessage("1 vp for each commercial card owned by adjacent players.")
                .setSpecialVps(new SpecialDependsPlayed(Card.Type.COMMERCIAL, 1, true, false)).build());

        cards.add(new CardImpl.Builder(Card.Type.GUILD, Era2.Philosophers_Guild)
                .setCost(Card.Resource.CLAY, 3)
                .setCost(Card.Resource.CLOTH, 1)
                .setCost(Card.Resource.PAPER, 1)
                .setMessage("1 vp for each scientific card owned by adjacent players.")
                .setSpecialVps(new SpecialDependsPlayed(Card.Type.SCIENCE, 1, true, false)).build());

        cards.add(new CardImpl.Builder(Card.Type.GUILD, Era2.Spy_Guild)
                .setCost(Card.Resource.CLAY, 3)
                .setCost(Card.Resource.GLASS, 1)
                .setMessage("1 vp for each military card owned by adjacent players.")
                .setSpecialVps(new SpecialDependsPlayed(Card.Type.MILITARY, 1, true, false)).build());

        cards.add(new CardImpl.Builder(Card.Type.GUILD, Era2.Strategy_Guild)
                .setCost(Card.Resource.ORE, 2)
                .setCost(Card.Resource.STONE, 1)
                .setCost(Card.Resource.CLOTH, 1)
                .setMessage("1 vp for each military defeat by adjacent players.")
                .setSpecialVps(new AdjacentMilitaryLosses()).build());

        cards.add(new CardImpl.Builder(Card.Type.GUILD, Era2.Shipowners_Guild)
                .setCost(Card.Resource.WOOD, 3)
                .setCost(Card.Resource.PAPER, 1)
                .setCost(Card.Resource.GLASS, 1)
                .setMessage("1 vp for each resource, industrial, and guild card.")
                .setSpecialVps(new MultiSpecial(
                        new SpecialDependsPlayed(Card.Type.RESOURCE, 1, false, true),
                        new SpecialDependsPlayed(Card.Type.INDUSTRY, 1, false, true),
                        new SpecialDependsPlayed(Card.Type.GUILD, 1, false, true))).build());

        cards.add(new CardImpl.Builder(Card.Type.GUILD, Era2.Scientists_Guild)
                .setCost(Card.Resource.WOOD, 2)
                .setCost(Card.Resource.ORE, 2)
                .setCost(Card.Resource.CLOTH, 1)
                .setProduct(Card.Resource.COMPASS, 1)
                .setProduct(Card.Resource.GEAR, 1)
                .setProduct(Card.Resource.TABLET, 1).build());

        cards.add(new CardImpl.Builder(Card.Type.GUILD, Era2.Magistrates_Guild)
                .setCost(Card.Resource.WOOD, 3)
                .setCost(Card.Resource.STONE, 1)
                .setCost(Card.Resource.CLOTH, 1)
                .setMessage("1 vp for each structure card owned by adjacent players.")
                .setSpecialVps(new SpecialDependsPlayed(Card.Type.STRUCTURE, 1, true, false)).build());

        cards.add(new CardImpl.Builder(Card.Type.GUILD, Era2.Builders_Guild)
                .setCost(Card.Resource.STONE, 2)
                .setCost(Card.Resource.CLAY, 2)
                .setCost(Card.Resource.GLASS, 1)
                .setMessage("1 vp for each completed wonder stage by you or adjacent players.")
                .setSpecialVps(new SpecialDependsPlayed(Card.Type.STAGE, 1, true, false)).build());

        return cards;
    }

    private static List<Wonder> getWondersA() {
        List<Wonder> wonders = new ArrayList<>();

        //Rhodes
        wonders.add(new WonderImpl.Builder(Wonders.The_Colossus_of_Rhodes, Card.Resource.ORE)
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_1)
                        .setCost(Card.Resource.WOOD, 2)
                        .setProduct(Card.Resource.VP, 3).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_2)
                        .setCost(Card.Resource.CLAY, 3)
                        .setProduct(Card.Resource.SHIELD, 2).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_3)
                        .setCost(Card.Resource.ORE, 4)
                        .setProduct(Card.Resource.VP, 7).build())
                .build());

        //Alexandria
        wonders.add(new WonderImpl.Builder(Wonders.The_Lighthouse_of_Alexandria, Card.Resource.GLASS)
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_1)
                        .setCost(Card.Resource.STONE, 2)
                        .setProduct(Card.Resource.VP, 3).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_2)
                        .setCost(Card.Resource.ORE, 2)
                        .setProduct(Card.Resource.CLAY, 1)
                        .setProduct(Card.Resource.ORE, 1)
                        .setProduct(Card.Resource.WOOD, 1)
                        .setProduct(Card.Resource.STONE, 1).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_3)
                        .setCost(Card.Resource.GLASS, 2)
                        .setProduct(Card.Resource.VP, 7).build())
                .build());

        //Ephesus
        wonders.add(new WonderImpl.Builder(Wonders.The_Temple_of_Artemis_in_Ephesus, Card.Resource.PAPER)
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_1)
                        .setCost(Card.Resource.STONE, 2)
                        .setProduct(Card.Resource.VP, 3).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_2)
                        .setCost(Card.Resource.WOOD, 2)
                        .setProduct(Card.Resource.GOLD, 9).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_3)
                        .setCost(Card.Resource.PAPER, 2)
                        .setProduct(Card.Resource.VP, 7).build())
                .build());

        //Babylon
        wonders.add(new WonderImpl.Builder(Wonders.The_Hanging_Gardens_of_Babylon, Card.Resource.CLAY)
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_1)
                        .setCost(Card.Resource.CLAY, 2)
                        .setProduct(Card.Resource.VP, 3).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_2)
                        .setCost(Card.Resource.WOOD, 3)
                        .setProduct(Card.Resource.TABLET, 1)
                        .setProduct(Card.Resource.COMPASS, 1)
                        .setProduct(Card.Resource.GEAR, 1).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_3)
                        .setCost(Card.Resource.CLAY, 4)
                        .setProduct(Card.Resource.VP, 7).build())
                .build());

        //Olympia
        wonders.add(new WonderImpl.Builder(Wonders.The_Statue_of_Zeus_in_Olympia, Card.Resource.WOOD)
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_1)
                        .setCost(Card.Resource.WOOD, 2)
                        .setProduct(Card.Resource.VP, 3).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_2)
                        .setCost(Card.Resource.STONE, 2)
                        .addAttribute(Card.Attribute.Play1Free)
                        .setMessage("Once per age can build a card for free.").build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_3)
                        .setCost(Card.Resource.ORE, 2)
                        .setProduct(Card.Resource.VP, 7).build())
                .build());

        //Halicarnassus
        wonders.add(new WonderImpl.Builder(Wonders.The_Mausoleum_of_Halicarnassus, Card.Resource.CLOTH)
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_1)
                        .setCost(Card.Resource.CLAY, 2)
                        .setProduct(Card.Resource.VP, 3).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_2)
                        .setCost(Card.Resource.ORE, 3)
                        .addAttribute(Card.Attribute.FreeBuild)
                        .setMessage("Can look through all discards since the beginning of the game, pick one, and build it for free.").build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_3)
                        .setCost(Card.Resource.CLOTH, 2)
                        .setProduct(Card.Resource.VP, 7).build())
                .build());

        //Giza
        wonders.add(new WonderImpl.Builder(Wonders.The_Pyrimids_of_Giza, Card.Resource.STONE)
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_1)
                        .setCost(Card.Resource.STONE, 2)
                        .setProduct(Card.Resource.VP, 3).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_2)
                        .setCost(Card.Resource.WOOD, 3)
                        .setProduct(Card.Resource.VP, 5).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_3)
                        .setCost(Card.Resource.STONE, 4)
                        .setProduct(Card.Resource.VP, 7).build())
                .build());

        return wonders;
    }

    private static List<Wonder> getWondersB() {
        List<Wonder> wonders = new ArrayList<>();

        //Rhodes
        wonders.add(new WonderImpl.Builder(Wonders.The_Colossus_of_Rhodes, Card.Resource.ORE)
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_1)
                        .setCost(Card.Resource.STONE, 3)
                        .setProduct(Card.Resource.SHIELD, 1)
                        .setProduct(Card.Resource.VP, 3)
                        .setProduct(Card.Resource.GOLD, 3).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_2)
                        .setCost(Card.Resource.ORE, 4)
                        .setProduct(Card.Resource.SHIELD, 1)
                        .setProduct(Card.Resource.VP, 4)
                        .setProduct(Card.Resource.GOLD, 4).build())
                .build());

        //Alexandria
        wonders.add(new WonderImpl.Builder(Wonders.The_Lighthouse_of_Alexandria, Card.Resource.GLASS)
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_1)
                        .setCost(Card.Resource.CLAY, 2)
                        .setProduct(Card.Resource.CLAY, 1)
                        .setProduct(Card.Resource.ORE, 1)
                        .setProduct(Card.Resource.WOOD, 1)
                        .setProduct(Card.Resource.STONE, 1).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_2)
                        .setCost(Card.Resource.WOOD, 2)
                        .setProduct(Card.Resource.GLASS, 1)
                        .setProduct(Card.Resource.CLOTH, 1)
                        .setProduct(Card.Resource.PAPER, 1).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_3)
                        .setCost(Card.Resource.STONE, 3)
                        .setProduct(Card.Resource.VP, 7).build())
                .build());

        //Ephesus
        wonders.add(new WonderImpl.Builder(Wonders.The_Temple_of_Artemis_in_Ephesus, Card.Resource.PAPER)
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_1)
                        .setCost(Card.Resource.STONE, 2)
                        .setProduct(Card.Resource.VP, 2)
                        .setProduct(Card.Resource.GOLD, 4).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_2)
                        .setCost(Card.Resource.WOOD, 2)
                        .setProduct(Card.Resource.VP, 3)
                        .setProduct(Card.Resource.GOLD, 4).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_3)
                        .setCost(Card.Resource.GLASS, 1)
                        .setCost(Card.Resource.CLOTH, 1)
                        .setCost(Card.Resource.PAPER, 1)
                        .setProduct(Card.Resource.VP, 5)
                        .setProduct(Card.Resource.GOLD, 4).build())
                .build());

        //Babylon
        wonders.add(new WonderImpl.Builder(Wonders.The_Hanging_Gardens_of_Babylon, Card.Resource.CLAY)
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_1)
                        .setCost(Card.Resource.CLAY, 1)
                        .setCost(Card.Resource.CLOTH, 1)
                        .setProduct(Card.Resource.VP, 3).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_2)
                        .setCost(Card.Resource.WOOD, 2)
                        .setCost(Card.Resource.GLASS, 1)
                        .addAttribute(Card.Attribute.Play7thCard)
                        .setMessage("Can now play 7th age card rather than discarding it.").build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_3)
                        .setCost(Card.Resource.CLAY, 3)
                        .setCost(Card.Resource.PAPER, 1)
                        .setProduct(Card.Resource.TABLET, 1)
                        .setProduct(Card.Resource.COMPASS, 1)
                        .setProduct(Card.Resource.GEAR, 1).build())
                .build());

        //Olympia
        wonders.add(new WonderImpl.Builder(Wonders.The_Statue_of_Zeus_in_Olympia, Card.Resource.WOOD)
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_1)
                        .setCost(Card.Resource.WOOD, 2)
                        .setMessage("Can trade 1 coin for resources with adjacent players.")
                        .setTradeType(Card.TradeType.resource)
                        .addTradeDirection(Game.Direction.EAST)
                        .addTradeDirection(Game.Direction.WEST).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_2)
                        .setCost(Card.Resource.STONE, 2)
                        .setProduct(Card.Resource.VP, 5).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_3)
                        .setCost(Card.Resource.ORE, 2)
                        .setCost(Card.Resource.CLOTH, 1)
                        .setMessage("Can copy one Guild card built by an adjacent player.")
                        .setSpecialVps(new BestAdjacentGuildVps()).build())
                .build());

        //Halicarnassus
        wonders.add(new WonderImpl.Builder(Wonders.The_Mausoleum_of_Halicarnassus, Card.Resource.CLOTH)
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_1)
                        .setCost(Card.Resource.ORE, 2)
                        .setProduct(Card.Resource.VP, 2)
                        .addAttribute(Card.Attribute.FreeBuild)
                        .setMessage("Can look through all discards since the beginning of the game, pick one, and build it for free.").build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_2)
                        .setCost(Card.Resource.CLAY, 3)
                        .setProduct(Card.Resource.VP, 1)
                        .addAttribute(Card.Attribute.FreeBuild)
                        .setMessage("Can look through all discards since the beginning of the game, pick one, and build it for free.").build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_3)
                        .setCost(Card.Resource.CLOTH, 1)
                        .setCost(Card.Resource.PAPER, 1)
                        .setCost(Card.Resource.GLASS, 1)
                        .addAttribute(Card.Attribute.FreeBuild)
                        .setMessage("Can look through all discards since the beginning of the game, pick one, and build it for free.").build())
                .build());

        //Giza
        wonders.add(new WonderImpl.Builder(Wonders.The_Pyrimids_of_Giza, Card.Resource.STONE)
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_1)
                        .setCost(Card.Resource.WOOD, 2)
                        .setProduct(Card.Resource.VP, 3).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_2)
                        .setCost(Card.Resource.STONE, 3)
                        .setProduct(Card.Resource.VP, 5).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_3)
                        .setCost(Card.Resource.CLAY, 3)
                        .setProduct(Card.Resource.VP, 5).build())
                .addStage(new CardImpl.Builder(Card.Type.STAGE, WonderStages.Stage_4)
                        .setCost(Card.Resource.STONE, 4)
                        .setCost(Card.Resource.PAPER, 1)
                        .setProduct(Card.Resource.VP, 7).build())
                .build());

        return wonders;
    }

    private enum Era0 {
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
        Scriptorium
    }

    private enum Era1 {
        Sawmill,
        Quarry,
        Brickyard,
        Foundry,
        Loom,
        Glassworks,
        Press,
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
        School
    }

    private enum Era2 {
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
        Builders_Guild
    }

    enum Wonders {
        The_Colossus_of_Rhodes,
        The_Lighthouse_of_Alexandria,
        The_Temple_of_Artemis_in_Ephesus,
        The_Hanging_Gardens_of_Babylon,
        The_Statue_of_Zeus_in_Olympia,
        The_Mausoleum_of_Halicarnassus,
        The_Pyrimids_of_Giza
    }

    private enum WonderStages {
        Stage_1,
        Stage_2,
        Stage_3,
        Stage_4
    }
}
