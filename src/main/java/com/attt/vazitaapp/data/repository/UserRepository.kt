package com.attt.vazitaapp.data.repository


import com.attt.vazitaapp.data.local.TokenManager
import com.attt.vazitaapp.data.remote.api.UserApi
import com.attt.vazitaapp.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for user management operations
 * Accessible only to Center Chef (Admin) users
 */
@Singleton
class UserRepository @Inject constructor(
    private val userApi: UserApi,
    private val tokenManager: TokenManager
) {

    /**
     * Get all users for the current center
     *
     * @return Result containing list of users on success or Exception on failure
     */
    suspend fun getUsers(): Result<List<User>> = withContext(Dispatchers.IO) {
        try {
            val response = userApi.getUsers()
            if (response.isSuccessful) {
                return@withContext Result.success(response.body() ?: emptyList())
            } else {
                return@withContext Result.failure(Exception("Failed to fetch users: ${response.message()}"))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    /**
     * Get user by ID
     *
     * @param userId User ID
     * @return Result containing user on success or Exception on failure
     */
    suspend fun getUserById(userId: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = userApi.getUserById(userId)
            if (response.isSuccessful && response.body() != null) {
                return@withContext Result.success(response.body()!!)
            } else {
                return@withContext Result.failure(Exception("Failed to fetch user: ${response.message()}"))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    /**
     * Create a new user in the current center
     *
     * @param user User data to create
     * @return Result containing created user on success or Exception on failure
     */
    suspend fun createUser(user: User): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = userApi.createUser(user)
            if (response.isSuccessful && response.body() != null) {
                return@withContext Result.success(response.body()!!)
            } else {
                return@withContext Result.failure(Exception("Failed to create user: ${response.message()}"))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    /**
     * Update an existing user
     *
     * @param userId User ID to update
     * @param user Updated user data
     * @return Result containing updated user on success or Exception on failure
     */
    suspend fun updateUser(userId: String, user: User): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = userApi.updateUser(userId, user)
            if (response.isSuccessful && response.body() != null) {
                return@withContext Result.success(response.body()!!)
            } else {
                return@withContext Result.failure(Exception("Failed to update user: ${response.message()}"))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    /**
     * Delete a user
     *
     * @param userId User ID to delete
     * @return Result containing Unit on success or Exception on failure
     */
    suspend fun deleteUser(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = userApi.deleteUser(userId)
            if (response.isSuccessful) {
                return@withContext Result.success(Unit)
            } else {
                return@withContext Result.failure(Exception("Failed to delete user: ${response.message()}"))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    /**
     * Check if current user is Admin (Center Chef)
     *
     * @return true if user has admin role
     */
    fun isAdmin(): Boolean {
        return tokenManager.getUserRole() == "ROLE_ADMIN"
    }
}