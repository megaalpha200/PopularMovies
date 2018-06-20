package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.Interfaces.MovieTrailerOnClickListener;
import com.example.android.popularmovies.Models.MovieTrailer;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.MovieTrailerViewHolder> {

    private Context mContext;
    private List<MovieTrailer> movieTrailerData;
    private MovieTrailerOnClickListener movieTrailerOnClickListener;

    public MovieTrailerAdapter(Context mContext, List<MovieTrailer> movieTrailerData, MovieTrailerOnClickListener movieTrailerOnClickListener) {
        this.mContext = mContext;
        this.movieTrailerData = movieTrailerData;
        this.movieTrailerOnClickListener = movieTrailerOnClickListener;
    }

    @NonNull
    @Override
    public MovieTrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.trailer_item_view, parent, false);

        return new MovieTrailerViewHolder(view, movieTrailerOnClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieTrailerViewHolder holder, int position) {
        holder.bind(movieTrailerData.get(position));
    }

    @Override
    public int getItemCount() {
        return movieTrailerData.size();
    }

    public List<MovieTrailer> getMovieTrailerData() {
        return movieTrailerData;
    }

    public void setMovieTrailerData(List<MovieTrailer> movieTrailerData) {
        this.movieTrailerData = movieTrailerData;
    }

    public MovieTrailerOnClickListener getMovieTrailerOnClickListener() {
        return movieTrailerOnClickListener;
    }

    public void setMovieTrailerOnClickListener(MovieTrailerOnClickListener movieTrailerOnClickListener) {
        this.movieTrailerOnClickListener = movieTrailerOnClickListener;
    }

    class MovieTrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.play_btn_image_view) ImageView PlayBtnImageView;
        @BindView(R.id.trailer_title_text_view) TextView TrailerTitleTextView;
        @BindView(R.id.share_btn_image_view) ImageView ShareTrailerLinkImageView;
        MovieTrailerOnClickListener mListener;

        MovieTrailerViewHolder(View view, MovieTrailerOnClickListener listener) {
            super(view);
            ButterKnife.bind(this, view);

            mListener = listener;
            PlayBtnImageView.setOnClickListener(this);
            ShareTrailerLinkImageView.setOnClickListener(this);
        }

        void bind(MovieTrailer trailerData){
            TrailerTitleTextView.setText(trailerData.getTrailerTitle());
        }

        @Override
        public void onClick(View view) {
            mListener.OnClick(view, getAdapterPosition());
        }
    }
}
