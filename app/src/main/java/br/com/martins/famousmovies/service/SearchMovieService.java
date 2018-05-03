package br.com.martins.famousmovies.service;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
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
public class SearchMovieService extends AsyncTask<Object,Void,List<Movie>>  {

    private static final String TAG = SearchMovieService.class.getSimpleName();

    private AsyncTaskDelegate delegate = null;
    private Context mContext;

    public SearchMovieService(Context context, AsyncTaskDelegate responder){
        this.mContext = context;
        this.delegate = responder;
    }

    @Override
    protected void onPreExecute() {
        delegate.onPreExecute();
    }

    @Override
    protected List<Movie> doInBackground(Object... objects) {
        try {
            Object obj = objects[0];
            if(obj instanceof  URL){

                URL url = (URL)obj;
                String jsonMoviesResponse = NetworkUtils.getResponseFromHttpUrl(url);
                return TheMovieDbUtils.getListMoviesFromJson(jsonMoviesResponse);

            }else if(obj instanceof Uri){

                Uri uri = (Uri)obj;
                Cursor query = mContext.getContentResolver().query(uri,
                        null,
                        null,
                        null,
                        null,
                        null);
                return TheMovieDbUtils.getListMoviesFromCursor(query);
            }

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
