package net.dumtoad.srednow7.backend;

import net.dumtoad.srednow7.backend.implementation.specialValue.SpecialValue;
import net.dumtoad.srednow7.backend.implementation.variableResource.VariableResource;

public interface Card {

    Enum getEnum();

    CharSequence getMessage();

    Type getType();

    boolean providesTrade(Game.Direction direction, TradeType type);

    ResQuant getProducts(Player player);

    ResQuant getCosts(Player player);

    int getSpecialVps(Player player);

    boolean isSpecialVps();

    int getSpecialGold(Player player);

    boolean isSpecialGold();

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
        GUILD
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
        resource,
        industry
    }

    enum Attribute {
        Play7thCard,
        Play1Free,
        FreeBuild
    }

    interface Builder {
        Builder setMessage(String message);

        Builder setCosts(VariableResource costs);

        Builder setProducts(VariableResource products);

        Builder setSpecialGold(SpecialValue specialGold);

        Builder setSpecialVps(SpecialValue specialVps);

        Builder setTradeType(TradeType tradeType);

        Builder addTradeDirection(Game.Direction direction);

        Builder setMakesFree(Enum card);

        Builder setMakesThisFree(Enum card);

        Builder addAttribute(Attribute attribute);

        Card build();
    }

}
