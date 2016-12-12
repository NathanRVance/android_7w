package net.dumtoad.srednow7.backend;

import net.dumtoad.srednow7.backend.implementation.variableResource.ResourceStrategy;

public interface Card {

    Enum getEnum();

    CharSequence getMessage();

    Type getType();

    boolean providesTrade(Game.Direction direction, TradeType type);

    ResQuant getProducts(Player player);

    ResourceStrategy.ResourceStyle getProductionStyle();

    ResQuant getCosts(Player player);

    boolean isSpecialIn(Resource resource);

    CardList makeThisFree();

    CardList makesFree();

    boolean providesAttribute(Attribute attribute);

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
        INDUSTRY
    }

    enum Attribute {
        PLAY_7TH_CARD,
        PLAY_1_FREE,
        FREE_BUILD
    }

    interface Builder {
        Builder setMessage(String message);

        Builder setCosts(ResourceStrategy costs);

        Builder setProducts(ResourceStrategy products);

        Builder setTradeType(TradeType tradeType);

        Builder addTradeDirection(Game.Direction direction);

        Builder setMakesFree(Enum card);

        Builder addAttribute(Attribute attribute);

        Card build();
    }

}
