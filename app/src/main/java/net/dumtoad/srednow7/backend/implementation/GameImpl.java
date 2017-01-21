package net.dumtoad.srednow7.backend.implementation;

import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.CardList;
import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.backend.Setup;
import net.dumtoad.srednow7.backend.Wonder;
import net.dumtoad.srednow7.bus.Bus;
import net.dumtoad.srednow7.bus.DisplayFactory;
import net.dumtoad.srednow7.bus.SaveUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameImpl implements Game {
    private static final long serialVersionUID = -1169145061029792425L;
    public static GameImpl INSTANCE;

    private List<PlayerImpl> players = new ArrayList<>();
    private List<Setup> setups = new ArrayList<>();
    private List<CardList> hands = new ArrayList<>();
    private int era;
    private int round;
    private CardList discards = new CardListImpl();

    public GameImpl() {
        INSTANCE = this;
    }

    @Override
    public void initialize() {
        String[] playerNames = SaveUtil.getPlayerNames();
        boolean[] ais = SaveUtil.getIsAI();
        Generate.initialize();
        players = new ArrayList<>();
        for (int playerID = 0; playerID < playerNames.length; playerID++) {
            players.add(new PlayerImpl(playerNames[playerID], ais[playerID]));
        }

        setups = new ArrayList<>();
        List<Wonder[]> wonders = Generate.getWonders();
        Random r = new Random();
        for (int i = 0; i < playerNames.length; i++) {
            Wonder[] wonder = wonders.remove(r.nextInt(wonders.size()));
            setups.add(new SetupImpl(i, wonder));
        }

        era = 0;
        hands = Generate.dealHands(era, playerNames.length);

        for (int playerID = 0; playerID < playerNames.length; playerID++) {
            if (players.get(playerID).isAI()) {
                players.get(playerID).getAI().selectWonderSide(playerID);
            } else {
                Bus.bus.getUI().display(Card.Resource.GOLD,
                        DisplayFactory.getDisplay(DisplayFactory.DisplayType.WonderSideSelect, playerID));
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
        if (playDiscard()) return;

        round++;

        if (round == Generate.getCardsPerPlayer()) {
            if (play7thCard()) return;
        }

        if (round >= Generate.getCardsPerPlayer()) {
            endEra();
        } else {
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
                player.startTurn();
            }
            for (PlayerImpl player : players) {
                doTurn(player, hands.get(players.indexOf(player)));
            }
        }
    }

    private boolean playDiscard() {
        if (round >= Generate.getCardsPerPlayer()) endEra();

        for (PlayerImpl player : players) {
            if (player.isPlayDiscard() && !discards.isEmpty()) {
                doTurn(player, discards);
                return true;
            }
        }
        return false;
    }

    private boolean play7thCard() {
        boolean someonePlayed = false;
        for (PlayerImpl player : players) {
            if (player.hasAttribute(Card.Attribute.PLAY_7TH_CARD)) {
                doTurn(player, hands.get(players.indexOf(player)));
                someonePlayed = true;
            }
        }
        return someonePlayed;
    }

    private void endEra() {
        System.out.println("Ending era");
        round = 0;
        for (PlayerImpl player : players) {
            player.getScore().resolveMilitary(era);
            player.setPlayedFree(false);
        }
        era++;
        if (era == 3) {
            Bus.bus.getUI().display(Card.Resource.GOLD,
                    DisplayFactory.getDisplay(DisplayFactory.DisplayType.EndOfGame, 0));
            Bus.bus.deleteSave();
        } else {
            for (CardList cardList : hands) {
                discards.addAll(cardList);
            }
            hands = Generate.dealHands(era, players.size());
            startRound();
        }
    }

    synchronized void finishedTurn(PlayerImpl playerFinished) {
        playerFinished.finishTurn();
        //Bus.bus.saveToMemory(); //Move this somewhere better
        boolean done = true;
        for (PlayerImpl player : players) {
            done &= player.hasFinishedTurn();
        }
        if (done) {
            for (PlayerImpl player : players) {
                player.resolveBuild();
            }
            resolveActions();
            Bus.bus.saveToMemory();
            startRound();
        }
    }

    private void resolveActions() {
        List<PlayerImpl> actors = new ArrayList<>();
        for (PlayerImpl player : players) {
            if (player.getActionPrecidence() >= 0) {
                actors.add(player);
            }
        }
        Collections.sort(actors, (player, t1) -> {
            if (player.getActionPrecidence() > t1.getActionPrecidence())
                return 1;
            if (player.getActionPrecidence() == t1.getActionPrecidence())
                return 0;
            return -1;
        });
        for(PlayerImpl player : actors) {
            player.resolveAction();
        }
    }

    private void doTurn(PlayerImpl player, CardList hand) {
        player.setHand(hand);
        player.startTurn();
        if (player.isAI()) {
            Thread thread = new Thread(() -> {
                player.getAI().doTurn();
            });
            thread.start();
        } else {
            Bus.bus.getUI().display(Card.Resource.GOLD,
                    DisplayFactory.getDisplay(DisplayFactory.DisplayType.Turn, players.indexOf(player)));
        }
    }

    @Override
    public Setup getSetup(int playerID) {
        return setups.get(playerID);
    }

    @Override
    public List<PlayerImpl> getPlayers() {
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
    public int getRound() {
        return round;
    }

    @Override
    public void discard(Card card) {
        discards.add(card);
    }

    @Override
    public void startNewGame() {
        Bus.bus.getUI().reset();
        Bus.bus.getUI().initDisplayQueue(Card.Resource.GOLD,
                DisplayFactory.getDisplay(DisplayFactory.DisplayType.Blank, 0));
        Bus.bus.getUI().display(Card.Resource.GOLD,
                DisplayFactory.getDisplay(DisplayFactory.DisplayType.Setup, 0));
    }

    @Override
    public void reset() {
        players = new ArrayList<>();
        setups = new ArrayList<>();
        hands = new ArrayList<>();
        era = 0;
        round = 0;
        discards = new CardListImpl();
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        Generate.initialize(); //Must be done first so that cards are regenerated
        s.defaultReadObject();
        INSTANCE = this;
        for (int i = 0; i < players.size(); i++) {
            if (setups.get(i).isFinished()) {
                players.get(i).setWonder(setups.get(i).getWonder());
            }
        }
        for (int i = 0; i < hands.size(); i++) {
            players.get(i).setHand(hands.get(i));
        }
    }
}
