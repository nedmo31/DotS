package backend;

public class NamedOwnership extends OwnershipsRow {
    
    public final String name;

    NamedOwnership(int uid, int tid, int count, String name) {
        super(uid, tid, count);
        this.name = name;
    }
    
}
