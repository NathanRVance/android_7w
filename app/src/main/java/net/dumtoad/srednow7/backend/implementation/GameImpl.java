package net.dumtoad.srednow7.backend.implementation;

import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.CardCreator;
import net.dumtoad.srednow7.backend.CardList;
import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.backend.Setup;
import net.dumtoad.srednow7.backend.Wonder;
import net.dumtoad.srednow7.backend.util.SaveUtil;
import net.dumtoad.srednow7.bus.Bus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public enum GameImpl implements Game {
    INSTANCE;

    private List<PlayerImpl> players = new ArrayList<>();
    private List<Setup> setups = new ArrayList<>();
    private List<CardList> hands = new ArrayList<>();
    private int era;
    private int round;
    private CardCreator cardCreator;
    private CardList discards = new CardListImpl();

    @Override
    public void initialize(CharSequence[] playerNames, boolean[] ais) {
        players = new ArrayList<>();
        for (int playerID = 0; playerID < playerNames.length; playerID++) {
            players.add(new PlayerImpl(playerNames[playerID], ais[playerID]));
        }

        cardCreator = new CardCreatorImpl(playerNames.length);
        setups = new ArrayList<>();
        List<Wonder[]> wonders = cardCreator.getWonders();
        Random r = new Random();
        for (int i = 0; i < playerNames.length; i++) {
            Wonder[] wonder = wonders.remove(r.nextInt(wonders.size()));
            setups.add(new SetupImpl(i, wonder));
        }

        era = 0;
        hands = cardCreator.dealHands(era, playerNames.length);

        for (int playerID = 0; playerID < playerNames.length; playerID++) {
            if (players.get(playerID).isAI()) {
                players.get(playerID).getAI().selectWonderSide(playerID);
            } else {
                Bus.bus.getUI().displayWonderSideSelect(playerID);
            }
        }
    }

    void setWonder(int playerID, Wonder wonder) {
        players.get(playerID).setWonder(wonder);

        //Once all the wonders are in, we're ready to start the game proper.
        if (allWondersSelected()) {
            startRound();
        }
    }

    private boolean allWondersSelected() {
        boolean allIn = true;
        for (Player player : players) {
            allIn &= player.getWonder() != null;
        }
        return allIn;
    }

    private void startRound() {
        for (PlayerImpl player : players) {
            if (player.isPlayDiscard() && !discards.isEmpty()) {
                doTurn(player, discards);
                return;
            }
        }

        if (round == 6) {
            round++;
            boolean someonePlayed = false;
            for (PlayerImpl player : players) {
                if (player.canPlay7thCard()) {
                    doTurn(player, hands.get(players.indexOf(player)));
                    someonePlayed = true;
                }
            }
            if (someonePlayed)
                return;
        }

        if (round >= 6) {
            round = 0;
            for (PlayerImpl player : players) {
                player.getScore().resolveMilitary(era);
                player.setPlayedFree(false);
            }
            era++;
            if (era == 3) {
                Bus.bus.getUI().displayEndOfGame();
            } else {
                for (CardList cardList : hands) {
                    discards.addAll(cardList);
                }
                hands = cardCreator.dealHands(era, players.size());
                startRound();
            }
            return;
        }

        round++;
        //Since there are many different ways a round can end, we'll start by passing the hands (this won't affect gameplay)
        if (getPassingDirection() == Direction.WEST) {
            //Players are sorted west to east
            CardList tmp = hands.remove(0);
            hands.add(tmp);
        } else {
            CardList tmp = hands.remove(hands.size() - 1);
            hands.add(0, tmp);
        }
        //System.out.printf("Doing turns for era %d, round %d\n", era, round);
        for (PlayerImpl player : players) {
            player.startTurn();
        }
        for (PlayerImpl player : players) {
            doTurn(player, hands.get(players.indexOf(player)));
        }
    }

    @Override
    public synchronized void finishedTurn() {
        saveGame();
        boolean done = true;
        for (PlayerImpl player : players) {
            done &= player.hasFinishedTurn();
        }
        if (done) {
            for (PlayerImpl player : players) {
                player.resolveBuild();
            }
            startRound();
        }
    }

    private void doTurn(PlayerImpl player, CardList hand) {
        int playerID = players.indexOf(player);
        player.setHand(hand);
        player.startTurn();
        if (player.isAI()) {
            Thread thread = new Thread(() -> {
                player.getAI().doTurn();
            });
            thread.start();
            //player.getAI().doTurn();
        } else {
            Bus.bus.getUI().displayTurn(playerID);
        }
    }

    @Override
    public Setup getSetup(int playerID) {
        return setups.get(playerID);
    }

    @Override
    public List<? extends Player> getPlayers() {
        return players;
    }

    @Override
    public PlayerImpl getPlayerDirection(Player player, Direction direction) {
        //This is perfectly valid since there's only one type of player bouncing around
        @SuppressWarnings("SuspiciousMethodCalls") int index = players.indexOf(player);
        index = (direction == Direction.WEST) ? index - 1 : index + 1;
        if (index == -1) index = players.size() - 1;
        if (index == players.size()) index = 0;
        return players.get(index);
    }

    @Override
    public Direction getPassingDirection() {
        return (era % 2 == 0) ? Direction.WEST : Direction.EAST;
    }

    @Override
    public int getEra() {
        return era;
    }

    @Override
    public void discard(Card card) {
        discards.add(card);
    }

    @Override
    public void startNewGame() {
        SaveUtil.deleteSave();
        Bus.bus.getUI().invalidateView();
        Bus.bus.getUI().displaySetup();
    }

    @Override
    public void saveGame() {
        SaveUtil.saveGame(getContents());
    }

    @Override
    public void restoreSave() {
        try {
            restoreContents(SaveUtil.loadGame());
        } catch (Exception e) {
            e.printStackTrace();
            startNewGame();
        }
        Bus.bus.getUI().invalidateView();
    }

    private void continueGame() {
        //System.out.println("#################### Continuing game");
        if (players.isEmpty()) {
            //System.out.println("#################### Setting up");
            Bus.bus.getUI().invalidateView();
            Bus.bus.getUI().displaySetup();
        } else if (!allWondersSelected()) {
            for (int playerID = 0; playerID < players.size(); playerID++) {
                if (players.get(playerID).getWonder() == null) {
                    //System.out.println("#################### Selecting wonder for player " + playerID);
                    if (players.get(playerID).isAI()) {
                        players.get(playerID).getAI().selectWonderSide(playerID);
                    } else {
                        Bus.bus.getUI().displayWonderSideSelect(playerID);
                    }
                }
            }
        } else {
            //System.out.println("#################### Starting turns");
            for (PlayerImpl player : players) {
                if (!player.hasFinishedTurn()) {
                    //System.out.println("#################### Doing turn");
                    CardList hand = (player.isPlayDiscard()) ? discards : player.getHand();
                    doTurn(player, hand);
                }
            }
        }
    }

    @Override
    public void deleteSave() {
        SaveUtil.deleteSave();
    }

    @Override
    public void reset() {
        players = new ArrayList<>();
        setups = new ArrayList<>();
        hands = new ArrayList<>();
        era = 0;
        round = 0;
        cardCreator = null;
        discards = new CardListImpl();
    }

    @Override
    public CardCreator getCardCreator() {
        return cardCreator;
    }

    public Serializable getContents() {
        Serializable[] contents = new Serializable[8];
        contents[0] = cardCreator;
        contents[1] = players.toArray(new PlayerImpl[players.size()]);
        contents[2] = setups.toArray(new Setup[players.size()]);
        contents[3] = hands.toArray(new CardList[hands.size()]);
        contents[4] = era;
        contents[5] = round;
        contents[6] = discards;
        return contents;
    }

    public void restoreContents(Serializable contents) throws Exception {
        Serializable[] in = (Serializable[]) contents;
        cardCreator = (CardCreator) in[0];
        players = new ArrayList<>(Arrays.asList((PlayerImpl[]) in[1]));
        setups = new ArrayList<>(Arrays.asList((Setup[]) in[2]));
        for (int i = 0; i < players.size(); i++) {
            if (setups.get(i).isFinished()) {
                players.get(i).setWonder(setups.get(i).getWonder());
            }
        }
        hands = new ArrayList<>(Arrays.asList((CardList[]) in[3]));
        for (int i = 0; i < hands.size(); i++) {
            players.get(i).setHand(hands.get(i));
        }
        era = (int) in[4];
        round = (int) in[5];
        discards = (CardList) in[6];
        continueGame();
    }
}
