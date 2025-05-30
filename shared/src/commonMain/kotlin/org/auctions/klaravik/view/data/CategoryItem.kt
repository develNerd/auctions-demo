package org.auctions.klaravik.view.data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.auctions.klaravik.data.model.CategoryItemDTO

@Serializable
data class CategoryItem(
    @SerialName("headline")
    val headline: String,
    @SerialName("id")
    val id: Int,
    @SerialName("level")
    val level: Int,
    @SerialName("parentId")
    val parentId: Int? = null,
    val children: MutableList<CategoryItem>? = null
) {
    fun CategoryItemDTO.toCategoryResponseItem(): CategoryItem {
        return CategoryItem(headline, id, level, parentId)
    }


    fun isRoot(): Boolean = parentId == null

    fun isLeaf(): Boolean = children.isNullOrEmpty()

    fun isChildOf(parent: CategoryItem): Boolean = parentId == parent.id

    fun isParentOf(child: CategoryItem): Boolean = id == child.parentId
}