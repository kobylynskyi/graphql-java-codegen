import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseField
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseProjection
import java.util.Objects
import scala.collection.mutable.HashMap
import scala.collection.JavaConverters._

/**
 * Response projection for EventProperty
 */
class EventPropertyResponseProjection() extends GraphQLResponseProjection() {

    def this(projection: EventPropertyResponseProjection) = {
        this()
        if (projection != null) {
            for (field <- projection.fields.values.asScala) {
                add$(field)
            }
        }
    }

    def this(projections: scala.Seq[EventPropertyResponseProjection]) = {
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

    def all$(): EventPropertyResponseProjection = all$(3)

    def all$(maxDepth: Int): EventPropertyResponseProjection = {
        this.floatVal()
        this.booleanVal()
        this.intVal()
        this.intVals()
        this.stringVal()
        if (projectionDepthOnFields.getOrElse("EventPropertyResponseProjection.EventPropertyResponseProjection.child", 0) <= maxDepth) {
            projectionDepthOnFields.put("EventPropertyResponseProjection.EventPropertyResponseProjection.child", projectionDepthOnFields.getOrElse("EventPropertyResponseProjection.EventPropertyResponseProjection.child", 0) + 1)
            this.child(new EventPropertyResponseProjection().all$(maxDepth - projectionDepthOnFields.getOrElse("EventPropertyResponseProjection.EventPropertyResponseProjection.child", 0)))
        }
        if (projectionDepthOnFields.getOrElse("EventPropertyResponseProjection.EventResponseProjection.parent", 0) <= maxDepth) {
            projectionDepthOnFields.put("EventPropertyResponseProjection.EventResponseProjection.parent", projectionDepthOnFields.getOrElse("EventPropertyResponseProjection.EventResponseProjection.parent", 0) + 1)
            this.parent(new EventResponseProjection().all$(maxDepth - projectionDepthOnFields.getOrElse("EventPropertyResponseProjection.EventResponseProjection.parent", 0)))
        }
        this.typename()
        this
    }

    def floatVal(): EventPropertyResponseProjection = {
        floatVal(null.asInstanceOf[String])
    }

    def floatVal(alias: String): EventPropertyResponseProjection = {
        add$(new GraphQLResponseField("floatVal").alias(alias))
        this
    }

    def booleanVal(): EventPropertyResponseProjection = {
        booleanVal(null.asInstanceOf[String])
    }

    def booleanVal(alias: String): EventPropertyResponseProjection = {
        add$(new GraphQLResponseField("booleanVal").alias(alias))
        this
    }

    def intVal(): EventPropertyResponseProjection = {
        intVal(null.asInstanceOf[String])
    }

    def intVal(alias: String): EventPropertyResponseProjection = {
        add$(new GraphQLResponseField("intVal").alias(alias))
        this
    }

    def intVals(): EventPropertyResponseProjection = {
        intVals(null.asInstanceOf[String])
    }

    def intVals(alias: String): EventPropertyResponseProjection = {
        add$(new GraphQLResponseField("intVals").alias(alias))
        this
    }

    def stringVal(): EventPropertyResponseProjection = {
        stringVal(null.asInstanceOf[String])
    }

    def stringVal(alias: String): EventPropertyResponseProjection = {
        add$(new GraphQLResponseField("stringVal").alias(alias))
        this
    }

    def child(subProjection: EventPropertyResponseProjection): EventPropertyResponseProjection = {
        child(null.asInstanceOf[String], subProjection)
    }

    def child(alias: String, subProjection: EventPropertyResponseProjection): EventPropertyResponseProjection = {
        add$(new GraphQLResponseField("child").alias(alias).projection(subProjection))
        this
    }

    def child(input: EventPropertyChildParametrizedInput,subProjection: EventPropertyResponseProjection): EventPropertyResponseProjection = {
        child(null.asInstanceOf[String], input, subProjection)
    }

    def child(alias: String, input: EventPropertyChildParametrizedInput , subProjection: EventPropertyResponseProjection): EventPropertyResponseProjection = {
        add$(new GraphQLResponseField("child").alias(alias).parameters(input).projection(subProjection))
        this
    }

    def parent(subProjection: EventResponseProjection): EventPropertyResponseProjection = {
        parent(null.asInstanceOf[String], subProjection)
    }

    def parent(alias: String, subProjection: EventResponseProjection): EventPropertyResponseProjection = {
        add$(new GraphQLResponseField("parent").alias(alias).projection(subProjection))
        this
    }

    def parent(input: EventPropertyParentParametrizedInput,subProjection: EventResponseProjection): EventPropertyResponseProjection = {
        parent(null.asInstanceOf[String], input, subProjection)
    }

    def parent(alias: String, input: EventPropertyParentParametrizedInput , subProjection: EventResponseProjection): EventPropertyResponseProjection = {
        add$(new GraphQLResponseField("parent").alias(alias).parameters(input).projection(subProjection))
        this
    }

    def typename(): EventPropertyResponseProjection = {
        typename(null.asInstanceOf[String])
    }

    def typename(alias: String): EventPropertyResponseProjection = {
        add$(new GraphQLResponseField("__typename").alias(alias))
        this
    }

    override def deepCopy$(): EventPropertyResponseProjection = new EventPropertyResponseProjection(this)

    override def equals(obj: Any): Boolean = {
        if (this == obj) {
            return true
        }
        if (obj == null || getClass != obj.getClass) {
            return false
        }
        val that = obj.asInstanceOf[EventPropertyResponseProjection]
        Objects.equals(fields, that.fields)
    }

    override def hashCode(): Int = Objects.hash(fields)

}
