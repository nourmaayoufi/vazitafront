package com.attt.vazitaapp.data.model

data class DossierDefect(
    val datCtrl: String,
    val numCentre: String,
    val nDossier: String,
    val dateHeureEnregistrement: String,
    val numChassis: String,
    val codeDefaut: String, // This is the "chifra" code
    val matAgent: String    // Inspector ID
)