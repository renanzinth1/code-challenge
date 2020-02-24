package com.arctouch.rzt.helper;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arctouch.rzt.R;
import com.arctouch.rzt.api.TmdbApi;
import com.arctouch.rzt.base.BaseActivity;
import com.arctouch.rzt.home.DetailsActivity;
import com.arctouch.rzt.model.Movie;
import com.arctouch.rzt.util.MovieImageUrlBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieHelper {

    private Context context;

    private final ImageView movieBackdrop;
    private final TextView movieName;
    private final TextView movieData;
    private final ImageView moviePoster;
    private final TextView movieGenre;
    private final TextView movieOverview;


    public MovieHelper(Context context, DetailsActivity activity) {
        movieBackdrop = activity.findViewById(R.id.movie_backdrop_detail);
        movieName = activity.findViewById(R.id.movie_name_detail);
        movieData = activity.findViewById(R.id.movie_date_detail);
        moviePoster = activity.findViewById(R.id.movie_poster_detail);
        movieGenre = activity.findViewById(R.id.movie_genre_detail);
        movieOverview = activity.findViewById(R.id.movie_overview_detail);
        this.context = context;
    }

    public void fillMovieDetails(Movie movie) {

        long id = movie.id;

        Call<Movie> call = new BaseActivity().getTmdbApi()
                                .movie(id, TmdbApi.API_KEY, TmdbApi.DEFAULT_LANGUAGE);

        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {

                Movie movie = response.body();

                if (movie != null)
                    fetchedMovie(movie);

            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {

                Toast.makeText(
                        context,
                        "Filme não encontrado. Erro:" + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    private void fetchedMovie(Movie movie) {

        final MovieImageUrlBuilder movieImageUrlBuilder = new MovieImageUrlBuilder();

        String backdropPath = movie.backdropPath;
        String posterPath = movie.posterPath;

        if(TextUtils.isEmpty(backdropPath) == false) {
            loadImage(movieBackdrop, backdropPath, movieImageUrlBuilder);
        }

        if(TextUtils.isEmpty(posterPath) == false){
            loadImage(moviePoster, posterPath, movieImageUrlBuilder);
        }

        movieName.setText(movie.title);
        movieData.setText(movie.releaseDate);
        movieGenre.setText(TextUtils.join(", ", movie.genres));
        movieOverview.setText(movie.overview);
    }

    private void loadImage(ImageView imageView,
                           String imagePath,
                           MovieImageUrlBuilder movieImageUrlBuilder) {

        RequestManager requestManager = Glide.with(context);

        if(imageView == movieBackdrop)
            requestManager
                    .load(movieImageUrlBuilder.buildBackdropUrl(imagePath))
                    .into(movieBackdrop);

        if(imageView == moviePoster)
            requestManager
                    .load(movieImageUrlBuilder.buildPosterUrl(imagePath))
                    .into(moviePoster);
    }
}
