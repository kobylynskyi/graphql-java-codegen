import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLParametrizedInput
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequestSerializer
import scala.collection.JavaConverters._

/**
 * Parametrized input for field child in type EventProperty
 */
case class EventPropertyChildParametrizedInput(
    first: scala.Option[Int],
    last: scala.Option[Int]
) extends GraphQLParametrizedInput {

    override def deepCopy(): EventPropertyChildParametrizedInput = {
        EventPropertyChildParametrizedInput(
            this.first,
            this.last
        )
    }

    override def toString(): String = {
        scala.Seq(
            if (first.isDefined) "first: " + GraphQLRequestSerializer.getEntry(first.get) else "",
            if (last.isDefined) "last: " + GraphQLRequestSerializer.getEntry(last.get) else ""
        ).filter(_ != "").mkString("(", ",", ")")
    }

}
