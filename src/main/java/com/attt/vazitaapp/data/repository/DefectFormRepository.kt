package com.attt.vazitaapp.data.repository


import com.attt.vazitaapp.data.local.TokenManager
import com.attt.vazitaapp.data.local.dao.DefectFormDao
import com.attt.vazitaapp.data.model.DefectForm
import com.attt.vazitaapp.data.model.DossierDefect
import com.attt.vazitaapp.data.remote.api.DefectApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for handling defect form operations
 * Includes saving/retrieving draft forms and submitting completed forms
 */
@Singleton
class DefectFormRepository @Inject constructor(
    private val defectApi: DefectApi,
    private val defectFormDao: DefectFormDao,
    private val tokenManager: TokenManager,
    private val inspectionRepository: InspectionRepository
) {

    /**
     * Get form draft for a specific dossier if it exists
     *
     * @param dossierNumber Dossier number to retrieve form for
     * @return Flow of DefectForm for continuous observation
     */
    fun getFormDraftFlow(dossierNumber: String): Flow<DefectForm?> {
        return defectFormDao.getFormDraftFlow(dossierNumber)
    }

    /**
     * Get form draft as a one-time operation
     *
     * @param dossierNumber Dossier number to retrieve form for
     * @return Result containing form draft or null if doesn't exist
     */
    suspend fun getFormDraft(dossierNumber: String): Result<DefectForm?> = withContext(Dispatchers.IO) {
        try {
            val formDraft = defectFormDao.getFormDraft(dossierNumber)
            return@withContext Result.success(formDraft)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    /**
     * Save or update form draft
     *
     * @param defectForm Form draft to save
     * @return Result containing saved form
     */
    suspend fun saveFormDraft(defectForm: DefectForm): Result<DefectForm> = withContext(Dispatchers.IO) {
        try {
            // Update modification timestamp
            val updatedForm = defectForm.copy(lastModified = Date())
            defectFormDao.insertOrUpdate(updatedForm)
            return@withContext Result.success(updatedForm)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    /**
     * Delete form draft
     *
     * @param dossierNumber Dossier number to delete form for
     * @return Result containing Unit on success
     */
    suspend fun deleteFormDraft(dossierNumber: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            defectFormDao.deleteFormDraft(dossierNumber)
            return@withContext Result.success(Unit)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    /**
     * Submit completed defect form
     *
     * @param defectForm Completed defect form to submit
     * @return Result containing DossierDefect on success
     */
    suspend fun submitDefectForm(defectForm: DefectForm): Result<List<DossierDefect>> = withContext(Dispatchers.IO) {
        try {
            // Get current user ID from token manager
            val inspectorId = tokenManager.getUserId() ?:
            return@withContext Result.failure(Exception("User ID not available"))

            // Create submission request
            val dossierDefects = defectForm.selectedDefects.map { defect ->
                DossierDefect(
                    controlDate = Date(),
                    centerId = tokenManager.getCenterId() ?: "",
                    dossierNumber = defectForm.dossierNumber,
                    registrationTime = defectForm.registrationTime ?: Date(),
                    chassisNumber = defectForm.chassisNumber ?: "",
                    defectCode = "${defect.chapterCode}${defect.pointCode}${defect.alterationCode}",
                    inspectorId = inspectorId
                )
            }

            // Submit to API
            val response = defectApi.submitDefects(dossierDefects)
            if (response.isSuccessful) {
                // Delete local draft on success
                defectFormDao.deleteFormDraft(defectForm.dossierNumber)

                // Remove car from queue
                inspectionRepository.removeCarFromQueue(defectForm.dossierNumber)

                return@withContext Result.success(dossierDefects)
            } else {
                return@withContext Result.failure(Exception("Failed to submit defect form: ${response.message()}"))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    /**
     * Get submitted defect reports, accessible to Admin and Adjoint roles
     *
     * @param page Page number for pagination
     * @param pageSize Number of items per page
     * @param filterByDate Optional date filter
     * @param filterByInspector Optional inspector ID filter
     * @return Result containing list of submitted defect reports
     */
    suspend fun getSubmittedDefects(
        page: Int,
        pageSize: Int,
        filterByDate: Date? = null,
        filterByInspector: String? = null
    ): Result<List<DossierDefect>> = withContext(Dispatchers.IO) {
        try {
            val response = defectApi.getSubmittedDefects(page, pageSize, filterByDate, filterByInspector)
            if (response.isSuccessful) {
                return@withContext Result.success(response.body() ?: emptyList())
            } else {
                return@withContext Result.failure(Exception("Failed to fetch submitted defects: ${response.message()}"))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    /**
     * Get defect details for a specific dossier
     *
     * @param dossierNumber Dossier number to get defects for
     * @return Result containing list of dossier defects
     */
    suspend fun getDefectsByDossier(dossierNumber: String): Result<List<DossierDefect>> =
        withContext(Dispatchers.IO) {
            try {
                val response = defectApi.getDefectsByDossier(dossierNumber)
                if (response.isSuccessful) {
                    return@withContext Result.success(response.body() ?: emptyList())
                } else {
                    return@withContext Result.failure(Exception("Failed to fetch defects: ${response.message()}"))
                }
            } catch (e: Exception) {
                return@withContext Result.failure(e)
            }
        }

    /**
     * Update existing defect record (Admin/Adjoint only)
     *
     * @param dossierDefect Updated defect data
     * @return Result containing updated defect on success
     */
    suspend fun updateDefect(dossierDefect: DossierDefect): Result<DossierDefect> = withContext(Dispatchers.IO) {
        try {
            val response = defectApi.updateDefect(dossierDefect)
            if (response.isSuccessful && response.body() != null) {
                return@withContext Result.success(response.body()!!)
            } else {
                return@withContext Result.failure(Exception("Failed to update defect: ${response.message()}"))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    /**
     * Delete defect record (Admin/Adjoint only)
     *
     * @param defectId Defect ID to delete
     * @return Result containing Unit on success
     */
    suspend fun deleteDefect(defectId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = defectApi.deleteDefect(defectId)
            if (response.isSuccessful) {
                return@withContext Result.success(Unit)
            } else {
                return@withContext Result.failure(Exception("Failed to delete defect: ${response.message()}"))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
}