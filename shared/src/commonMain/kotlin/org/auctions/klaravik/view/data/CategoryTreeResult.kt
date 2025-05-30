package org.auctions.klaravik.view.data

data class CategoryTreeResult(
    val roots: List<CategoryItem>,
    val idMap: Map<Int, CategoryItem>
    )