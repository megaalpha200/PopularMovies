package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.Interfaces.MovieTrailerOnClickListener;
import com.example.android.popularmovies.Models.MovieReview;
import com.example.android.popularmovies.Models.MovieTrailer;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.MovieReviewViewHolder> {

    private Context mContext;
    private List<MovieReview> movieReviewData;

    public MovieReviewAdapter(Context mContext, List<MovieReview> movieReviewData) {
        this.mContext = mContext;
        this.movieReviewData = movieReviewData;
    }

    @NonNull
    @Override
    public MovieReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.review_item_view, parent, false);

        return new MovieReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieReviewViewHolder holder, int position) {
        holder.bind(movieReviewData.get(position));
    }

    @Override
    public int getItemCount() {
        return movieReviewData.size();
    }

    public List<MovieReview> getMovieReviewData() {
        return movieReviewData;
    }

    public void setMovieReviewData(List<MovieReview> movieReviewData) {
        this.movieReviewData = movieReviewData;
    }

    class MovieReviewViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.review_author_text_view) TextView ReviewAuthorTextView;
        @BindView(R.id.review_content_text_view) TextView ReviewContentTextView;

        MovieReviewViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void bind(MovieReview reviewData){
            ReviewAuthorTextView.setText(reviewData.getAuthor());
            ReviewContentTextView.setText(reviewData.getContent());
        }
    }
}
