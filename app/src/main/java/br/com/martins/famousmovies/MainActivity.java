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
import br.com.martins.famousmovies.utils.TheMovieDbApi;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler,AsyncTaskDelegate {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int ACTION_DETAILS = 1000;

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
    private static final String MN_MOVIE_SELECTED = "MN_MOVIE_SELECTED";
    private Parcelable savedRecyclerLayoutState;

    private TheMovieDbApi.MovieCategory selected;

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

        if(savedInstanceState != null)
        {
            doRestoreInstanceActions(savedInstanceState);
        }

        doActionLoad();
    }

    private void doActionLoad() {
        selected = selected != null ? selected : TheMovieDbApi.MovieCategory.popular;
        loadMovies(selected);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
       try{
           outState.putParcelable(RV_MOVIE_LAYOUT_STATE,
                   mRecyclerViewMovie.getLayoutManager().onSaveInstanceState());

           outState.putString(MN_MOVIE_SELECTED,selected.name());

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
            doRestoreInstanceActions(savedInstanceState);
        }
    }

    private void doRestoreInstanceActions(Bundle savedInstanceState) {
        try{
            savedRecyclerLayoutState = savedInstanceState
                    .getParcelable(RV_MOVIE_LAYOUT_STATE);
            if(savedInstanceState.getString(MN_MOVIE_SELECTED) != null){
                selected = TheMovieDbApi.MovieCategory.valueOf(savedInstanceState.getString(MN_MOVIE_SELECTED));
            }
        }catch (Exception e){
            Log.e(TAG,"Error on doRestoreInstanceActions",e);
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
        loadMovies(TheMovieDbApi.MovieCategory.favorites_from_content_provider);
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
        loadMovies(TheMovieDbApi.MovieCategory.top_rated);
    }

    private void orderByPop() {
       mMovieAdapter.setListMovies(null);
       loadMovies(TheMovieDbApi.MovieCategory.popular);
    }

    private void loadMovies(TheMovieDbApi.MovieCategory movieCategory) {
        try {

            selected = movieCategory;

            if(NetworkUtils.isConnectOnNetwork(this)){

                showMovieDataView();

                if(movieCategory.equals(TheMovieDbApi.MovieCategory.popular)
                        || movieCategory.equals(TheMovieDbApi.MovieCategory.top_rated)){

                    URL url = TheMovieDbApi.buildMovieUrl(movieCategory);
                    new SearchMovieService(this,this).execute(url);

                }else if(movieCategory.equals(TheMovieDbApi.MovieCategory.favorites_from_content_provider)){

                    searchFavorites();

                }
            }else{
                if(movieCategory.equals(TheMovieDbApi.MovieCategory.favorites_from_content_provider)){

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

                    String msg = selected.equals(TheMovieDbApi.MovieCategory.favorites_from_content_provider) ?
                            getString(R.string.no_favorites_results) :
                            getString(R.string.no_results);

                    showErrorMessage(msg);
                } else {
                    mMovieAdapter.setListMovies(listMovie);
                    if (savedRecyclerLayoutState != null) {
                        mRecyclerViewMovie.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
                    }
                }
            }else{
                String msg = getString(R.string.no_results);
                if(selected.equals(TheMovieDbApi.MovieCategory.favorites_from_content_provider)){
                    msg = getString(R.string.no_favorites_results);
                }
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
        startActivityForResult(intent,1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case ACTION_DETAILS:
                if(selected.equals(TheMovieDbApi.MovieCategory.favorites_from_content_provider) &&
                        resultCode == DetailActivity.ACTION_RESULT_RELOAD)
                    doActionLoad();
                break;
            default:
        }
    }

    private void showSnackRetry(View parent, final TheMovieDbApi.MovieCategory orderBy){
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
