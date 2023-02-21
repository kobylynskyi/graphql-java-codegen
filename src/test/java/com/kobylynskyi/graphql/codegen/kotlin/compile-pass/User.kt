import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequestSerializer
import java.util.StringJoiner

/**
 * type with directive using enum value
 */
data class User(
    val name: String?,
    val friends: List<User?>?
) {

    // In the future, it maybe change.
    override fun toString(): String {
        val joiner = StringJoiner(", ", "{ ", " }")
        if (name != null) {
            joiner.add("name: " + GraphQLRequestSerializer.getEntry(name))
        }
        if (friends != null) {
            joiner.add("friends: " + GraphQLRequestSerializer.getEntry(friends))
        }
        return joiner.toString()
    }
}
