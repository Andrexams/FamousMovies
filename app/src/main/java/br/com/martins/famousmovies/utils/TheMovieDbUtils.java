package br.com.martins.famousmovies.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import br.com.martins.famousmovies.model.Movie;

/**
 * Created by Andre Martins dos Santos on 02/04/2018.
 */

public class TheMovieDbUtils {

    private static final String TAG = TheMovieDbUtils.class.getSimpleName();

    private static final String API_KEY = "replace_with_api_key";
    private static final String API_KEY_PARAM = "api_key";

    private static final String THE_MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3";
    private static final String MOVIE_PATH = "movie";

    //http://api.themoviedb.org/3/MOVIE_PATH/top_rated?api_key=51e8e2ab21439c2c51d3803c26e32249
    //http://api.themoviedb.org/3/MOVIE_PATH/popular?api_key=51e8e2ab21439c2c51d3803c26e32249

    public enum MovieOrderBy {
        popular,
        top_rated
    }

    public static URL buildMovieUrl(MovieOrderBy movieOrderBy) {
        Uri builtUri = Uri.parse(THE_MOVIE_DB_BASE_URL).buildUpon()
                .appendPath(MOVIE_PATH)
                .appendPath(movieOrderBy.name())
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Built URI " + url);
        return url;
    }

    public static List<Movie> getListMoviesFromJson(Context context, String moviesJsonString)
            throws JSONException {

        List<Movie> movieList = new ArrayList<Movie>();

        JSONObject forecastJson = new JSONObject(moviesJsonString);
        JSONArray resultsArray = forecastJson.getJSONArray("results");

        for (int i = 0; i < resultsArray.length(); i++) {

            JSONObject movie = resultsArray.getJSONObject(i);

            Movie movieModel = new Movie();
            movieModel.setId(movie.getLong("id"));
            movieModel.setOriginalTitle(movie.getString("original_title"));
            movieModel.setOverview(movie.getString("overview"));
            movieModel.setPosterPath(movie.getString("poster_path"));
            movieModel.setVoteAverage(movie.getInt("vote_average"));
            movieModel.setBackdropPath(movie.getString("backdrop_path"));

            //TODO movieModel.setDate

            movieList.add(movieModel);

        }
        return movieList;
    }



}
