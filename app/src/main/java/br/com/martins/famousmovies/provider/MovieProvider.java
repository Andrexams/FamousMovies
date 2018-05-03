package br.com.martins.famousmovies.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import br.com.martins.famousmovies.persistence.MovieContract;
import br.com.martins.famousmovies.persistence.MovieDbHelper;
import br.com.martins.famousmovies.persistence.MovieContract.MovieEntry;

/**
 * Created by Andre Martins dos Santos on 02/05/2018.
 */
public class MovieProvider extends ContentProvider {

    private static final int CODE_MOVIE = 100;

    private static UriMatcher mUriMatcher = createUriMatcher();

    private static UriMatcher createUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, MovieContract.PATH_MOVIE, CODE_MOVIE);
        return matcher;
    }

    private MovieDbHelper mMovieDbHelper;

    @Override
    public boolean onCreate() {
        mMovieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        Cursor cursor = null;
        switch (mUriMatcher.match(uri)) {
            case CODE_MOVIE:
                SQLiteDatabase readableDatabase = mMovieDbHelper.getReadableDatabase();
                cursor = readableDatabase.query(MovieEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        MovieEntry.COLUMN_RELEASE_DATE,
                        null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        long row = -1;
        switch (mUriMatcher.match(uri)) {
            case CODE_MOVIE:
                SQLiteDatabase writableDatabase = mMovieDbHelper.getWritableDatabase();
                row = writableDatabase.insert(MovieEntry.TABLE_NAME, null, contentValues);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        if(row > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rows = -1;
        switch (mUriMatcher.match(uri)) {
            case CODE_MOVIE:
                SQLiteDatabase writableDatabase = mMovieDbHelper.getWritableDatabase();
                rows = writableDatabase.delete(MovieEntry.TABLE_NAME,"_id=?",selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        if(rows > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        throw new RuntimeException(
                "update method not implemented.");
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException(
                "getType method not implemented.");
    }
}
