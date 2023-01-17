package admin;

/**
 * The LikeRow to mimic a row from the Mlikes table
 */
public class OwnershipsTable {

    public final int uid, tid, count;

    OwnershipsTable(int uid, int tid, int count) {
        this.uid = uid;
        this.tid = tid;
        this.count = count;
    }
    
}
