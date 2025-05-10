package com.attt.vazitaapp.data.local
import androidx.databinding.adapters.Converters
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.attt.vazitaapp.data.local.dao.*
import com.attt.vazitaapp.data.model.*

@Database(
    entities = [
        CarQueue::class,
        Chapter::class,
        DefectPoint::class,
        Alteration::class,
        DefectForm::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun carQueueDao(): CarQueueDao
    abstract fun chapterDao(): ChapterDao
    abstract fun pointDao(): PointDao
    abstract fun alterationDao(): AlterationDao
    abstract fun defectFormDao(): DefectFormDao
}