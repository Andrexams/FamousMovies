package br.com.martins.famousmovies;

import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Locale;

import br.com.martins.famousmovies.model.Movie;

/**
 * Created by Andre Martins dos Santos on 29/03/2018.
 */

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String EXTRA_MOVIE = "MOVIE";

    private Movie mMovie;
    private ImageView mImageView;
    private TextView mTextViewTitle;
    private TextView mTextViewOverview;
    private TextView mTextViewDate;
    private TextView mTextViewRate;

    private static final SimpleDateFormat spdf = new SimpleDateFormat("EEE, d MMM yyyy",Locale.US);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mImageView = (ImageView)findViewById(R.id.iv_mini_poster);
        mTextViewTitle = (TextView) findViewById(R.id.tv_movie_title);
        mTextViewOverview = (TextView) findViewById(R.id.tv_movie_overview);
        mTextViewDate = (TextView) findViewById(R.id.tv_movie_date);
        mTextViewRate = (TextView) findViewById(R.id.tv_movie_rate);

        Intent intentToOpenDetail = getIntent();

        if(intentToOpenDetail.hasExtra(DetailActivity.EXTRA_MOVIE)){
            mMovie = (Movie) intentToOpenDetail.getSerializableExtra(EXTRA_MOVIE);
            loadData();
        }
    }

    private void loadData(){
        Picasso.with(this)
                .load(mMovie.getBackdropPath())
                .into(mImageView);
        mTextViewTitle.setText(mMovie.getOriginalTitle());
        mTextViewDate.setText(spdf.format(mMovie.getReleaseDate()));
        mTextViewOverview.setText(mMovie.getOverview());

        Double voteAvg = mMovie.getVoteAverage();
        if(voteAvg < 6){
            mTextViewRate.setTextColor(Color.RED);
        }else if(voteAvg >= 6 && voteAvg <= 8){
            mTextViewRate.setTextColor(Color.YELLOW);
        }else if(voteAvg > 8) {
            mTextViewRate.setTextColor(Color.GREEN);
        }
        mTextViewRate.setText(mMovie.getVoteAverage().toString());
    }

}
