
/**
 * Single event by ID.
 */
interface EventByIdQueryResolver {

    /**
     * Single event by ID.
     */
    @Throws(Exception::class)
    fun eventById(id: String): Event

}
