package org.auctions.klaravik.data

import org.auctions.klaravik.data.model.CategoryItemDTO

object TestDataProvider {

    fun provideDummyCategoryItemDTO(): List<CategoryItemDTO> {
        return listOf(CategoryItemDTO(
            id = 1,
            headline = "Test Category",
            parentId = 2,
            level = 1,
        ))
    }
}