package admin;

public class TeamTable {

    public final int tid, price, wins, losses, pointsfor, pointsagainst;

    public final String name;

    TeamTable(int tid, int price, int wins, int losses, int pointsfor, int pointsagainst, String name) {
        this.tid = tid; this.price = price; this.wins = wins;
        this.losses = losses; this.pointsfor = pointsfor; 
        this.pointsagainst = pointsagainst;
        this.name = name;
    }

}