import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLParametrizedInput
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequestSerializer
import java.util.StringJoiner
/**
 * Parametrized input for field parent in type EventProperty
 */
data class EventPropertyParentParametrizedInput(
    val withStatus: EventStatus?,
    val createdAfter: String?
) : GraphQLParametrizedInput {

    override fun deepCopy(): EventPropertyParentParametrizedInput {
        return EventPropertyParentParametrizedInput(
            this.withStatus,
            this.createdAfter
        )
    
    }

    override fun toString(): String {
        val joiner = StringJoiner(", ", "( ", " )")
        if (withStatus != null) {
            joiner.add("withStatus: " + GraphQLRequestSerializer.getEntry(withStatus))
        }
        if (createdAfter != null) {
            joiner.add("createdAfter: " + GraphQLRequestSerializer.getEntry(createdAfter))
        }
        return joiner.toString()
    }
}
