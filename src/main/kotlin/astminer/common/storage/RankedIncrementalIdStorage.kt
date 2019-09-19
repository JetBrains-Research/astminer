package astminer.common.storage

class RankedIncrementalIdStorage<T> {
    private var keyCounter = 0L
    val idPerItem: MutableMap<T, Long> = HashMap()
    private val idCountMap: MutableMap<Long, Long> = HashMap()
    private var idCountRanks: Map<Long, Long>? = null

    private fun putAndIncrementKey(item: T): Long {
        idPerItem[item] = ++keyCounter
        return keyCounter
    }

    private fun incrementIdCount(id: Long) {
        idCountMap[id] = idCountMap.getOrDefault(id, 0) + 1
    }

    fun record(item: T): Long {
        val id = idPerItem[item] ?: putAndIncrementKey(item)
        incrementIdCount(id)
        return id
    }

    fun getId(item: T): Long = idPerItem[item] ?: 0

    fun getIdCount(id: Long) = idCountMap.getOrDefault(id, 0)

    fun lookUpValue(id: Long): T? {
        return idPerItem.entries.firstOrNull { it.value == id }?.key
    }

    fun getKeyRank(item: T) = getIdRank(getId(item))

    fun getIdRank(id: Long): Long {
        if (idCountRanks == null) {
            computeRanks()
        }
        return idCountRanks?.get(id) ?: 0
    }

    fun computeRanks() {
        val sortedEntries = idCountMap.entries
                .sortedBy { it.value }
                .reversed()
                .map { it.key }
                .toList()
        val idRankMap = mutableMapOf<Long, Long>()
        for (i in sortedEntries.indices) {
            idRankMap[sortedEntries[i]] = (i + 1).toLong()
        }
        idCountRanks = idRankMap
    }
}