package net.dumtoad.srednow7.backend;

public interface Setup extends Savable {

    Wonder getWonder();

    void setWonderSide(int side);

    void finish();

}
