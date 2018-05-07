package br.com.martins.famousmovies.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.martins.famousmovies.model.Movie;
import br.com.martins.famousmovies.persistence.MovieContract.MovieEntry;

/**
 * Created by Andre Martins dos Santos on 06/05/2018.
 */
public class FavoriteMovieDao {

    private static Uri uri = MovieContract.MovieEntry.CONTENT_URI;

    public static List<Movie> getListFavorites(Context context) throws Exception{
        Cursor cursor = context.getContentResolver().query(uri,
                null,
                null,
                null,
                null,
                null);
        return getListMoviesFromCursor(cursor);
    }

    public static Movie getMovie(Context context, Long movieId) throws Exception{
        Cursor cursor = context.getContentResolver().query(uri,
                null,
                MovieEntry._ID+"=?",
                new String[]{String.valueOf(movieId)},
                null,
                null);
        if(cursor != null && cursor.moveToFirst()){
            return getMovie(cursor);
        }
        return null;
    }

    public static void insertMovie(Context context, Movie movie) throws Exception{
        context.getContentResolver().insert(uri,getContentValues(movie));
    }

    public static void deleteMovie(Context context, Movie movie) throws Exception{
        context.getContentResolver().delete(uri,
                MovieEntry._ID+"=?",
                new String[]{String.valueOf(movie.getId())});
    }

    public static List<Movie> getListMoviesFromCursor(Cursor cursor)
            throws Exception {
        List<Movie> movieList = new ArrayList<>();
        if(cursor != null){
            int count = cursor.getCount();
            for(int i = 0; i < count; i++){
                if(cursor.moveToPosition(i)){
                    movieList.add(getMovie(cursor));
                }
            }
        }
        return movieList;
    }

    private static Movie getMovie(Cursor cursor) {
        Movie movie = new Movie();
        movie.setId(cursor.getLong(cursor.getColumnIndex(MovieContract.MovieEntry._ID)));
        movie.setOriginalTitle(cursor.getString(
                cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE)));

        int index = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH);
        if(!cursor.isNull(index)){
            movie.setBackdropPath(cursor.getString(index));
        }

        index = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        if(!cursor.isNull(index)){
            movie.setPosterPath(cursor.getString(index));
        }

        index = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        if(!cursor.isNull(index)){
            movie.setOverview(cursor.getString(index));
        }

        index = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
        if(!cursor.isNull(index)){
            movie.setVoteAverage(cursor.getDouble(index));
        }

        index = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        if(!cursor.isNull(index)){
            movie.setReleaseDate(new Date(cursor.getLong(index)));
        }
        return movie;
    }

    public static ContentValues getContentValues(Movie movie){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieEntry._ID,movie.getId());
        contentValues.put(MovieEntry.COLUMN_ORIGINAL_TITLE,movie.getOriginalTitle());
        contentValues.put(MovieEntry.COLUMN_OVERVIEW,movie.getOverview());
        contentValues.put(MovieEntry.COLUMN_BACKDROP_PATH,movie.getBackdropPath());
        contentValues.put(MovieEntry.COLUMN_POSTER_PATH,movie.getPosterPath());
        contentValues.put(MovieEntry.COLUMN_VOTE_AVERAGE,movie.getVoteAverage());
        contentValues.put(MovieEntry.COLUMN_RELEASE_DATE,movie.getReleaseDate().getTime());
        return contentValues;
    }
}
