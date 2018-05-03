package br.com.martins.famousmovies;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
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
import br.com.martins.famousmovies.persistence.MovieContract;
import br.com.martins.famousmovies.service.SearchMovieService;
import br.com.martins.famousmovies.utils.NetworkUtils;
import br.com.martins.famousmovies.utils.TheMovieDbUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler,AsyncTaskDelegate {

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

        loadMovies(TheMovieDbUtils.MovieCategory.popular);
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
            case (R.id.action_favorites):
                showFavorites();
                break;
            default:
                return false;
        }
        return true;
    }

    private void showFavorites() {
        mMovieAdapter.setListMovies(null);
        loadMovies(TheMovieDbUtils.MovieCategory.favorites_from_content_provider);
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
        loadMovies(TheMovieDbUtils.MovieCategory.top_rated);
    }

    private void orderByPop() {
       mMovieAdapter.setListMovies(null);
       loadMovies(TheMovieDbUtils.MovieCategory.popular);
    }

    private void loadMovies(TheMovieDbUtils.MovieCategory movieCategory) {
        try {
            if(NetworkUtils.isConnectOnNetwork(this)){

                showMovieDataView();

                if(movieCategory.equals(TheMovieDbUtils.MovieCategory.popular)
                        || movieCategory.equals(TheMovieDbUtils.MovieCategory.top_rated)){

                    URL url = TheMovieDbUtils.buildMovieUrl(movieCategory);
                    new SearchMovieService(this,this).execute(url);

                }else if(movieCategory.equals(TheMovieDbUtils.MovieCategory.favorites_from_content_provider)){

                    searchFavorites();

                }
            }else{
                if(movieCategory.equals(TheMovieDbUtils.MovieCategory.favorites_from_content_provider)){

                    searchFavorites();

                }else{

                    showErrorMessage(getString(R.string.no_internet_message));
                    showSnackRetry(mFrameLayoutActivity,movieCategory);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error on load", e);
            showErrorMessage(getString(R.string.error_message));
        }
    }

    private void searchFavorites() {
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        new SearchMovieService(this,this).execute(uri);
    }

    @Override
    public void onPreExecute() {
        mProgressBarLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPostExecute(Object output) {
        try {
            if(output != null){
                List<Movie> listMovie = (List<Movie>) output;
                mProgressBarLoading.setVisibility(View.INVISIBLE);
                if (listMovie == null || listMovie.isEmpty()) {
                    showErrorMessage(getString(R.string.no_results));
                } else {
                    mMovieAdapter.setListMovies(listMovie);
                    if (savedRecyclerLayoutState != null) {
                        mRecyclerViewMovie.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
                    }
                }
            }else{
                showErrorMessage(getString(R.string.no_results));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error executing AsyncTaskDelegate.onPostExecute", e);
        }
    }

    @Override
    public void onException(Exception e) {
        showErrorMessage(getString(R.string.error_message));
    }

    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(this,DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_MOVIE,movie);
        startActivity(intent);
    }

    private void showSnackRetry(View parent, final TheMovieDbUtils.MovieCategory orderBy){
        Snackbar snackbar = Snackbar
                .make(parent, "", Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadMovies(orderBy);
                    }
                });
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }
}
