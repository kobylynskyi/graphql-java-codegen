
/**
 * List of events of a specified category.
 */
interface EventsByCategoryAndStatusQueryResolver {

    /**
     * List of events of a specified category.
     */
    @Throws(Exception::class)
    fun eventsByCategoryAndStatus(categoryId: String, status: EventStatus?): List<Event?>

}
