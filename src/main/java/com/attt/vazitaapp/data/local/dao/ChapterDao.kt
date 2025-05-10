package com.attt.vazitaapp.data.local.dao

import androidx.room.*
import com.attt.vazitaapp.data.model.Chapter
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterDao {
    @Query("SELECT * FROM chapters ORDER BY codeChapter")
    fun getAllChapters(): Flow<List<Chapter>>

    @Query("SELECT * FROM chapters WHERE codeChapter = :chapterCode")
    suspend fun getChapterByCode(chapterCode: String): Chapter?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(chapters: List<Chapter>)

    @Query("DELETE FROM chapters")
    suspend fun deleteAll()
}
