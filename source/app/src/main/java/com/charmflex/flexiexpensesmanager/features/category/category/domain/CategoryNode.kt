package com.charmflex.flexiexpensesmanager.features.category.category.domain

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
    allowAddChild: (child: NodeClass) -> Boolean = { true },
    nodeBuilder: (categoryLevel: Int, EntityClass) -> NodeClass,
): NodeClass {
    return nodeBuilder(1, responseEntity).also { node ->
        parentCatIDChildrenMap[retrievalKey(responseEntity)]?.let { children ->
            val updatedChildren = children.map { childEntity ->
                buildCategoryTree(
                    childEntity, retrievalKey, parentCatIDChildrenMap,allowAddChild,
                ) { categoryLevel, entityClass -> nodeBuilder(categoryLevel + 1, entityClass) }
            }
            node.addChildren(
                updatedChildren.filter { allowAddChild(it) }
            )
        }
    }
}