import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseField
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseProjection
import java.util.Objects

/**
 * Response projection for EventProperty
 */
open class EventPropertyResponseProjection : GraphQLResponseProjection {

    constructor(): super()

    constructor(projection: EventPropertyResponseProjection): super(projection)

    constructor(projections: List<EventPropertyResponseProjection>): super(projections)

    private val projectionDepthOnFields: MutableMap<String, Int> by lazy { mutableMapOf<String, Int>() }

    fun `all$`(): EventPropertyResponseProjection = `all$`(3)

    fun `all$`(maxDepth: Int): EventPropertyResponseProjection {
        this.floatVal()
        this.booleanVal()
        this.intVal()
        this.intVals()
        this.stringVal()
        if (projectionDepthOnFields.getOrDefault("EventPropertyResponseProjection.EventPropertyResponseProjection.child", 0) <= maxDepth) {
            projectionDepthOnFields["EventPropertyResponseProjection.EventPropertyResponseProjection.child"] = projectionDepthOnFields.getOrDefault("EventPropertyResponseProjection.EventPropertyResponseProjection.child", 0) + 1
            this.child(EventPropertyResponseProjection().`all$`(maxDepth - projectionDepthOnFields.getOrDefault("EventPropertyResponseProjection.EventPropertyResponseProjection.child", 0)))
        }
        if (projectionDepthOnFields.getOrDefault("EventPropertyResponseProjection.EventResponseProjection.parent", 0) <= maxDepth) {
            projectionDepthOnFields["EventPropertyResponseProjection.EventResponseProjection.parent"] = projectionDepthOnFields.getOrDefault("EventPropertyResponseProjection.EventResponseProjection.parent", 0) + 1
            this.parent(EventResponseProjection().`all$`(maxDepth - projectionDepthOnFields.getOrDefault("EventPropertyResponseProjection.EventResponseProjection.parent", 0)))
        }
        this.typename()
        return this
    }

    fun floatVal(): EventPropertyResponseProjection = floatVal(null)

    fun floatVal(alias: String?): EventPropertyResponseProjection {
        `add$`(GraphQLResponseField("floatVal").alias(alias))
        return this
    }

    fun booleanVal(): EventPropertyResponseProjection = booleanVal(null)

    fun booleanVal(alias: String?): EventPropertyResponseProjection {
        `add$`(GraphQLResponseField("booleanVal").alias(alias))
        return this
    }

    fun intVal(): EventPropertyResponseProjection = intVal(null)

    fun intVal(alias: String?): EventPropertyResponseProjection {
        `add$`(GraphQLResponseField("intVal").alias(alias))
        return this
    }

    fun intVals(): EventPropertyResponseProjection = intVals(null)

    fun intVals(alias: String?): EventPropertyResponseProjection {
        `add$`(GraphQLResponseField("intVals").alias(alias))
        return this
    }

    fun stringVal(): EventPropertyResponseProjection = stringVal(null)

    fun stringVal(alias: String?): EventPropertyResponseProjection {
        `add$`(GraphQLResponseField("stringVal").alias(alias))
        return this
    }

    fun child(subProjection: EventPropertyResponseProjection): EventPropertyResponseProjection = child(null, subProjection)

    fun child(alias: String?, subProjection: EventPropertyResponseProjection): EventPropertyResponseProjection {
        `add$`(GraphQLResponseField("child").alias(alias).projection(subProjection))
        return this
    }

    fun child(input: EventPropertyChildParametrizedInput, subProjection: EventPropertyResponseProjection): EventPropertyResponseProjection = child(null, input, subProjection)

    fun child(alias: String?, input: EventPropertyChildParametrizedInput, subProjection: EventPropertyResponseProjection): EventPropertyResponseProjection {
        `add$`(GraphQLResponseField("child").alias(alias).parameters(input).projection(subProjection))
        return this
    }

    fun parent(subProjection: EventResponseProjection): EventPropertyResponseProjection = parent(null, subProjection)

    fun parent(alias: String?, subProjection: EventResponseProjection): EventPropertyResponseProjection {
        `add$`(GraphQLResponseField("parent").alias(alias).projection(subProjection))
        return this
    }

    fun parent(input: EventPropertyParentParametrizedInput, subProjection: EventResponseProjection): EventPropertyResponseProjection = parent(null, input, subProjection)

    fun parent(alias: String?, input: EventPropertyParentParametrizedInput, subProjection: EventResponseProjection): EventPropertyResponseProjection {
        `add$`(GraphQLResponseField("parent").alias(alias).parameters(input).projection(subProjection))
        return this
    }

    fun typename(): EventPropertyResponseProjection = typename(null)

    fun typename(alias: String?): EventPropertyResponseProjection {
        `add$`(GraphQLResponseField("__typename").alias(alias))
        return this
    }

    override fun `deepCopy$`(): EventPropertyResponseProjection = EventPropertyResponseProjection(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val that = other as EventPropertyResponseProjection
        return Objects.equals(fields, that.fields)
    }

    override fun hashCode(): Int = Objects.hash(fields)

}
