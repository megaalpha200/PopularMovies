package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.Models.Movie;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jose A. Alvarado on 5/28/2018.
 */

public class MovieDetailsActivity extends AppCompatActivity {

    @BindView(R.id.movie_poster_image_view) ImageView posterImageView;
    @BindView(R.id.user_rating_text_view) TextView userRatingTextView;
    @BindView(R.id.release_date_text_view) TextView releaseDateTextView;
    @BindView(R.id.plot_synopsis_text_view) TextView plotSynopsisTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();

        try {
            Movie movieInfo = getIntent().getParcelableExtra(getString(R.string.movie_data_key));

            actionBar.setTitle(movieInfo.getMovieTitle());

            Picasso.with(this)
                    .load(movieInfo.getMoviePosterUri())
                    .placeholder(R.drawable.ic_local_movies_black_24dp)
                    .into(posterImageView);

            userRatingTextView.append("\n\t\t" + Double.toString(movieInfo.getUserRating()) + "/10");
            releaseDateTextView.append("\n\t\t" + movieInfo.getReleaseDate());
            plotSynopsisTextView.append("\n\t\t" + movieInfo.getMoviePlotSynopsis());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
