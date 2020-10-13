package com.example.along_the_road;

import android.provider.BaseColumns;


public final class Databases {

    /* Inner class that defines the table contents */
    public static final class UserDB implements BaseColumns {
        public static final String TABLE_NAME = "User";
        public static final String UID = "uid";
        public static final String EMAIL = "email";
        public static final String NAME = "name";
        public static final String CITY = "city";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + UserDB.TABLE_NAME + " (" +
                        UserDB.UID + " TEXT NOT NULL PRIMARY KEY, " +
                        UserDB.EMAIL + " TEXT NOT NULL, " +
                        UserDB.NAME + " TEXT NOT NULL," +
                        UserDB.CITY + " TEXT)";

        public static final String SQL_DROP_TABLE =
                "DROP TABLE " + UserDB.TABLE_NAME + ";";
    }

}