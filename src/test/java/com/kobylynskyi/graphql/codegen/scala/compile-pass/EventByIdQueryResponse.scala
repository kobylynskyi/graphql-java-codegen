import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResult
import java.util.{ Map => JMap }

/**
 * Single event by ID.
 */
class EventByIdQueryResponse extends GraphQLResult[JMap[String, Event]] {

    /**
     * Single event by ID.
     */
    def eventById(): Event = {
        val data: JMap[String, Event] = getData
        if (data != null) data.get(EventByIdQueryResponse.OPERATION_NAME) else null
    }

}

object EventByIdQueryResponse {

    private final val OPERATION_NAME: String = "eventById"

}
