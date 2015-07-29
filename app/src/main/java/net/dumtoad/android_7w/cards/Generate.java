package net.dumtoad.android_7w.cards;

import java.util.ArrayList;

/**
 * Created by nathav63 on 7/27/15.
 */
public class Generate {

    private static ArrayList<Card> era1;
    private static ArrayList<Card> era2;
    private static ArrayList<Card> era3;

    private static ArrayList<Wonder> wonders;

    public static void generateCards() {
        era1 = getEra1Cards();
        era2 = getEra2Cards();
        era3 = getEra3Cards();
        couponCards(); //Adds coupons now that all cards are instantiated

        wonders = getWonders_private();
    }

    public static Deck getEra1Deck(int numPlayers) {
        return getDeck(era1deck, numPlayers * 7);
    }

    public static Deck getEra2Deck(int numPlayers) {
        return getDeck(era2deck, numPlayers * 7);
    }

    public static Deck getEra3Cards(int numPlayers) {
        return getDeck(era3deck, numPlayers * 6 - 2);
    }

    public static Deck getGuildCards() {
        return getDeck(guilds, 10);
    }

    private static Deck getDeck(Enum[] cardNames, int numCards) {
        Deck deck = new Deck();
        for(int i = 0; i < numCards; i++) {
            deck.add(findCardByName(cardNames[i]).getCopy());
        }
        return deck;
    }

    public static ArrayList<Wonder> getWonders() {
        return wonders;
    }

    private static void couponCards() {
        findCardByName(Era1.Baths).couponFor(findCardByName(Era2.Aqueduct));
        findCardByName(Era1.Altar).couponFor(findCardByName(Era2.Temple));
        findCardByName(Era1.Theater).couponFor(findCardByName(Era2.Statue));
        findCardByName(Era1.East_Trading_Post).couponFor(findCardByName(Era2.Forum));
        findCardByName(Era1.West_Trading_Post).couponFor(findCardByName(Era2.Forum));
        findCardByName(Era1.Marketplace).couponFor(findCardByName(Era2.Caravansery));
        findCardByName(Era1.Apothecary).couponFor(findCardByName(Era2.Stables));
        findCardByName(Era1.Apothecary).couponFor(findCardByName(Era2.Dispensary));
        findCardByName(Era1.Workshop).couponFor(findCardByName(Era2.Archery_Range));
        findCardByName(Era1.Workshop).couponFor(findCardByName(Era2.Laboratory));
        findCardByName(Era1.Scriptorium).couponFor(findCardByName(Era2.Courthouse));
        findCardByName(Era1.Scriptorium).couponFor(findCardByName(Era2.Library));
        findCardByName(Era2.Temple).couponFor(findCardByName(Era3.Pantheon));
        findCardByName(Era2.Statue).couponFor(findCardByName(Era3.Gardens));
        findCardByName(Era2.Forum).couponFor(findCardByName(Era3.Haven));
        findCardByName(Era2.Caravansery).couponFor(findCardByName(Era3.Lighthouse));
        findCardByName(Era2.Walls).couponFor(findCardByName(Era3.Fortifications));
        findCardByName(Era2.Training_Ground).couponFor(findCardByName(Era3.Circus));
        findCardByName(Era2.Dispensary).couponFor(findCardByName(Era3.Lodge));
        findCardByName(Era2.Dispensary).couponFor(findCardByName(Era3.Arena));
        findCardByName(Era2.Laboratory).couponFor(findCardByName(Era3.Siege_Workshop));
        findCardByName(Era2.Laboratory).couponFor(findCardByName(Era3.Observatory));
        findCardByName(Era2.Library).couponFor(findCardByName(Era3.Senate));
        findCardByName(Era2.Library).couponFor(findCardByName(Era3.University));
        findCardByName(Era2.School).couponFor(findCardByName(Era3.Academy));
        findCardByName(Era2.School).couponFor(findCardByName(Era3.Study));
    }

    private static Card findCardByName(Enum name) {
        for(Card card : era1)
            if(card.getName().equals(name))
                return card;
        for(Card card : era2)
            if(card.getName().equals(name))
                return card;
        for(Card card : era3)
            if(card.getName().equals(name))
                return card;
        return null;
    }

    public enum Era1 {
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

    private static final Enum[] era1deck = { Era1.Lumber_Yard, Era1.Stone_Pit, Era1.Clay_Pool, Era1.Ore_Vein, Era1.Clay_Pit, Era1.Timber_Yard, Era1.Loom,
    Era1.Glassworks, Era1.Press, Era1.Baths, Era1.Altar, Era1.Theater, Era1.East_Trading_Post, Era1.West_Trading_Post, Era1.Marketplace,
    Era1.Stockade, Era1.Barracks, Era1.Guard_Tower, Era1.Apothecary, Era1.Workshop, Era1.Scriptorium, Era1.Lumber_Yard, Era1.Ore_Vein, Era1.Excavation,
    Era1.Pawnshop, Era1.Tavern, Era1.Guard_Tower, Era1.Scriptorium, Era1.Stone_Pit, Era1.Clay_Pool, Era1.Forest_Cave, Era1.Altar, Era1.Tavern,
    Era1.Barracks, Era1.Apothecary, Era1.Tree_Farm, Era1.Mine, Era1.Loom, Era1.Glassworks, Era1.Press, Era1.Theater, Era1.Marketplace, Era1.Pawnshop,
    Era1.Baths, Era1.Tavern, Era1.East_Trading_Post, Era1.West_Trading_Post, Era1.Stockade, Era1.Workshop};

    public enum Era2 {
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

    private static final Enum[] era2deck = { Era2.Sawmill, Era2.Quarry, Era2.Brickyard, Era2.Foundry, Era2.Loom, Era2.Glassworks, Era2.Press, Era2.Aqueduct,
    Era2.Temple, Era2.Statue, Era2.Courthouse, Era2.Forum, Era2.Caravansery, Era2.Vineyard, Era2.Walls, Era2.Stables, Era2.Archery_Range,
    Era2.Dispensary, Era2.Laboratory, Era2.Library, Era2.School, Era2.Sawmill, Era2.Quarry, Era2.Brickyard, Era2.Foundry, Era2.Bazaar, Era2.Training_Ground,
    Era2.Dispensary, Era2.Loom, Era2.Glassworks, Era2.Press, Era2.Courthouse, Era2.Caravansery, Era2.Stables, Era2.Laboratory, Era2.Temple,
    Era2.Forum, Era2.Caravansery, Era2.Vineyard, Era2.Training_Ground, Era2.Archery_Range, Era2.Library, Era2.Aqueduct, Era2.Statue, Era2.Forum,
    Era2.Bazaar, Era2.Walls, Era2.Training_Ground, Era2.School };

    public enum Era3 {
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

    private static final Enum[] era3deck = { Era3.Pantheon, Era3.Gardens, Era3.Town_Hall, Era3.Palace, Era3.Senate, Era3.Haven, Era3.Lighthouse,
    Era3.Arena, Era3.Fortifications, Era3.Arsenal, Era3.Siege_Workshop, Era3.Lodge, Era3.Observatory, Era3.University, Era3.Academy, Era3.Study,
    Era3.Gardens, Era3.Haven, Era3.Chamber_Of_Commerce, Era3.Circus, Era3.Arsenal, Era3.University, Era3.Town_Hall, Era3.Senate, Era3.Arena,
    Era3.Circus, Era3.Siege_Workshop, Era3.Study, Era3.Pantheon, Era3.Town_Hall, Era3.Lighthouse, Era3.Chamber_Of_Commerce, Era3.Circus,
    Era3.Lodge, Era3.Palace, Era3.Arena, Era3.Fortifications, Era3.Arsenal, Era3.Observatory, Era3.Academy };

    private static final Enum[] guilds = { Era3.Workers_Guild, Era3.Craftmens_Guild, Era3.Traders_Guild, Era3.Philosophers_Guild, Era3.Spy_Guild,
    Era3.Strategy_Guild, Era3.Shipowners_Guild, Era3.Scientists_Guild, Era3.Magistrates_Guild, Era3.Builders_Guild };

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

    private static ArrayList<Card> getEra1Cards() {
        ArrayList<Card> cards = new ArrayList<>();
        Card card;

        card = new Card(Card.Type.RESOURCE, Era1.Lumber_Yard);
        card.setProducts(Card.Product.WOOD, 1);
        cards.add(card);

        card = new Card(Card.Type.RESOURCE, Era1.Stone_Pit);
        card.setProducts(Card.Product.STONE, 1);
        cards.add(card);

        card = new Card(Card.Type.RESOURCE, Era1.Clay_Pool);
        card.setProducts(Card.Product.CLAY, 1);
        cards.add(card);

        card = new Card(Card.Type.RESOURCE, Era1.Ore_Vein);
        card.setProducts(Card.Product.ORE, 1);
        cards.add(card);

        card = new Card(Card.Type.RESOURCE, Era1.Tree_Farm);
        card.setCost(Card.Resource.GOLD, 1);
        card.setProducts(Card.Product.WOOD, 1);
        card.setProducts(Card.Product.CLAY, 1);
        cards.add(card);

        card = new Card(Card.Type.RESOURCE, Era1.Excavation);
        card.setCost(Card.Resource.GOLD, 1);
        card.setProducts(Card.Product.STONE, 1);
        card.setProducts(Card.Product.CLAY, 1);
        cards.add(card);

        card = new Card(Card.Type.RESOURCE, Era1.Clay_Pit);
        card.setCost(Card.Resource.GOLD, 1);
        card.setProducts(Card.Product.CLAY, 1);
        card.setProducts(Card.Product.ORE, 1);
        cards.add(card);

        card = new Card(Card.Type.RESOURCE, Era1.Timber_Yard);
        card.setCost(Card.Resource.GOLD, 1);
        card.setProducts(Card.Product.STONE, 1);
        card.setProducts(Card.Product.WOOD, 1);
        cards.add(card);

        card = new Card(Card.Type.RESOURCE, Era1.Forest_Cave);
        card.setCost(Card.Resource.GOLD, 1);
        card.setProducts(Card.Product.WOOD, 1);
        card.setProducts(Card.Product.ORE, 1);
        cards.add(card);

        card = new Card(Card.Type.RESOURCE, Era1.Mine);
        card.setCost(Card.Resource.GOLD, 1);
        card.setProducts(Card.Product.ORE, 1);
        card.setProducts(Card.Product.STONE, 1);
        cards.add(card);

        card = new Card(Card.Type.INDUSTRY, Era1.Loom);
        card.setProducts(Card.Product.CLOTH, 1);
        cards.add(card);

        card = new Card(Card.Type.INDUSTRY, Era1.Glassworks);
        card.setProducts(Card.Product.GLASS, 1);
        cards.add(card);

        card = new Card(Card.Type.INDUSTRY, Era1.Press);
        card.setProducts(Card.Product.PAPER, 1);
        cards.add(card);

        card = new Card(Card.Type.STRUCTURE, Era1.Pawnshop);
        card.setProducts(Card.Product.VP, 3);
        cards.add(card);

        card = new Card(Card.Type.STRUCTURE, Era1.Baths);
        card.setCost(Card.Resource.STONE, 1);
        card.setProducts(Card.Product.VP, 3);
        cards.add(card);

        card = new Card(Card.Type.STRUCTURE, Era1.Altar);
        card.setProducts(Card.Product.VP, 2);
        cards.add(card);

        card = new Card(Card.Type.STRUCTURE, Era1.Theater);
        card.setProducts(Card.Product.VP, 2);
        cards.add(card);

        card = new Card(Card.Type.COMMERCIAL, Era1.Tavern);
        card.setProducts(Card.Product.GOLD, 5);
        cards.add(card);

        card = new Card(Card.Type.COMMERCIAL, Era1.East_Trading_Post);
        card.setMessage("Can trade 1 coin for resources with player to the east.");
        cards.add(card);

        card = new Card(Card.Type.COMMERCIAL, Era1.West_Trading_Post);
        card.setMessage("Can trade 1 coin for resources with player to the west.");
        cards.add(card);

        card = new Card(Card.Type.COMMERCIAL, Era1.Marketplace);
        card.setMessage("Can trade 1 coin for industry products with adjacent players.");
        cards.add(card);

        card = new Card(Card.Type.MILITARY, Era1.Stockade);
        card.setCost(Card.Resource.WOOD, 1);
        card.setProducts(Card.Product.SHIELD, 1);
        cards.add(card);

        card = new Card(Card.Type.MILITARY, Era1.Barracks);
        card.setCost(Card.Resource.ORE, 1);
        card.setProducts(Card.Product.SHIELD, 1);
        cards.add(card);

        card = new Card(Card.Type.MILITARY, Era1.Guard_Tower);
        card.setCost(Card.Resource.CLAY, 1);
        card.setProducts(Card.Product.SHIELD, 1);
        cards.add(card);

        card = new Card(Card.Type.SCIENCE, Era1.Apothecary);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setProducts(Card.Product.COMPASS, 1);
        cards.add(card);

        card = new Card(Card.Type.SCIENCE, Era1.Workshop);
        card.setCost(Card.Resource.GLASS, 1);
        card.setProducts(Card.Product.GEAR, 1);
        cards.add(card);

        card = new Card(Card.Type.SCIENCE, Era1.Scriptorium);
        card.setCost(Card.Resource.PAPER, 1);
        card.setProducts(Card.Product.TABLET, 1);
        cards.add(card);

        return cards;
    }

    private static ArrayList<Card> getEra2Cards() {
        ArrayList<Card> cards = new ArrayList<>();
        Card card;

        card = new Card(Card.Type.RESOURCE, Era2.Sawmill);
        card.setCost(Card.Resource.GOLD, 1);
        card.setProducts(Card.Product.WOOD, 2);
        cards.add(card);

        card = new Card(Card.Type.RESOURCE, Era2.Quarry);
        card.setCost(Card.Resource.GOLD, 1);
        card.setProducts(Card.Product.STONE, 2);
        cards.add(card);

        card = new Card(Card.Type.RESOURCE, Era2.Brickyard);
        card.setCost(Card.Resource.GOLD, 1);
        card.setProducts(Card.Product.CLAY, 2);
        cards.add(card);

        card = new Card(Card.Type.RESOURCE, Era2.Foundry);
        card.setCost(Card.Resource.GOLD, 1);
        card.setProducts(Card.Product.ORE, 2);
        cards.add(card);

        card = new Card(Card.Type.INDUSTRY, Era2.Loom);
        card.setProducts(Card.Product.CLOTH, 1);
        cards.add(card);

        card = new Card(Card.Type.INDUSTRY, Era2.Glassworks);
        card.setProducts(Card.Product.GLASS, 1);
        cards.add(card);

        card = new Card(Card.Type.INDUSTRY, Era2.Press);
        card.setProducts(Card.Product.PAPER, 1);
        cards.add(card);

        card = new Card(Card.Type.STRUCTURE, Era2.Aqueduct);
        card.setCost(Card.Resource.STONE, 3);
        card.setProducts(Card.Product.VP, 5);
        cards.add(card);

        card = new Card(Card.Type.STRUCTURE, Era2.Temple);
        card.setCost(Card.Resource.WOOD, 1);
        card.setCost(Card.Resource.CLAY, 1);
        card.setCost(Card.Resource.GLASS, 1);
        card.setProducts(Card.Product.VP, 3);
        cards.add(card);

        card = new Card(Card.Type.STRUCTURE, Era2.Statue);
        card.setCost(Card.Resource.ORE, 2);
        card.setCost(Card.Resource.WOOD, 1);
        card.setProducts(Card.Product.VP, 4);
        cards.add(card);

        card = new Card(Card.Type.STRUCTURE, Era2.Courthouse);
        card.setCost(Card.Resource.CLAY, 2);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setProducts(Card.Product.VP, 4);
        cards.add(card);

        card = new Card(Card.Type.COMMERCIAL, Era2.Forum);
        card.setCost(Card.Resource.CLAY, 2);
        card.setProducts(Card.Product.CLOTH, 1);
        card.setProducts(Card.Product.GLASS, 1);
        card.setProducts(Card.Product.PAPER, 1);
        cards.add(card);

        card = new Card(Card.Type.COMMERCIAL, Era2.Caravansery);
        card.setCost(Card.Resource.WOOD, 2);
        card.setProducts(Card.Product.WOOD, 1);
        card.setProducts(Card.Product.CLAY, 1);
        card.setProducts(Card.Product.STONE, 1);
        card.setProducts(Card.Product.ORE, 1);
        cards.add(card);

        card = new Card(Card.Type.COMMERCIAL, Era2.Vineyard);
        card.setMessage("1 coin for each resource card of adjacent players or your own.");
        cards.add(card);

        card = new Card(Card.Type.COMMERCIAL, Era2.Bazaar);
        card.setMessage("2 coins for each industry card of adjacent players or your own.");
        cards.add(card);

        card = new Card(Card.Type.MILITARY, Era2.Walls);
        card.setCost(Card.Resource.STONE, 3);
        card.setProducts(Card.Product.SHIELD, 2);
        cards.add(card);

        card = new Card(Card.Type.MILITARY, Era2.Training_Ground);
        card.setCost(Card.Resource.ORE, 2);
        card.setCost(Card.Resource.WOOD, 1);
        card.setProducts(Card.Product.SHIELD, 2);
        cards.add(card);

        card = new Card(Card.Type.MILITARY, Era2.Stables);
        card.setCost(Card.Resource.ORE, 1);
        card.setCost(Card.Resource.CLAY, 1);
        card.setCost(Card.Resource.WOOD, 1);
        card.setProducts(Card.Product.SHIELD, 2);
        cards.add(card);

        card = new Card(Card.Type.MILITARY, Era2.Archery_Range);
        card.setCost(Card.Resource.ORE, 1);
        card.setCost(Card.Resource.WOOD, 2);
        card.setProducts(Card.Product.SHIELD, 2);
        cards.add(card);

        card = new Card(Card.Type.SCIENCE, Era2.Dispensary);
        card.setCost(Card.Resource.GLASS, 1);
        card.setCost(Card.Resource.ORE, 2);
        card.setProducts(Card.Product.COMPASS, 1);
        cards.add(card);

        card = new Card(Card.Type.SCIENCE, Era2.Laboratory);
        card.setCost(Card.Resource.PAPER, 1);
        card.setCost(Card.Resource.CLAY, 2);
        card.setProducts(Card.Product.GEAR, 1);
        cards.add(card);

        card = new Card(Card.Type.SCIENCE, Era2.Library);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setCost(Card.Resource.STONE, 2);
        card.setProducts(Card.Product.TABLET, 1);
        cards.add(card);

        card = new Card(Card.Type.SCIENCE, Era2.School);
        card.setCost(Card.Resource.PAPER, 1);
        card.setCost(Card.Resource.WOOD, 1);
        card.setProducts(Card.Product.TABLET, 1);
        cards.add(card);

        return cards;
    }

    private static ArrayList<Card> getEra3Cards() {
        ArrayList<Card> cards = new ArrayList<>();
        Card card;

        card = new Card(Card.Type.STRUCTURE, Era3.Pantheon);
        card.setCost(Card.Resource.CLAY, 2);
        card.setCost(Card.Resource.ORE, 1);
        card.setCost(Card.Resource.PAPER, 1);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setCost(Card.Resource.GLASS, 1);
        card.setProducts(Card.Product.VP, 7);
        cards.add(card);

        card = new Card(Card.Type.STRUCTURE, Era3.Gardens);
        card.setCost(Card.Resource.CLAY, 2);
        card.setCost(Card.Resource.WOOD, 1);
        card.setProducts(Card.Product.VP, 5);
        cards.add(card);

        card = new Card(Card.Type.STRUCTURE, Era3.Town_Hall);
        card.setCost(Card.Resource.STONE, 2);
        card.setCost(Card.Resource.ORE, 1);
        card.setCost(Card.Resource.GLASS, 1);
        card.setProducts(Card.Product.VP, 6);
        cards.add(card);

        card = new Card(Card.Type.STRUCTURE, Era3.Palace);
        card.setCost(Card.Resource.WOOD, 1);
        card.setCost(Card.Resource.CLAY, 1);
        card.setCost(Card.Resource.ORE, 1);
        card.setCost(Card.Resource.STONE, 1);
        card.setCost(Card.Resource.PAPER, 1);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setCost(Card.Resource.GLASS, 1);
        card.setProducts(Card.Product.VP, 8);
        cards.add(card);

        card = new Card(Card.Type.STRUCTURE, Era3.Senate);
        card.setCost(Card.Resource.WOOD, 2);
        card.setCost(Card.Resource.ORE, 1);
        card.setCost(Card.Resource.STONE, 1);
        card.setProducts(Card.Product.VP, 6);
        cards.add(card);

        card = new Card(Card.Type.COMMERCIAL, Era3.Haven);
        card.setCost(Card.Resource.ORE, 1);
        card.setCost(Card.Resource.WOOD, 1);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setMessage("1 coin and 1 vp for each resource card.");
        cards.add(card);

        card = new Card(Card.Type.COMMERCIAL, Era3.Lighthouse);
        card.setCost(Card.Resource.STONE, 1);
        card.setCost(Card.Resource.GLASS, 1);
        card.setMessage("1 coin and 1 vp for each commercial card.");
        cards.add(card);

        card = new Card(Card.Type.COMMERCIAL, Era3.Chamber_Of_Commerce);
        card.setCost(Card.Resource.CLAY, 2);
        card.setCost(Card.Resource.PAPER, 1);
        card.setMessage("2 coins and 2 vps for each industrial card.");
        cards.add(card);

        card = new Card(Card.Type.COMMERCIAL, Era3.Arena);
        card.setCost(Card.Resource.STONE, 2);
        card.setCost(Card.Resource.ORE, 1);
        card.setMessage("3 coins and 1 vp for each completed wonder stage.");
        cards.add(card);

        card = new Card(Card.Type.MILITARY, Era3.Fortifications);
        card.setCost(Card.Resource.ORE, 3);
        card.setCost(Card.Resource.STONE, 1);
        card.setProducts(Card.Product.SHIELD, 3);
        cards.add(card);

        card = new Card(Card.Type.MILITARY, Era3.Circus);
        card.setCost(Card.Resource.STONE, 3);
        card.setCost(Card.Resource.ORE, 1);
        card.setProducts(Card.Product.SHIELD, 3);
        cards.add(card);

        card = new Card(Card.Type.MILITARY, Era3.Arsenal);
        card.setCost(Card.Resource.ORE, 1);
        card.setCost(Card.Resource.WOOD, 2);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setProducts(Card.Product.SHIELD, 3);
        cards.add(card);

        card = new Card(Card.Type.MILITARY, Era3.Siege_Workshop);
        card.setCost(Card.Resource.CLAY, 3);
        card.setCost(Card.Resource.WOOD, 1);
        card.setProducts(Card.Product.SHIELD, 3);
        cards.add(card);

        card = new Card(Card.Type.SCIENCE, Era3.Lodge);
        card.setCost(Card.Resource.CLAY, 2);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setCost(Card.Resource.PAPER, 1);
        card.setProducts(Card.Product.COMPASS, 1);
        cards.add(card);

        card = new Card(Card.Type.SCIENCE, Era3.Observatory);
        card.setCost(Card.Resource.ORE, 2);
        card.setCost(Card.Resource.GLASS, 1);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setProducts(Card.Product.GEAR, 1);
        cards.add(card);

        card = new Card(Card.Type.SCIENCE, Era3.University);
        card.setCost(Card.Resource.WOOD, 2);
        card.setCost(Card.Resource.GLASS, 1);
        card.setCost(Card.Resource.PAPER, 1);
        card.setProducts(Card.Product.TABLET, 1);
        cards.add(card);

        card = new Card(Card.Type.SCIENCE, Era3.Academy);
        card.setCost(Card.Resource.GLASS, 2);
        card.setCost(Card.Resource.STONE, 3);
        card.setProducts(Card.Product.COMPASS, 1);
        cards.add(card);

        card = new Card(Card.Type.SCIENCE, Era3.Study);
        card.setCost(Card.Resource.WOOD, 1);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setCost(Card.Resource.PAPER, 1);
        card.setProducts(Card.Product.GEAR, 1);
        cards.add(card);

        card = new Card(Card.Type.GUILD, Era3.Workers_Guild);
        card.setCost(Card.Resource.ORE, 2);
        card.setCost(Card.Resource.CLAY, 1);
        card.setCost(Card.Resource.STONE, 1);
        card.setCost(Card.Resource.WOOD, 1);
        card.setMessage(
                "1 vp for each resource card owned by adjacent players.");
        cards.add(card);

        card = new Card(Card.Type.GUILD, Era3.Craftmens_Guild);
        card.setCost(Card.Resource.ORE, 2);
        card.setCost(Card.Resource.STONE, 2);
        card.setMessage(
                "2 vps for each industrial card owned by adjacent players.");
        cards.add(card);

        card = new Card(Card.Type.GUILD, Era3.Traders_Guild);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setCost(Card.Resource.PAPER, 1);
        card.setCost(Card.Resource.GLASS, 1);
        card.setMessage(
                "1 vp for each commercial card owned by adjacent players.");
        cards.add(card);

        card = new Card(Card.Type.GUILD, Era3.Philosophers_Guild);
        card.setCost(Card.Resource.CLAY, 3);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setCost(Card.Resource.PAPER, 1);
        card.setMessage(
                "1 vp for each scientific card owned by adjacent players.");
        cards.add(card);

        card = new Card(Card.Type.GUILD, Era3.Spy_Guild);
        card.setCost(Card.Resource.CLAY, 3);
        card.setCost(Card.Resource.GLASS, 1);
        card.setMessage(
                "1 vp for each military card owned by adjacent players.");
        cards.add(card);

        card = new Card(Card.Type.GUILD, Era3.Strategy_Guild);
        card.setCost(Card.Resource.ORE, 2);
        card.setCost(Card.Resource.STONE, 1);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setMessage(
                "1 vp for each military defeat by adjacent players.");
        cards.add(card);

        card = new Card(Card.Type.GUILD, Era3.Shipowners_Guild);
        card.setCost(Card.Resource.WOOD, 3);
        card.setCost(Card.Resource.PAPER, 1);
        card.setCost(Card.Resource.GLASS, 1);
        card.setMessage(
                "1 vp for each resource, industrial, and guild card.");
        cards.add(card);

        card = new Card(Card.Type.GUILD, Era3.Scientists_Guild);
        card.setCost(Card.Resource.WOOD, 2);
        card.setCost(Card.Resource.ORE, 2);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setMessage(
                "Counts as either a compass, gear or tablet.");
        cards.add(card);

        card = new Card(Card.Type.GUILD, Era3.Magistrates_Guild);
        card.setCost(Card.Resource.WOOD, 3);
        card.setCost(Card.Resource.STONE, 1);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setMessage(
                "1 vp for each structure card owned by adjacent players.");
        cards.add(card);

        card = new Card(Card.Type.GUILD, Era3.Builders_Guild);
        card.setCost(Card.Resource.STONE, 2);
        card.setCost(Card.Resource.CLAY, 2);
        card.setCost(Card.Resource.GLASS, 1);
        card.setMessage(
                "1 vp for each completed wonder stage by you or adjacent players.");
        cards.add(card);

        return cards;
    }

    private static ArrayList<Wonder> getWonders_private() {
        ArrayList<Wonder> wonders = new ArrayList<>();
        Wonder wonder;
        Card card;
        ArrayList<Card> stages;

        //Rhodes
        wonder = new Wonder(Wonders.The_Colossus_of_Rhodes);
        wonder.setResource(Card.Resource.ORE);
        stages = new ArrayList<>();
        card = new Card(Card.Type.STAGE, WonderStages.Stage_1);
        card.setCost(Card.Resource.WOOD, 2);
        card.setProducts(Card.Product.VP, 3);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_2);
        card.setCost(Card.Resource.CLAY, 3);
        card.setProducts(Card.Product.SHIELD, 2);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_3);
        card.setCost(Card.Resource.ORE, 4);
        card.setProducts(Card.Product.VP, 7);
        stages.add(card);
        wonder.setStagesA(stages);

        stages = new ArrayList<>();
        card = new Card(Card.Type.STAGE, WonderStages.Stage_1);
        card.setCost(Card.Resource.STONE, 3);
        card.setProducts(Card.Product.SHIELD, 1);
        card.setProducts(Card.Product.VP, 3);
        card.setProducts(Card.Product.GOLD, 3);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_2);
        card.setCost(Card.Resource.ORE, 4);
        card.setProducts(Card.Product.SHIELD, 1);
        card.setProducts(Card.Product.VP, 4);
        card.setProducts(Card.Product.GOLD, 4);
        stages.add(card);
        wonder.setStagesB(stages);
        wonders.add(wonder);


        //Alexandria
        wonder = new Wonder(Wonders.The_Lighthouse_of_Alexandria);
        wonder.setResource(Card.Resource.GLASS);
        stages = new ArrayList<>();
        card = new Card(Card.Type.STAGE, WonderStages.Stage_1);
        card.setCost(Card.Resource.STONE, 2);
        card.setProducts(Card.Product.VP, 3);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_2);
        card.setCost(Card.Resource.ORE, 2);
        card.setProducts(Card.Product.CLAY, 1);
        card.setProducts(Card.Product.ORE, 1);
        card.setProducts(Card.Product.WOOD, 1);
        card.setProducts(Card.Product.STONE, 1);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_3);
        card.setCost(Card.Resource.GLASS, 2);
        card.setProducts(Card.Product.VP, 7);
        stages.add(card);
        wonder.setStagesA(stages);

        stages = new ArrayList<>();
        card = new Card(Card.Type.STAGE, WonderStages.Stage_1);
        card.setCost(Card.Resource.CLAY, 2);
        card.setProducts(Card.Product.CLAY, 1);
        card.setProducts(Card.Product.ORE, 1);
        card.setProducts(Card.Product.WOOD, 1);
        card.setProducts(Card.Product.STONE, 1);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_2);
        card.setCost(Card.Resource.WOOD, 2);
        card.setProducts(Card.Product.GLASS, 1);
        card.setProducts(Card.Product.CLOTH, 1);
        card.setProducts(Card.Product.PAPER, 1);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_3);
        card.setCost(Card.Resource.STONE, 3);
        card.setProducts(Card.Product.VP, 7);
        stages.add(card);
        wonder.setStagesB(stages);
        wonders.add(wonder);


        //Ephesus
        wonder = new Wonder(Wonders.The_Temple_of_Artemis_in_Ephesus);
        wonder.setResource(Card.Resource.PAPER);
        stages = new ArrayList<>();
        card = new Card(Card.Type.STAGE, WonderStages.Stage_1);
        card.setCost(Card.Resource.STONE, 2);
        card.setProducts(Card.Product.VP, 3);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_2);
        card.setCost(Card.Resource.WOOD, 2);
        card.setProducts(Card.Product.GOLD, 9);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_3);
        card.setCost(Card.Resource.PAPER, 2);
        card.setProducts(Card.Product.VP, 7);
        stages.add(card);
        wonder.setStagesA(stages);

        stages = new ArrayList<>();
        card = new Card(Card.Type.STAGE, WonderStages.Stage_1);
        card.setCost(Card.Resource.STONE, 2);
        card.setProducts(Card.Product.VP, 2);
        card.setProducts(Card.Product.GOLD, 4);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_2);
        card.setCost(Card.Resource.WOOD, 2);
        card.setProducts(Card.Product.VP, 3);
        card.setProducts(Card.Product.GOLD, 4);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_3);
        card.setCost(Card.Resource.GLASS, 1);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setCost(Card.Resource.PAPER, 1);
        card.setProducts(Card.Product.VP, 5);
        card.setProducts(Card.Product.GOLD, 4);
        stages.add(card);
        wonder.setStagesB(stages);
        wonders.add(wonder);


        //Babylon
        wonder = new Wonder(Wonders.The_Hanging_Gardens_of_Babylon);
        wonder.setResource(Card.Resource.CLAY);
        stages = new ArrayList<>();
        card = new Card(Card.Type.STAGE, WonderStages.Stage_1);
        card.setCost(Card.Resource.CLAY, 2);
        card.setProducts(Card.Product.VP, 3);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_2);
        card.setCost(Card.Resource.WOOD, 3);
        card.setProducts(Card.Product.TABLET, 1);
        card.setProducts(Card.Product.COMPASS, 1);
        card.setProducts(Card.Product.GEAR, 1);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_3);
        card.setCost(Card.Resource.CLAY, 4);
        card.setProducts(Card.Product.VP, 7);
        stages.add(card);
        wonder.setStagesA(stages);

        stages = new ArrayList<>();
        card = new Card(Card.Type.STAGE, WonderStages.Stage_1);
        card.setCost(Card.Resource.CLAY, 1);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setProducts(Card.Product.VP, 3);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_2);
        card.setCost(Card.Resource.WOOD, 2);
        card.setCost(Card.Resource.GLASS, 1);
        card.setMessage("Can now play 7th age card rather than discarding it.");
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_3);
        card.setCost(Card.Resource.CLAY, 3);
        card.setCost(Card.Resource.PAPER, 1);
        card.setProducts(Card.Product.TABLET, 1);
        card.setProducts(Card.Product.COMPASS, 1);
        card.setProducts(Card.Product.GEAR, 1);
        stages.add(card);
        wonder.setStagesB(stages);
        wonders.add(wonder);


        //Olympia
        wonder = new Wonder(Wonders.The_Statue_of_Zeus_in_Olympia);
        wonder.setResource(Card.Resource.WOOD);
        stages = new ArrayList<>();
        card = new Card(Card.Type.STAGE, WonderStages.Stage_1);
        card.setCost(Card.Resource.WOOD, 2);
        card.setProducts(Card.Product.VP, 3);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_2);
        card.setCost(Card.Resource.STONE, 2);
        card.setMessage("Once per age can build a card for free.");
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_3);
        card.setCost(Card.Resource.ORE, 2);
        card.setProducts(Card.Product.VP, 7);
        stages.add(card);
        wonder.setStagesA(stages);

        stages = new ArrayList<>();
        card = new Card(Card.Type.STAGE, WonderStages.Stage_1);
        card.setCost(Card.Resource.WOOD, 2);
        card.setMessage("Can trade 1 coin for resources with adjacent players.");
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_2);
        card.setCost(Card.Resource.STONE, 2);
        card.setProducts(Card.Product.VP, 5);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_3);
        card.setCost(Card.Resource.ORE, 2);
        card.setCost(Card.Resource.CLOTH, 1);
        card.setMessage("Can copy one Guild card built by an adjacent player.");
        stages.add(card);
        wonder.setStagesB(stages);
        wonders.add(wonder);


        //Halicarnassus
        wonder = new Wonder(Wonders.The_Mausoleum_of_Halicarnassus);
        wonder.setResource(Card.Resource.CLOTH);
        stages = new ArrayList<>();
        card = new Card(Card.Type.STAGE, WonderStages.Stage_1);
        card.setCost(Card.Resource.CLAY, 2);
        card.setProducts(Card.Product.VP, 3);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_2);
        card.setCost(Card.Resource.ORE, 3);
        card.setMessage("Can look through all discards since the beginning of the game, pick one, and build it for free.");
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_3);
        card.setCost(Card.Resource.CLOTH, 2);
        card.setProducts(Card.Product.VP, 7);
        stages.add(card);
        wonder.setStagesA(stages);

        stages = new ArrayList<>();
        card = new Card(Card.Type.STAGE, WonderStages.Stage_1);
        card.setCost(Card.Resource.ORE, 2);
        card.setProducts(Card.Product.VP, 2);
        card.setMessage("Can look through all discards since the beginning of the game, pick one, and build it for free.");
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_2);
        card.setCost(Card.Resource.CLAY, 3);
        card.setProducts(Card.Product.VP, 1);
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
        stages = new ArrayList<>();
        card = new Card(Card.Type.STAGE, WonderStages.Stage_1);
        card.setCost(Card.Resource.STONE, 2);
        card.setProducts(Card.Product.VP, 3);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_2);
        card.setCost(Card.Resource.WOOD, 3);
        card.setProducts(Card.Product.VP, 5);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_3);
        card.setCost(Card.Resource.STONE, 4);
        card.setProducts(Card.Product.VP, 7);
        stages.add(card);
        wonder.setStagesA(stages);

        stages = new ArrayList<>();
        card = new Card(Card.Type.STAGE, WonderStages.Stage_1);
        card.setCost(Card.Resource.WOOD, 2);
        card.setProducts(Card.Product.VP, 3);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_2);
        card.setCost(Card.Resource.STONE, 3);
        card.setProducts(Card.Product.VP, 5);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_3);
        card.setCost(Card.Resource.CLAY, 3);
        card.setProducts(Card.Product.VP, 5);
        stages.add(card);

        card = new Card(Card.Type.STAGE, WonderStages.Stage_4);
        card.setCost(Card.Resource.STONE, 4);
        card.setCost(Card.Resource.PAPER, 1);
        card.setProducts(Card.Product.VP, 7);
        stages.add(card);
        wonder.setStagesB(stages);
        wonders.add(wonder);

        return wonders;
    }
}
