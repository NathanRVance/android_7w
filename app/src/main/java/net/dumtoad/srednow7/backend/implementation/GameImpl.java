package net.dumtoad.srednow7.backend.implementation;

import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.CardCreator;
import net.dumtoad.srednow7.backend.CardList;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.backend.Savable;
import net.dumtoad.srednow7.backend.Setup;
import net.dumtoad.srednow7.backend.Wonder;
import net.dumtoad.srednow7.backend.util.SaveUtil;
import net.dumtoad.srednow7.bus.Bus;

import java.io.Serializable;
import java.util.ArrayList;
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
        saveGame();
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
        for (PlayerImpl player : players) {
            doTurn(player, hands.get(players.indexOf(player)));
        }
    }

    @Override
    public synchronized void finishedTurn() {
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
    public Player getPlayerDirection(Player player, Direction direction) {
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
        System.out.println("#################### Continuing game");
        if (players.isEmpty()) {
            System.out.println("#################### Setting up");
            Bus.bus.getUI().invalidateView();
            Bus.bus.getUI().displaySetup();
        }
        else if (allWondersSelected()) {
            System.out.println("#################### All wonders are selected");
            for(PlayerImpl player : players) {
                if(!player.hasFinishedTurn()) {
                    System.out.println("#################### Doing turn");
                    CardList hand = (player.isPlayDiscard())? discards : player.getHand();
                    doTurn(player, hand);
                }
            }
        } else {
            for (int playerID = 0; playerID < players.size(); playerID++) {
                if(players.get(playerID).getWonder() == null) {
                    System.out.println("#################### Selecting wonder for player " + playerID);
                    if (players.get(playerID).isAI()) {
                        players.get(playerID).getAI().selectWonderSide(playerID);
                    } else {
                        Bus.bus.getUI().displayWonderSideSelect(playerID);
                    }
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

    @Override
    public Serializable getContents() {
        Serializable[] contents = new Serializable[8];
        if (cardCreator != null) contents[0] = cardCreator.getContents();
        contents[1] = saveList(players);
        contents[2] = saveList(setups);
        contents[3] = saveList(hands);
        contents[4] = era;
        contents[5] = round;
        contents[6] = discards.getContents();
        return contents;
    }

    private Serializable[] saveList(List<? extends Savable> savables) {
        Serializable[] ret = new Serializable[savables.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = savables.get(i).getContents();
        }
        return ret;
    }

    @Override
    public void restoreContents(Serializable contents) throws Exception {
        Serializable[] in = (Serializable[]) contents;
        cardCreator = new CardCreatorImpl(players.size());
        cardCreator.restoreContents(in[0]);
        //noinspection unchecked
        players = (List<PlayerImpl>) restoreArray((Serializable[]) in[1], PlayerImpl.class);
        //noinspection unchecked
        setups = (List<Setup>) restoreArray((Serializable[]) in[2], SetupImpl.class);
        for(int i = 0; i < players.size(); i++) {
            if(setups.get(i).isFinished()) {
                players.get(i).setWonder(setups.get(i).getWonder());
            }
        }
        //noinspection unchecked
        hands = (List<CardList>) restoreArray((Serializable[]) in[3], CardListImpl.class);
        for(int i = 0; i < hands.size(); i++) {
            players.get(i).setHand(hands.get(i));
        }
        era = (int) in[4];
        round = (int) in[5];
        discards.restoreContents(in[6]);
        continueGame();
    }

    private List<? extends Savable> restoreArray(Serializable[] contents, Class<? extends Savable> type) throws Exception {
        List<Savable> ret = new ArrayList<>();
        for (Serializable content : contents) {
            Savable s = type.newInstance();
            s.restoreContents(content);
            ret.add(s);
        }
        return ret;
    }
}
