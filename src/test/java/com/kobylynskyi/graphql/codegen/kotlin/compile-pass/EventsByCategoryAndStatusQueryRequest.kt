import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperation
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperationRequest
import java.util.Objects

/**
 * List of events of a specified category.
 */
open class EventsByCategoryAndStatusQueryRequest(private val alias: String?) : GraphQLOperationRequest {

    companion object {
        const val OPERATION_NAME: String = "eventsByCategoryAndStatus"
        val OPERATION_TYPE: GraphQLOperation = GraphQLOperation.QUERY
    }

    private val input: MutableMap<String, Any?> = LinkedHashMap()
    private val useObjectMapperForInputSerialization: MutableSet<String> = HashSet()

    constructor(): this(null)

    fun setCategoryId(categoryId: String) {
        this.input["categoryId"] = categoryId
    }

    fun setStatus(status: EventStatus?) {
        this.input["status"] = status
    }

    override fun getOperationType(): GraphQLOperation = OPERATION_TYPE

    override fun getOperationName(): String = OPERATION_NAME

    override fun getAlias(): String? = alias ?: OPERATION_NAME

    override fun getInput(): MutableMap<String, Any?> = input

    override fun getUseObjectMapperForInputSerialization(): MutableSet<String> = useObjectMapperForInputSerialization

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val that = other as EventsByCategoryAndStatusQueryRequest
        return Objects.equals(operationType, that.operationType) &&
                   Objects.equals(operationName, that.operationName) &&
                   Objects.equals(input, that.input)
    }

    override fun hashCode(): Int = Objects.hash(operationType, operationName, input)

    override fun toString(): String = Objects.toString(input)

}
