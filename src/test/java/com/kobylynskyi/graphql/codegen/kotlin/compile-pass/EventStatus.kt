/**
 * Possible statuses of the event
 */
enum class EventStatus(val graphqlName: String) {

    /**
     * OPEN status
     * Means just created
     */
    OPEN("OPEN"),
    IN_PROGRESS("IN_PROGRESS"),
    /**
     * Logging completed
     */
    LOGGED("LOGGED")
}
