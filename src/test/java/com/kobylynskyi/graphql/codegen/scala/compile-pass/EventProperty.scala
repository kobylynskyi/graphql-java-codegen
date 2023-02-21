import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequestSerializer
import scala.collection.JavaConverters._

/**
 * An event property have all possible types
 */
case class EventProperty(
    floatVal: scala.Option[Double],
    booleanVal: scala.Option[Boolean],
    intVal: Int,
    intVals: scala.Seq[Int],
    stringVal: String
) {

    override def toString(): String = {
        scala.Seq(
            if (floatVal.isDefined) "floatVal: " + GraphQLRequestSerializer.getEntry(floatVal.get) else "",
            if (booleanVal.isDefined) "booleanVal: " + GraphQLRequestSerializer.getEntry(booleanVal.get) else "",
            "intVal: " + GraphQLRequestSerializer.getEntry(intVal),
            if (intVals != null) "intVals: " + GraphQLRequestSerializer.getEntry(intVals.asJava) else "",
            if (stringVal != null) "stringVal: " + GraphQLRequestSerializer.getEntry(stringVal) else ""
        ).filter(_ != "").mkString("{", ",", "}")
    }
}

