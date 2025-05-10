package com.attt.vazitaapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chapters")
data class Chapter(
    @PrimaryKey
    val codeChapter: String,
    val libelleChapter: String
)