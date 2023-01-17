package backend;


public class TeamRow {

    public final int tid, price, wins, losses, pointsfor, pointsagainst;

    public final String name;

    TeamRow(int tid, String name, int price, int wins, int losses, int pointsfor, int pointsagainst) {
        this.tid = tid; this.price = price; this.wins = wins;
        this.losses = losses; this.pointsfor = pointsfor; 
        this.pointsagainst = pointsagainst;
        this.name = name;
    }

}