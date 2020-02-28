package com.arctouch.codechallenge.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.arctouch.codechallenge.R;
import com.arctouch.codechallenge.Repository.MovieRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InitialActivity extends AppCompatActivity {

    private Button btn_initialActivity;

    private MovieRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        btn_initialActivity = findViewById(R.id.btn_initial_activity);

        repository = new MovieRepository(this);

        Long id = getRandomId();
        repository.getRandomMovieById(id);

        btnToHome();

    }

    private void btnToHome() {
        btn_initialActivity.setOnClickListener(view -> {
            Intent intent = new Intent(InitialActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }

    private Long getRandomId() {

        Random random = new Random();
        List<Integer> idMovies = new ArrayList<>();

        // Three id movies
        idMovies.add(496243);
        idMovies.add(419430);
        idMovies.add(38700);

        int i = random.nextInt(idMovies.size());

        return Long.valueOf(idMovies.get(i));
    }
}
