import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResult

/**
 * Single event by ID.
 */
open class EventByIdQueryResponse : GraphQLResult<MutableMap<String, Event>>() {

    companion object {
        const val OPERATION_NAME: String = "eventById"
    }

    /**
     * Single event by ID.
     */
    fun eventById(): Event {
        val data: MutableMap<String, Event> = super.getData()
        return data.getValue(OPERATION_NAME)
    }
}
