package com.attt.vazitaapp.data.local.dao

import androidx.room.*
import com.attt.vazitaapp.data.model.DefectPoint
import kotlinx.coroutines.flow.Flow

@Dao
interface PointDao {
    @Query("SELECT * FROM defect_points WHERE codeChapter = :chapterCode ORDER BY codePoint")
    fun getPointsByChapter(chapterCode: String): Flow<List<DefectPoint>>

    @Query("SELECT * FROM defect_points WHERE codePoint = :pointCode AND codeChapter = :chapterCode")
    suspend fun getPoint(pointCode: String, chapterCode: String): DefectPoint?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(points: List<DefectPoint>)

    @Query("DELETE FROM defect_points")
    suspend fun deleteAll()
}