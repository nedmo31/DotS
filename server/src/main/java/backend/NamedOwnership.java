package backend;

public class NamedOwnership extends OwnershipsRow {
    
    public final String name;
    public final int price;

    NamedOwnership(int uid, int tid, int count, String name, int price) {
        super(uid, tid, count);
        this.name = name;
        this.price = price;
    }
    
}
