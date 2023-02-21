
/**
 * Events by IDs.
 */
interface EventsByIdsQueryResolver {

    /**
     * Events by IDs.
     */
    @Throws(Exception::class)
    fun eventsByIds(ids: List<String>): List<Event>

}
