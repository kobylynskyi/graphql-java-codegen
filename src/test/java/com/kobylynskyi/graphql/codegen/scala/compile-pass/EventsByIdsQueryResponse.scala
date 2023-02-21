import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResult
import java.util.{ Map => JMap }

/**
 * Events by IDs.
 */
class EventsByIdsQueryResponse extends GraphQLResult[JMap[String, scala.Seq[Event]]] {

    /**
     * Events by IDs.
     */
    def eventsByIds(): scala.Seq[Event] = {
        val data: JMap[String, scala.Seq[Event]] = getData
        if (data != null) data.get(EventsByIdsQueryResponse.OPERATION_NAME) else null
    }

}

object EventsByIdsQueryResponse {

    private final val OPERATION_NAME: String = "eventsByIds"

}
