package com.example.learnit;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkUtils {
    private static final String TAG = "NetworkUtils";

    /**
     * Check if the device has an active internet connection
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) 
            context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        if (connectivityManager == null) {
            return false;
        }
        
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
            return false;
        }
        
        return activeNetworkInfo.isConnected();
    }

    /**
     * Check if the device has WiFi connection
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) 
            context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        if (connectivityManager == null) {
            return false;
        }
        
        NetworkInfo wifiNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork == null) {
            return false;
        }
        
        return wifiNetwork.isConnected();
    }

    /**
     * Check if the device has mobile data connection
     */
    public static boolean isMobileDataConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) 
            context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        if (connectivityManager == null) {
            return false;
        }
        
        NetworkInfo mobileNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork == null) {
            return false;
        }
        
        return mobileNetwork.isConnected();
    }

    /**
     * Get a user-friendly network status message
     */
    public static String getNetworkStatusMessage(Context context) {
        boolean networkAvailable = isNetworkAvailable(context);
        if (networkAvailable == false) {
            return "No Internet Connection";
        }
        
        boolean wifiConnected = isWifiConnected(context);
        if (wifiConnected == true) {
            return "Connected to WiFi";
        }
        
        boolean mobileConnected = isMobileDataConnected(context);
        if (mobileConnected == true) {
            return "Connected to Mobile Data";
        }
        
        return "Connected to Network";
    }

    /**
     * Log network status for debugging
     */
    public static void logNetworkStatus(Context context) {
        String statusMessage = getNetworkStatusMessage(context);
        Log.d(TAG, "Network Status: " + statusMessage);
        
        boolean wifiConnected = isWifiConnected(context);
        Log.d(TAG, "WiFi Connected: " + wifiConnected);
        
        boolean mobileConnected = isMobileDataConnected(context);
        Log.d(TAG, "Mobile Data Connected: " + mobileConnected);
        
        boolean networkAvailable = isNetworkAvailable(context);
        Log.d(TAG, "Network Available: " + networkAvailable);
    }
}
