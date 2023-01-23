package backend;

public class TransactionsRow {
    public final int uid, tid, change, price;

    public TransactionsRow(int uid, int tid, int change, int price) {
        this.tid = tid; this.price = price; this.uid = uid; this.change = change;
    }
}
