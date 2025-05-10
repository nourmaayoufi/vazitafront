package com.attt.vazitaapp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "alterations",
    primaryKeys = ["codeAlteration", "codeChapter", "codePoint"],
    indices = [Index("codeChapter"), Index("codePoint")],
    foreignKeys = [
        ForeignKey(
            entity = Chapter::class,
            parentColumns = ["codeChapter"],
            childColumns = ["codeChapter"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DefectPoint::class,
            parentColumns = ["codePoint", "codeChapter"],
            childColumns = ["codePoint", "codeChapter"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Alteration(
    val codeAlteration: String,
    val libelleAlteration: String,
    val codeChapter: String,
    val codePoint: String
)
