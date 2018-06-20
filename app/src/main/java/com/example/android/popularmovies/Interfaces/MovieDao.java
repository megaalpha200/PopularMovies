package com.example.android.popularmovies.Interfaces;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.android.popularmovies.Models.Movie;
import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movie ORDER BY movie_id")
    LiveData<List<Movie>> loadAllMovies();

    @Insert
    void insertMovie(Movie movie);

    @Delete
    void removeMovie(Movie movie);

    @Query("SELECT * FROM movie WHERE movie_id = :id")
    Movie loadMovieById(int id);
}
