package br.com.martins.famousmovies;

import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import br.com.martins.famousmovies.model.Movie;
import br.com.martins.famousmovies.model.Review;
import br.com.martins.famousmovies.model.Video;
import br.com.martins.famousmovies.persistence.FavoriteMovieDao;
import br.com.martins.famousmovies.utils.NetworkUtils;
import br.com.martins.famousmovies.utils.TheMovieDbApi;
import br.com.martins.famousmovies.utils.URLUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Andre Martins dos Santos on 29/03/2018.
 */
public class DetailActivity extends AppCompatActivity implements VideoAdapter.MovieAdapterOnClickHandler{

    private static final String TAG = DetailActivity.class.getSimpleName();
    public static final String EXTRA_MOVIE = "MOVIE";
    public static final int ACTION_RESULT_RELOAD = 1001;

    @BindView(R.id.tv_detail_error_message_display)
    TextView mTextViewDetailErrorMessage;

    @BindView(R.id.ll_detail_data)
    LinearLayout mLinearLayoutData;

    @BindView(R.id.iv_mini_poster)
    ImageView mImageView;

    @BindView(R.id.tv_movie_title)
    TextView mTextViewTitle;

    @BindView(R.id.tv_movie_overview)
    TextView mTextViewOverview;

    @BindView(R.id.tv_movie_date)
    TextView mTextViewDate;

    @BindView(R.id.tv_movie_rate)
    TextView mTextViewRate;

    @BindView(R.id.fl_detail_layout)
    FrameLayout mFrameLayoutDetail;

    @BindView(R.id.iv_favorite)
    ImageView mFavorite;

    @BindView(R.id.tv_reviews)
    TextView mTextReviews;

    @BindView(R.id.rv_video)
    RecyclerView mRecyclerViewVideo;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar mProgressBarLoading;

    private Movie mMovie = null;
    private VideoAdapter mVideoAdapter;

    private static final SimpleDateFormat releaseDateFormat = new SimpleDateFormat("EEE, d MMM yyyy", Locale.US);

    private static final String RV_VIDEO_LAYOUT_STATE = "RV_VIDEO_LAYOUT_STATE";
    private Parcelable savedRecyclerLayoutState;
    private boolean favoriteClicked;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL,false);
        mRecyclerViewVideo.setLayoutManager(linearLayoutManager);
        mRecyclerViewVideo.setHasFixedSize(true);

        mVideoAdapter = new VideoAdapter(this);
        mRecyclerViewVideo.setAdapter(mVideoAdapter);
        mRecyclerViewVideo.setSaveEnabled(true);

        Intent intentToOpenDetail = getIntent();
        if (intentToOpenDetail.hasExtra(DetailActivity.EXTRA_MOVIE)) {
            Bundle bundle = intentToOpenDetail.getExtras();
            mMovie = (Movie) bundle.getParcelable(EXTRA_MOVIE);
            loadData();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            setResult(favoriteClicked ? ACTION_RESULT_RELOAD : RESULT_OK);
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        setResult(favoriteClicked ? ACTION_RESULT_RELOAD : RESULT_OK);
        super.onBackPressed();
    }

    private void loadData() {
        try {
            if (NetworkUtils.isConnectOnNetwork(this)) {
                new LoadDataTask().execute();
            } else {
                showErrorMessage(getString(R.string.no_internet_message));
                showSnackRetry(mLinearLayoutData);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error on load detail", e);
            showErrorMessage(getString(R.string.error_message));
            showSnackRetry(mFrameLayoutDetail);
        }
    }

    private class LoadDataTask extends AsyncTask<Void,Void,Object[]>{

        @Override
        protected void onPreExecute() {
            mLinearLayoutData.setVisibility(View.INVISIBLE);
            mProgressBarLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected Object[] doInBackground(Void... voids) {
            Object[] result = new Object[2];
            try{
                //Set favorite.
                fillFavorite();
                // Buscar videos e revisoes
                result[0] = getListVideo();
                result[1] = getListReview();
            }catch (Exception e){
                Log.e(TAG,"Error on fetching movie",e);
            }
            return result;
        }

        @Override
        protected void onPostExecute(Object...objects) {
            showMovieDataView();

            Picasso.with(DetailActivity.this)
                    .load(mMovie.getBackdropPath())
                    .into(mImageView);

            mTextViewTitle.setText(mMovie.getOriginalTitle());
            mTextViewDate.setText(releaseDateFormat.format(mMovie.getReleaseDate()));
            mTextViewOverview.setText(mMovie.getOverview());

            setVoteAverage();
            setFavoriteView();

            if(objects[0] != null){
                setVideos(objects[0]);
            }
            if(objects[1] != null){
                setReviews(objects[1]);
            }

            mProgressBarLoading.setVisibility(View.INVISIBLE);
        }
    }

    private void fillFavorite() throws Exception {
        Movie movie = FavoriteMovieDao.getMovie(DetailActivity.this,mMovie.getId());
        if(movie != null){
            mMovie.setFavorite(true);
        }else{
            mMovie.setFavorite(false);
        }
    }

    private List<Review> getListReview() throws Exception {
        URL url = TheMovieDbApi.buildMovieReviewsUrl(mMovie.getId());
        String responseFromHttpUrl = NetworkUtils.getResponseFromHttpUrl(url);
        return TheMovieDbApi.getListMovieReviewFromJson(responseFromHttpUrl);
    }

    private List<Video> getListVideo() throws Exception {
        URL url = TheMovieDbApi.buildMovieVideosUrl(mMovie.getId());
        String responseFromHttpUrl = NetworkUtils.getResponseFromHttpUrl(url);
        return TheMovieDbApi.getListMovieVideoFromJson(responseFromHttpUrl);
    }

    private void setVoteAverage() {
        Double voteAvg = mMovie.getVoteAverage();
        if (voteAvg != null && voteAvg > 0) {
            if (voteAvg < 6) {
                mTextViewRate.setTextColor(Color.RED);
            } else if (voteAvg >= 6 && voteAvg <= 8) {
                mTextViewRate.setTextColor(Color.YELLOW);
            } else if (voteAvg > 8) {
                mTextViewRate.setTextColor(Color.GREEN);
            }
            mTextViewRate.setText(mMovie.getVoteAverage().toString());
        } else {
            mTextViewRate.setText(getString(R.string.no_votes));
            mTextViewRate.setTextSize(22);
        }
    }

    private void setVideos(Object object) {
        List<Video> movieList = (List<Video>) object;
        mVideoAdapter.setListVideo(movieList);
        if (savedRecyclerLayoutState != null) {
            mRecyclerViewVideo.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    private void setReviews(Object object) {
        List<Review> reviewList = (List<Review>) object;
        if(reviewList != null){
            for(Review review : reviewList){
                String textReview = getString(R.string.by) +" "+ review.getAuthor() + ",\n" + review.getContent();
                mTextReviews.append(textReview);
                mTextReviews.append("\n\n");
            }
        }
    }

    private void setFavoriteView() {
        if(mMovie.getFavorite()){
            mFavorite.setImageDrawable(getResources().getDrawable(android.R.drawable.star_big_on));
            mFavorite.setContentDescription(getString(R.string.content_desc_tap_unfavorite));
        }else{
            mFavorite.setImageDrawable(getResources().getDrawable(android.R.drawable.star_big_off));
            mFavorite.setContentDescription(getString(R.string.content_desc_tap_favorite));
        }
    }

    private void showMovieDataView() {
        mTextViewDetailErrorMessage.setVisibility(View.INVISIBLE);
        mLinearLayoutData.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(String errorMessage) {
        mLinearLayoutData.setVisibility(View.INVISIBLE);
        mTextViewDetailErrorMessage.setText(errorMessage);
        mTextViewDetailErrorMessage.setVisibility(View.VISIBLE);
    }

    private void showSnackRetry(View parent){
        Snackbar snackbar = Snackbar
                .make(parent, "", Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadData();
                    }
                });
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }

    public void setFavorite(View view){
        try{
            if(this.mMovie.getFavorite()){
                FavoriteMovieDao.deleteMovie(this,mMovie);
                mMovie.setFavorite(false);
            }else{
                FavoriteMovieDao.insertMovie(this,mMovie);
                mMovie.setFavorite(true);
            }
            this.favoriteClicked = true;
            setFavoriteView();
        }catch (Exception e){
            Log.e(TAG,"Error on setting favorite",e);
        }
    }

    @Override
    public void onClick(Video video) {
        if(video.getSite().toLowerCase().equals(getString(R.string.youtube))){
           Uri uri = URLUtils.buildYoutubeVideoUri(video.getKey());
           Intent intent = new Intent(Intent.ACTION_VIEW,uri);
           startActivity(intent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        try{
            outState.putParcelable(RV_VIDEO_LAYOUT_STATE,
                    mRecyclerViewVideo.getLayoutManager().onSaveInstanceState());
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
                    .getParcelable(RV_VIDEO_LAYOUT_STATE);
        }
    }



}
