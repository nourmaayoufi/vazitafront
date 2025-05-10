package com.attt.vazitaapp.data.remote.api


import com.attt.vazitaapp.data.model.AnalyticsRequest
import com.attt.vazitaapp.data.model.AnalyticsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AnalyticsApi {
    @POST("analytics/dashboard")
    suspend fun getDashboardData(
        @Body analyticsRequest: AnalyticsRequest
    ): Response<AnalyticsResponse>

    @GET("analytics/defects/frequent")
    suspend fun getMostFrequentDefects(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("carType") carType: String? = null,
        @Query("limit") limit: Int = 10
    ): Response<AnalyticsResponse>

    @GET("analytics/defects/by-center")
    suspend fun getDefectsByCenter(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("carType") carType: String? = null
    ): Response<AnalyticsResponse>

    @GET("analytics/defects/by-time")
    suspend fun getDefectsByTimePeriod(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("carType") carType: String? = null,
        @Query("groupBy") groupBy: String = "month" // day, week, month, year
    ): Response<AnalyticsResponse>

    @GET("analytics/export")
    suspend fun exportAnalyticsData(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("carType") carType: String? = null,
        @Query("format") format: String = "csv" // csv, pdf
    ): Response<String>
}