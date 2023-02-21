import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResult

/**
 * Version of the application.
 */
open class VersionQueryResponse : GraphQLResult<MutableMap<String, String>>() {

    companion object {
        const val OPERATION_NAME: String = "version"
    }

    /**
     * Version of the application.
     */
    fun version(): String {
        val data: MutableMap<String, String> = super.getData()
        return data.getValue(OPERATION_NAME)
    }
}
