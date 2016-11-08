package net.dumtoad.srednow7.backend;

public interface Score extends Savable {
    void resolveMilitary(int era);

    int getMilitaryVps();

    int getMilitaryLosses();

    int getGoldVps();

    int getWonderVps();

    int getStructureVps();

    int getCommercialVps();

    int getGuildVps();

    int getScienceVps();

    int getTotalVPs();
}
