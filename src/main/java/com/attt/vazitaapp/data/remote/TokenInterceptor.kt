package com.attt.vazitaapp.data.remote


import android.content.Context
import android.content.Intent
import com.attt.vazitaapp.data.local.TokenManager
import com.attt.vazitaapp.ui.auth.LoginActivity
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpURLConnection

/**
 * Interceptor that attaches JWT tokens to requests and handles token refresh and expiration.
 */
class TokenInterceptor(
    private val context: Context,
    private val tokenManager: TokenManager
) : Interceptor {

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip token attachment for authentication routes
        if (isAuthRoute(originalRequest)) {
            return chain.proceed(originalRequest)
        }

        // Get the current access token
        val accessToken = tokenManager.getAccessToken() ?: return handleUnauthorized(chain, originalRequest)

        // Add the token to the request
        val requestWithToken = addTokenToRequest(originalRequest, accessToken)
        var response = chain.proceed(requestWithToken)

        // If we got a 401 (Unauthorized), try to refresh the token
        if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
            response.close()

            // Try to refresh the token
            val newAccessToken = refreshToken()

            // If refresh succeeded, retry the request with the new token
            if (newAccessToken != null) {
                val newRequestWithToken = addTokenToRequest(originalRequest, newAccessToken)
                return chain.proceed(newRequestWithToken)
            }

            // If refresh failed, handle unauthorized
            return handleUnauthorized(chain, originalRequest)
        }

        return response
    }

    /**
     * Adds the JWT token to the request header.
     */
    private fun addTokenToRequest(request: Request, token: String): Request {
        return request.newBuilder()
            .header(AUTHORIZATION_HEADER, BEARER_PREFIX + token)
            .build()
    }

    /**
     * Attempts to refresh the access token using the refresh token.
     * Returns null if the refresh fails.
     */
    private fun refreshToken(): String? {
        val refreshToken = tokenManager.getRefreshToken() ?: return null

        return try {
            runBlocking {
                // Create a new service instance for the refresh token request to avoid recursion
                val authApi = ApiService.getInstance().authApi
                val response = authApi.refreshToken(refreshToken)

                if (response.isSuccessful) {
                    val tokenResponse = response.body()
                    if (tokenResponse != null) {
                        // Save the new tokens
                        tokenManager.saveTokens(
                            accessToken = tokenResponse.accessToken,
                            refreshToken = tokenResponse.refreshToken,
                            expiresIn = tokenResponse.expiresIn
                        )
                        return@runBlocking tokenResponse.accessToken
                    }
                }

                // If we get here, refresh failed
                null
            }
        } catch (e: Exception) {
            // Log the error or handle it as needed
            null
        }
    }

    /**
     * Handles unauthorized (401) responses.
     * Clears tokens and directs user to login screen.
     */
    private fun handleUnauthorized(chain: Interceptor.Chain, request: Request): Response {
        // Clear tokens
        tokenManager.clearTokens()

        // Reset to central API
        RetrofitClient.getInstance().resetToCentral()

        // Redirect to login activity (requires main thread, so we'll just log the user out and continue)
        val intent = Intent(context, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)

        // Proceed with the original request without a token
        // This will likely fail, but we need to return something
        return chain.proceed(request)
    }

    /**
     * Determines if a request is for an authentication route that doesn't need a token.
     */
    private fun isAuthRoute(request: Request): Boolean {
        val url = request.url.toString()
        return url.contains("/auth/login") || url.contains("/auth/refresh")
    }
}