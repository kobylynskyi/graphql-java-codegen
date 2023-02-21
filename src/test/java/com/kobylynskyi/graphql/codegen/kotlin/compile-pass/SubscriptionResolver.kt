
interface SubscriptionResolver {

    /**
     * Subscribe to events
     */
    @Throws(Exception::class)
    fun eventsCreated(): List<Event>

}
