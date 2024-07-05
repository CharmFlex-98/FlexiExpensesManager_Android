package com.charmflex.flexiexpensesmanager.features.category.category.domain

import org.w3c.dom.Node

internal interface CategoryNode<NodeClass : CategoryNode<NodeClass>> {
    val categoryId: Int
    val categoryName: String
    val parentCategoryId: Int
    val children: List<NodeClass>

    fun addChildren(children: List<NodeClass>)
}

typealias ChildrenNodeRetrievalKey = Int
typealias ParentChildrenNodeMap<T> = Map<ChildrenNodeRetrievalKey, List<T>>

// Build Node
internal fun <NodeClass : CategoryNode<NodeClass>, EntityClass> buildCategoryTree(
    responseEntity: EntityClass,
    retrievalKey: (EntityClass) -> ChildrenNodeRetrievalKey,
    parentCatIDChildrenMap: ParentChildrenNodeMap<EntityClass>,
    nodeBuilder: (categoryLevel: Int, EntityClass) -> NodeClass,
): NodeClass {
    return nodeBuilder(1, responseEntity).also { node ->
        parentCatIDChildrenMap[retrievalKey(responseEntity)]?.let { children ->
            node.addChildren(
                children.map { childEntity ->
                    buildCategoryTree(
                        childEntity, retrievalKey, parentCatIDChildrenMap,
                    ) { categoryLevel, entityClass -> nodeBuilder(categoryLevel + 1, entityClass) }
                }
            )
        }
    }
}