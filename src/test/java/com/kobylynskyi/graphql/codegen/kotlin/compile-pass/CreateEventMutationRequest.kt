import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperation
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperationRequest
import java.util.Objects

/**
 * Create a new event.
 */
open class CreateEventMutationRequest(private val alias: String?) : GraphQLOperationRequest {

    companion object {
        const val OPERATION_NAME: String = "createEvent"
        val OPERATION_TYPE: GraphQLOperation = GraphQLOperation.MUTATION
    }

    private val input: MutableMap<String, Any?> = LinkedHashMap()
    private val useObjectMapperForInputSerialization: MutableSet<String> = HashSet()

    constructor(): this(null)

    fun setCategoryId(categoryId: String) {
        this.input["categoryId"] = categoryId
    }

    fun setCreatedBy(createdBy: String?) {
        this.input["createdBy"] = createdBy
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
        val that = other as CreateEventMutationRequest
        return Objects.equals(operationType, that.operationType) &&
                   Objects.equals(operationName, that.operationName) &&
                   Objects.equals(input, that.input)
    }

    override fun hashCode(): Int = Objects.hash(operationType, operationName, input)

    override fun toString(): String = Objects.toString(input)

}
