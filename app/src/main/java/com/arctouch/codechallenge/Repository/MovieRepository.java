package com.arctouch.codechallenge.Repository;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arctouch.codechallenge.R;
import com.arctouch.codechallenge.api.TmdbApi;
import com.arctouch.codechallenge.base.BaseActivity;
import com.arctouch.codechallenge.data.Cache;
import com.arctouch.codechallenge.home.DetailsActivity;
import com.arctouch.codechallenge.home.HomeAdapter;
import com.arctouch.codechallenge.model.Genre;
import com.arctouch.codechallenge.model.Movie;
import com.arctouch.codechallenge.util.MovieImageUrlBuilder;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieRepository extends AppCompatActivity {

    private Activity activity;
    private List<Movie> movies;
    private HomeAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    public static long PAGE_NUMBER = 1L;

    final MovieImageUrlBuilder movieImageUrlBuilder = new MovieImageUrlBuilder();

    public MovieRepository(Activity activity) {
        this.activity = activity;
    }

    public MovieRepository(Activity activity, HomeAdapter adapter, RecyclerView recyclerView, ProgressBar progressBar) {
        this.activity = activity;
        this.adapter = adapter;
        this.recyclerView = recyclerView;
        this.progressBar = progressBar;
    }

    public void getRandomMovieById(Long id) {

        Call<Movie> call = new BaseActivity().getTmdbApi()
                                .movie(id, TmdbApi.API_KEY, TmdbApi.DEFAULT_LANGUAGE);

        ImageView initialImage = activity.findViewById(R.id.iv_initial_activity);

        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {

                Movie movie = response.body();

                Glide.with(activity)
                        .load(movieImageUrlBuilder.buildBackdropUrl(movie.backdropPath))
                        .into(initialImage);
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {

                Toast.makeText(
                        activity,
                        "Filme não encontrado. Erro: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @SuppressLint("CheckResult")
    public void getMovies() {

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

                    //HomeAdapter adapter = new HomeAdapter(movies);
                    recyclerView.setAdapter(adapter);

                    adapter.setOnItemClickListener(movie -> {
                        Intent intent = new Intent(activity, DetailsActivity.class);
                        intent.putExtra("movie", movie);
                        startActivity(intent);
                    });

                    progressBar.setVisibility(View.GONE);
                });
    }
}
