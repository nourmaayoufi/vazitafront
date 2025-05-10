package com.attt.vazitaapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "car_queue")
data class CarQueue(
    @PrimaryKey
    val nDossier: String,
    val numChassis: String,
    val immatriculation: String,
    val cPiste: String,
    val dateHeureEnregistrement: String
)
