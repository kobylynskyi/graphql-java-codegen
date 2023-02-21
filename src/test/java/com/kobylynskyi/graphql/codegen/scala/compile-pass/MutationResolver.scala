
trait MutationResolver {

    /**
     * Create a new event.
     */
    @throws[Exception]
    def createEvent(categoryId: String, createdBy: String): Event

}
