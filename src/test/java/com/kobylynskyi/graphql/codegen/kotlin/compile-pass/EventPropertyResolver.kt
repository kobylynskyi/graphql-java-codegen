
/**
 * Resolver for EventProperty
 */
interface EventPropertyResolver {

    /**
     * Properties
     */
    @Throws(Exception::class)
    fun child(eventProperty: EventProperty, first: Int?, last: Int?): List<EventProperty?>?

    /**
     * Parent event of the property
     */
    @Throws(Exception::class)
    fun parent(eventProperty: EventProperty, withStatus: EventStatus?, createdAfter: String?): Event?

}
