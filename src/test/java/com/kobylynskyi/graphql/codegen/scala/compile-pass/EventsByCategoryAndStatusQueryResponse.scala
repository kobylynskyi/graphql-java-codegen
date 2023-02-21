import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResult
import java.util.{ Map => JMap }

/**
 * List of events of a specified category.
 */
class EventsByCategoryAndStatusQueryResponse extends GraphQLResult[JMap[String, scala.Seq[Event]]] {

    /**
     * List of events of a specified category.
     */
    def eventsByCategoryAndStatus(): scala.Seq[Event] = {
        val data: JMap[String, scala.Seq[Event]] = getData
        if (data != null) data.get(EventsByCategoryAndStatusQueryResponse.OPERATION_NAME) else null
    }

}

object EventsByCategoryAndStatusQueryResponse {

    private final val OPERATION_NAME: String = "eventsByCategoryAndStatus"

}
