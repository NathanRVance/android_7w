package net.dumtoad.srednow7.cards;

import net.dumtoad.srednow7.cards.special.AdjacentMilitaryLosses;
import net.dumtoad.srednow7.cards.special.BestAdjacentGuildVps;
import net.dumtoad.srednow7.cards.special.MultiSpecial;
import net.dumtoad.srednow7.cards.special.SpecialDependsPlayed;

import java.util.ArrayList;

public class Generate {

    private static ArrayList<Card> era0;
    private static ArrayList<Card> era1;
    private static ArrayList<Card> era2;

    private static ArrayList<Wonder> wonders;

    static void generateCards() {
        era0 = getEra0Cards();
        era1 = getEra1Cards();
        era2 = getEra2Cards();
        couponCards(); //Adds coupons now that all cards are instantiated

        wonders = getWonders_private();
    }

    static Deck getEra0Deck(int numPlayers) {
        return getDeck(era0deck, numPlayers * 7);
    }

    static Deck getEra1Deck(int numPlayers) {
        return getDeck(era1deck, numPlayers * 7);
    }

    static Deck getEra2Cards(int numPlayers) {
        return getDeck(era2deck, numPlayers * 6 - 2);
    }

    static Deck getGuildCards() {
        return getDeck(guilds, 10);
    }

    private static Deck getDeck(Enum[] cardNames, int numCards) {
        Deck deck = new Deck();
        for(int i = 0; i < numCards; i++) {
            deck.add(findCardByName(cardNames[i]));
        }
        return deck;
    }

    public static ArrayList<Wonder> getWonders() {
        return wonders;
    }

    private static void couponCards() {
        findCardByName(Era0.Baths).couponFor(findCardByName(Era1.Aqueduct));
        findCardByName(Era0.Altar).couponFor(findCardByName(Era1.Temple));
        findCardByName(Era0.Theater).couponFor(findCardByName(Era1.Statue));
        findCardByName(Era0.East_Trading_Post).couponFor(findCardByName(Era1.Forum));
        findCardByName(Era0.West_Trading_Post).couponFor(findCardByName(Era1.Forum));
        findCardByName(Era0.Marketplace).couponFor(findCardByName(Era1.Caravansery));
        findCardByName(Era0.Apothecary).couponFor(findCardByName(Era1.Stables));
        findCardByName(Era0.Apothecary).couponFor(findCardByName(Era1.Dispensary));
        findCardByName(Era0.Workshop).couponFor(findCardByName(Era1.Archery_Range));
        findCardByName(Era0.Workshop).couponFor(findCardByName(Era1.Laboratory));
        findCardByName(Era0.Scriptorium).couponFor(findCardByName(Era1.Courthouse));
        findCardByName(Era0.Scriptorium).couponFor(findCardByName(Era1.Library));
        findCardByName(Era1.Temple).couponFor(findCardByName(Era2.Pantheon));
        findCardByName(Era1.Statue).couponFor(findCardByName(Era2.Gardens));
        findCardByName(Era1.Forum).couponFor(findCardByName(Era2.Haven));
        findCardByName(Era1.Caravansery).couponFor(findCardByName(Era2.Lighthouse));
        findCardByName(Era1.Walls).couponFor(findCardByName(Era2.Fortifications));
        findCardByName(Era1.Training_Ground).couponFor(findCardByName(Era2.Circus));
        findCardByName(Era1.Dispensary).couponFor(findCardByName(Era2.Lodge));
        findCardByName(Era1.Dispensary).couponFor(findCardByName(Era2.Arena));
        findCardByName(Era1.Laboratory).couponFor(findCardByName(Era2.Siege_Workshop));
        findCardByName(Era1.Laboratory).couponFor(findCardByName(Era2.Observatory));
        findCardByName(Era1.Library).couponFor(findCardByName(Era2.Senate));
        findCardByName(Era1.Library).couponFor(findCardByName(Era2.University));
        findCardByName(Era1.School).couponFor(findCardByName(Era2.Academy));
        findCardByName(Era1.School).couponFor(findCardByName(Era2.Study));
    }

    private static Card findCardByName(Enum name) {
        for(Card card : era0)
            if(card.getName().equals(name))
                return card;
        for(Card card : era1)
            if(card.getName().equals(name))
                return card;
        for(Card card : era2)
            if(card.getName().equals(name))
                return card;
        throw new RuntimeException("That shouldn't have happended!");
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

    private static final Enum[] era0deck = { Era0.Lumber_Yard, Era0.Stone_Pit, Era0.Clay_Pool, Era0.Ore_Vein, Era0.Clay_Pit, Era0.Timber_Yard, Era0.Loom,
    Era0.Glassworks, Era0.Press, Era0.Baths, Era0.Altar, Era0.Theater, Era0.East_Trading_Post, Era0.West_Trading_Post, Era0.Marketplace,
    Era0.Stockade, Era0.Barracks, Era0.Guard_Tower, Era0.Apothecary, Era0.Workshop, Era0.Scriptorium, Era0.Lumber_Yard, Era0.Ore_Vein, Era0.Excavation,
    Era0.Pawnshop, Era0.Tavern, Era0.Guard_Tower, Era0.Scriptorium, Era0.Stone_Pit, Era0.Clay_Pool, Era0.Forest_Cave, Era0.Altar, Era0.Tavern,
    Era0.Barracks, Era0.Apothecary, Era0.Tree_Farm, Era0.Mine, Era0.Loom, Era0.Glassworks, Era0.Press, Era0.Theater, Era0.Marketplace, Era0.Pawnshop,
    Era0.Baths, Era0.Tavern, Era0.East_Trading_Post, Era0.West_Trading_Post, Era0.Stockade, Era0.Workshop};

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

    private static final Enum[] era1deck = { Era1.Sawmill, Era1.Quarry, Era1.Brickyard, Era1.Foundry, Era1.Loom, Era1.Glassworks, Era1.Press, Era1.Aqueduct,
    Era1.Temple, Era1.Statue, Era1.Courthouse, Era1.Forum, Era1.Caravansery, Era1.Vineyard, Era1.Walls, Era1.Stables, Era1.Archery_Range,
    Era1.Dispensary, Era1.Laboratory, Era1.Library, Era1.School, Era1.Sawmill, Era1.Quarry, Era1.Brickyard, Era1.Foundry, Era1.Bazaar, Era1.Training_Ground,
    Era1.Dispensary, Era1.Loom, Era1.Glassworks, Era1.Press, Era1.Courthouse, Era1.Caravansery, Era1.Stables, Era1.Laboratory, Era1.Temple,
    Era1.Forum, Era1.Caravansery, Era1.Vineyard, Era1.Training_Ground, Era1.Archery_Range, Era1.Library, Era1.Aqueduct, Era1.Statue, Era1.Forum,
    Era1.Bazaar, Era1.Walls, Era1.Training_Ground, Era1.School };

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

    private static final Enum[] era2deck = { Era2.Pantheon, Era2.Gardens, Era2.Town_Hall, Era2.Palace, Era2.Senate, Era2.Haven, Era2.Lighthouse,
    Era2.Arena, Era2.Fortifications, Era2.Arsenal, Era2.Siege_Workshop, Era2.Lodge, Era2.Observatory, Era2.University, Era2.Academy, Era2.Study,
    Era2.Gardens, Era2.Haven, Era2.Chamber_Of_Commerce, Era2.Circus, Era2.Arsenal, Era2.University, Era2.Town_Hall, Era2.Senate, Era2.Arena,
    Era2.Circus, Era2.Siege_Workshop, Era2.Study, Era2.Pantheon, Era2.Town_Hall, Era2.Lighthouse, Era2.Chamber_Of_Commerce, Era2.Circus,
    Era2.Lodge, Era2.Palace, Era2.Arena, Era2.Fortifications, Era2.Arsenal, Era2.Observatory, Era2.Academy };

    private static final Enum[] guilds = { Era2.Workers_Guild, Era2.Craftmens_Guild, Era2.Traders_Guild, Era2.Philosophers_Guild, Era2.Spy_Guild,
    Era2.Strategy_Guild, Era2.Shipowners_Guild, Era2.Scientists_Guild, Era2.Magistrates_Guild, Era2.Builders_Guild };

    public enum Wonders {
        The_Colossus_of_Rhodes,
        The_Lighthouse_of_Alexandria,
        The_Temple_of_Artemis_in_Ephesus,
        The_Hanging_Gardens_of_Babylon,
        The_Statue_of_Zeus_in_Olympia,
        The_Mausoleum_of_Halicarnassus,
        The_Pyrimids_of_Giza
    }

    public enum WonderStages {
        Stage_1,
        Stage_2,
        Stage_3,
        Stage_4
    }

    private static ArrayList<Card> getEra0Cards() {
        ArrayList<Card> cards = new ArrayList<>();
        Card card;

        card = new Card(Card.Type.RESOURCE, Era0.Lumber_Yard);
        card.setProducts(Card.Resource.WOOD, 1);
        cards.add(card);

        card = new Card(Card.Type.RESOURCE, Era0.Stone_Pit);
        card.setProducts(Card.Resource.STONE, 1);
        cards.add(card);

        card = new Card(Card.Type.RESOURCE, Era0.Clay_Pool);
        card.setProducts(Card.Resource.CLAY, 1);
        cards.add(card);

        card = new Card(Card.Type.RESOURCE, Era0.Ore_Vein);
        card.setProducts(Card.Resource.ORE, 1);
        cards.add(card);

        card = new Card(Card.Type.RESOURCE, Era0.Tree_Farm);
        card.setCost(Card.Resource.GOLD, 1);
        card.setProducts(Card.Resource.WOOD, 1);
        card.setProducts(Card.Resource.CLAY, 1);
        cards.add(card);

        card = new Card(Card.Type.RESOURCE, Era0.Excavation);
        card.setCost(Card.Resource.GOLD, 1);
        card.setProducts(Card.Resource.STONE, 1);
        card.setProducts(Card.Resource.CLAY, 1);
        cards.add(card);

        card = new Card(Card.Type.RESOURCE, Era0.Clay_Pit);
        card.setCost(Card.Resource.GOLD, 1);
        card.setProducts(Card.Resource.CLAY, 1);
        card.setProducts(Card.Resource.ORE, 1);
        cards.add(card);

        card = new Card(Card.Type.RESOURCE, Era0.Timber_Yard);
        card.setCost(Card.Resource.GOLD, 1);
        card.setProducts(Card.Resource.STONE, 1);
        card.setProducts(Card.Resource.WOOD, 1);
        cards.add(card);

        card = new Card(Card.Type.RESOURCE, Era0.Forest_Cave);
        card.setCost(Card.Resource.GOLD, 1);
        card.setProducts(Card.Resource.WOOD, 1);
        card.setProducts(Card.Resource.ORE, 1);
        cards.add(card);

        card = new Card(Card.Type.RESOURCE, Era0.Mine);
        card.setCost(Card.Resource.GOLD, 1);
        card.setProducts(Card.Resource.ORE, 1);
        card.setProducts(Card.Resource.STONE, 1);
        cards.add(card);

        card = new Card(Card.Type.INDUSTRY, Era0.Loom);
        card.setProducts(Card.Resource.CLOTH, 1);
        cards.add(card);

        card = new Card(Card.Type.INDUSTRY, Era0.Glassworks);
        card.setProducts(Card.Resource.GLASS, 1);
        cards.add(card);

        card = new Card(Card.Type.INDUSTRY, Era0.Press);
        card.setProducts(Card.Resource.PAPER, 1);
        cards.add(card);

        card = new Card(Card.Type.STRUCTURE, Era0.Pawnshop);
        card.setProducts(Card.Resource.VP, 3);
        cards.add(card);

        card = new Card(Card.Type.STRUCTURE, Era0.Baths);
        card.setCost(Card.Resource.STONE, 1);
        card.setProducts(Card.Resource.VP, 3);
        cards.add(card);

        card = new Card(Card.Type.STRUCTURE, Era0.Altar);
        card.setProducts(Card.Resource.VP, 2);
        cards.add(card);

        card = new Card(Card.Type.STRUCTURE, Era0.Theater);
        card.setProducts(Card.Resource.VP, 2);
        cards.add(card);

        card = new Card(Card.Type.COMMERCIAL, Era0.Tavern);
        card.setProducts(Card.Resource.GOLD, 5);
        cards.add(card);

        card = new Card(Card.Type.COMMERCIAL, Era0.East_Trading_Post);
        card.setMessage("Can trade 1 coin for resources with player to the east.");
        card.setTradeType(Card.TradeType.resource);
        card.setTradeDirection(Card.TradeDirection.east);
        cards.add(card);

        card = new Card(Card.Type.COMMERCIAL, Era0.West_Trading_Post);
        card.setMessage("Can trade 1 coin for resources with player to the west.");
        card.setTradeType(Card.TradeType.resource);
        card.setTradeDirection(Card.TradeDirection.west);
        cards.add(card);

        card = new Card(Card.Type.COMMERCIAL, Era0.Marketplace);
        card.setMessage("Can trade 1 coin for industry products with adjacent players.");
        card.setTradeType(Card.TradeType.industry);
        card.setTradeDirection(Card.TradeDirection.both);
        cards.add(card);

        card = new Card(Card.Type.MILITARY, Era0.Stockade);
        card.setCost(Card.Resource.WOOD, 1);
        card.setProducts(Card.Resource.SHIELD, 1);
        cards.add(card);

        card = new Card(Card.Type.MILITARY, Era0.Barracks);
        card.setCost(Card.Resource.ORE, 1);
        card.setProducts(Card.Resource.SHIELD, 1);
        cards.add(card);

        card = new Card(Card.Type.MILITARY, Era0.Guard_Tower);
        card.setCost(Card.Resource.CLAY, 1);
        card.setProducts(Card.Resource.SHIELD, 1);
        cards.add(card);

        card = new Card(Card.Type.SCIENCE, Era0.Apothecary);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setProducts(Card.Resource.COMPASS, 1);
        cards.add(card);

        card = new Card(Card.Type.SCIENCE, Era0.Workshop);
        card.setCost(Card.Resource.GLASS, 1);
        card.setProducts(Card.Resource.GEAR, 1);
        cards.add(card);

        card = new Card(Card.Type.SCIENCE, Era0.Scriptorium);
        card.setCost(Card.Resource.PAPER, 1);
        card.setProducts(Card.Resource.TABLET, 1);
        cards.add(card);

        return cards;
    }

    private static ArrayList<Card> getEra1Cards() {
        ArrayList<Card> cards = new ArrayList<>();
        Card card;

        card = new Card(Card.Type.RESOURCE, Era1.Sawmill);
        card.setCost(Card.Resource.GOLD, 1);
        card.setProducts(Card.Resource.WOOD, 2);
        cards.add(card);

        card = new Card(Card.Type.RESOURCE, Era1.Quarry);
        card.setCost(Card.Resource.GOLD, 1);
        card.setProducts(Card.Resource.STONE, 2);
        cards.add(card);

        card = new Card(Card.Type.RESOURCE, Era1.Brickyard);
        card.setCost(Card.Resource.GOLD, 1);
        card.setProducts(Card.Resource.CLAY, 2);
        cards.add(card);

        card = new Card(Card.Type.RESOURCE, Era1.Foundry);
        card.setCost(Card.Resource.GOLD, 1);
        card.setProducts(Card.Resource.ORE, 2);
        cards.add(card);

        card = new Card(Card.Type.INDUSTRY, Era1.Loom);
        card.setProducts(Card.Resource.CLOTH, 1);
        cards.add(card);

        card = new Card(Card.Type.INDUSTRY, Era1.Glassworks);
        card.setProducts(Card.Resource.GLASS, 1);
        cards.add(card);

        card = new Card(Card.Type.INDUSTRY, Era1.Press);
        card.setProducts(Card.Resource.PAPER, 1);
        cards.add(card);

        card = new Card(Card.Type.STRUCTURE, Era1.Aqueduct);
        card.setCost(Card.Resource.STONE, 3);
        card.setProducts(Card.Resource.VP, 5);
        cards.add(card);

        card = new Card(Card.Type.STRUCTURE, Era1.Temple);
        card.setCost(Card.Resource.WOOD, 1);
        card.setCost(Card.Resource.CLAY, 1);
        card.setCost(Card.Resource.GLASS, 1);
        card.setProducts(Card.Resource.VP, 3);
        cards.add(card);

        card = new Card(Card.Type.STRUCTURE, Era1.Statue);
        card.setCost(Card.Resource.ORE, 2);
        card.setCost(Card.Resource.WOOD, 1);
        card.setProducts(Card.Resource.VP, 4);
        cards.add(card);

        card = new Card(Card.Type.STRUCTURE, Era1.Courthouse);
        card.setCost(Card.Resource.CLAY, 2);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setProducts(Card.Resource.VP, 4);
        cards.add(card);

        card = new Card(Card.Type.COMMERCIAL, Era1.Forum);
        card.setCost(Card.Resource.CLAY, 2);
        card.setProducts(Card.Resource.CLOTH, 1);
        card.setProducts(Card.Resource.GLASS, 1);
        card.setProducts(Card.Resource.PAPER, 1);
        cards.add(card);

        card = new Card(Card.Type.COMMERCIAL, Era1.Caravansery);
        card.setCost(Card.Resource.WOOD, 2);
        card.setProducts(Card.Resource.WOOD, 1);
        card.setProducts(Card.Resource.CLAY, 1);
        card.setProducts(Card.Resource.STONE, 1);
        card.setProducts(Card.Resource.ORE, 1);
        cards.add(card);

        card = new Card(Card.Type.COMMERCIAL, Era1.Vineyard);
        card.setMessage("1 coin for each resource card of adjacent players or your own.");
        card.setSpecialGold(new SpecialDependsPlayed(Card.Type.RESOURCE, 1, true, true));
        cards.add(card);

        card = new Card(Card.Type.COMMERCIAL, Era1.Bazaar);
        card.setMessage("2 coins for each industry card of adjacent players or your own.");
        card.setSpecialGold(new SpecialDependsPlayed(Card.Type.INDUSTRY, 2, true, true));
        cards.add(card);

        card = new Card(Card.Type.MILITARY, Era1.Walls);
        card.setCost(Card.Resource.STONE, 3);
        card.setProducts(Card.Resource.SHIELD, 2);
        cards.add(card);

        card = new Card(Card.Type.MILITARY, Era1.Training_Ground);
        card.setCost(Card.Resource.ORE, 2);
        card.setCost(Card.Resource.WOOD, 1);
        card.setProducts(Card.Resource.SHIELD, 2);
        cards.add(card);

        card = new Card(Card.Type.MILITARY, Era1.Stables);
        card.setCost(Card.Resource.ORE, 1);
        card.setCost(Card.Resource.CLAY, 1);
        card.setCost(Card.Resource.WOOD, 1);
        card.setProducts(Card.Resource.SHIELD, 2);
        cards.add(card);

        card = new Card(Card.Type.MILITARY, Era1.Archery_Range);
        card.setCost(Card.Resource.ORE, 1);
        card.setCost(Card.Resource.WOOD, 2);
        card.setProducts(Card.Resource.SHIELD, 2);
        cards.add(card);

        card = new Card(Card.Type.SCIENCE, Era1.Dispensary);
        card.setCost(Card.Resource.GLASS, 1);
        card.setCost(Card.Resource.ORE, 2);
        card.setProducts(Card.Resource.COMPASS, 1);
        cards.add(card);

        card = new Card(Card.Type.SCIENCE, Era1.Laboratory);
        card.setCost(Card.Resource.PAPER, 1);
        card.setCost(Card.Resource.CLAY, 2);
        card.setProducts(Card.Resource.GEAR, 1);
        cards.add(card);

        card = new Card(Card.Type.SCIENCE, Era1.Library);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setCost(Card.Resource.STONE, 2);
        card.setProducts(Card.Resource.TABLET, 1);
        cards.add(card);

        card = new Card(Card.Type.SCIENCE, Era1.School);
        card.setCost(Card.Resource.PAPER, 1);
        card.setCost(Card.Resource.WOOD, 1);
        card.setProducts(Card.Resource.TABLET, 1);
        cards.add(card);

        return cards;
    }

    private static ArrayList<Card> getEra2Cards() {
        ArrayList<Card> cards = new ArrayList<>();
        Card card;

        card = new Card(Card.Type.STRUCTURE, Era2.Pantheon);
        card.setCost(Card.Resource.CLAY, 2);
        card.setCost(Card.Resource.ORE, 1);
        card.setCost(Card.Resource.PAPER, 1);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setCost(Card.Resource.GLASS, 1);
        card.setProducts(Card.Resource.VP, 7);
        cards.add(card);

        card = new Card(Card.Type.STRUCTURE, Era2.Gardens);
        card.setCost(Card.Resource.CLAY, 2);
        card.setCost(Card.Resource.WOOD, 1);
        card.setProducts(Card.Resource.VP, 5);
        cards.add(card);

        card = new Card(Card.Type.STRUCTURE, Era2.Town_Hall);
        card.setCost(Card.Resource.STONE, 2);
        card.setCost(Card.Resource.ORE, 1);
        card.setCost(Card.Resource.GLASS, 1);
        card.setProducts(Card.Resource.VP, 6);
        cards.add(card);

        card = new Card(Card.Type.STRUCTURE, Era2.Palace);
        card.setCost(Card.Resource.WOOD, 1);
        card.setCost(Card.Resource.CLAY, 1);
        card.setCost(Card.Resource.ORE, 1);
        card.setCost(Card.Resource.STONE, 1);
        card.setCost(Card.Resource.PAPER, 1);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setCost(Card.Resource.GLASS, 1);
        card.setProducts(Card.Resource.VP, 8);
        cards.add(card);

        card = new Card(Card.Type.STRUCTURE, Era2.Senate);
        card.setCost(Card.Resource.WOOD, 2);
        card.setCost(Card.Resource.ORE, 1);
        card.setCost(Card.Resource.STONE, 1);
        card.setProducts(Card.Resource.VP, 6);
        cards.add(card);

        card = new Card(Card.Type.COMMERCIAL, Era2.Haven);
        card.setCost(Card.Resource.ORE, 1);
        card.setCost(Card.Resource.WOOD, 1);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setMessage("1 coin and 1 vp for each resource card.");
        card.setSpecialGold(new SpecialDependsPlayed(Card.Type.RESOURCE, 1, false, true));
        card.setSpecialVps(new SpecialDependsPlayed(Card.Type.RESOURCE, 1, false, true));
        cards.add(card);

        card = new Card(Card.Type.COMMERCIAL, Era2.Lighthouse);
        card.setCost(Card.Resource.STONE, 1);
        card.setCost(Card.Resource.GLASS, 1);
        card.setMessage("1 coin and 1 vp for each commercial card.");
        card.setSpecialGold(new SpecialDependsPlayed(Card.Type.COMMERCIAL, 1, false, true));
        card.setSpecialVps(new SpecialDependsPlayed(Card.Type.COMMERCIAL, 1, false, true));
        cards.add(card);

        card = new Card(Card.Type.COMMERCIAL, Era2.Chamber_Of_Commerce);
        card.setCost(Card.Resource.CLAY, 2);
        card.setCost(Card.Resource.PAPER, 1);
        card.setMessage("2 coins and 2 vps for each industrial card.");
        card.setSpecialGold(new SpecialDependsPlayed(Card.Type.INDUSTRY, 2, false, true));
        card.setSpecialVps(new SpecialDependsPlayed(Card.Type.INDUSTRY, 2, false, true));
        cards.add(card);

        card = new Card(Card.Type.COMMERCIAL, Era2.Arena);
        card.setCost(Card.Resource.STONE, 2);
        card.setCost(Card.Resource.ORE, 1);
        card.setMessage("3 coins and 1 vp for each completed wonder stage");
        card.setSpecialGold(new SpecialDependsPlayed(Card.Type.STAGE, 3, false, true));
        card.setSpecialVps(new SpecialDependsPlayed(Card.Type.STAGE, 1, false, true));
        cards.add(card);

        card = new Card(Card.Type.MILITARY, Era2.Fortifications);
        card.setCost(Card.Resource.ORE, 3);
        card.setCost(Card.Resource.STONE, 1);
        card.setProducts(Card.Resource.SHIELD, 3);
        cards.add(card);

        card = new Card(Card.Type.MILITARY, Era2.Circus);
        card.setCost(Card.Resource.STONE, 3);
        card.setCost(Card.Resource.ORE, 1);
        card.setProducts(Card.Resource.SHIELD, 3);
        cards.add(card);

        card = new Card(Card.Type.MILITARY, Era2.Arsenal);
        card.setCost(Card.Resource.ORE, 1);
        card.setCost(Card.Resource.WOOD, 2);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setProducts(Card.Resource.SHIELD, 3);
        cards.add(card);

        card = new Card(Card.Type.MILITARY, Era2.Siege_Workshop);
        card.setCost(Card.Resource.CLAY, 3);
        card.setCost(Card.Resource.WOOD, 1);
        card.setProducts(Card.Resource.SHIELD, 3);
        cards.add(card);

        card = new Card(Card.Type.SCIENCE, Era2.Lodge);
        card.setCost(Card.Resource.CLAY, 2);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setCost(Card.Resource.PAPER, 1);
        card.setProducts(Card.Resource.COMPASS, 1);
        cards.add(card);

        card = new Card(Card.Type.SCIENCE, Era2.Observatory);
        card.setCost(Card.Resource.ORE, 2);
        card.setCost(Card.Resource.GLASS, 1);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setProducts(Card.Resource.GEAR, 1);
        cards.add(card);

        card = new Card(Card.Type.SCIENCE, Era2.University);
        card.setCost(Card.Resource.WOOD, 2);
        card.setCost(Card.Resource.GLASS, 1);
        card.setCost(Card.Resource.PAPER, 1);
        card.setProducts(Card.Resource.TABLET, 1);
        cards.add(card);

        card = new Card(Card.Type.SCIENCE, Era2.Academy);
        card.setCost(Card.Resource.GLASS, 1);
        card.setCost(Card.Resource.STONE, 3);
        card.setProducts(Card.Resource.COMPASS, 1);
        cards.add(card);

        card = new Card(Card.Type.SCIENCE, Era2.Study);
        card.setCost(Card.Resource.WOOD, 1);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setCost(Card.Resource.PAPER, 1);
        card.setProducts(Card.Resource.GEAR, 1);
        cards.add(card);

        card = new Card(Card.Type.GUILD, Era2.Workers_Guild);
        card.setCost(Card.Resource.ORE, 2);
        card.setCost(Card.Resource.CLAY, 1);
        card.setCost(Card.Resource.STONE, 1);
        card.setCost(Card.Resource.WOOD, 1);
        card.setMessage(
                "1 vp for each resource card owned by adjacent players.");
        card.setSpecialVps(new SpecialDependsPlayed(Card.Type.RESOURCE, 1, true, false));
        cards.add(card);

        card = new Card(Card.Type.GUILD, Era2.Craftmens_Guild);
        card.setCost(Card.Resource.ORE, 2);
        card.setCost(Card.Resource.STONE, 2);
        card.setMessage(
                "2 vps for each industrial card owned by adjacent players.");
        card.setSpecialVps(new SpecialDependsPlayed(Card.Type.INDUSTRY, 2, true, false));
        cards.add(card);

        card = new Card(Card.Type.GUILD, Era2.Traders_Guild);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setCost(Card.Resource.PAPER, 1);
        card.setCost(Card.Resource.GLASS, 1);
        card.setMessage(
                "1 vp for each commercial card owned by adjacent players.");
        card.setSpecialVps(new SpecialDependsPlayed(Card.Type.COMMERCIAL, 1, true, false));
        cards.add(card);

        card = new Card(Card.Type.GUILD, Era2.Philosophers_Guild);
        card.setCost(Card.Resource.CLAY, 3);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setCost(Card.Resource.PAPER, 1);
        card.setMessage(
                "1 vp for each scientific card owned by adjacent players.");
        card.setSpecialVps(new SpecialDependsPlayed(Card.Type.SCIENCE, 1, true, false));
        cards.add(card);

        card = new Card(Card.Type.GUILD, Era2.Spy_Guild);
        card.setCost(Card.Resource.CLAY, 3);
        card.setCost(Card.Resource.GLASS, 1);
        card.setMessage(
                "1 vp for each military card owned by adjacent players.");
        card.setSpecialVps(new SpecialDependsPlayed(Card.Type.MILITARY, 1, true, false));
        cards.add(card);

        card = new Card(Card.Type.GUILD, Era2.Strategy_Guild);
        card.setCost(Card.Resource.ORE, 2);
        card.setCost(Card.Resource.STONE, 1);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setMessage(
                "1 vp for each military defeat by adjacent players.");
        card.setSpecialVps(new AdjacentMilitaryLosses());
        cards.add(card);

        card = new Card(Card.Type.GUILD, Era2.Shipowners_Guild);
        card.setCost(Card.Resource.WOOD, 3);
        card.setCost(Card.Resource.PAPER, 1);
        card.setCost(Card.Resource.GLASS, 1);
        card.setMessage(
                "1 vp for each resource, industrial, and guild card.");
        card.setSpecialVps(new MultiSpecial(
                new SpecialDependsPlayed(Card.Type.RESOURCE, 1, false, true),
                new SpecialDependsPlayed(Card.Type.INDUSTRY, 1, false, true),
                new SpecialDependsPlayed(Card.Type.GUILD, 1, false, true)
        ));
        cards.add(card);

        card = new Card(Card.Type.GUILD, Era2.Scientists_Guild);
        card.setCost(Card.Resource.WOOD, 2);
        card.setCost(Card.Resource.ORE, 2);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setProducts(Card.Resource.COMPASS, 1);
        card.setProducts(Card.Resource.GEAR, 1);
        card.setProducts(Card.Resource.TABLET, 1);
        cards.add(card);

        card = new Card(Card.Type.GUILD, Era2.Magistrates_Guild);
        card.setCost(Card.Resource.WOOD, 3);
        card.setCost(Card.Resource.STONE, 1);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setMessage(
                "1 vp for each structure card owned by adjacent players.");
        card.setSpecialVps(new SpecialDependsPlayed(Card.Type.STRUCTURE, 1, true, false));
        cards.add(card);

        card = new Card(Card.Type.GUILD, Era2.Builders_Guild);
        card.setCost(Card.Resource.STONE, 2);
        card.setCost(Card.Resource.CLAY, 2);
        card.setCost(Card.Resource.GLASS, 1);
        card.setMessage(
                "1 vp for each completed wonder stage by you or adjacent players.");
        card.setSpecialVps(new SpecialDependsPlayed(Card.Type.STAGE, 1, true, false));
        cards.add(card);

        return cards;
    }

    private static ArrayList<Wonder> getWonders_private() {
        ArrayList<Wonder> wonders = new ArrayList<>();
        Wonder wonder;
        Card card;
        CardCollection stages;

        //Rhodes
        wonder = new Wonder(Wonders.The_Colossus_of_Rhodes);
        wonder.setResource(Card.Resource.ORE);
        stages = new CardCollection();
        card = new Card(Card.Type.STAGE, WonderStages.Stage_1);
        card.setCost(Card.Resource.WOOD, 2);
        card.setProducts(Card.Resource.VP, 3);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_2);
        card.setCost(Card.Resource.CLAY, 3);
        card.setProducts(Card.Resource.SHIELD, 2);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_3);
        card.setCost(Card.Resource.ORE, 4);
        card.setProducts(Card.Resource.VP, 7);
        stages.add(card);
        wonder.setStagesA(stages);

        stages = new CardCollection();
        card = new Card(Card.Type.STAGE, WonderStages.Stage_1);
        card.setCost(Card.Resource.STONE, 3);
        card.setProducts(Card.Resource.SHIELD, 1);
        card.setProducts(Card.Resource.VP, 3);
        card.setProducts(Card.Resource.GOLD, 3);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_2);
        card.setCost(Card.Resource.ORE, 4);
        card.setProducts(Card.Resource.SHIELD, 1);
        card.setProducts(Card.Resource.VP, 4);
        card.setProducts(Card.Resource.GOLD, 4);
        stages.add(card);
        wonder.setStagesB(stages);
        wonders.add(wonder);


        //Alexandria
        wonder = new Wonder(Wonders.The_Lighthouse_of_Alexandria);
        wonder.setResource(Card.Resource.GLASS);
        stages = new CardCollection();
        card = new Card(Card.Type.STAGE, WonderStages.Stage_1);
        card.setCost(Card.Resource.STONE, 2);
        card.setProducts(Card.Resource.VP, 3);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_2);
        card.setCost(Card.Resource.ORE, 2);
        card.setProducts(Card.Resource.CLAY, 1);
        card.setProducts(Card.Resource.ORE, 1);
        card.setProducts(Card.Resource.WOOD, 1);
        card.setProducts(Card.Resource.STONE, 1);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_3);
        card.setCost(Card.Resource.GLASS, 2);
        card.setProducts(Card.Resource.VP, 7);
        stages.add(card);
        wonder.setStagesA(stages);

        stages = new CardCollection();
        card = new Card(Card.Type.STAGE, WonderStages.Stage_1);
        card.setCost(Card.Resource.CLAY, 2);
        card.setProducts(Card.Resource.CLAY, 1);
        card.setProducts(Card.Resource.ORE, 1);
        card.setProducts(Card.Resource.WOOD, 1);
        card.setProducts(Card.Resource.STONE, 1);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_2);
        card.setCost(Card.Resource.WOOD, 2);
        card.setProducts(Card.Resource.GLASS, 1);
        card.setProducts(Card.Resource.CLOTH, 1);
        card.setProducts(Card.Resource.PAPER, 1);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_3);
        card.setCost(Card.Resource.STONE, 3);
        card.setProducts(Card.Resource.VP, 7);
        stages.add(card);
        wonder.setStagesB(stages);
        wonders.add(wonder);


        //Ephesus
        wonder = new Wonder(Wonders.The_Temple_of_Artemis_in_Ephesus);
        wonder.setResource(Card.Resource.PAPER);
        stages = new CardCollection();
        card = new Card(Card.Type.STAGE, WonderStages.Stage_1);
        card.setCost(Card.Resource.STONE, 2);
        card.setProducts(Card.Resource.VP, 3);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_2);
        card.setCost(Card.Resource.WOOD, 2);
        card.setProducts(Card.Resource.GOLD, 9);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_3);
        card.setCost(Card.Resource.PAPER, 2);
        card.setProducts(Card.Resource.VP, 7);
        stages.add(card);
        wonder.setStagesA(stages);

        stages = new CardCollection();
        card = new Card(Card.Type.STAGE, WonderStages.Stage_1);
        card.setCost(Card.Resource.STONE, 2);
        card.setProducts(Card.Resource.VP, 2);
        card.setProducts(Card.Resource.GOLD, 4);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_2);
        card.setCost(Card.Resource.WOOD, 2);
        card.setProducts(Card.Resource.VP, 3);
        card.setProducts(Card.Resource.GOLD, 4);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_3);
        card.setCost(Card.Resource.GLASS, 1);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setCost(Card.Resource.PAPER, 1);
        card.setProducts(Card.Resource.VP, 5);
        card.setProducts(Card.Resource.GOLD, 4);
        stages.add(card);
        wonder.setStagesB(stages);
        wonders.add(wonder);


        //Babylon
        wonder = new Wonder(Wonders.The_Hanging_Gardens_of_Babylon);
        wonder.setResource(Card.Resource.CLAY);
        stages = new CardCollection();
        card = new Card(Card.Type.STAGE, WonderStages.Stage_1);
        card.setCost(Card.Resource.CLAY, 2);
        card.setProducts(Card.Resource.VP, 3);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_2);
        card.setCost(Card.Resource.WOOD, 3);
        card.setProducts(Card.Resource.TABLET, 1);
        card.setProducts(Card.Resource.COMPASS, 1);
        card.setProducts(Card.Resource.GEAR, 1);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_3);
        card.setCost(Card.Resource.CLAY, 4);
        card.setProducts(Card.Resource.VP, 7);
        stages.add(card);
        wonder.setStagesA(stages);

        stages = new CardCollection();
        card = new Card(Card.Type.STAGE, WonderStages.Stage_1);
        card.setCost(Card.Resource.CLAY, 1);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setProducts(Card.Resource.VP, 3);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_2);
        card.setCost(Card.Resource.WOOD, 2);
        card.setCost(Card.Resource.GLASS, 1);
        card.setMessage("Can now play 7th age card rather than discarding it.");
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_3);
        card.setCost(Card.Resource.CLAY, 3);
        card.setCost(Card.Resource.PAPER, 1);
        card.setProducts(Card.Resource.TABLET, 1);
        card.setProducts(Card.Resource.COMPASS, 1);
        card.setProducts(Card.Resource.GEAR, 1);
        stages.add(card);
        wonder.setStagesB(stages);
        wonders.add(wonder);


        //Olympia
        wonder = new Wonder(Wonders.The_Statue_of_Zeus_in_Olympia);
        wonder.setResource(Card.Resource.WOOD);
        stages = new CardCollection();
        card = new Card(Card.Type.STAGE, WonderStages.Stage_1);
        card.setCost(Card.Resource.WOOD, 2);
        card.setProducts(Card.Resource.VP, 3);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_2);
        card.setCost(Card.Resource.STONE, 2);
        card.setMessage("Once per age can build a card for free.");
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_3);
        card.setCost(Card.Resource.ORE, 2);
        card.setProducts(Card.Resource.VP, 7);
        stages.add(card);
        wonder.setStagesA(stages);

        stages = new CardCollection();
        card = new Card(Card.Type.STAGE, WonderStages.Stage_1);
        card.setCost(Card.Resource.WOOD, 2);
        card.setMessage("Can trade 1 coin for resources with adjacent players.");
        card.setTradeType(Card.TradeType.resource);
        card.setTradeDirection(Card.TradeDirection.both);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_2);
        card.setCost(Card.Resource.STONE, 2);
        card.setProducts(Card.Resource.VP, 5);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_3);
        card.setCost(Card.Resource.ORE, 2);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setMessage("Can copy one Guild card built by an adjacent player.");
        card.setSpecialVps(new BestAdjacentGuildVps());
        stages.add(card);
        wonder.setStagesB(stages);
        wonders.add(wonder);


        //Halicarnassus
        wonder = new Wonder(Wonders.The_Mausoleum_of_Halicarnassus);
        wonder.setResource(Card.Resource.CLOTH);
        stages = new CardCollection();
        card = new Card(Card.Type.STAGE, WonderStages.Stage_1);
        card.setCost(Card.Resource.CLAY, 2);
        card.setProducts(Card.Resource.VP, 3);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_2);
        card.setCost(Card.Resource.ORE, 3);
        card.setMessage("Can look through all discards since the beginning of the game, pick one, and build it for free.");
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_3);
        card.setCost(Card.Resource.CLOTH, 2);
        card.setProducts(Card.Resource.VP, 7);
        stages.add(card);
        wonder.setStagesA(stages);

        stages = new CardCollection();
        card = new Card(Card.Type.STAGE, WonderStages.Stage_1);
        card.setCost(Card.Resource.ORE, 2);
        card.setProducts(Card.Resource.VP, 2);
        card.setMessage("Can look through all discards since the beginning of the game, pick one, and build it for free.");
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_2);
        card.setCost(Card.Resource.CLAY, 3);
        card.setProducts(Card.Resource.VP, 1);
        card.setMessage("Can look through all discards since the beginning of the game, pick one, and build it for free.");

        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_3);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setCost(Card.Resource.PAPER, 1);
        card.setCost(Card.Resource.GLASS, 1);
        card.setMessage("Can look through all discards since the beginning of the game, pick one, and build it for free.");
        stages.add(card);
        wonder.setStagesB(stages);
        wonders.add(wonder);


        //Giza
        wonder = new Wonder(Wonders.The_Pyrimids_of_Giza);
        wonder.setResource(Card.Resource.STONE);
        stages = new CardCollection();
        card = new Card(Card.Type.STAGE, WonderStages.Stage_1);
        card.setCost(Card.Resource.STONE, 2);
        card.setProducts(Card.Resource.VP, 3);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_2);
        card.setCost(Card.Resource.WOOD, 3);
        card.setProducts(Card.Resource.VP, 5);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_3);
        card.setCost(Card.Resource.STONE, 4);
        card.setProducts(Card.Resource.VP, 7);
        stages.add(card);
        wonder.setStagesA(stages);

        stages = new CardCollection();
        card = new Card(Card.Type.STAGE, WonderStages.Stage_1);
        card.setCost(Card.Resource.WOOD, 2);
        card.setProducts(Card.Resource.VP, 3);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_2);
        card.setCost(Card.Resource.STONE, 3);
        card.setProducts(Card.Resource.VP, 5);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_3);
        card.setCost(Card.Resource.CLAY, 3);
        card.setProducts(Card.Resource.VP, 5);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_4);
        card.setCost(Card.Resource.STONE, 4);
        card.setCost(Card.Resource.PAPER, 1);
        card.setProducts(Card.Resource.VP, 7);
        stages.add(card);
        wonder.setStagesB(stages);
        wonders.add(wonder);

        return wonders;
    }
}
