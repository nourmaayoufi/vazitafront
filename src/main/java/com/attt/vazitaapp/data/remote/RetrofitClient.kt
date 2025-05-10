package com.attt.vazitaapp.data.remote



import android.content.Context
import com.attt.vazitaapp.data.local.TokenManager
import com.attt.vazitaapp.data.model.Center
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Provides Retrofit client instances for API communication.
 * Handles different configurations for central and local center databases.
 */
class RetrofitClient private constructor() {

    private var centralRetrofit: Retrofit? = null
    private var localRetrofit: Retrofit? = null
    private var currentCenter: Center? = null

    companion object {
        private const val CENTRAL_BASE_URL = "https://centrale-mobile-api.inspection.com/"
        private const val CONNECT_TIMEOUT = 30L
        private const val READ_TIMEOUT = 30L
        private const val WRITE_TIMEOUT = 30L

        @Volatile
        private var instance: RetrofitClient? = null

        fun getInstance(): RetrofitClient {
            return instance ?: synchronized(this) {
                instance ?: RetrofitClient().also { instance = it }
            }
        }
    }

    /**
     * Initializes the Retrofit clients with the application context.
     * Creates both central and authentication-only clients.
     *
     * @param context Application context for accessing SharedPreferences
     * @param tokenManager For handling JWT tokens
     */
    fun init(context: Context, tokenManager: TokenManager) {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val tokenInterceptor = TokenInterceptor(context, tokenManager)

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(tokenInterceptor)
            .build()

        centralRetrofit = Retrofit.Builder()
            .baseUrl(CENTRAL_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Initialize the ApiService with the central retrofit instance
        ApiService.getInstance().init(centralRetrofit!!)
    }

    /**
     * Gets the appropriate Retrofit instance based on whether a center is selected.
     * If a center is selected, returns the local center-specific Retrofit instance.
     * Otherwise, returns the central authentication Retrofit instance.
     */
    fun getRetrofit(): Retrofit {
        return localRetrofit ?: centralRetrofit
        ?: throw IllegalStateException("RetrofitClient must be initialized before use")
    }

    /**
     * Switches the API to use a specific center's database.
     * Creates a new Retrofit instance with the center-specific URL.
     *
     * @param center The center to switch to
     * @param context Application context for accessing SharedPreferences
     * @param tokenManager For handling JWT tokens
     */
    fun switchToCenter(center: Center, context: Context, tokenManager: TokenManager) {
        // Only recreate if center has changed
        if (currentCenter?.id != center.id) {
            currentCenter = center

            val baseUrl = buildCenterUrl(center)

            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val tokenInterceptor = TokenInterceptor(context, tokenManager)

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(tokenInterceptor)
                .build()

            localRetrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            // Update the ApiService with the new retrofit instance
            ApiService.getInstance().reinitializeForCenter(localRetrofit!!, center)
        }
    }

    /**
     * Resets to the central authentication client.
     * Used during logout or session expiration.
     */
    fun resetToCentral() {
        currentCenter = null
        localRetrofit = null

        centralRetrofit?.let {
            ApiService.getInstance().init(it)
        }
    }

    /**
     * Builds the base URL for a specific center.
     *
     * @param center The center to build the URL for
     * @return The base URL for the center's API
     */
    private fun buildCenterUrl(center: Center): String {
        // Based on the center details, construct the appropriate URL
        // This would typically use CENTRE_CVT.MACHINE and CENTRE_CVT.SID
        return "https://${center.machine}.inspection.com/api/${center.sid}/"
    }
}