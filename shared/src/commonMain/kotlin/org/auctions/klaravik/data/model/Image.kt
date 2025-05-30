package org.auctions.klaravik.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Image(
    @SerialName("largeUrl")
    val largeUrl: String,
    @SerialName("thumbUrl")
    val thumbUrl: String
)