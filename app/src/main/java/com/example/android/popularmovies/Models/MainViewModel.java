package com.example.android.popularmovies.Models;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import com.example.android.popularmovies.database.AppDatabase;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private LiveData<List<Movie>> movies;

    public MainViewModel(@NonNull Application application) {
        super(application);

        AppDatabase database = AppDatabase.getInstance(application);
        movies = database.movieDao().loadAllMovies();
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }
}
