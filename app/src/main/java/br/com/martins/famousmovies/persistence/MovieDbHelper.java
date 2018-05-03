package br.com.martins.famousmovies.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import br.com.martins.famousmovies.persistence.MovieContract.MovieEntry;
/**
 * Created by Andre Martins dos Santos on 02/05/2018.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "movie_database";
    private static final int DB_VERSION = 1;

    public MovieDbHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_WEATHER_TABLE =
            "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                    MovieEntry._ID + " INTEGER PRIMARY KEY, " +
                    MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                    MovieEntry.COLUMN_BACKDROP_PATH + " TEXT," +
                    MovieEntry.COLUMN_POSTER_PATH + " TEXT, " +
                    MovieEntry.COLUMN_OVERVIEW + " TEXT, " +
                    MovieEntry.COLUMN_VOTE_AVERAGE + " REAL, " +
                    MovieEntry.COLUMN_RELEASE_DATE + " DATETIME ) ";

        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
