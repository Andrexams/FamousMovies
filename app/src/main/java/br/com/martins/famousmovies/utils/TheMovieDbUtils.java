package br.com.martins.famousmovies.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.martins.famousmovies.BuildConfig;
import br.com.martins.famousmovies.model.Movie;

/**
 * Created by Andre Martins dos Santos on 02/04/2018.
 */

public class TheMovieDbUtils {

    private static final String API_KEY = BuildConfig.api_key;
    private static final String TAG = TheMovieDbUtils.class.getSimpleName();
    private static final String API_KEY_PARAM = "api_key";
    private static final String THE_MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3";
    private static final String MOVIE_PATH = "movie";
    private static final String THE_MOVIE_DB_IMG_BASE_URL = "http://image.tmdb.org/t/p/w342/";
    private static final SimpleDateFormat spdf = new SimpleDateFormat("yyyy-MM-dd");

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

    public static List<Movie> getListMoviesFromJson(String moviesJsonString)
            throws Exception {

        List<Movie> movieList = new ArrayList<>();

        JSONObject forecastJson = new JSONObject(moviesJsonString);
        JSONArray resultsArray = forecastJson.getJSONArray("results");

        for (int i = 0; i < resultsArray.length(); i++) {

            JSONObject movie = resultsArray.getJSONObject(i);

            Movie movieModel = new Movie();
            movieModel.setId(movie.getLong("id"));
            movieModel.setOriginalTitle(movie.optString("original_title"));
            movieModel.setOverview(movie.optString("overview"));
            movieModel.setVoteAverage(movie.optDouble("vote_average"));

            String posterPath = movie.optString("poster_path");
            if(isNotNullEmpty(posterPath)){
                movieModel.setPosterPath(THE_MOVIE_DB_IMG_BASE_URL + posterPath);
            }

            String backdropPath = movie.optString("backdrop_path");
            if(isNotNullEmpty(backdropPath)){
                movieModel.setBackdropPath(THE_MOVIE_DB_IMG_BASE_URL + backdropPath);
            }

            String sReleaseDate = movie.optString("release_date");
            if(isNotNullEmpty(sReleaseDate)){
                movieModel.setReleaseDate(spdf.parse(sReleaseDate));
            }

            movieList.add(movieModel);

        }
        return movieList;
    }

    public static boolean isNotNullEmpty(String value){
        return (value != null && !value.isEmpty());
    }
}
