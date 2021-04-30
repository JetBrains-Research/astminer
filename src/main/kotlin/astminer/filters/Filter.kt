package astminer.filters

interface Filter<T> {
    fun isFiltered(entity: T): Boolean
}
