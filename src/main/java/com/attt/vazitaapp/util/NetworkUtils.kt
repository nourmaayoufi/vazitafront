package com.attt.vazitaapp.util


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.attt.vazitaapp.VazitaApp
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * Utility class for network operations
 */
object NetworkUtils {

    /**
     * Check if the device has an active internet connection
     */
    fun isNetworkAvailable(): Boolean {
        val context = VazitaApp.instance
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            networkInfo.isConnected
        }
    }

    /**
     * Get user-friendly error message from exceptions
     */
    fun getErrorMessage(throwable: Throwable): String {
        return when (throwable) {
            is HttpException -> {
                when (throwable.code()) {
                    401 -> "Unauthorized. Please log in again."
                    403 -> "Access denied. You don't have permission to perform this action."
                    404 -> "Resource not found."
                    500 -> "Server error. Please try again later."
                    else -> "Network error: ${throwable.message()}"
                }
            }
            is SocketTimeoutException -> "Connection timed out. Please check your internet connection."
            is IOException -> "Network error. Please check your internet connection."
            else -> throwable.message ?: "Unknown error occurred"
        }
    }
}