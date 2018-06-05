package com.example.android.popularmovies.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.example.android.popularmovies.R;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Jose A. Alvarado on 5/24/2018.
 */

public class NetworkUtils {

    private final static String TMDB_API_KEY = ""; //TODO Add API KEY HERE
    private final static String POPULAR_RETRIEVAL_URL = "http://api.themoviedb.org/3/movie/popular?api_key=" + TMDB_API_KEY;
    private final static String HIGH_RATED_RETRIEVAL_URL = "http://api.themoviedb.org/3/movie/top_rated?api_key=" + TMDB_API_KEY;

    private final static String BASE_TMDB_URL = "http://image.tmdb.org/t/p/w185";

    public static boolean isOnline = true;
    private static NetworkBroadcastReceiver networkBroadcastRecieverListener;
    private static BroadcastReceiver networkBroadcastReceiver;

    public static JSONObject retrieveMovieData(String sortChoice, Context context) throws IOException {

        if(sortChoice.equals(context.getString(R.string.popular_sort_pref_val))) {
            return getResponseFromHttpUrl(new URL(POPULAR_RETRIEVAL_URL));
        }
        else if(sortChoice.equals(context.getString(R.string.high_rating_sort_pref_val))) {
            return getResponseFromHttpUrl(new URL(HIGH_RATED_RETRIEVAL_URL));
        }
        else {
            return getResponseFromHttpUrl(new URL(POPULAR_RETRIEVAL_URL));
        }
    }

    public static Uri buildMovieImageURL(String imgFilePath) {
        Uri.Builder uriBuilder = Uri.parse(BASE_TMDB_URL).buildUpon()
                .appendPath(imgFilePath);

        return uriBuilder.build();
    }

    public static JSONObject getResponseFromHttpUrl(URL url) throws IOException {
        OkHttpClient client = new OkHttpClient();

        try {
            Request request = new Request.Builder()
                    .url(url)
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
        networkBroadcastRecieverListener = listener;
    }

    public static void registerNetworkBroadcastReceiver(Context context) {
        networkBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Source: https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                 isOnline = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                 if (networkBroadcastRecieverListener != null) {
                     networkBroadcastRecieverListener.onNetworkStatusChanged(isOnline);
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
