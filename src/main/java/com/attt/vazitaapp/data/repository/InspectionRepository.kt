package com.attt.vazitaapp.data.repository


import com.attt.vazitaapp.data.local.dao.CarQueueDao
import com.attt.vazitaapp.data.local.dao.ChapterDao
import com.attt.vazitaapp.data.local.dao.PointDao
import com.attt.vazitaapp.data.local.dao.AlterationDao
import com.attt.vazitaapp.data.model.CarQueue
import com.attt.vazitaapp.data.model.Chapter
import com.attt.vazitaapp.data.model.DefectPoint
import com.attt.vazitaapp.data.model.Alteration
import com.attt.vazitaapp.data.remote.api.InspectionApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for inspection-related operations
 * Handles car queue data and defect hierarchy (chapters, points, alterations)
 */
@Singleton
class InspectionRepository @Inject constructor(
    private val inspectionApi: InspectionApi,
    private val carQueueDao: CarQueueDao,
    private val chapterDao: ChapterDao,
    private val pointDao: PointDao,
    private val alterationDao: AlterationDao
) {

    /**
     * Get car queue as Flow for continuous observation
     *
     * @return Flow of car queue list
     */
    fun getCarQueueFlow(): Flow<List<CarQueue>> {
        return carQueueDao.getCarQueueFlow()
    }

    /**
     * Get car queue as a one-time operation
     *
     * @return Result containing car queue list
     */
    suspend fun getCarQueue(): Result<List<CarQueue>> = withContext(Dispatchers.IO) {
        try {
            val localData = carQueueDao.getCarQueue()
            if (localData.isNotEmpty()) {
                return@withContext Result.success(localData)
            }

            // Fallback to API if local data is empty
            return@withContext refreshCarQueue()
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    /**
     * Refresh car queue from API and update local storage
     *
     * @return Result containing car queue list
     */
    suspend fun refreshCarQueue(): Result<List<CarQueue>> = withContext(Dispatchers.IO) {
        try {
            val response = inspectionApi.getCarQueue()
            if (response.isSuccessful) {
                val carQueue = response.body() ?: emptyList()
                // Update local storage
                carQueueDao.clearAndInsertAll(carQueue)
                return@withContext Result.success(carQueue)
            } else {
                return@withContext Result.failure(Exception("Failed to fetch car queue: ${response.message()}"))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    /**
     * Get car details by dossier number
     *
     * @param dossierNumber Dossier number to look up
     * @return Result containing car details
     */
    suspend fun getCarByDossierNumber(dossierNumber: String): Result<CarQueue> = withContext(Dispatchers.IO) {
        try {
            val localCar = carQueueDao.getCarByDossierNumber(dossierNumber)
            if (localCar != null) {
                return@withContext Result.success(localCar)
            }

            // Fallback to API
            val response = inspectionApi.getCarByDossierNumber(dossierNumber)
            if (response.isSuccessful && response.body() != null) {
                return@withContext Result.success(response.body()!!)
            } else {
                return@withContext Result.failure(Exception("Car not found: ${response.message()}"))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    /**
     * Remove car from queue after inspection
     *
     * @param dossierNumber Dossier number to remove
     * @return Result containing Unit on success
     */
    suspend fun removeCarFromQueue(dossierNumber: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // First update local DB
            carQueueDao.deleteCar(dossierNumber)

            // Then sync with server
            val response = inspectionApi.removeCarFromQueue(dossierNumber)
            if (response.isSuccessful) {
                return@withContext Result.success(Unit)
            } else {
                // Rollback local deletion on API failure
                refreshCarQueue()
                return@withContext Result.failure(Exception("Failed to remove car from queue: ${response.message()}"))
            }
        } catch (e: Exception) {
            // Try to refresh to ensure consistency
            refreshCarQueue()
            return@withContext Result.failure(e)
        }
    }

    /**
     * Get all chapters for defect form
     *
     * @return Result containing list of chapters
     */
    suspend fun getChapters(): Result<List<Chapter>> = withContext(Dispatchers.IO) {
        try {
            var chapters = chapterDao.getChapters()
            if (chapters.isEmpty()) {
                // Fetch from API if local DB empty
                val response = inspectionApi.getChapters()
                if (response.isSuccessful) {
                    chapters = response.body() ?: emptyList()
                    // Cache chapters locally
                    chapterDao.insertAll(chapters)
                } else {
                    return@withContext Result.failure(Exception("Failed to fetch chapters: ${response.message()}"))
                }
            }
            return@withContext Result.success(chapters)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    /**
     * Get defect points for a specific chapter
     *
     * @param chapterCode Chapter code to filter points
     * @return Result containing list of defect points
     */
    suspend fun getDefectPointsByChapter(chapterCode: String): Result<List<DefectPoint>> = withContext(Dispatchers.IO) {
        try {
            var points = pointDao.getPointsByChapter(chapterCode)
            if (points.isEmpty()) {
                // Fetch from API if local DB empty
                val response = inspectionApi.getDefectPointsByChapter(chapterCode)
                if (response.isSuccessful) {
                    points = response.body() ?: emptyList()
                    // Cache points locally
                    pointDao.insertAll(points)
                } else {
                    return@withContext Result.failure(Exception("Failed to fetch defect points: ${response.message()}"))
                }
            }
            return@withContext Result.success(points)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    /**
     * Get alterations for a specific chapter and point
     *
     * @param chapterCode Chapter code
     * @param pointCode Point code
     * @return Result containing list of alterations
     */
    suspend fun getAlterationsByChapterAndPoint(chapterCode: String, pointCode: String): Result<List<Alteration>> =
        withContext(Dispatchers.IO) {
            try {
                var alterations = alterationDao.getAlterationsByChapterAndPoint(chapterCode, pointCode)
                if (alterations.isEmpty()) {
                    // Fetch from API if local DB empty
                    val response = inspectionApi.getAlterationsByChapterAndPoint(chapterCode, pointCode)
                    if (response.isSuccessful) {
                        alterations = response.body() ?: emptyList()
                        // Cache alterations locally
                        alterationDao.insertAll(alterations)
                    } else {
                        return@withContext Result.failure(Exception("Failed to fetch alterations: ${response.message()}"))
                    }
                }
                return@withContext Result.success(alterations)
            } catch (e: Exception) {
                return@withContext Result.failure(e)
            }
        }

    /**
     * Refresh defect hierarchy data (chapters, points, alterations)
     *
     * @return Result containing Unit on success
     */
    suspend fun refreshDefectHierarchy(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Get chapters
            val chaptersResponse = inspectionApi.getChapters()
            if (chaptersResponse.isSuccessful) {
                val chapters = chaptersResponse.body() ?: emptyList()
                chapterDao.clearAndInsertAll(chapters)

                // For each chapter, get points
                for (chapter in chapters) {
                    val pointsResponse = inspectionApi.getDefectPointsByChapter(chapter.codeChapter)
                    if (pointsResponse.isSuccessful) {
                        val points = pointsResponse.body() ?: emptyList()
                        pointDao.insertAll(points)

                        // For each point, get alterations
                        for (point in points) {
                            val alterationsResponse = inspectionApi.getAlterationsByChapterAndPoint(
                                chapter.codeChapter,
                                point.codePoint
                            )
                            if (alterationsResponse.isSuccessful) {
                                val alterations = alterationsResponse.body() ?: emptyList()
                                alterationDao.insertAll(alterations)
                            }
                        }
                    }
                }

                return@withContext Result.success(Unit)
            } else {
                return@withContext Result.failure(Exception("Failed to refresh defect hierarchy: ${chaptersResponse.message()}"))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
}