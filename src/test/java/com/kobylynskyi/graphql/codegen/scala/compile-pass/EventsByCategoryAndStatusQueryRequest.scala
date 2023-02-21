import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperation
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperationRequest
import java.util.{ LinkedHashMap => JLinkedHashMap }
import java.util.{ Map => JMap, Set => JSet }
import java.util.Objects
import scala.collection.mutable
import scala.collection.JavaConverters._
import EventStatus._

/**
 * List of events of a specified category.
 */
class EventsByCategoryAndStatusQueryRequest(alias: String) extends GraphQLOperationRequest {

    private final lazy val input = new JLinkedHashMap[String, java.lang.Object]()
    private final lazy val useObjectMapperForInputSerialization: mutable.Set[String] = mutable.Set()

    def setCategoryId(categoryId: String): Unit = {
        this.input.put("categoryId", categoryId)
    }

    def setStatus(status: EventStatus): Unit = {
        this.input.put("status", status)
    }

    override def getOperationType(): GraphQLOperation = EventsByCategoryAndStatusQueryRequest.OPERATION_TYPE

    override def getOperationName(): String = EventsByCategoryAndStatusQueryRequest.OPERATION_NAME

    override def getAlias(): String = if (alias != null) alias else EventsByCategoryAndStatusQueryRequest.OPERATION_NAME

    override def getInput(): JMap[String, java.lang.Object] = input

    override def getUseObjectMapperForInputSerialization(): JSet[String] = useObjectMapperForInputSerialization.asJava

    override def equals(obj: Any): Boolean = {
        if (this == obj) {
            return true
        }
        if (obj == null || getClass != obj.getClass) {
            return false
        }
        val that = obj.asInstanceOf[EventsByCategoryAndStatusQueryRequest]
        Objects.equals(getOperationType(), that.getOperationType()) &&
                   Objects.equals(getOperationName(), that.getOperationName()) &&
                   Objects.equals(input, that.input)
    }

    override def hashCode(): Int = Objects.hash(getOperationType(), getOperationName(), input)

    override def toString(): String = Objects.toString(input)
}

object EventsByCategoryAndStatusQueryRequest {

    final val OPERATION_NAME: String = "eventsByCategoryAndStatus"
    final val OPERATION_TYPE: GraphQLOperation = GraphQLOperation.QUERY

    def apply(alias: String) = new EventsByCategoryAndStatusQueryRequest(alias)

    def apply() = new EventsByCategoryAndStatusQueryRequest(null)

}
