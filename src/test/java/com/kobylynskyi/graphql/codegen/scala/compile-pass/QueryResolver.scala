import EventStatus._

trait QueryResolver {

    /**
     * Version of the application.
     */
    @throws[Exception]
    def version(): String

    /**
     * List of events of a specified category.
     */
    @throws[Exception]
    def eventsByCategoryAndStatus(categoryId: String, status: EventStatus): scala.Seq[Event]

    /**
     * Single event by ID.
     */
    @throws[Exception]
    def eventById(id: String): Event

    /**
     * Events by IDs.
     */
    @throws[Exception]
    def eventsByIds(ids: scala.Seq[String]): scala.Seq[Event]

}
