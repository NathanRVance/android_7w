package net.dumtoad.android_7w.controller;

import android.os.Bundle;

import net.dumtoad.android_7w.cards.Card;
import net.dumtoad.android_7w.cards.CardCollection;
import net.dumtoad.android_7w.cards.Hand;
import net.dumtoad.android_7w.player.Player;

import java.util.ArrayList;

public class TableController {

    private MasterViewController mvc;
    private TurnController tc;
    private CardCollection discards;
    private int era;
    private int playerTurn = 0;

    public TableController(MasterViewController mvc) {
        this.mvc = mvc;
        this.tc = new TurnController(mvc);
        discards = new CardCollection();
        era = 0;
    }

    public TableController(MasterViewController mvc, Bundle savedInstanceState) {
        this.mvc = mvc;
        discards = new CardCollection(mvc.getDatabase().getAllCards(), savedInstanceState.getString("discards"));
        era = savedInstanceState.getInt("era");
        tc = new TurnController(mvc, savedInstanceState.getBundle("tc"));
    }

    public Bundle getInstanceState() {
        Bundle outstate = new Bundle();
        outstate.putString("discards", discards.getOrder());
        outstate.putInt("era", era);
        outstate.putBundle("tc", tc.getInstanceState());
        return outstate;
    }

    public void discard(Card card) {
        discards.add(card);
    }

    public TurnController getTurnController() {
        return tc;
    }

    private void discardHands() {
        for(int i = 0; i < mvc.getNumPlayers(); i++) {
            discard(mvc.getPlayer(i).getHand().get(0));
        }
    }

    private void endEra() {
        for (int i = 0; i < mvc.getNumPlayers(); i++) {
            mvc.getPlayer(i).getScore().resolveMilitary(era);
        }
    }

    public void startEra() {
        ArrayList<Hand> hands = mvc.getDatabase().dealHands(era);
        for (int i = 0; i < mvc.getNumPlayers(); i++) {
            mvc.getPlayer(i).setHand(hands.get(i));
        }
        nextPlayerStart();
    }

    public void endTurn() {
        for (int i = 0; i < mvc.getNumPlayers(); i++) {
            mvc.getPlayer(i).finishTurn();
        }
        //Wait for all players to finish before doing special actions
        for (int i = 0; i < mvc.getNumPlayers(); i++) {
            mvc.getPlayer(i).specialAction();
        }
    }

    public void passTheHand() {
        if (era == 1) { //pass East
            Hand tmp = mvc.getPlayer(0).getHand();
            for (int i = 1; i < mvc.getNumPlayers() - 1; i++) {
                mvc.getPlayer(i).setHand(mvc.getPlayer(i + 1).getHand());
            }
            mvc.getPlayer(mvc.getNumPlayers() - 1).setHand(tmp);
        } else { //pass West
            Hand tmp = mvc.getPlayer(mvc.getNumPlayers() - 1).getHand();
            for (int i = mvc.getNumPlayers() - 1; i > 0; i--) {
                mvc.getPlayer(i).setHand(mvc.getPlayer(i - 1).getHand());
            }
            mvc.getPlayer(0).setHand(tmp);
        }
    }

    public void nextPlayerStart() {
        if (playerTurn < mvc.getNumPlayers()) {
            tc.startTurn(playerTurn++);
        } else {
            playerTurn = 0;
            if (mvc.getPlayer(0).getHand().size() == 1) { //end of era
                discardHands();
                endTurn();
                endEra();
                era++;
                startEra();
            } else {
                endTurn();
                passTheHand();
                nextPlayerStart();
            }
        }
    }

    public Player getPlayerDirection(boolean west, Player asker) {
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

    public int getNumHumanPlayers() {
        int num = 0;
        for (int i = 0; i < mvc.getNumPlayers(); i++)
            if (!mvc.getPlayer(i).isAI()) num++;
        return num;
    }

}
