package br.com.martins.famousmovies;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.List;

import br.com.martins.famousmovies.model.Movie;
import br.com.martins.famousmovies.utils.NetworkUtils;
import br.com.martins.famousmovies.utils.TheMovieDbUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.rv_movie)
    RecyclerView mRecyclerViewMovie;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar mProgressBarLoading;

    @BindView(R.id.tv_error_message_display)
    TextView mTextViewErrorMessage;

    @BindView(R.id.fl_activity_main)
    FrameLayout mFrameLayoutActivity;

    private MovieAdapter mMovieAdapter;

    private static final String RV_MOVIE_LAYOUT_STATE = "RV_MOVIE_LAYOUT_STATE";
    private Parcelable savedRecyclerLayoutState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerViewMovie.setLayoutManager(gridLayoutManager);
        mRecyclerViewMovie.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerViewMovie.setAdapter(mMovieAdapter);
        mRecyclerViewMovie.setSaveEnabled(true);

        loadMovies(TheMovieDbUtils.MovieOrderBy.popular);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
       try{
           outState.putParcelable(RV_MOVIE_LAYOUT_STATE,
                   mRecyclerViewMovie.getLayoutManager().onSaveInstanceState());
       }catch (Exception e){
           Log.e(TAG,"Error on onSaveInstanceState",e);
       }
       super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null)
        {
            savedRecyclerLayoutState = savedInstanceState
                    .getParcelable(RV_MOVIE_LAYOUT_STATE);
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
        mMovieAdapter.setListMovies(null);
        loadMovies(TheMovieDbUtils.MovieOrderBy.top_rated);
    }

    private void orderByPop() {
       mMovieAdapter.setListMovies(null);
       loadMovies(TheMovieDbUtils.MovieOrderBy.popular);
    }

    private void loadMovies(TheMovieDbUtils.MovieOrderBy orderBy) {
        try {
            if(NetworkUtils.isConnectOnNetwork(this)){
                showMovieDataView();
                URL url = TheMovieDbUtils.buildMovieUrl(orderBy);
                new SearchMoviesTask().execute(url);
            }else{
                showErrorMessage(getString(R.string.no_internet_message));
                showSnackRetry(mFrameLayoutActivity,orderBy);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error on load", e);
            showErrorMessage(getString(R.string.error_message));
        }
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

                        /*if(lastFirstVisiblePosition != null){
                            ((GridLayoutManager) mRecyclerViewMovie.getLayoutManager())
                                    .scrollToPositionWithOffset(lastFirstVisiblePosition,0);
                        }*/
                        if(savedRecyclerLayoutState != null){
                            mRecyclerViewMovie.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
                        }

                    }
                }
                Log.i(TAG,"onPostExecute");
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

    private void showSnackRetry(View parent, final TheMovieDbUtils.MovieOrderBy orderBy){
        Snackbar snackbar = Snackbar
                .make(parent, null, Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadMovies(orderBy);
                    }
                });
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }

}
