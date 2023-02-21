import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequestSerializer
import scala.collection.JavaConverters._
import EventStatus._

/**
 * An event that describes a thing that happens
 */
case class Event(
    id: String,
    categoryId: String,
    properties: scala.Seq[EventProperty],
    @com.fasterxml.jackson.module.scala.JsonScalaEnumeration(classOf[EventStatusTypeRefer])
    status: EventStatus,
    createdBy: String,
    createdDateTime: String,
    active: scala.Option[Boolean],
    rating: scala.Option[Int]
) {

    override def toString(): String = {
        scala.Seq(
            if (id != null) "id: " + GraphQLRequestSerializer.getEntry(id) else "",
            if (categoryId != null) "categoryId: " + GraphQLRequestSerializer.getEntry(categoryId) else "",
            if (properties != null) "properties: " + GraphQLRequestSerializer.getEntry(properties.asJava) else "",
            if (status != null) "status: " + GraphQLRequestSerializer.getEntry(status) else "",
            if (createdBy != null) "createdBy: " + GraphQLRequestSerializer.getEntry(createdBy) else "",
            if (createdDateTime != null) "createdDateTime: " + GraphQLRequestSerializer.getEntry(createdDateTime) else "",
            if (active.isDefined) "active: " + GraphQLRequestSerializer.getEntry(active.get) else "",
            if (rating.isDefined) "rating: " + GraphQLRequestSerializer.getEntry(rating.get) else ""
        ).filter(_ != "").mkString("{", ",", "}")
    }
}

