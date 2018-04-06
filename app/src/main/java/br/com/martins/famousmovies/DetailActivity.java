package br.com.martins.famousmovies;

import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import br.com.martins.famousmovies.model.Movie;

/**
 * Created by Andre Martins dos Santos on 29/03/2018.
 */
public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();
    public static final String EXTRA_MOVIE = "MOVIE";

    private TextView mTextViewDetailErrorMessage;
    private LinearLayout mLinearLayoutData;

    private Movie mMovie;
    private ImageView mImageView;
    private TextView mTextViewTitle;
    private TextView mTextViewOverview;
    private TextView mTextViewDate;
    private TextView mTextViewRate;
    private TextView mTextViewPeopleAvgVotesLabel;

    private static final SimpleDateFormat releaseDateFormat = new SimpleDateFormat("EEE, d MMM yyyy", Locale.US);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTextViewDetailErrorMessage = (TextView)findViewById(R.id.tv_detail_error_message_display);
        mLinearLayoutData = (LinearLayout) findViewById(R.id.ll_detail_data);

        mImageView = (ImageView)findViewById(R.id.iv_mini_poster);
        mTextViewTitle = (TextView) findViewById(R.id.tv_movie_title);
        mTextViewOverview = (TextView) findViewById(R.id.tv_movie_overview);
        mTextViewDate = (TextView) findViewById(R.id.tv_movie_date);
        mTextViewRate = (TextView) findViewById(R.id.tv_movie_rate);
        mTextViewPeopleAvgVotesLabel = (TextView) findViewById(R.id.tv_avg_vote);

        try{
            Intent intentToOpenDetail = getIntent();
            if(intentToOpenDetail.hasExtra(DetailActivity.EXTRA_MOVIE)){
                mMovie = (Movie) intentToOpenDetail.getSerializableExtra(EXTRA_MOVIE);
                loadData();
            }
        }catch (Exception e){
            Log.e(TAG,"Erro on load detail",e);
            showErrorMessage(getString(R.string.error_message));
        }
    }

    private void loadData(){

        showMovieDataView();

        Picasso.with(this)
                .load(mMovie.getBackdropPath())
                .into(mImageView);

        mTextViewTitle.setText(mMovie.getOriginalTitle());
        mTextViewDate.setText(releaseDateFormat.format(mMovie.getReleaseDate()));
        mTextViewOverview.setText(mMovie.getOverview());

        Double voteAvg = mMovie.getVoteAverage();

        if(voteAvg != null && voteAvg > 0){
            if(voteAvg < 6){
                mTextViewRate.setTextColor(Color.RED);
            }else if(voteAvg >= 6 && voteAvg <= 8){
                mTextViewRate.setTextColor(Color.YELLOW);
            }else if(voteAvg > 8) {
                mTextViewRate.setTextColor(Color.GREEN);
            }
            mTextViewRate.setText(mMovie.getVoteAverage().toString());
        }else{
            mTextViewRate.setText(getString(R.string.no_votes));
            mTextViewRate.setTextSize(22);
            mTextViewPeopleAvgVotesLabel.setVisibility(View.GONE);
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

}
