package com.example.android.popularmovies.Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.popularmovies.Utils.NetworkUtils;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jose A. Alvarado on 5/28/2018.
 */

@Entity(tableName = "movie")
public class Movie implements Parcelable {
    @PrimaryKey @ColumnInfo(name = "movie_id") @SerializedName("id") private int movieID;
    @ColumnInfo(name = "movie_title") @SerializedName("original_title") private String movieTitle;
    @ColumnInfo(name = "movie_plot_synopsis") @SerializedName("overview") private String moviePlotSynopsis;
    @ColumnInfo(name = "user_rating")  @SerializedName("vote_average") private double userRating;
    @ColumnInfo(name = "release_date") @SerializedName("release_date") private String releaseDate;
    @ColumnInfo(name = "poster_path") @SerializedName("poster_path") private String moviePosterUri;

    @Ignore private List<MovieTrailer> movieTrailers;
    @Ignore private List<MovieReview> movieReviews;

    @Ignore
    public Movie() {

    }

    public Movie(int movieID, String movieTitle, String moviePlotSynopsis, double userRating, String releaseDate, String moviePosterUri) {
        this.movieID = movieID;
        this.movieTitle = movieTitle;
        this.moviePlotSynopsis = moviePlotSynopsis;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.moviePosterUri = moviePosterUri;
    }

    @Ignore
    protected Movie(Parcel in) {
        movieID = in.readInt();
        movieTitle = in.readString();
        moviePlotSynopsis = in.readString();
        userRating = in.readDouble();
        releaseDate = in.readString();
        moviePosterUri = in.readString();
        in.readList(movieTrailers, MovieTrailer.class.getClassLoader());
        in.readList(movieReviews, MovieReview.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movieID);
        dest.writeString(movieTitle);
        dest.writeString(moviePlotSynopsis);
        dest.writeDouble(userRating);
        dest.writeString(releaseDate);
        dest.writeString(moviePosterUri);
        dest.writeList(movieTrailers);
        dest.writeList(movieReviews);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public int getMovieID() {
        return movieID;
    }

    public void setMovieID(int movieID) {
        this.movieID = movieID;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMoviePlotSynopsis() {
        return moviePlotSynopsis;
    }

    public void setMoviePlotSynopsis(String moviePlotSynopsis) {
        this.moviePlotSynopsis = moviePlotSynopsis;
    }

    public double getUserRating() {
        return userRating;
    }

    public void setUserRating(double userRating) {
        this.userRating = userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getMoviePosterUri() {
        return moviePosterUri;
    }

    public void setMoviePosterUri(String moviePosterUri) {
        this.moviePosterUri = moviePosterUri;
    }

    public List<MovieTrailer> getMovieTrailers() {
        return movieTrailers;
    }

    public void setMovieTrailers(List<MovieTrailer> movieTrailers) {
        this.movieTrailers = movieTrailers;
    }

    public List<MovieReview> getMovieReviews() {
        return movieReviews;
    }

    public void setMovieReviews(List<MovieReview> movieReviews) {
        this.movieReviews = movieReviews;
    }
}
