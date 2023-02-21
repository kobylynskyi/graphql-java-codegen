import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResult
import java.util.{ Map => JMap }

/**
 * Subscribe to events
 */
class EventsCreatedSubscriptionResponse extends GraphQLResult[JMap[String, scala.Seq[Event]]] {

    /**
     * Subscribe to events
     */
    def eventsCreated(): scala.Seq[Event] = {
        val data: JMap[String, scala.Seq[Event]] = getData
        if (data != null) data.get(EventsCreatedSubscriptionResponse.OPERATION_NAME) else null
    }

}

object EventsCreatedSubscriptionResponse {

    private final val OPERATION_NAME: String = "eventsCreated"

}
