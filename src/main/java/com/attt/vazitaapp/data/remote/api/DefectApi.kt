package com.attt.vazitaapp.data.remote.api


import com.attt.vazitaapp.data.model.DefectSubmissionRequest
import com.attt.vazitaapp.data.model.DossierDefect
import com.attt.vazitaapp.data.model.DefectEditRequest
import retrofit2.Response
import retrofit2.http.*

interface DefectApi {
    @POST("defects/submit")
    suspend fun submitDefectForm(
        @Body defectSubmissionRequest: DefectSubmissionRequest
    ): Response<Any>

    @GET("defects")
    suspend fun getDefects(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("inspector") inspector: String? = null,
        @Query("chassisNumber") chassisNumber: String? = null
    ): Response<List<DossierDefect>>

    @GET("defects/{dossierNumber}")
    suspend fun getDefectsByDossierNumber(
        @Path("dossierNumber") dossierNumber: String
    ): Response<List<DossierDefect>>

    @PUT("defects/{defectId}")
    suspend fun updateDefect(
        @Path("defectId") defectId: String,
        @Body defectEditRequest: DefectEditRequest
    ): Response<DossierDefect>

    @GET("defects/search")
    suspend fun searchDefects(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<List<DossierDefect>>
}