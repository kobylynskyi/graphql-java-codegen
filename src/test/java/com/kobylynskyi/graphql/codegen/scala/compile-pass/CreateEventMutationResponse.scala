import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResult
import java.util.{ Map => JMap }

/**
 * Create a new event.
 */
class CreateEventMutationResponse extends GraphQLResult[JMap[String, Event]] {

    /**
     * Create a new event.
     */
    def createEvent(): Event = {
        val data: JMap[String, Event] = getData
        if (data != null) data.get(CreateEventMutationResponse.OPERATION_NAME) else null
    }

}

object CreateEventMutationResponse {

    private final val OPERATION_NAME: String = "createEvent"

}
