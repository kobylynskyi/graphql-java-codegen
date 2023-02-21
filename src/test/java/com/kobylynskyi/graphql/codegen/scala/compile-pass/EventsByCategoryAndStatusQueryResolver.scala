import EventStatus._

/**
 * List of events of a specified category.
 */
trait EventsByCategoryAndStatusQueryResolver {

    /**
     * List of events of a specified category.
     */
    @throws[Exception]
    def eventsByCategoryAndStatus(categoryId: String, status: EventStatus): scala.Seq[Event]

}
