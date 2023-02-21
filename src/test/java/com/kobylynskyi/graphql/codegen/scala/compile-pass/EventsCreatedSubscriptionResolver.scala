
/**
 * Subscribe to events
 */
trait EventsCreatedSubscriptionResolver {

    /**
     * Subscribe to events
     */
    @throws[Exception]
    def eventsCreated(): scala.Seq[Event]

}
