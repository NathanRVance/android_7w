package net.dumtoad.srednow7.controller;

import android.os.Bundle;

import net.dumtoad.srednow7.cards.Card;
import net.dumtoad.srednow7.cards.Generate;
import net.dumtoad.srednow7.cards.Hand;
import net.dumtoad.srednow7.player.Player;

import java.util.ArrayList;

public class TableController {

    private MasterViewController mvc;
    private Hand discards;
    private int era;
    private int playerTurn = -1;
    private Thread[] threads = new Thread[0]; //Initialize to avoid null pointer
    private Phase phase = Phase.main;

    TableController(MasterViewController mvc) {
        this.mvc = mvc;
        discards = new Hand();
        era = 0;
    }

    TableController(MasterViewController mvc, Bundle savedInstanceState) {
        this.mvc = mvc;
        discards = new Hand(mvc.getDatabase().getAllCards(), savedInstanceState.getString("discards"));
        era = savedInstanceState.getInt("era");
        playerTurn = savedInstanceState.getInt("playerTurn");
        phase = Phase.valueOf(savedInstanceState.getString("phase"));
    }

    Bundle getInstanceState() {
        Bundle outstate = new Bundle();
        outstate.putString("discards", discards.getOrder());
        outstate.putInt("era", era);
        outstate.putInt("playerTurn", playerTurn);
        outstate.putString("phase", phase.toString());
        return outstate;
    }

    public void discard(Card card) {
        discards.add(card);
    }

    public Hand getDiscards() {
        return discards;
    }

    void startEra() {
        if (era == 3) { //We're done!
            mvc.endGame();
            return;
        }

        ArrayList<Hand> hands = mvc.getDatabase().dealHands(era);
        for (int i = 0; i < mvc.getNumPlayers(); i++) {
            mvc.getPlayer(i).setHand(hands.get(i));
        }
        doTurn();
    }

    private void passTheHand() {
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

    private Player[] getPlayers(boolean isAI) {
        Player[] ret = new Player[(isAI) ? mvc.getNumPlayers() - getNumHumanPlayers() : getNumHumanPlayers()];
        int retp = 0;
        for (Player player : mvc.getPlayers()) {
            if (isAI && player.isAI() || !isAI && !player.isAI()) {
                ret[retp++] = player;
            }
        }
        return ret;
    }

    private void doTurn() {
        phase = Phase.main;
        playerTurn = -1;
        Player[] ais = getPlayers(true);
        doAiTurns(ais);
        doNextHumanTurn();
    }

    private void doAiTurns(final Player[] ais) {
        threads = new Thread[ais.length];
        for (int i = 0; i < ais.length; i++) {
            final Player ai = ais[i];
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    ai.startTurn(false);
                }
            });
            threads[i].start();
        }
    }

    private void doNextHumanTurn() {
        Player[] humans = getPlayers(false);
        while (++playerTurn < mvc.getNumPlayers()) {
            for (Player player : humans) {
                if (mvc.getPlayerNum(player) == playerTurn) {
                    player.startTurn(false);
                    return;
                }
            }
        }
        //All out of humans to do, so time to end the turn.
        endTurn();
    }

    public void iFinishedMyTurn(Player player) {
        /**
         * This could have been called by several things.
         * 1) An AI player finishing a turn
         * 2) A human player finishing a turn
         * 3) An AI or human player playing a 7th card
         * 4) An AI or human player completing a special action
         */
        if (phase == Phase.main) {
            if (!player.isAI()) {
                doNextHumanTurn();
            }
        } else {
            player.finishTurn();
            endTurn();
        }
    }

    private void endTurn() {
        //Join all ai threads
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //Perform trades, build cards, etc.
        if (phase == Phase.main) {
            for (Player player : mvc.getPlayers()) {
                player.finishTurn();
            }
        }

        if (isEndOfEra()) {
            phase = Phase.play7th;
            if (play7thCard()) return;
            discardHands();
        }

        //Wait for all players to finish before doing special actions
        phase = Phase.special;
        boolean specialAction = false;
        for (Player player : mvc.getPlayers()) {
            specialAction |= player.specialAction();
            player.flush(); //Sets the turn buffer to null
        }
        if (specialAction) return;
        if (isEndOfEra()) {
            endEra();
            era++;
            startEra();
        } else {
            passTheHand();
            doTurn();
        }
    }

    private boolean isEndOfEra() {
        return mvc.getPlayer(0).getHand().size() <= 1;
    }

    private boolean play7thCard() {
        //Babylon side B stage 2 can play their 7th card
        for (Player player : mvc.getPlayers()) {
            if (player.getWonder().getName() == Generate.Wonders.The_Hanging_Gardens_of_Babylon
                    && !player.getWonderSide()) {
                if (player.getPlayedCards().contains(Generate.WonderStages.Stage_2)
                        || player.getBufferCard() != null && player.getBufferCard().getName() == Generate.WonderStages.Stage_2) {
                    if (player.getHand().size() == 0) return false;
                    player.flush();
                    player.startTurn(false);
                    return true;
                }
            }
        }
        return false;
    }

    private void discardHands() {
        for (Player player : mvc.getPlayers()) {
            //If player could play his 7th card, there's nothing to discard
            if (player.getHand().size() == 1) discard(player.getHand().remove(0));
        }
    }

    private void endEra() {
        for (Player player : mvc.getPlayers()) {
            player.getScore().resolveMilitary(era);
            player.refreshFreeBuild();
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

    int getCurrentPlayerNum() {
        return playerTurn;
    }

    int getNumHumanPlayers() {
        int num = 0;
        for (Player player : mvc.getPlayers())
            if (!player.isAI()) num++;
        return num;
    }

    private enum Phase {
        main,
        play7th,
        special
    }

}
