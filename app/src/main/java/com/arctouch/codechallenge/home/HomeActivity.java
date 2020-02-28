package com.arctouch.codechallenge.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;

import com.arctouch.codechallenge.R;
import com.arctouch.codechallenge.api.TmdbApi;
import com.arctouch.codechallenge.base.BaseActivity;
import com.arctouch.codechallenge.data.Cache;
import com.arctouch.codechallenge.model.Genre;
import com.arctouch.codechallenge.model.Movie;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LinearLayoutManager linearLayout;

    private static List<Movie> movies;

    //private MovieRepository repository;
    //private HomeAdapter adapter;

    private boolean isScrolling = false;
    private static long PAGE_NUMBER = 1L;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //Was called Log.i() to verify if onCreate() method is called again after change orientation.
        //To verify, I used the Logcat window.
        Log.i("onCreate", "Calling onCreate() method of HomeActivity()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);

        linearLayout = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayout);

        //adapter = new HomeAdapter(movies);
        //repository = new MovieRepository(this, adapter, recyclerView, progressBar);
        //repository.getMovies();

        loadMovies();

        scrollListener();
    }

    @SuppressLint("CheckResult")
    private void loadMovies() {

        progressBar.setVisibility(View.VISIBLE);

        new BaseActivity().getTmdbApi()
                .upcomingMovies(TmdbApi.API_KEY, TmdbApi.DEFAULT_LANGUAGE, PAGE_NUMBER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    for (Movie movie : response.results) {
                        movie.genres = new ArrayList<>();
                        for (Genre genre : Cache.getGenres()) {
                            if (movie.genreIds.contains(genre.id)) {
                                movie.genres.add(genre);
                            }
                        }
                    }

                    movies = response.results;

                    HomeAdapter adapter = new HomeAdapter(movies);
                    recyclerView.setAdapter(adapter);

                    adapter.setOnItemClickListener(movie -> {
                        Intent intent = new Intent(this, DetailsActivity.class);
                        intent.putExtra("movie", movie);
                        startActivity(intent);
                    });

                    progressBar.setVisibility(View.GONE);
                });

    }

    private void scrollListener() {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int currentItems = linearLayout.getChildCount();
                int totalItems = linearLayout.getItemCount();
                int scrollOutItems = linearLayout.findFirstVisibleItemPosition();

                boolean isTotalItems = (currentItems + scrollOutItems) == totalItems;

                if (isScrolling && isTotalItems) {

                    isScrolling = false;

                    PAGE_NUMBER++;
                    loadMovies();
                    //repository.getMovies();
                }
            }
        });
    }
}
