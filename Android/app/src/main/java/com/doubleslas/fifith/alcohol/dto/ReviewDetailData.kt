package com.doubleslas.fifith.alcohol.dto

data class ReviewDetailData(
    val date: String?,
    val drink: Int?,
    val hangover: Int?,
    val place: String?,
    val price: Int?,
    val privacy: Boolean = false
)