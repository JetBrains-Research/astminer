package astminer.common.storage

typealias Id = Long

/**
 * This storage automatically assigns each item an id
 * and records how many times each item has been recorded in the storage.
 * It ranks items by the number of times they have been recorded.
 */
class RankedIncrementalIdStorage<T> {
    private var keyCounter = 0L
    val idPerItem: MutableMap<T, Id> = HashMap()
    private val idCountMap: MutableMap<Id, Long> = HashMap()
    private var idCountRanks: Map<Id, Long>? = null

    private fun putAndIncrementKey(item: T): Long {
        idPerItem[item] = ++keyCounter
        return keyCounter
    }

    private fun incrementIdCount(id: Id) {
        idCountMap[id] = idCountMap.getOrDefault(id, 0) + 1
    }

    /**
     * Puts the item into the storage or increments the count of [item] in the storage if it is already present.
     * @param item The item to be put in the storage
     * @return The id of the recorded item
     */
    fun record(item: T): Id {
        val id = idPerItem[item] ?: putAndIncrementKey(item)
        incrementIdCount(id)
        return id
    }

    /**
     * Returns the id of the item if the item was recorded with the record(item) method, returns 0 otherwise.
     */
    fun getId(item: T): Long = idPerItem[item] ?: 0

    /**
     * Returns the number of times the item with the provided [id] has been recorded in the storage.
     */
    fun getIdCount(id: Id) = idCountMap.getOrDefault(id, 0)

    /**
     * Returns the item by its [id]
     */
    fun lookUpValue(id: Id): T? = idPerItem.entries.firstOrNull { it.value == id }?.key

    /**
     * Returns the rank of the [item]
     * @see getIdRank
     */
    fun getKeyRank(item: T) = getIdRank(getId(item))

    /**
     * Returns the rank of the item with this [id].
     * The item that has been recorded in the storage the most times has the rank 1,
     * the second most recorded item has the rank 2, and so on...
     */
    fun getIdRank(id: Id): Long {
        if (idCountRanks == null) {
            computeRanks()
        }
        return idCountRanks?.get(id) ?: 0
    }

    /**
     * Computes the ranks
     * @see getIdRank
     */
    fun computeRanks() {
        val sortedIds = idCountMap.entries
            .sortedBy { it.value }
            .reversed()
            .map { it.key }
            .toList()
        val idRankMap = mutableMapOf<Id, Long>()
        for ((index, id) in sortedIds.withIndex()) {
            idRankMap[id] = (index + 1).toLong()
        }
        idCountRanks = idRankMap
    }
}
