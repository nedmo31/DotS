package admin;

import java.sql.Date;

public class TeamHistoryRow {
    public final int tid, price;
    public final Date date;

    public TeamHistoryRow(int tid, int price, Date date) {
        this.tid = tid; this.price = price; this.date = date;
    }
}
