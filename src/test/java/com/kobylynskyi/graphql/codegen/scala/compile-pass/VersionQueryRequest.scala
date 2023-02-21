import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperation
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperationRequest
import java.util.{ LinkedHashMap => JLinkedHashMap }
import java.util.{ Map => JMap, Set => JSet }
import java.util.Objects
import scala.collection.mutable
import scala.collection.JavaConverters._

/**
 * Version of the application.
 */
class VersionQueryRequest(alias: String) extends GraphQLOperationRequest {

    private final lazy val input = new JLinkedHashMap[String, java.lang.Object]()
    private final lazy val useObjectMapperForInputSerialization: mutable.Set[String] = mutable.Set()

    override def getOperationType(): GraphQLOperation = VersionQueryRequest.OPERATION_TYPE

    override def getOperationName(): String = VersionQueryRequest.OPERATION_NAME

    override def getAlias(): String = if (alias != null) alias else VersionQueryRequest.OPERATION_NAME

    override def getInput(): JMap[String, java.lang.Object] = input

    override def getUseObjectMapperForInputSerialization(): JSet[String] = useObjectMapperForInputSerialization.asJava

    override def equals(obj: Any): Boolean = {
        if (this == obj) {
            return true
        }
        if (obj == null || getClass != obj.getClass) {
            return false
        }
        val that = obj.asInstanceOf[VersionQueryRequest]
        Objects.equals(getOperationType(), that.getOperationType()) &&
                   Objects.equals(getOperationName(), that.getOperationName()) &&
                   Objects.equals(input, that.input)
    }

    override def hashCode(): Int = Objects.hash(getOperationType(), getOperationName(), input)

    override def toString(): String = Objects.toString(input)
}

object VersionQueryRequest {

    final val OPERATION_NAME: String = "version"
    final val OPERATION_TYPE: GraphQLOperation = GraphQLOperation.QUERY

    def apply(alias: String) = new VersionQueryRequest(alias)

    def apply() = new VersionQueryRequest(null)

}
