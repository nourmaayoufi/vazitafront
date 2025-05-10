package com.attt.vazitaapp.data.model

data class AnalyticsData(
    val period: String,
    val carType: String?,
    val defectCounts: Map<String, Int>,
    val defectLabels: Map<String, String>,
    val totalInspections: Int
)