package com.example.android.popularmovies;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.Interfaces.MoviePosterOnClickListener;
import com.example.android.popularmovies.Models.Movie;
import com.squareup.picasso.Picasso;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jose A. Alvarado on 5/23/2018.
 */

public class MoviePosterAdapter extends RecyclerView.Adapter<MoviePosterAdapter.PosterViewHolder> {
    private Context mContext;
    private List<Movie> movies;
    private MoviePosterOnClickListener posterOnClickListener;

    public MoviePosterAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public MoviePosterAdapter(Context context, List<Movie> movies, MoviePosterOnClickListener posterClickListener) {
        mContext = context;
        this.movies = movies;
        posterOnClickListener = posterClickListener;
    }

    @NonNull
    @Override
    public PosterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.poster_item_view, parent, false);

        return new PosterViewHolder(view, posterOnClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PosterViewHolder holder, int position) {
        Picasso.with(mContext).cancelRequest(holder.posterImageView);
        holder.bind(movies.get(position));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public void setPosterOnClickListener(MoviePosterOnClickListener posterOnClickListener) {
        this.posterOnClickListener = posterOnClickListener;
    }

    class PosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.movie_poster_image_view) ImageView posterImageView;
        MoviePosterOnClickListener mListener;

        PosterViewHolder(View itemView, MoviePosterOnClickListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mListener = listener;

            posterImageView.setOnClickListener(this);
        }

        void bind(Movie movie) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                Picasso.with(mContext)
                        .load(movie.getMoviePosterUri())
                        .placeholder(R.drawable.ic_local_movies_black_24dp)
                        .into(posterImageView);
            }
            else {
                Picasso.with(mContext)
                        .load(movie.getMoviePosterUri())
                        .into(posterImageView);
            }
        }

        @Override
        public void onClick(View view) {
            mListener.OnClick(view, getAdapterPosition());
        }
    }
}
