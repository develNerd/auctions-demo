package org.auctions.klaravik.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.auctions.klaravik.view.data.CategoryItem

@Serializable
data class CategoryItemDTO(
    @SerialName("headline")
    val headline: String,
    @SerialName("id")
    val id: Int,
    @SerialName("level")
    val level: Int,
    @SerialName("parentId")
    val parentId: Int? = null,
) {
    fun toCategoryItem(): CategoryItem {
        return CategoryItem(
            headline = headline,
            id = id,
            level = level,
            parentId = parentId
        )
    }
}