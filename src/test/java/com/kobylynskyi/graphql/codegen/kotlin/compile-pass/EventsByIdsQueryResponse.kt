import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResult

/**
 * Events by IDs.
 */
open class EventsByIdsQueryResponse : GraphQLResult<MutableMap<String, List<Event>>>() {

    companion object {
        const val OPERATION_NAME: String = "eventsByIds"
    }

    /**
     * Events by IDs.
     */
    fun eventsByIds(): List<Event> {
        val data: MutableMap<String, List<Event>> = super.getData()
        return data.getValue(OPERATION_NAME)
    }
}
