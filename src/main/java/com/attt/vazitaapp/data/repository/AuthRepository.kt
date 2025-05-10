package com.attt.vazitaapp.data.repository


import com.attt.vazitaapp.data.local.TokenManager
import com.attt.vazitaapp.data.remote.api.AuthApi
import com.attt.vazitaapp.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository handling authentication operations
 */
@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) {

    /**
     * Perform user login
     *
     * @param userId User ID
     * @param password User password
     * @return Result containing User on success or Exception on failure
     */
    suspend fun login(userId: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = authApi.login(userId, password)
            if (response.isSuccessful && response.body() != null) {
                val jwtResponse = response.body()!!
                // Save token to secure storage
                tokenManager.saveToken(jwtResponse.token)
                tokenManager.saveRefreshToken(jwtResponse.refreshToken)
                tokenManager.saveUserId(userId)
                tokenManager.saveUserRole(jwtResponse.role)
                tokenManager.saveCenterId(jwtResponse.centerId)

                return@withContext Result.success(
                    User(
                        id = userId,
                        firstName = jwtResponse.firstName,
                        lastName = jwtResponse.lastName,
                        role = jwtResponse.role,
                        centerId = jwtResponse.centerId
                    )
                )
            } else {
                return@withContext Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    /**
     * Refresh the authentication token
     *
     * @return Result containing new token on success or Exception on failure
     */
    suspend fun refreshToken(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val refreshToken = tokenManager.getRefreshToken() ?: return@withContext Result.failure(Exception("No refresh token available"))

            val response = authApi.refreshToken(refreshToken)
            if (response.isSuccessful && response.body() != null) {
                val jwtResponse = response.body()!!
                // Save new tokens
                tokenManager.saveToken(jwtResponse.token)
                tokenManager.saveRefreshToken(jwtResponse.refreshToken)

                return@withContext Result.success(jwtResponse.token)
            } else {
                return@withContext Result.failure(Exception("Token refresh failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    /**
     * Logout user by invalidating tokens
     */
    suspend fun logout(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = tokenManager.getToken() ?: return@withContext Result.success(Unit)

            // Call API to invalidate token
            val response = authApi.logout(token)

            // Clear local token storage regardless of API result
            tokenManager.clearTokens()

            if (response.isSuccessful) {
                return@withContext Result.success(Unit)
            } else {
                return@withContext Result.failure(Exception("Logout failed on server: ${response.message()}"))
            }
        } catch (e: Exception) {
            // Still clear tokens even if API call fails
            tokenManager.clearTokens()
            return@withContext Result.failure(e)
        }
    }

    /**
     * Check if user is logged in
     *
     * @return true if user has valid token
     */
    fun isLoggedIn(): Boolean {
        return tokenManager.getToken() != null
    }

    /**
     * Get current user role
     *
     * @return User role or null if not logged in
     */
    fun getUserRole(): String? {
        return tokenManager.getUserRole()
    }

    /**
     * Get current center ID
     *
     * @return Center ID or null if not logged in
     */
    fun getCenterId(): String? {
        return tokenManager.getCenterId()
    }
}