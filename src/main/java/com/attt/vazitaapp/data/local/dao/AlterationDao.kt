package com.attt.vazitaapp.data.local.dao

import androidx.room.*
import com.attt.vazitaapp.data.model.Alteration
import kotlinx.coroutines.flow.Flow

@Dao
interface AlterationDao {
    @Query("SELECT * FROM alterations WHERE codeChapter = :chapterCode AND codePoint = :pointCode ORDER BY codeAlteration")
    fun getAlterationsByPoint(chapterCode: String, pointCode: String): Flow<List<Alteration>>

    @Query("SELECT * FROM alterations WHERE codeAlteration = :alterationCode AND codeChapter = :chapterCode AND codePoint = :pointCode")
    suspend fun getAlteration(alterationCode: String, chapterCode: String, pointCode: String): Alteration?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(alterations: List<Alteration>)

    @Query("DELETE FROM alterations")
    suspend fun deleteAll()
}