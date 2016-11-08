package net.dumtoad.srednow7.ui;


import net.dumtoad.srednow7.backend.ResQuant;

public interface TradeUI {

    void update(int goldAvailable, ResQuant resourcesForSale, ResQuant resourcesBought, ResQuant prices);

}
