package backend;

import java.util.ArrayList;

public class TeamRow {

    public final int tid, price, wins, losses, pointsfor, pointsagainst;

    public final String name;

    public final ArrayList<TeamHistoryRow> history;

    public final static int BASE_VALUE = 50;
    public final static double RECENCY_WEIGHT = .75;

    TeamRow(int tid, String name, int price, int wins, int losses, int pointsfor, int pointsagainst, ArrayList<TeamHistoryRow> history) {
        this.tid = tid; this.price = price; this.wins = wins;
        this.losses = losses; this.pointsfor = pointsfor; 
        this.pointsagainst = pointsagainst;
        this.name = name; this.history = history;
    }

    /**
     * Essentially, we use total values to determine the new value,
     * while giving more weight to the most recent result. BASE_VALUE
     * will be multiplied by some constant multiple of the ratios
     * (wins / losses) and (pointsfor / pointsagainst)
     * 
     * @param win did the team just win?
     * @param pFor points scored this game
     * @param pAgainst points the other team scored this game
     * @return the new price
     */
    public int getNewPrice(boolean win, double pFor, int pAgainst) {
        double heavyWeight = (( ((double)wins + 1) / (losses + 1)) * ((double)(pointsfor+1) / (pointsagainst + 1))) * (RECENCY_WEIGHT * BASE_VALUE);
        double lightWeight;
        if (win)
            lightWeight = (((double)pFor + 15) / (pAgainst+1)) * ((1-RECENCY_WEIGHT) * BASE_VALUE);
        else 
            lightWeight = (((double)pFor + 1) / (pAgainst+1)) * ((1-RECENCY_WEIGHT) * BASE_VALUE);
        return (int) (heavyWeight + lightWeight);
    }   

}