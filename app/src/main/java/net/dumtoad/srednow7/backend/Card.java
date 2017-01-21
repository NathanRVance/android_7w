package net.dumtoad.srednow7.backend;

import net.dumtoad.srednow7.backend.implementation.variableResource.ResourceStrategy;

public interface Card {

    String getName();

    CharSequence getMessage();

    Type getType();

    int getEra();

    boolean providesTrade(Game.Direction direction, TradeType type);

    boolean makesTradeCheaper(Game.Direction direction);

    ResQuant getProducts(Player player);

    ResQuant getProductsNotSpecial();

    ResourceStrategy.ResourceStyle getProductionStyle();

    ResQuant getCosts(Player player);

    boolean isSpecialIn(Resource resource);

    CardList makeThisFree();

    CardList makesFree();

    boolean providesAttribute(Attribute attribute);

    void performAction(Player player);

    //Actions will be performed in order of precidence (low to high), with ties ordered arbitrarily.
    //0 is minimum, max is Integer.MAX_VALUE.
    int getActionPrecidence();

    ResponseCallback getCallback();

    enum Type {
        STAGE,
        RESOURCE,
        INDUSTRY,
        STRUCTURE,
        COMMERCIAL,
        MILITARY,
        SCIENCE,
        GUILD,
        BLACK
    }

    enum Resource {
        GOLD,
        WOOD,
        STONE,
        CLAY,
        ORE,
        CLOTH,
        GLASS,
        PAPER,
        COMPASS,
        GEAR,
        TABLET,
        VP,
        SHIELD
    }

    enum TradeType {
        RESOURCE,
        INDUSTRY,
        ONECHEAPER
    }

    enum Attribute {
        PLAY_7TH_CARD,
        PLAY_1_FREE,
        FREE_BUILD,
        FREE_WONDER,
        DOVE
    }

    interface ResponseCallback {
        void callback(int playerID, String response);
    }

}
