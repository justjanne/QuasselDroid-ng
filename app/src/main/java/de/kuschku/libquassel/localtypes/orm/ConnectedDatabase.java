package de.kuschku.libquassel.localtypes.orm;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = ConnectedDatabase.NAME, version = ConnectedDatabase.VERSION)
public class ConnectedDatabase {
    public static final String NAME = "ConnectedDatabase";
    public static final int VERSION = 2;
}