import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequestSerializer
import java.util.StringJoiner

/**
 * An event that describes a thing that happens
 */
data class Event(
    val id: String?,
    val categoryId: String?,
    val properties: List<EventProperty?>?,
    val status: EventStatus?,
    val createdBy: String?,
    val createdDateTime: String?,
    val active: Boolean?,
    val rating: Int?
) {

    // In the future, it maybe change.
    override fun toString(): String {
        val joiner = StringJoiner(", ", "{ ", " }")
        if (id != null) {
            joiner.add("id: " + GraphQLRequestSerializer.getEntry(id))
        }
        if (categoryId != null) {
            joiner.add("categoryId: " + GraphQLRequestSerializer.getEntry(categoryId))
        }
        if (properties != null) {
            joiner.add("properties: " + GraphQLRequestSerializer.getEntry(properties))
        }
        if (status != null) {
            joiner.add("status: " + GraphQLRequestSerializer.getEntry(status))
        }
        if (createdBy != null) {
            joiner.add("createdBy: " + GraphQLRequestSerializer.getEntry(createdBy))
        }
        if (createdDateTime != null) {
            joiner.add("createdDateTime: " + GraphQLRequestSerializer.getEntry(createdDateTime))
        }
        if (active != null) {
            joiner.add("active: " + GraphQLRequestSerializer.getEntry(active))
        }
        if (rating != null) {
            joiner.add("rating: " + GraphQLRequestSerializer.getEntry(rating))
        }
        return joiner.toString()
    }
}
