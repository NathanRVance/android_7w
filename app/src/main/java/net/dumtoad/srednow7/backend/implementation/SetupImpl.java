package net.dumtoad.srednow7.backend.implementation;

import net.dumtoad.srednow7.backend.Setup;
import net.dumtoad.srednow7.backend.Wonder;

import java.io.Serializable;

class SetupImpl implements Setup {

    private int playerID;
    private Wonder[] wonderSides;
    private int side = 0;

    SetupImpl(int playerID, Wonder[] wonderSides) {
        this.playerID = playerID;
        this.wonderSides = wonderSides;
    }

    SetupImpl() {}

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
        BackendImpl.INSTANCE.setWonder(playerID, getWonder());
    }

    @Override
    public Serializable getContents() {
        Serializable[] contents = new Serializable[2];
        contents[0] = wonderSides[side].getEnum();
        contents[1] = side;
        return contents;
    }

    @Override
    public void restoreContents(Serializable contents) {
        Serializable[] in = (Serializable[]) contents;
        for (Wonder[] wonder : BackendImpl.INSTANCE.getCardCreator().getWonders()) {
            if (wonder[0].getEnum() == in[0]) {
                wonderSides = wonder;
                break;
            }
        }
        side = (int) in[1];
    }
}
