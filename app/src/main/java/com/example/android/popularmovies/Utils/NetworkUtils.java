package com.example.android.popularmovies.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import com.example.android.popularmovies.R;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Jose A. Alvarado on 5/24/2018.
 */

public class NetworkUtils {

    private final static String TMDB_API_KEY = ""; //TODO Add API KEY HERE
    private final static Uri BASE_TMDB_URI = Uri.parse("http://api.themoviedb.org/3/movie");
    private final static String POPULAR_RETRIEVAL_URL_PATH = "popular";
    private final static String HIGH_RATED_RETRIEVAL_URL_PATH = "top_rated";
    private final static String TRAILERS_RETRIEVAL_URL_PATH = "videos";
    private final static String REVIEWS_RETRIEVAL_URL_PATH = "reviews";
    private final static String API_KEY_QUERY = "api_key";

    private final static String BASE_TMDB_POSTER_URL = "http://image.tmdb.org/t/p/w185";

    private final static Uri YOUTUBE_BASE_URI = Uri.parse("https://www.youtube.com/watch");

    public static boolean isOnline = true;
    private static NetworkBroadcastReceiver networkBroadcastReceiverListener;
    private static BroadcastReceiver networkBroadcastReceiver;

    public static JSONObject retrieveMovieData(String selectionChoice, Context context) {

        if(selectionChoice.equals(context.getString(R.string.popular_sort_pref_val))) {
            return getResponseFromHttpUrl(POPULAR_RETRIEVAL_URL_PATH);
        }
        else if(selectionChoice.equals(context.getString(R.string.high_rating_sort_pref_val))) {
            return getResponseFromHttpUrl(HIGH_RATED_RETRIEVAL_URL_PATH);
        }
        else
            return new JSONObject();
    }

    public static JSONObject retrieveMovieData(String selectionChoice, String id, Context context) {
        if(selectionChoice.equals(context.getString(R.string.movie_trailer_data_key))) {
            return getResponseFromHttpUrl(TRAILERS_RETRIEVAL_URL_PATH, id);
        }
        else if(selectionChoice.equals(context.getString(R.string.movie_review_data_key))) {
            return getResponseFromHttpUrl(REVIEWS_RETRIEVAL_URL_PATH, id);
        }
        else
            return new JSONObject();
    }

    public static Uri buildMovieImageURL(String imgFilePath) {
        Uri.Builder uriBuilder = Uri.parse(BASE_TMDB_POSTER_URL).buildUpon()
                .appendPath(imgFilePath);

        return uriBuilder.build();
    }

    public static Uri buildYoutubeTrailerUri(String trailerKey) {
        Uri.Builder builder = YOUTUBE_BASE_URI.buildUpon()
                .appendQueryParameter("v", trailerKey);

        return builder.build();
    }

    private static JSONObject getResponseFromHttpUrl(String urlPath) {
        Uri.Builder builder = BASE_TMDB_URI.buildUpon()
                .appendPath(urlPath)
                .appendQueryParameter(API_KEY_QUERY, TMDB_API_KEY);

        Uri dataRetrievalUri = builder.build();

        OkHttpClient client = new OkHttpClient();

        try {
            Request request = new Request.Builder()
                    .url(dataRetrievalUri.toString())
                    .build();

            Response response = client.newCall(request).execute();

            return new JSONObject(response.body().string());
        }
        catch (Exception ex) {
            return null;
        }
    }

    private static JSONObject getResponseFromHttpUrl(String urlPath, String id) {
        Uri.Builder builder = BASE_TMDB_URI.buildUpon()
                .appendPath(id)
                .appendPath(urlPath)
                .appendQueryParameter(API_KEY_QUERY, TMDB_API_KEY);

        Uri dataRetrievalUri = builder.build();

        OkHttpClient client = new OkHttpClient();

        try {
            Request request = new Request.Builder()
                    .url(dataRetrievalUri.toString())
                    .build();

            Response response = client.newCall(request).execute();

            return new JSONObject(response.body().string());
        }
        catch (Exception ex) {
            return null;
        }
    }

    public static BroadcastReceiver getNetworkBroadcastReceiver() {
        return networkBroadcastReceiver;
    }

    public static void setNetworkBroadcastReceiverListener(NetworkBroadcastReceiver listener) {
        networkBroadcastReceiverListener = listener;
    }

    public static void registerNetworkBroadcastReceiver(Context context) {
        networkBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Source: https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                 isOnline = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                 if (networkBroadcastReceiverListener != null) {
                     networkBroadcastReceiverListener.onNetworkStatusChanged(isOnline);
                 }
            }
        };

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(networkBroadcastReceiver, intentFilter);
    }

    public interface NetworkBroadcastReceiver {
        void onNetworkStatusChanged(boolean isConnected);
    }
}
