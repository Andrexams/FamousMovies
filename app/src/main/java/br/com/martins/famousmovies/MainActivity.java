package br.com.martins.famousmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.List;

import br.com.martins.famousmovies.model.Movie;
import br.com.martins.famousmovies.utils.NetworkUtils;
import br.com.martins.famousmovies.utils.TheMovieDbUtils;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerViewMovie;
    private MovieAdapter mMovieAdapter;
    private ProgressBar mProgressBarLoading;
    private TextView mTextViewErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerViewMovie = (RecyclerView) findViewById(R.id.rv_movie);
        mProgressBarLoading = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mTextViewErrorMessage = (TextView)  findViewById(R.id.tv_error_message_display);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerViewMovie.setLayoutManager(gridLayoutManager);
        mRecyclerViewMovie.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerViewMovie.setAdapter(mMovieAdapter);

        try{
            loadMovies(TheMovieDbUtils.MovieOrderBy.popular);
        }catch (Exception e){
            Log.e(TAG,"Error on load",e);
            showErrorMessage(getString(R.string.error_message));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case (R.id.action_order_by_pop):
                orderByPop();
                break;
            case (R.id.action_order_by_rating):
                orderByRating();
                break;
            default:
                return false;
        }
        return true;
    }

    private void showMovieDataView() {
        mTextViewErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerViewMovie.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(String errorMessage) {
        mRecyclerViewMovie.setVisibility(View.INVISIBLE);
        mTextViewErrorMessage.setText(errorMessage);
        mTextViewErrorMessage.setVisibility(View.VISIBLE);
    }

    private void orderByRating() {
        try{
            mMovieAdapter.setListMovies(null);
            loadMovies(TheMovieDbUtils.MovieOrderBy.top_rated);
        }catch (Exception e){
            Log.e(TAG,"Error on orderByRating",e);
        }
    }

    private void orderByPop() {
        try{
            mMovieAdapter.setListMovies(null);
            loadMovies(TheMovieDbUtils.MovieOrderBy.popular);
        }catch (Exception e){
            Log.e(TAG,"Error on orderByPop",e);
        }
    }

    private void loadMovies(TheMovieDbUtils.MovieOrderBy movieOrderBy){
       showMovieDataView();
       URL url = TheMovieDbUtils.buildMovieUrl(movieOrderBy);
       new SearchMoviesTask().execute(url);
    }

    private class SearchMoviesTask extends AsyncTask<URL,Void,List<Movie>>{

        private Exception mException;

        @Override
        protected void onPreExecute() {
            mProgressBarLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(URL... url) {
            try {
                String jsonMoviesResponse = NetworkUtils.getResponseFromHttpUrl(url[0]);
                List<Movie> listMovie = TheMovieDbUtils.getListMoviesFromJson(MainActivity.this, jsonMoviesResponse);
                return listMovie;
            } catch (Exception e) {
                this.mException = e;
                Log.e(TAG, "Error executing SearchMoviesTask.doInBackground", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> listMovie) {
            try{
                mProgressBarLoading.setVisibility(View.INVISIBLE);
                if(mException != null){
                    showErrorMessage(getString(R.string.error_message));
                }else{
                    if(listMovie == null || listMovie.isEmpty()){
                        showErrorMessage(getString(R.string.no_results));
                    }else{
                        mMovieAdapter.setListMovies(listMovie);
                    }
                }
            }catch (Exception e){
                this.mException = e;
                Log.e(TAG,"Error executing SearchMoviesTask.onPostExecute",e);
            }
        }
    }

    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(this,DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_MOVIE,movie);
        startActivity(intent);
    }
}
