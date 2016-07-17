package net.dumtoad.srednow7.controller;

import android.os.Bundle;

import net.dumtoad.srednow7.cards.Card;
import net.dumtoad.srednow7.cards.Generate;
import net.dumtoad.srednow7.cards.Hand;
import net.dumtoad.srednow7.player.Player;

import java.util.ArrayList;

public class TableController {

    private MasterViewController mvc;
    private TurnController tc;
    private Hand discards;
    private int era;
    private int playerTurn = -1;

    public TableController(MasterViewController mvc) {
        this.mvc = mvc;
        this.tc = new TurnController(mvc);
        discards = new Hand();
        era = 0;
    }

    public TableController(MasterViewController mvc, Bundle savedInstanceState) {
        this.mvc = mvc;
        discards = new Hand(mvc.getDatabase().getAllCards(), savedInstanceState.getString("discards"));
        era = savedInstanceState.getInt("era");
        tc = new TurnController(mvc, savedInstanceState.getBundle("tc"));
        playerTurn = savedInstanceState.getInt("playerTurn");
    }

    public Bundle getInstanceState() {
        Bundle outstate = new Bundle();
        outstate.putString("discards", discards.getOrder());
        outstate.putInt("era", era);
        outstate.putBundle("tc", tc.getInstanceState());
        outstate.putInt("playerTurn", playerTurn);
        return outstate;
    }

    public void discard(Card card) {
        discards.add(card);
    }

    public Hand getDiscards() {
        return discards;
    }

    public TurnController getTurnController() {
        return tc;
    }

    private boolean play7thCard() {
        //Babylon side B stage 2 can play their 7th card
        for (int i = 0; i < mvc.getNumPlayers(); i++) {
            Player player = mvc.getPlayer(i);
            if (player.getWonder().getName() == Generate.Wonders.The_Hanging_Gardens_of_Babylon
                    && !player.getWonderSide()) {
                if (player.getPlayedCards().contains(Generate.WonderStages.Stage_2)
                        || player.getBufferCard() != null && player.getBufferCard().getName() == Generate.WonderStages.Stage_2) {
                    if (player.getHand().size() == 0) return false;
                    player.finishTurn();
                    player.flush();
                    tc.startTurn(i, false);
                    return true;
                }
            }
        }
        return false;
    }

    private void discardHands() {
        for (Player player : mvc.getPlayers()) {
            //If player could play his 7th card, there's nothing to discard
            if (player.getHand().size() == 1) discard(player.getHand().get(0));
        }
    }

    private void endEra() {
        for (Player player : mvc.getPlayers()) {
            player.getScore().resolveMilitary(era);
            player.refreshFreeBuild();
        }
    }

    public void startEra() {
        if (era == 3) { //We're done!
            mvc.endGame();
            return;
        }

        ArrayList<Hand> hands = mvc.getDatabase().dealHands(era);
        for (int i = 0; i < mvc.getNumPlayers(); i++) {
            mvc.getPlayer(i).setHand(hands.get(i));
        }
        nextPlayerStart();
    }

    public boolean endTurn() {
        for (Player player : mvc.getPlayers()) {
            player.finishTurn();
        }
        //Wait for all players to finish before doing special actions
        boolean specialAction = false;
        for (Player player : mvc.getPlayers()) {
            specialAction |= player.specialAction();
            player.flush();
        }
        return specialAction;
    }

    public void passTheHand() {
        if (getPassingDirection()) { //pass West
            Hand tmp = mvc.getPlayer(mvc.getNumPlayers() - 1).getHand();
            for (int i = mvc.getNumPlayers() - 1; i > 0; i--) {
                mvc.getPlayer(i).setHand(mvc.getPlayer(i - 1).getHand());
            }
            mvc.getPlayer(0).setHand(tmp);
        } else { //pass East
            Hand tmp = mvc.getPlayer(0).getHand();
            for (int i = 0; i < mvc.getNumPlayers() - 1; i++) {
                mvc.getPlayer(i).setHand(mvc.getPlayer(i + 1).getHand());
            }
            mvc.getPlayer(mvc.getNumPlayers() - 1).setHand(tmp);
        }
    }

    public boolean getPassingDirection() {
        return era != 1;
    }

    public int getEra() {
        return era;
    }

    public void nextPlayerStart() {
        playerTurn++;
        if (playerTurn < mvc.getNumPlayers() && mvc.getPlayer(playerTurn).getHand().size() > 1) {
            tc.startTurn(playerTurn, false);
        } else {
            playerTurn = -1;
            if (mvc.getPlayer(0).getHand().size() <= 1) { //end of era
                if (play7thCard()) return;
                discardHands();
                if (endTurn()) return;
                endEra();
                era++;
                startEra();
            } else {
                if (endTurn()) return;
                passTheHand();
                nextPlayerStart();
            }
        }
    }

    public Player getPlayerDirection(Player asker, boolean west) {
        int playerNum;
        for (playerNum = 0; playerNum < mvc.getNumPlayers(); playerNum++) {
            if (asker == mvc.getPlayer(playerNum)) break;
        }
        if (west) {
            playerNum++;
            if (playerNum == mvc.getNumPlayers()) playerNum = 0;
        } else {
            playerNum--;
            if (playerNum < 0) playerNum = mvc.getNumPlayers() - 1;
        }
        return mvc.getPlayer(playerNum);
    }

    public int getPlayerDirection(int start, boolean direction) {
        if (direction) {
            return (start + 1) % mvc.getNumPlayers();
        } else {
            start--;
            if (start < 0) {
                return mvc.getNumPlayers() - 1;
            }
        }
        return start;
    }

    public int getNumHumanPlayers() {
        int num = 0;
        for (Player player : mvc.getPlayers())
            if (!player.isAI()) num++;
        return num;
    }

}
