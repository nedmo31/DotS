package admin;

public class TeamRow {

    public final int tid, price, wins, losses, pointsfor, pointsagainst, lastprice;

    public final String name;

    TeamRow(int tid, String name, int price, int wins, int losses, int pointsfor, int pointsagainst, int lastprice) {
        this.tid = tid; this.price = price; this.wins = wins;
        this.losses = losses; this.pointsfor = pointsfor; 
        this.pointsagainst = pointsagainst;
        this.name = name; this.lastprice = lastprice;
    }

}