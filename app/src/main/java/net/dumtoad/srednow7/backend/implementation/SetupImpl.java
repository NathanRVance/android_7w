package net.dumtoad.srednow7.backend.implementation;

import net.dumtoad.srednow7.backend.Setup;
import net.dumtoad.srednow7.backend.Wonder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

class SetupImpl implements Setup {

    private static final long serialVersionUID = -7585646360586715796L;
    private int playerID;
    private transient Wonder[] wonderSides;
    private int side = 0;
    private boolean finished = false;

    SetupImpl(int playerID, Wonder[] wonderSides) {
        this.playerID = playerID;
        this.wonderSides = wonderSides;
    }

    @Override
    public Wonder getWonder() {
        return wonderSides[side];
    }

    @Override
    public void setWonderSide(int side) {
        this.side = side;
    }

    @Override
    public void finish() {
        finished = true;
        GameImpl.INSTANCE.setWonder(playerID, getWonder());
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeObject(wonderSides[side].getEnum());
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        Enum name = (Enum) s.readObject();
        for (Wonder[] wonder : GameImpl.INSTANCE.getCardCreator().getWonders()) {
            if (wonder[0].getEnum() == name) {
                wonderSides = wonder;
                break;
            }
        }
    }

    /*@Override
    public Serializable getContents() {
        Serializable[] contents = new Serializable[4];
        contents[0] = wonderSides[side].getEnum();
        contents[1] = side;
        contents[2] = playerID;
        contents[3] = finished;
        return contents;
    }

    @Override
    public void restoreContents(Serializable contents) {
        Serializable[] in = (Serializable[]) contents;
        for (Wonder[] wonder : GameImpl.INSTANCE.getCardCreator().getWonders()) {
            if (wonder[0].getEnum() == in[0]) {
                wonderSides = wonder;
                break;
            }
        }
        side = (int) in[1];
        playerID = (int) in[2];
        finished = (boolean) in[3];
    }*/
}
