package com.example.android.popularmovies;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.Interfaces.MoviePosterOnClickListener;
import com.example.android.popularmovies.Models.MainViewModel;
import com.example.android.popularmovies.Models.Movie;
import com.example.android.popularmovies.Utils.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends NetworkAwareActivity implements SharedPreferences.OnSharedPreferenceChangeListener, MoviePosterOnClickListener {

    private List<Movie> retrievedMovieData;

    @BindView(R.id.no_connection_text_view) TextView noNetworkConnectionTextView;
    @BindView(R.id.movie_poster_recycler_view) RecyclerView moviePosterRecyclerView;
    @BindView(R.id.loading_progress_bar) ProgressBar loadingProgressBar;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setUpSharedPreferences(PreferenceManager.getDefaultSharedPreferences(this));

        super.onCreate(savedInstanceState);
    }

    private void setUpSharedPreferences(SharedPreferences preferences) {
        sharedPreferences = preferences;
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void loadUI(Bundle savedInstanceState) {
        noNetworkConnectionTextView.setVisibility(View.INVISIBLE);
        loadingProgressBar.setVisibility(View.VISIBLE);

        String prefStr = sharedPreferences.getString(getString(R.string.sort_preference_key), getResources().getString(R.string.popular_sort_pref_val));


        if (prefStr.equals(getString(R.string.favorites_sort_pref_val))) {

            final MainViewModel mainViewModel = new MainViewModel(getApplication());

            mainViewModel.getMovies().observe(this, new Observer<List<Movie>>() {
                @Override
                public void onChanged(@Nullable List<Movie> movies) {
                    mainViewModel.getMovies().removeObserver(this);
                    retrievedMovieData = movies;
                    setUpRecyclerView(movies);
                }
            });
        }
        else if (savedInstanceState != null && savedInstanceState.getParcelableArrayList(getString(R.string.movie_data_key)) != null) {
            List<Movie> movies = savedInstanceState.getParcelableArrayList(getString(R.string.movie_data_key));
            setUpRecyclerView(movies);
        }
        else if (retrievedMovieData == null) {
            MoviesRetrievalTask moviesRetrievalTask  = new MoviesRetrievalTask();
            moviesRetrievalTask.execute(prefStr);
        }
        else {
            loadingProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void loadNoConnectionUI() {
        if (moviePosterRecyclerView.getAdapter() == null || moviePosterRecyclerView.getAdapter().getItemCount() == 0) {
            noNetworkConnectionTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(getString(R.string.movie_data_key), (ArrayList<Movie>) retrievedMovieData);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @SuppressLint("StaticFieldLeak")
    class MoviesRetrievalTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject popularMoviesObj = null;

            try {
                popularMoviesObj =  NetworkUtils.retrieveMovieData(strings[0], MainActivity.this);
            }
            catch (Exception e){
                e.printStackTrace();
                loadingProgressBar.setVisibility(View.INVISIBLE);
            }

            return popularMoviesObj;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                JSONArray resultsArr = jsonObject.getJSONArray(getString(R.string.movie_data_results_key));
                ArrayList<Movie> movieData = new ArrayList<>();

                for(int i = 0; i < resultsArr.length(); i++) {
                    JSONObject currentMovieData = (JSONObject) resultsArr.get(i);
                    Uri currMoviePosterPath = NetworkUtils.buildMovieImageURL(currentMovieData.getString(getString(R.string.movie_poster_url_key)).substring(1));

                    Gson gson = new GsonBuilder().create();
                    Movie currentMovieDetails = gson.fromJson(String.valueOf(currentMovieData), Movie.class);
                    currentMovieDetails.setMoviePosterUri(currMoviePosterPath.toString());

                    movieData.add(currentMovieDetails);
                    setUpRecyclerView(movieData);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                loadingProgressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setUpRecyclerView(List<Movie> movieData) {
        retrievedMovieData = movieData;

        GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 2);
        MoviePosterAdapter mAdapter = new MoviePosterAdapter(MainActivity.this);
        mAdapter.setMovies(movieData);
        mAdapter.setPosterOnClickListener(MainActivity.this);

        moviePosterRecyclerView.setLayoutManager(layoutManager);
        moviePosterRecyclerView.setHasFixedSize(true);

        moviePosterRecyclerView.setAdapter(mAdapter);

        noNetworkConnectionTextView.setVisibility(View.INVISIBLE);
        loadingProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent intent;

        if (id == R.id.action_settings) {
            intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //MoviesRetrievalTask moviesRetrievalTask  = new MoviesRetrievalTask();
        //moviesRetrievalTask.execute(sharedPreferences.getString(getString(R.string.sort_preference_key), getResources().getString(R.string.popular_sort_pref_val)));

        retrievedMovieData = null;
        //loadUI(null);
    }

    @Override
    public void OnClick(View view, int position) {
        Movie currentMovieData = retrievedMovieData.get(position);

        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.movie_data_key), currentMovieData);

        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }
}
