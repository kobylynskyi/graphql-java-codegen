
/**
 * Single event by ID.
 */
trait EventByIdQueryResolver {

    /**
     * Single event by ID.
     */
    @throws[Exception]
    def eventById(id: String): Event

}
