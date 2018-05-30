package com.example.android.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.Interfaces.MoviePosterOnClickListener;
import com.squareup.picasso.Picasso;


import java.util.List;

/**
 * Created by Jose A. Alvarado on 5/23/2018.
 */

public class MoviePosterAdapter extends RecyclerView.Adapter<MoviePosterAdapter.PosterViewHolder> {
    private Context mContext;
    private List<Uri> moviePosterUris;
    private MoviePosterOnClickListener posterOnClickListener;

    public MoviePosterAdapter(Context context, List<Uri> posterUris, MoviePosterOnClickListener posterClickListener) {
        mContext = context;
        moviePosterUris = posterUris;
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
        holder.bind(moviePosterUris.get(position));
    }

    @Override
    public int getItemCount() {
        return moviePosterUris.size();
    }

    class PosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView posterImageView;
        MoviePosterOnClickListener mListener;

        PosterViewHolder(View itemView, MoviePosterOnClickListener listener) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.movie_poster_image_view);
            mListener = listener;

            posterImageView.setOnClickListener(this);
        }

        void bind(Uri imageUri) {
            Picasso.with(mContext)
                    .load(imageUri)
                    .placeholder(R.drawable.ic_local_movies_black_24dp)
                    .into(posterImageView);
        }

        @Override
        public void onClick(View view) {
            mListener.OnClick(view, getAdapterPosition());
        }
    }
}
