import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResult

/**
 * Create a new event.
 */
open class CreateEventMutationResponse : GraphQLResult<MutableMap<String, Event>>() {

    companion object {
        const val OPERATION_NAME: String = "createEvent"
    }

    /**
     * Create a new event.
     */
    fun createEvent(): Event {
        val data: MutableMap<String, Event> = super.getData()
        return data.getValue(OPERATION_NAME)
    }
}
