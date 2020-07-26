package com.ottsandbox.pointme.models.dtos

import java.time.LocalDateTime

data class CompletedNavigationOperation(
    val operationId: Int,
    val destinationName: String,
    val startLatitude: Double,
    val startLongitude: Double,
    val endLatitude: Double,
    val endLongitude: Double,
    val operationDate: LocalDateTime
)