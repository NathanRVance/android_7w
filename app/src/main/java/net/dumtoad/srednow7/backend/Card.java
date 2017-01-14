package net.dumtoad.srednow7.backend;

import net.dumtoad.srednow7.backend.implementation.action.Action;
import net.dumtoad.srednow7.backend.implementation.variableResource.ResourceStrategy;

public interface Card {

    String getName();

    CharSequence getMessage();

    Type getType();

    boolean providesTrade(Game.Direction direction, TradeType type);

    boolean makesTradeCheaper(Game.Direction direction);

    ResQuant getProducts(Player player);

    ResourceStrategy.ResourceStyle getProductionStyle();

    ResQuant getCosts(Player player);

    boolean isSpecialIn(Resource resource);

    CardList makeThisFree();

    CardList makesFree();

    boolean providesAttribute(Attribute attribute);

    void performActions(Player player);

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

    interface Builder {
        Builder setMessage(String message);

        Builder setCosts(ResourceStrategy costs);

        Builder setProducts(ResourceStrategy products);

        Builder setTradeType(TradeType tradeType);

        Builder addTradeDirection(Game.Direction direction);

        Builder setMakesFree(String card);

        Builder addAttribute(Attribute attribute);

        Builder addAction(Action action);

        Card build();
    }

}
