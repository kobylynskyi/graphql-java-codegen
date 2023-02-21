
/**
 * Create a new event.
 */
interface CreateEventMutationResolver {

    /**
     * Create a new event.
     */
    @Throws(Exception::class)
    fun createEvent(categoryId: String, createdBy: String?): Event

}
