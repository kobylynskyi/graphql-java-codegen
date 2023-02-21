
/**
 * Events by IDs.
 */
trait EventsByIdsQueryResolver {

    /**
     * Events by IDs.
     */
    @throws[Exception]
    def eventsByIds(ids: scala.Seq[String]): scala.Seq[Event]

}
