package switzersearch.com.switzertrainsearch.data;

import android.provider.BaseColumns;

/**
 * Created by ${Pawan} on 1/12/2018.
 */

public class SQLTableCreater {
    SQLTableCreater(){}
    public static class TrainConnections implements BaseColumns {
        public static final String TABLE_NAME = "trainconnections";
        public static final String COLUMN_NAME_CONNECTIONS = "connections";
    }

}
