package com.attt.vazitaapp.data.model


data class User(
    val idUser: String,
    val passe: String,
    val nom: String,
    val prenom: String,
    val noma: String?,
    val prenoma: String?,
    val codGrp: Int,        // 1 = ADMIN, 2 = INSPECTOR, 3 = ADJOINT
    val idCentre: String,
    val dateDeb: String?,
    val dateFin: String?,
    val etat: String?
)