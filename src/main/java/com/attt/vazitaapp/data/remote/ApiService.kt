package com.attt.vazitaapp.data.remote


import com.attt.vazitaapp.data.model.Center
import com.attt.vazitaapp.data.model.User
import com.attt.vazitaapp.data.remote.api.*
import retrofit2.Retrofit

/**
 * Single access point for all API services.
 * This class provides access to all API endpoints through a single interface.
 */
class ApiService private constructor() {

    lateinit var authApi: AuthApi
        private set

    lateinit var userApi: UserApi
        private set

    lateinit var inspectionApi: InspectionApi
        private set

    lateinit var defectApi: DefectApi
        private set

    lateinit var analyticsApi: AnalyticsApi
        private set

    companion object {
        @Volatile
        private var instance: ApiService? = null

        fun getInstance(): ApiService {
            return instance ?: synchronized(this) {
                instance ?: ApiService().also { instance = it }
            }
        }
    }

    /**
     * Initializes all API services with the provided Retrofit instance.
     * Must be called before using any API service.
     *
     * @param retrofit The Retrofit instance to use for creating API services.
     */
    fun init(retrofit: Retrofit) {
        authApi = retrofit.create(AuthApi::class.java)
        userApi = retrofit.create(UserApi::class.java)
        inspectionApi = retrofit.create(InspectionApi::class.java)
        defectApi = retrofit.create(DefectApi::class.java)
        analyticsApi = retrofit.create(AnalyticsApi::class.java)
    }

    /**
     * Reinitializes the API services with a new Retrofit instance.
     * This is useful when the base URL needs to be changed due to center switching.
     *
     * @param retrofit The new Retrofit instance to use.
     * @param center The center information that contains the new base URL.
     */
    fun reinitializeForCenter(retrofit: Retrofit, center: Center) {
        init(retrofit)
    }
}