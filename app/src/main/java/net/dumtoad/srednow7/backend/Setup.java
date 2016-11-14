package net.dumtoad.srednow7.backend;

import java.io.Serializable;

public interface Setup extends Serializable {

    Wonder getWonder();

    void setWonderSide(int side);

    void finish();

    boolean isFinished();

}
