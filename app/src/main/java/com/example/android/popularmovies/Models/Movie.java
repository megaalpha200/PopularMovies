package com.example.android.popularmovies.Models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.popularmovies.Utils.NetworkUtils;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jose A. Alvarado on 5/28/2018.
 */

public class Movie implements Parcelable {
    @SerializedName("original_title") private String movieTitle;
    @SerializedName("overview") private String moviePlotSynopsis;
    @SerializedName("vote_average") private double userRating;
    @SerializedName("release_date") private String releaseDate;
    @SerializedName("poster_path") private String moviePosterUri;

    public Movie() {

    }

    public Movie(String movieTitle, String moviePlotSynopsis, double userRating, String releaseDate, String moviePosterUri) {
        this.movieTitle = movieTitle;
        this.moviePlotSynopsis = moviePlotSynopsis;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.moviePosterUri = moviePosterUri;
    }

    protected Movie(Parcel in) {
        movieTitle = in.readString();
        moviePlotSynopsis = in.readString();
        userRating = in.readDouble();
        releaseDate = in.readString();
        moviePosterUri = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movieTitle);
        dest.writeString(moviePlotSynopsis);
        dest.writeDouble(userRating);
        dest.writeString(releaseDate);
        dest.writeString(moviePosterUri);
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

    public Uri getMoviePosterUri() {
        return NetworkUtils.buildMovieImageURL(moviePosterUri.substring(1));
    }

    public void setMoviePosterUri(String moviePosterUri) {
        this.moviePosterUri = moviePosterUri;
    }

}
