package com.attt.vazitaapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "defect_forms")
data class DefectForm(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val nDossier: String,
    val numChassis: String,
    val immatriculation: String,
    val selectedDefects: List<String>, // List of CODE_DEFAUT values
    val isSubmitted: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)