package com.example.android.popularmovies.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class MovieTrailer implements Parcelable{
    @SerializedName("name") private String trailerTitle;
    @SerializedName("key") private String trailerKey;

    public MovieTrailer(String trailerTitle, String trailerKey) {
        this.trailerTitle = trailerTitle;
        this.trailerKey = trailerKey;
    }

    public MovieTrailer(Parcel in) {
        trailerTitle = in.readString();
        trailerKey = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trailerTitle);
        dest.writeString(trailerKey);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MovieTrailer> CREATOR = new Creator<MovieTrailer>() {
        @Override
        public MovieTrailer createFromParcel(Parcel in) {
            return new MovieTrailer(in);
        }

        @Override
        public MovieTrailer[] newArray(int size) {
            return new MovieTrailer[size];
        }
    };

    public String getTrailerTitle() {
        return trailerTitle;
    }

    public void setTrailerTitle(String trailerTitle) {
        this.trailerTitle = trailerTitle;
    }

    public String getTrailerKey() {
        return trailerKey;
    }

    public void setTrailerKey(String trailerKey) {
        this.trailerKey = trailerKey;
    }
}
