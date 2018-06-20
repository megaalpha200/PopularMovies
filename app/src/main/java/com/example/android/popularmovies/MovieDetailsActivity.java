package com.example.android.popularmovies;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.Interfaces.MovieTrailerOnClickListener;
import com.example.android.popularmovies.Models.Movie;
import com.example.android.popularmovies.Models.MovieReview;
import com.example.android.popularmovies.Models.MovieTrailer;
import com.example.android.popularmovies.Utils.AppExecutors;
import com.example.android.popularmovies.Utils.NetworkUtils;
import com.example.android.popularmovies.database.AppDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jose A. Alvarado on 5/28/2018.
 */

public class MovieDetailsActivity extends NetworkAwareActivity implements MovieTrailerOnClickListener {

    @BindView(R.id.movie_poster_image_view) ImageView posterImageView;
    @BindView(R.id.user_rating_text_view) TextView userRatingTextView;
    @BindView(R.id.release_date_text_view) TextView releaseDateTextView;
    @BindView(R.id.plot_synopsis_text_view) TextView plotSynopsisTextView;
    @BindView(R.id.movie_trailers_recycler_view) RecyclerView movieTrailersRecyclerView;
    @BindView(R.id.movie_reviews_recycler_view) RecyclerView movieReviewsRecyclerView;

    private Movie movieInfo;
    private MenuItem FavMovieMenuItem;

    private boolean isFavorite = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void loadUI(Bundle savedInstanceState) {
        ActionBar actionBar = getSupportActionBar();

        try {
            if (savedInstanceState == null) {
                movieInfo = getIntent().getParcelableExtra(getString(R.string.movie_data_key));

                MovieTrailersRetrievalTask movieTrailersRetrievalTask = new MovieTrailersRetrievalTask();
                MovieReviewsRetrievalTask movieReviewsRetrievalTask = new MovieReviewsRetrievalTask();

                movieTrailersRetrievalTask.execute(movieInfo.getMovieID());
                movieReviewsRetrievalTask.execute(movieInfo.getMovieID());
            }
            else if (savedInstanceState.getParcelable(getString(R.string.movie_data_key)) != null) {
                movieInfo = savedInstanceState.getParcelable(getString(R.string.movie_data_key));

                setUpMovieTrailersRecyclerView(movieInfo.getMovieTrailers());
                setUpMovieReviewsRecyclerView(movieInfo.getMovieReviews());
            }

            actionBar.setTitle(movieInfo.getMovieTitle());

            Picasso.with(this)
                    .load(movieInfo.getMoviePosterUri())
                    .placeholder(R.drawable.ic_local_movies_black_24dp)
                    .into(posterImageView);

            userRatingTextView.setText(getString(R.string.user_rating_placeholder, Double.toString(movieInfo.getUserRating())));
            releaseDateTextView.setText(movieInfo.getReleaseDate());
            plotSynopsisTextView.setText(movieInfo.getMoviePlotSynopsis());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void loadNoConnectionUI() {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getString(R.string.movie_data_key), movieInfo);
    }

    @SuppressLint("StaticFieldLeak")
    class MovieTrailersRetrievalTask extends AsyncTask<Integer, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Integer... movieIDs) {
            int movieID = movieIDs[0];

            return NetworkUtils.retrieveMovieData(getString(R.string.movie_trailer_data_key), String.valueOf(movieID), MovieDetailsActivity.this);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                JSONArray movieTrailersResultsArr = jsonObject.getJSONArray(getString(R.string.movie_data_results_key));
                ArrayList<MovieTrailer> trailers = new ArrayList<>();

                for (int i = 0; i < movieTrailersResultsArr.length(); i++) {
                    JSONObject currTrailerData = (JSONObject) movieTrailersResultsArr.get(i);

                    Gson gson = new GsonBuilder().create();
                    MovieTrailer trailer = gson.fromJson(String.valueOf(currTrailerData), MovieTrailer.class);

                    trailers.add(trailer);
                }

                setUpMovieTrailersRecyclerView(trailers);

            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    class MovieReviewsRetrievalTask extends AsyncTask<Integer, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Integer... movieIDs) {
            int movieID = movieIDs[0];

            return NetworkUtils.retrieveMovieData(getString(R.string.movie_review_data_key), String.valueOf(movieID), MovieDetailsActivity.this);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                JSONArray movieReviewsResultsArr = jsonObject.getJSONArray(getString(R.string.movie_data_results_key));
                ArrayList<MovieReview> reviews = new ArrayList<>();

                for (int i = 0; i < movieReviewsResultsArr.length(); i++) {
                    JSONObject currReviewData = (JSONObject) movieReviewsResultsArr.get(i);

                    Gson gson = new GsonBuilder().create();
                    MovieReview review = gson.fromJson(String.valueOf(currReviewData), MovieReview.class);

                    reviews.add(review);
                }

                setUpMovieReviewsRecyclerView(reviews);

            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void setUpMovieTrailersRecyclerView(List<MovieTrailer> trailers) {
        movieInfo.setMovieTrailers(trailers);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MovieDetailsActivity.this);
        MovieTrailerAdapter movieTrailerAdapter = new MovieTrailerAdapter(MovieDetailsActivity.this, trailers, MovieDetailsActivity.this);

        movieTrailersRecyclerView.setLayoutManager(linearLayoutManager);
        movieTrailersRecyclerView.setHasFixedSize(true);
        movieTrailersRecyclerView.addItemDecoration(new DividerItemDecoration(MovieDetailsActivity.this, DividerItemDecoration.VERTICAL));
        movieTrailersRecyclerView.setAdapter(movieTrailerAdapter);
    }

    private void setUpMovieReviewsRecyclerView(List<MovieReview> reviews) {
        movieInfo.setMovieReviews(reviews);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MovieDetailsActivity.this);
        MovieReviewAdapter movieReviewAdapter = new MovieReviewAdapter(MovieDetailsActivity.this, reviews);

        movieReviewsRecyclerView.setLayoutManager(linearLayoutManager);
        movieReviewsRecyclerView.setHasFixedSize(true);
        movieReviewsRecyclerView.addItemDecoration(new DividerItemDecoration(MovieDetailsActivity.this, DividerItemDecoration.VERTICAL));
        movieReviewsRecyclerView.setAdapter(movieReviewAdapter);
    }

    private void toggleFavoriteStar(boolean isMarked, boolean showMessage) {
        String message;

        if (isMarked) {
            isFavorite = true;
            FavMovieMenuItem.setIcon(R.drawable.ic_star_yellow_24dp);
            message = getString(R.string.add_to_favorites_message);
        }
        else {
            isFavorite = false;
            FavMovieMenuItem.setIcon(R.drawable.ic_star_border_black_24dp);
            message = getString(R.string.remove_from_favorites_message);
        }

        if (showMessage)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_details, menu);

        FavMovieMenuItem = menu.findItem(R.id.action_add_to_favorites);

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Movie movie = AppDatabase.getInstance(MovieDetailsActivity.this).movieDao().loadMovieById(movieInfo.getMovieID());

                if (movie != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleFavoriteStar(true, false);
                        }
                    });
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_add_to_favorites) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    if (!isFavorite)
                        AppDatabase.getInstance(MovieDetailsActivity.this).movieDao().insertMovie(movieInfo);
                    else
                        AppDatabase.getInstance(MovieDetailsActivity.this).movieDao().removeMovie(movieInfo);


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isFavorite)
                                toggleFavoriteStar(false, true);
                            else
                                toggleFavoriteStar(true, true);
                        }
                    });
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnClick(View view, int position) {
        MovieTrailer trailer = movieInfo.getMovieTrailers().get(position);

        if (view.getId() == R.id.share_btn_image_view) {
            String mimeType="text/plain";

            ShareCompat.IntentBuilder
                    .from(this)
                    .setType(mimeType)
                    .setChooserTitle(getString(R.string.share_trailer_chooser_title, trailer.getTrailerTitle()))
                    .setText("Popular Movies App | " + trailer.getTrailerTitle() + " | " + NetworkUtils.buildYoutubeTrailerUri(trailer.getTrailerKey()).toString())
                    .startChooser();
        }
        else if (view.getId() == R.id.play_btn_image_view){
            Intent intent = new Intent(Intent.ACTION_VIEW, NetworkUtils.buildYoutubeTrailerUri(trailer.getTrailerKey()));

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }
}
