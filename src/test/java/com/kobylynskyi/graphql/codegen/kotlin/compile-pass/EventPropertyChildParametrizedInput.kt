import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLParametrizedInput
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequestSerializer
import java.util.StringJoiner
/**
 * Parametrized input for field child in type EventProperty
 */
data class EventPropertyChildParametrizedInput(
    val first: Int?,
    val last: Int?
) : GraphQLParametrizedInput {

    override fun deepCopy(): EventPropertyChildParametrizedInput {
        return EventPropertyChildParametrizedInput(
            this.first,
            this.last
        )
    
    }

    override fun toString(): String {
        val joiner = StringJoiner(", ", "( ", " )")
        if (first != null) {
            joiner.add("first: " + GraphQLRequestSerializer.getEntry(first))
        }
        if (last != null) {
            joiner.add("last: " + GraphQLRequestSerializer.getEntry(last))
        }
        return joiner.toString()
    }
}
