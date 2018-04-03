package br.com.martins.famousmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by Andre Martins dos Santos on 29/03/2018.
 */

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String EXTRA_MOVIE_ID = "MOVIE_ID";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intentToOpenDetail = getIntent();
        if(intentToOpenDetail.hasExtra(DetailActivity.EXTRA_MOVIE_ID)){
            Long movieId = intentToOpenDetail.getLongExtra(EXTRA_MOVIE_ID,-1l);
            Toast.makeText(this,String.valueOf(movieId),Toast.LENGTH_SHORT).show();
        }
    }

}
