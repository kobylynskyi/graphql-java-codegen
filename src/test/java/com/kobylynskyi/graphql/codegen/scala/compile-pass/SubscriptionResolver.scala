
trait SubscriptionResolver {

    /**
     * Subscribe to events
     */
    @throws[Exception]
    def eventsCreated(): scala.Seq[Event]

}
