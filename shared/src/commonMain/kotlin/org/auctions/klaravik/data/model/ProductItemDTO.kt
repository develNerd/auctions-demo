package org.auctions.klaravik.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.auctions.klaravik.view.data.ProductItem

@Serializable
data class ProductItemDTO(
    @SerialName("categoryLevel1")
    val categoryLevel1: Int? = null,
    @SerialName("categoryLevel2")
    val categoryLevel2: Int? = null,
    @SerialName("categoryLevel3")
    val categoryLevel3: Int? = null,
    @SerialName("currentBid")
    val currentBid: Long = 0,
    @SerialName("description")
    val description: String? = null,
    @SerialName("endDate")
    val endDate: String? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("image")
    val image: Image? = null,
    @SerialName("make")
    val make: String? = null,
    @SerialName("model")
    val model: String? = null,
    @SerialName("municipalityName")
    val municipalityName: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("preamble")
    val preamble: String? = null,
    @SerialName("reservePriceStatus")
    val reservePriceStatus: String? = null
) {
    fun toProductItem(): ProductItem {
        return ProductItem(
            categoryLevel1 = categoryLevel1,
            categoryLevel2 = categoryLevel2,
            categoryLevel3 = categoryLevel3,
            currentBid = currentBid,
            description = description,
            endDate = endDate,
            id = id ?: 0,
            image = image,
            make = make,
            model = model,
            municipalityName = municipalityName,
            name = name,
            preamble = preamble,
            reservePriceStatus = reservePriceStatus
        )
    }
}