package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.android.popularmovies.Utils.NetworkUtils;

public abstract class NetworkAwareActivity extends AppCompatActivity implements NetworkUtils.NetworkBroadcastReceiver {

    private void checkConnection(boolean isConnected, Bundle savedInstanceState) {
        loadUI(savedInstanceState);

        if(!isConnected) {
            Toast.makeText(this, getString(R.string.no_network_connection_str), Toast.LENGTH_SHORT).show();
            loadNoConnectionUI();
        }
    }
    abstract protected void loadUI(Bundle savedInstanceState);
    abstract protected void loadNoConnectionUI();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkConnection(NetworkUtils.isOnline, savedInstanceState);
    }

    @Override
    public void onNetworkStatusChanged(boolean isConnected) {
        checkConnection(isConnected, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkUtils.setNetworkBroadcastReceiverListener(this);
        NetworkUtils.registerNetworkBroadcastReceiver(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(NetworkUtils.getNetworkBroadcastReceiver());
    }
}
