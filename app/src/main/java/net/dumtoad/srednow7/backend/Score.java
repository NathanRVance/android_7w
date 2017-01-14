package net.dumtoad.srednow7.backend;

public interface Score {
    void resolveMilitary(int era);

    int getMilitaryVps();

    int[] getMilitaryVictories();

    int getMilitaryLosses();

    int getGoldVps();

    int getWonderVps();

    int getStructureVps();

    int getCommercialVps();

    int getGuildVps();

    int getScienceVps();

    int getTotalVPs();
}
