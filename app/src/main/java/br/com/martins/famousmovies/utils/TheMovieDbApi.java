package br.com.martins.famousmovies.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import android.net.Uri;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.martins.famousmovies.BuildConfig;
import br.com.martins.famousmovies.model.Movie;
import br.com.martins.famousmovies.model.Review;
import br.com.martins.famousmovies.model.Video;

/**
 * Created by Andre Martins dos Santos on 02/04/2018.
 */

public class TheMovieDbApi {

    private static final String API_KEY = BuildConfig.api_key;
    private static final String TAG = TheMovieDbApi.class.getSimpleName();
    private static final String API_KEY_PARAM = "api_key";
    private static final String THE_MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3";
    private static final String MOVIE_PATH = "movie";
    private static final String REVIEWS_PATH = "reviews";
    private static final String VIDEOS_PATH = "videos";
    private static final String THE_MOVIE_DB_IMG_BASE_URL = "http://image.tmdb.org/t/p/w342/";
    private static final SimpleDateFormat spdf = new SimpleDateFormat("yyyy-MM-dd");

    public enum MovieCategory {
        popular,
        top_rated,
        favorites_from_content_provider
    }

    public static URL buildMovieUrl(MovieCategory movieCategory) {
        Uri builtUri = Uri.parse(THE_MOVIE_DB_BASE_URL).buildUpon()
                .appendPath(MOVIE_PATH)
                .appendPath(movieCategory.name())
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

    public static URL buildMovieReviewsUrl(Long movieId) {
        Uri builtUri = Uri.parse(THE_MOVIE_DB_BASE_URL).buildUpon()
                .appendPath(MOVIE_PATH)
                .appendPath(String.valueOf(movieId))
                .appendPath(REVIEWS_PATH)
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

    public static URL buildMovieVideosUrl(Long movieId) {
        Uri builtUri = Uri.parse(THE_MOVIE_DB_BASE_URL).buildUpon()
                .appendPath(MOVIE_PATH)
                .appendPath(String.valueOf(movieId))
                .appendPath(VIDEOS_PATH)
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
        JSONObject movieJson = new JSONObject(moviesJsonString);
        JSONArray resultsArray = movieJson.getJSONArray("results");

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

    public static List<Review> getListMovieReviewFromJson(String movieReviewJsonString)
            throws Exception {

        List<Review> reviewList = new ArrayList<>();
        JSONObject reviewJson = new JSONObject(movieReviewJsonString);
        JSONArray resultsArray = reviewJson.getJSONArray("results");

        for (int i = 0; i < resultsArray.length(); i++) {
            JSONObject reviewObj = resultsArray.getJSONObject(i);
            Review review = new Review();
            review.setId(reviewObj.getString("id"));

            String author = reviewObj.optString("author");
            if(isNotNullEmpty(author)){
                review.setAuthor(author);
            }

            String content = reviewObj.optString("content");
            if(isNotNullEmpty(content)){
                review.setContent(content);
            }
            reviewList.add(review);
        }
        return reviewList;
    }

    public static List<Video> getListMovieVideoFromJson(String movieVideoJsonString)
            throws Exception {

        List<Video> reviewList = new ArrayList<>();
        JSONObject videoJson = new JSONObject(movieVideoJsonString);
        JSONArray resultsArray = videoJson.getJSONArray("results");

        for (int i = 0; i < resultsArray.length(); i++) {
            JSONObject reviewObj = resultsArray.getJSONObject(i);

            Video video =  new Video();
            video.setId(reviewObj.getString("id"));

            String name = reviewObj.optString("name");
            if(isNotNullEmpty(name)){
                video.setName(name);
            }

            String key = reviewObj.optString("key");
            if(isNotNullEmpty(key)){
                video.setKey(key);
            }

            String site = reviewObj.optString("site");
            if(isNotNullEmpty(site)){
                video.setSite(site);
            }

            String type = reviewObj.optString("type");
            if(isNotNullEmpty(type)){
                video.setType(type);
            }

            reviewList.add(video);
        }
        return reviewList;
    }

    public static boolean isNotNullEmpty(String value){
        return (value != null && !value.isEmpty());
    }

}
