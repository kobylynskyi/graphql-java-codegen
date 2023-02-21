import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperation
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperationRequest
import java.util.{ LinkedHashMap => JLinkedHashMap }
import java.util.{ Map => JMap, Set => JSet }
import java.util.Objects
import scala.collection.mutable
import scala.collection.JavaConverters._

/**
 * Events by IDs.
 */
class EventsByIdsQueryRequest(alias: String) extends GraphQLOperationRequest {

    private final lazy val input = new JLinkedHashMap[String, java.lang.Object]()
    private final lazy val useObjectMapperForInputSerialization: mutable.Set[String] = mutable.Set()

    def setIds(ids: scala.Seq[String]): Unit = {
        this.input.put("ids", ids)
    }

    override def getOperationType(): GraphQLOperation = EventsByIdsQueryRequest.OPERATION_TYPE

    override def getOperationName(): String = EventsByIdsQueryRequest.OPERATION_NAME

    override def getAlias(): String = if (alias != null) alias else EventsByIdsQueryRequest.OPERATION_NAME

    override def getInput(): JMap[String, java.lang.Object] = input

    override def getUseObjectMapperForInputSerialization(): JSet[String] = useObjectMapperForInputSerialization.asJava

    override def equals(obj: Any): Boolean = {
        if (this == obj) {
            return true
        }
        if (obj == null || getClass != obj.getClass) {
            return false
        }
        val that = obj.asInstanceOf[EventsByIdsQueryRequest]
        Objects.equals(getOperationType(), that.getOperationType()) &&
                   Objects.equals(getOperationName(), that.getOperationName()) &&
                   Objects.equals(input, that.input)
    }

    override def hashCode(): Int = Objects.hash(getOperationType(), getOperationName(), input)

    override def toString(): String = Objects.toString(input)
}

object EventsByIdsQueryRequest {

    final val OPERATION_NAME: String = "eventsByIds"
    final val OPERATION_TYPE: GraphQLOperation = GraphQLOperation.QUERY

    def apply(alias: String) = new EventsByIdsQueryRequest(alias)

    def apply() = new EventsByIdsQueryRequest(null)

}
