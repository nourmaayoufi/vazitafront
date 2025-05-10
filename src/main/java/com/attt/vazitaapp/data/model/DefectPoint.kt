package com.attt.vazitaapp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "defect_points",
    primaryKeys = ["codePoint", "codeChapter"],
    indices = [Index("codeChapter")],
    foreignKeys = [
        ForeignKey(
            entity = Chapter::class,
            parentColumns = ["codeChapter"],
            childColumns = ["codeChapter"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DefectPoint(
    val codePoint: String,
    val libellePoint: String,
    val codeChapter: String
)