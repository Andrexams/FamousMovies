package br.com.martins.famousmovies;

import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import br.com.martins.famousmovies.model.Movie;
import br.com.martins.famousmovies.utils.NetworkUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Andre Martins dos Santos on 29/03/2018.
 */
public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();
    public static final String EXTRA_MOVIE = "MOVIE";

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

    @BindView(R.id.tv_avg_vote)
    TextView mTextViewPeopleAvgVotesLabel;

    @BindView(R.id.fl_detail_layout)
    FrameLayout mFrameLayoutDetail;

    private Movie mMovie;

    private static final SimpleDateFormat releaseDateFormat = new SimpleDateFormat("EEE, d MMM yyyy", Locale.US);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Intent intentToOpenDetail = getIntent();
        if (intentToOpenDetail.hasExtra(DetailActivity.EXTRA_MOVIE)) {
            Bundle bundle = intentToOpenDetail.getExtras();
            mMovie = (Movie) bundle.getParcelable(EXTRA_MOVIE);
            loadData();
        }
    }

    private void loadData() {
        try {
            if (NetworkUtils.isConnectOnNetwork(this)) {

                showMovieDataView();
                Picasso.with(this)
                        .load(mMovie.getBackdropPath())
                        .into(mImageView);

                mTextViewTitle.setText(mMovie.getOriginalTitle());
                mTextViewDate.setText(releaseDateFormat.format(mMovie.getReleaseDate()));
                mTextViewOverview.setText(mMovie.getOverview());

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
                    mTextViewPeopleAvgVotesLabel.setVisibility(View.GONE);
                }
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

}
