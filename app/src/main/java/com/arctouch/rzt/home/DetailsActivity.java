package com.arctouch.rzt.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.arctouch.rzt.R;
import com.arctouch.rzt.helper.MovieHelper;
import com.arctouch.rzt.model.Movie;

public class DetailsActivity extends AppCompatActivity {

    MovieHelper movieHelper;

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Was called Log.i() to verify if onCreate() method is called again after change orientation.
        //To verify, I used Logcat window.
        Log.i("onCreate", "Calling onCreate() method of DetailsActivity");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        movieHelper = new MovieHelper(this, this);

        Intent intent = getIntent();
        movie = (Movie) intent.getSerializableExtra("movie");

        if(movie != null) {
            movieHelper.fillMovieDetails(movie);
        }
    }
}
