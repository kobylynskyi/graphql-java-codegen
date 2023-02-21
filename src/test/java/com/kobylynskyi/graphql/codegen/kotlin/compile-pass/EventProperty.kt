import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequestSerializer
import java.util.StringJoiner

/**
 * An event property have all possible types
 */
data class EventProperty(
    val floatVal: Double?,
    val booleanVal: Boolean?,
    val intVal: Int,
    val intVals: List<Int>?,
    val stringVal: String?
) {

    // In the future, it maybe change.
    override fun toString(): String {
        val joiner = StringJoiner(", ", "{ ", " }")
        if (floatVal != null) {
            joiner.add("floatVal: " + GraphQLRequestSerializer.getEntry(floatVal))
        }
        if (booleanVal != null) {
            joiner.add("booleanVal: " + GraphQLRequestSerializer.getEntry(booleanVal))
        }
        joiner.add("intVal: " + GraphQLRequestSerializer.getEntry(intVal))
        if (intVals != null) {
            joiner.add("intVals: " + GraphQLRequestSerializer.getEntry(intVals))
        }
        if (stringVal != null) {
            joiner.add("stringVal: " + GraphQLRequestSerializer.getEntry(stringVal))
        }
        return joiner.toString()
    }
}
