package com.timotuominen.app.data;

/**
 * Created by ttuo on 12/25/13.
 */
public class DataLayer {
    private static DataLayer instance = null;

    private DataLayer () {

    }

    public static DataLayer getInstance() {
        if (instance == null) {
            instance = new DataLayer();
        }
        return instance;
    }
}
