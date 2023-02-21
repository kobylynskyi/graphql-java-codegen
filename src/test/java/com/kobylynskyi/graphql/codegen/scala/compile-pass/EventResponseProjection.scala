import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseField
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseProjection
import java.util.Objects
import scala.collection.mutable.HashMap
import scala.collection.JavaConverters._

/**
 * Response projection for Event
 */
class EventResponseProjection() extends GraphQLResponseProjection() {

    def this(projection: EventResponseProjection) = {
        this()
        if (projection != null) {
            for (field <- projection.fields.values.asScala) {
                add$(field)
            }
        }
    }

    def this(projections: scala.Seq[EventResponseProjection]) = {
        this()
        if (projections != null) {
            for (projection <- projections) {
                if (projection != null) {
                    for (field <- projection.fields.values.asScala) {
                        add$(field)
                    }
                }
            }
        }
    }

    private final lazy val projectionDepthOnFields = new HashMap[String, Int]

    def all$(): EventResponseProjection = all$(3)

    def all$(maxDepth: Int): EventResponseProjection = {
        this.id()
        this.categoryId()
        if (projectionDepthOnFields.getOrElse("EventResponseProjection.EventPropertyResponseProjection.properties", 0) <= maxDepth) {
            projectionDepthOnFields.put("EventResponseProjection.EventPropertyResponseProjection.properties", projectionDepthOnFields.getOrElse("EventResponseProjection.EventPropertyResponseProjection.properties", 0) + 1)
            this.properties(new EventPropertyResponseProjection().all$(maxDepth - projectionDepthOnFields.getOrElse("EventResponseProjection.EventPropertyResponseProjection.properties", 0)))
        }
        this.status()
        this.createdBy()
        this.createdDateTime()
        this.active()
        this.rating()
        this.typename()
        this
    }

    def id(): EventResponseProjection = {
        id(null.asInstanceOf[String])
    }

    def id(alias: String): EventResponseProjection = {
        add$(new GraphQLResponseField("id").alias(alias))
        this
    }

    def categoryId(): EventResponseProjection = {
        categoryId(null.asInstanceOf[String])
    }

    def categoryId(alias: String): EventResponseProjection = {
        add$(new GraphQLResponseField("categoryId").alias(alias))
        this
    }

    def properties(subProjection: EventPropertyResponseProjection): EventResponseProjection = {
        properties(null.asInstanceOf[String], subProjection)
    }

    def properties(alias: String, subProjection: EventPropertyResponseProjection): EventResponseProjection = {
        add$(new GraphQLResponseField("properties").alias(alias).projection(subProjection))
        this
    }

    def status(): EventResponseProjection = {
        status(null.asInstanceOf[String])
    }

    def status(alias: String): EventResponseProjection = {
        add$(new GraphQLResponseField("status").alias(alias))
        this
    }

    def createdBy(): EventResponseProjection = {
        createdBy(null.asInstanceOf[String])
    }

    def createdBy(alias: String): EventResponseProjection = {
        add$(new GraphQLResponseField("createdBy").alias(alias))
        this
    }

    def createdDateTime(): EventResponseProjection = {
        createdDateTime(null.asInstanceOf[String])
    }

    def createdDateTime(alias: String): EventResponseProjection = {
        add$(new GraphQLResponseField("createdDateTime").alias(alias))
        this
    }

    def active(): EventResponseProjection = {
        active(null.asInstanceOf[String])
    }

    def active(alias: String): EventResponseProjection = {
        add$(new GraphQLResponseField("active").alias(alias))
        this
    }

    def rating(): EventResponseProjection = {
        rating(null.asInstanceOf[String])
    }

    def rating(alias: String): EventResponseProjection = {
        add$(new GraphQLResponseField("rating").alias(alias))
        this
    }

    def typename(): EventResponseProjection = {
        typename(null.asInstanceOf[String])
    }

    def typename(alias: String): EventResponseProjection = {
        add$(new GraphQLResponseField("__typename").alias(alias))
        this
    }

    override def deepCopy$(): EventResponseProjection = new EventResponseProjection(this)

    override def equals(obj: Any): Boolean = {
        if (this == obj) {
            return true
        }
        if (obj == null || getClass != obj.getClass) {
            return false
        }
        val that = obj.asInstanceOf[EventResponseProjection]
        Objects.equals(fields, that.fields)
    }

    override def hashCode(): Int = Objects.hash(fields)

}
