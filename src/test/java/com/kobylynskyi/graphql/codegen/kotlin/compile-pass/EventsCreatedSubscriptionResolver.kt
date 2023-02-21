
/**
 * Subscribe to events
 */
interface EventsCreatedSubscriptionResolver {

    /**
     * Subscribe to events
     */
    @Throws(Exception::class)
    fun eventsCreated(): List<Event>

}
