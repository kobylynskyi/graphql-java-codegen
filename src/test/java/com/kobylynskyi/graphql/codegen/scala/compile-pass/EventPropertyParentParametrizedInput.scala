import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLParametrizedInput
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequestSerializer
import scala.collection.JavaConverters._
import EventStatus._

/**
 * Parametrized input for field parent in type EventProperty
 */
case class EventPropertyParentParametrizedInput(
    withStatus: EventStatus,
    createdAfter: String
) extends GraphQLParametrizedInput {

    override def deepCopy(): EventPropertyParentParametrizedInput = {
        EventPropertyParentParametrizedInput(
            this.withStatus,
            this.createdAfter
        )
    }

    override def toString(): String = {
        scala.Seq(
            if (withStatus != null) "withStatus: " + GraphQLRequestSerializer.getEntry(withStatus) else "",
            if (createdAfter != null) "createdAfter: " + GraphQLRequestSerializer.getEntry(createdAfter) else ""
        ).filter(_ != "").mkString("(", ",", ")")
    }

}
