package admin;

/**
 * The LikeRow to mimic a row from the Mlikes table
 */
public class OwnershipsRow {

    public final int uid, tid, count;

    OwnershipsRow(int uid, int tid, int count) {
        this.uid = uid;
        this.tid = tid;
        this.count = count;
    }
    
}
