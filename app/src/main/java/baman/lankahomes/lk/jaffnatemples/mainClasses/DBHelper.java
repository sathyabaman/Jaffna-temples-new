package baman.lankahomes.lk.jaffnatemples.mainClasses;

/**
 * This class is for further enhancement. Now thw application will not use
 * internal database.
 *
 *
 * Created by administrator on 7/7/15.
 * Database helper class for SqlLiteDatabase
 * Copyright (C) 2015  Kanasalingam SathyaBaman
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    public static final String TAG = "DBHelper";

    // columns of the visits table
    public static final String TABLE_TEMPLE = "temples";
    public static final String COLUMN_TEMPLE_ID = "id";
    public static final String COLUMN_TEMPLE_TEMPLEID = "temple_id";
    public static final String COLUMN_TEMPLE_NAME = "name";
    public static final String COLUMN_TEMPLE_DESC = "description";
    public static final String COLUMN_TEMPLE_LAT = "latitude";
    public static final String COLUMN_TEMPLE_LNG = "longitude";
    public static final String COLUMN_TEMPLE_IMG ="image";


    private static final String DATABASE_NAME = "jaffnatemplesDB";
    private static final int DATABASE_VERSION = 1;


    // SQL statement of the visits table creation
    private static final String SQL_CREATE_TABLE_VISITS = "CREATE TABLE " + TABLE_TEMPLE + "("
            + COLUMN_TEMPLE_ID 				+ " INTEGER PRIMARY KEY, "
            + COLUMN_TEMPLE_TEMPLEID 	    + " INTEGER, "
            + COLUMN_TEMPLE_NAME 			+ " TEXT NOT NULL, "
            + COLUMN_TEMPLE_DESC 			+ " TEXT NOT NULL, "
            + COLUMN_TEMPLE_LAT 			+ " TEXT NOT NULL, "
            + COLUMN_TEMPLE_LNG 		    + " TEXT NOT NULL, "
            + COLUMN_TEMPLE_IMG 			+ " TEXT NOT NULL "
            +");";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(SQL_CREATE_TABLE_VISITS);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading the database from version " + oldVersion + " to " + newVersion);
        // clear all data
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEMPLE);
        // recreate the tables
        onCreate(db);
    }



}