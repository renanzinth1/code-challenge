package com.arctouch.rzt.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.arctouch.rzt.R;
import com.arctouch.rzt.api.TmdbApi;
import com.arctouch.rzt.base.BaseActivity;
import com.arctouch.rzt.data.Cache;
import com.arctouch.rzt.home.HomeActivity;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        new BaseActivity().getTmdbApi()
                .genres(TmdbApi.API_KEY, TmdbApi.DEFAULT_LANGUAGE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    Cache.setGenres(response.genres);
                    startActivity(new Intent(this, HomeActivity.class));
                    finish();
                });
    }
}
