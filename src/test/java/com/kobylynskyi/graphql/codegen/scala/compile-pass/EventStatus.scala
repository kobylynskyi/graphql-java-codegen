import com.fasterxml.jackson.core.`type`.TypeReference

/**
 * Possible statuses of the event
 */
object EventStatus extends Enumeration {

    type EventStatus = Value

    /**
     * OPEN status
     * Means just created
     */
    val OPEN: Value = Value("OPEN")
    val IN_PROGRESS: Value = Value("IN_PROGRESS")
    /**
     * Logging completed
     */
    val LOGGED: Value = Value("LOGGED")

}

class EventStatusTypeRefer extends TypeReference[EventStatus.type]
