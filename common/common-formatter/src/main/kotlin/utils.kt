package com.copperleaf.kodiak.common

public inline fun <T> Iterable<T>.withEach(action: T.() -> Unit): Unit {
    for (element in this) element.action()
}

fun <T> connectAllToParents(
    input: List<T>,
    remapItem: (IndexedItem<T>) -> T,
    getId: (T) -> String,
    splitId: (String) -> List<String> = { it.split(".") },
    toId: (List<String>) -> String = { it.joinToString(".") }
): List<T> {
    val indexedItems = mutableMapOf<String, IndexedItem<T>>()
    input.forEach {
        indexedItems[getId(it)] = IndexedItem(
            it,
            mutableListOf(),
            ""
        )
    }

    indexedItems.values.forEach { item ->
        var currentIdPieces = splitId(getId(item.item))

        current@ while (currentIdPieces.isNotEmpty()) {
            currentIdPieces = currentIdPieces.dropLast(1)
            val currentIdName = toId(currentIdPieces)
            val currentItem = indexedItems[currentIdName]
            if (currentItem != null) {
                currentItem.children.add(item)
                item.parentId = currentIdName
                break@current
            }
        }
    }

    return indexedItems.values.map(remapItem)
}

class IndexedItem<T>(
    val item: T,
    val children: MutableList<IndexedItem<T>>,
    var parentId: String?
)
