import EventStatus._

/**
 * Resolver for EventProperty
 */
trait EventPropertyResolver {

    /**
     * Properties
     */
    @throws[Exception]
    def child(eventProperty: EventProperty, first: scala.Option[Int], last: scala.Option[Int]): scala.Seq[EventProperty]

    /**
     * Parent event of the property
     */
    @throws[Exception]
    def parent(eventProperty: EventProperty, withStatus: EventStatus, createdAfter: String): Event

}
