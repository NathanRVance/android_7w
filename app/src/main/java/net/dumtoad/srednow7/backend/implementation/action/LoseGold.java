package net.dumtoad.srednow7.backend.implementation.action;

import net.dumtoad.srednow7.MainActivity;
import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.backend.implementation.GameImpl;
import net.dumtoad.srednow7.backend.implementation.PlayerImpl;
import net.dumtoad.srednow7.backend.implementation.specialValue.SpecialValue;
import net.dumtoad.srednow7.bus.Bus;
import net.dumtoad.srednow7.bus.DisplayFactory;

public class LoseGold implements Action, Card.ResponseCallback {

    private int amount;
    private SpecialValue specialValue = null;
    private String cardName;

    public LoseGold(int amount, String cardName) {
        this.amount = amount;
        this.cardName = cardName;
    }

    public LoseGold(SpecialValue specialValue, String cardName) {
        this.specialValue = specialValue;
        this.cardName = cardName;
    }

    private int getAmountToLose(Player player) {
        return (specialValue != null) ? specialValue.getSpecialValue(GameImpl.INSTANCE, player) : amount;
    }

    @Override
    public void act(Player player) {
        for (PlayerImpl p : GameImpl.INSTANCE.getPlayers()) {
            if (p == player) continue;
            int amountToLose = getAmountToLose(p);
            int amountCanLose = (amountToLose < p.getGold()) ? amountToLose : p.getGold();
            int amountLost = 0;
            if (p.isAI()) {
                amountLost = p.getAI().loseGold(amountCanLose);
            } else {
                String[] responses = new String[amountCanLose + 1];
                for (int i = 0; i < responses.length; i++) {
                    responses[i] = String.valueOf(i);
                }

                Bus.bus.getUI().initDisplayQueue(Card.Resource.VP, DisplayFactory.getNothingDisplay());
                Bus.bus.getUI().display(Card.Resource.VP, DisplayFactory.getDisplay(
                        GameImpl.INSTANCE.getPlayers().indexOf(p),
                        MainActivity.getMainActivity().getResources().getString(R.string.lose_gold_title),
                        MainActivity.getMainActivity().getResources().getString(R.string.lose_gold_message, p.getGold(), amountToLose),
                        responses,
                        cardName));
            }

            p.addGoldBuffer(amountLost * -1);
            p.incurDebt(amountToLose - amountLost);
        }
    }

    @Override
    public int getPrecidence() {
        return 10;
    }

    @Override
    public void callback(int playerID, String response) {
        Player player = GameImpl.INSTANCE.getPlayers().get(playerID);
        int amountLost = Integer.parseInt(response);
        player.addGoldBuffer(amountLost * -1);
        player.incurDebt(getAmountToLose(player) - amountLost);
    }
}
