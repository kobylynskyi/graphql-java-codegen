import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResult

/**
 * Subscribe to events
 */
open class EventsCreatedSubscriptionResponse : GraphQLResult<MutableMap<String, List<Event>>>() {

    companion object {
        const val OPERATION_NAME: String = "eventsCreated"
    }

    /**
     * Subscribe to events
     */
    fun eventsCreated(): List<Event> {
        val data: MutableMap<String, List<Event>> = super.getData()
        return data.getValue(OPERATION_NAME)
    }
}
