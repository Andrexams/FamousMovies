package br.com.martins.famousmovies.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.net.URL;
import java.util.List;

import br.com.martins.famousmovies.AsyncTaskDelegate;
import br.com.martins.famousmovies.model.Movie;
import br.com.martins.famousmovies.utils.NetworkUtils;
import br.com.martins.famousmovies.utils.TheMovieDbUtils;

/**
 * Created by Andre Martins dos Santos on 08/04/2018.
 */
public class SearchMovieService extends AsyncTask<URL,Void,List<Movie>>  {

    private static final String TAG = SearchMovieService.class.getSimpleName();

    private AsyncTaskDelegate delegate = null;

    public SearchMovieService(Context context, AsyncTaskDelegate responder){
        this.delegate = responder;
    }

    @Override
    protected void onPreExecute() {
        delegate.onPreExecute();
    }

    @Override
    protected List<Movie> doInBackground(URL... url) {
        try {
            String jsonMoviesResponse = NetworkUtils.getResponseFromHttpUrl(url[0]);
            return TheMovieDbUtils.getListMoviesFromJson(jsonMoviesResponse);
        } catch (Exception e) {
            delegate.onException(e);
            Log.e(TAG, "Error executing SearchMovieService.doInBackground", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Movie> listMovie) {
        delegate.onPostExecute(listMovie);
    }

}
