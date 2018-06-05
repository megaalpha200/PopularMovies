package com.example.android.popularmovies;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.Interfaces.MoviePosterOnClickListener;
import com.example.android.popularmovies.Models.Movie;
import com.example.android.popularmovies.Utils.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, MoviePosterOnClickListener, NetworkUtils.NetworkBroadcastReceiver {

    static ArrayList<Movie> retrievedMovieData;

    private TextView noNetworkConnectionTextView;
    private RecyclerView moviePosterRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noNetworkConnectionTextView = findViewById(R.id.no_connection_text_view);
        moviePosterRecyclerView = findViewById(R.id.movie_poster_recycler_view);

        checkConnection(NetworkUtils.isOnline);
    }

    private void loadUI() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        MoviesRetrievalTask moviesRetrievalTask  = new MoviesRetrievalTask();
        moviesRetrievalTask.execute(sharedPreferences.getString(getString(R.string.sort_preference_key), getResources().getString(R.string.popular_sort_pref_val)));
    }

    private void checkConnection(boolean isConnected) {
        loadUI();

        if (!isConnected) {

            if (moviePosterRecyclerView.getAdapter() == null || moviePosterRecyclerView.getAdapter().getItemCount() == 0) {
                noNetworkConnectionTextView.setVisibility(View.VISIBLE);
            }

            Toast.makeText(this, getString(R.string.no_network_connection_str), Toast.LENGTH_SHORT).show();
        }
        else {
            noNetworkConnectionTextView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onNetworkStatusChanged(boolean isConnected) {
        checkConnection(isConnected);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkUtils.setNetworkBroadcastReceiverListener(this);
        NetworkUtils.registerNetworkBroadcastReceiver(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(NetworkUtils.getNetworkBroadcastReceiver());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @SuppressLint("StaticFieldLeak")
    public class MoviesRetrievalTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject popularMoviesObj = null;

            try {
                popularMoviesObj =  NetworkUtils.retrieveMovieData(strings[0], MainActivity.this);
            }
            catch (Exception e){
                   e.printStackTrace();
            }

            return popularMoviesObj;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                JSONArray resultsArr = jsonObject.getJSONArray("results");
                ArrayList<Movie> movieData = new ArrayList<>();
                ArrayList<Uri> moviePosterUris = new ArrayList<>();

                retrievedMovieData = movieData;

                for(int i = 0; i < resultsArr.length(); i++) {
                    JSONObject currentMovieData = (JSONObject) resultsArr.get(i);
                    Uri currMoviePosterPath = NetworkUtils.buildMovieImageURL(currentMovieData.getString(getString(R.string.movie_poster_url_key)).substring(1));

                    Gson gson = new GsonBuilder().create();
                    Movie currentMovieDetails = gson.fromJson(String.valueOf(currentMovieData), Movie.class);

                    movieData.add(currentMovieDetails);
                    moviePosterUris.add(currMoviePosterPath);
                }

                GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 2);
                final MoviePosterAdapter mAdapter = new MoviePosterAdapter(MainActivity.this, moviePosterUris, MainActivity.this);

                moviePosterRecyclerView.setLayoutManager(layoutManager);
                moviePosterRecyclerView.setHasFixedSize(true);

                moviePosterRecyclerView.setAdapter(mAdapter);

                noNetworkConnectionTextView.setVisibility(View.INVISIBLE);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        MoviesRetrievalTask moviesRetrievalTask  = new MoviesRetrievalTask();
        moviesRetrievalTask.execute(sharedPreferences.getString(getString(R.string.sort_preference_key), getResources().getString(R.string.popular_sort_pref_val)));
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
