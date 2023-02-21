
interface QueryResolver {

    /**
     * Version of the application.
     */
    @Throws(Exception::class)
    fun version(): String

    /**
     * List of events of a specified category.
     */
    @Throws(Exception::class)
    fun eventsByCategoryAndStatus(categoryId: String, status: EventStatus?): List<Event?>

    /**
     * Single event by ID.
     */
    @Throws(Exception::class)
    fun eventById(id: String): Event

    /**
     * Events by IDs.
     */
    @Throws(Exception::class)
    fun eventsByIds(ids: List<String>): List<Event>

}
