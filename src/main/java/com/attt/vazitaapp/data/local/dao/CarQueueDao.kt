package com.attt.vazitaapp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.attt.vazitaapp.data.model.CarQueue

@Dao
interface CarQueueDao {
    @Query("SELECT * FROM car_queue ORDER BY dateHeureEnregistrement DESC")
    fun getAllCarsPaged(): PagingSource<Int, CarQueue>

    @Query("SELECT * FROM car_queue WHERE nDossier = :dossierNumber")
    suspend fun getCarByDossierNumber(dossierNumber: String): CarQueue?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cars: List<CarQueue>)

    @Delete
    suspend fun delete(car: CarQueue)

    @Query("DELETE FROM car_queue")
    suspend fun deleteAll()

    @Query("SELECT * FROM car_queue WHERE immatriculation LIKE '%' || :searchQuery || '%' OR numChassis LIKE '%' || :searchQuery || '%'")
    fun searchCars(searchQuery: String): PagingSource<Int, CarQueue>
}