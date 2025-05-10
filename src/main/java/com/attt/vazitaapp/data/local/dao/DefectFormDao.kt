package com.attt.vazitaapp.data.local.dao

import androidx.room.*
import com.attt.vazitaapp.data.model.DefectForm
import kotlinx.coroutines.flow.Flow

@Dao
interface DefectFormDao {
    @Query("SELECT * FROM defect_forms WHERE nDossier = :dossierNumber AND isSubmitted = 0")
    fun getUnsubmittedFormByDossier(dossierNumber: String): Flow<DefectForm?>

    @Query("SELECT * FROM defect_forms WHERE isSubmitted = 0")
    fun getAllUnsubmittedForms(): Flow<List<DefectForm>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(defectForm: DefectForm)

    @Update
    suspend fun update(defectForm: DefectForm)

    @Query("UPDATE defect_forms SET isSubmitted = 1 WHERE nDossier = :dossierNumber")
    suspend fun markAsSubmitted(dossierNumber: String)

    @Query("DELETE FROM defect_forms WHERE nDossier = :dossierNumber")
    suspend fun deleteByDossier(dossierNumber: String)

    @Query("DELETE FROM defect_forms WHERE isSubmitted = 1")
    suspend fun deleteAllSubmitted()
}