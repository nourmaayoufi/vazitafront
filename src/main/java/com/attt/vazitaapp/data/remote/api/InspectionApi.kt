package com.attt.vazitaapp.data.remote.api


import com.attt.vazitaapp.data.model.CarDossier
import com.attt.vazitaapp.data.model.Chapter
import com.attt.vazitaapp.data.model.DefectPoint
import com.attt.vazitaapp.data.model.Alteration
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface InspectionApi {
    @GET("inspection/queue")
    suspend fun getCarQueue(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<List<CarDossier>>

    @GET("inspection/chapters")
    suspend fun getChapters(): Response<List<Chapter>>

    @GET("inspection/chapters/{chapterCode}/points")
    suspend fun getDefectPointsByChapter(
        @Path("chapterCode") chapterCode: String
    ): Response<List<DefectPoint>>

    @GET("inspection/chapters/{chapterCode}/points/{pointCode}/alterations")
    suspend fun getAlterationsByChapterAndPoint(
        @Path("chapterCode") chapterCode: String,
        @Path("pointCode") pointCode: String
    ): Response<List<Alteration>>

    @GET("inspection/car/{dossierNumber}")
    suspend fun getCarDossierByNumber(
        @Path("dossierNumber") dossierNumber: String
    ): Response<CarDossier>

    @GET("inspection/search")
    suspend fun searchCarQueue(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<List<CarDossier>>
}