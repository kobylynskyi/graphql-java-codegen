import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResult

/**
 * List of events of a specified category.
 */
open class EventsByCategoryAndStatusQueryResponse : GraphQLResult<MutableMap<String, List<Event?>>>() {

    companion object {
        const val OPERATION_NAME: String = "eventsByCategoryAndStatus"
    }

    /**
     * List of events of a specified category.
     */
    fun eventsByCategoryAndStatus(): List<Event?> {
        val data: MutableMap<String, List<Event?>> = super.getData()
        return data.getValue(OPERATION_NAME)
    }
}
