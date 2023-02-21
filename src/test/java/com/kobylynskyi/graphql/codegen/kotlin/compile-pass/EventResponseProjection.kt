import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseField
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseProjection
import java.util.Objects

/**
 * Response projection for Event
 */
open class EventResponseProjection : GraphQLResponseProjection {

    constructor(): super()

    constructor(projection: EventResponseProjection): super(projection)

    constructor(projections: List<EventResponseProjection>): super(projections)

    private val projectionDepthOnFields: MutableMap<String, Int> by lazy { mutableMapOf<String, Int>() }

    fun `all$`(): EventResponseProjection = `all$`(3)

    fun `all$`(maxDepth: Int): EventResponseProjection {
        this.id()
        this.categoryId()
        if (projectionDepthOnFields.getOrDefault("EventResponseProjection.EventPropertyResponseProjection.properties", 0) <= maxDepth) {
            projectionDepthOnFields["EventResponseProjection.EventPropertyResponseProjection.properties"] = projectionDepthOnFields.getOrDefault("EventResponseProjection.EventPropertyResponseProjection.properties", 0) + 1
            this.properties(EventPropertyResponseProjection().`all$`(maxDepth - projectionDepthOnFields.getOrDefault("EventResponseProjection.EventPropertyResponseProjection.properties", 0)))
        }
        this.status()
        this.createdBy()
        this.createdDateTime()
        this.active()
        this.rating()
        this.typename()
        return this
    }

    fun id(): EventResponseProjection = id(null)

    fun id(alias: String?): EventResponseProjection {
        `add$`(GraphQLResponseField("id").alias(alias))
        return this
    }

    fun categoryId(): EventResponseProjection = categoryId(null)

    fun categoryId(alias: String?): EventResponseProjection {
        `add$`(GraphQLResponseField("categoryId").alias(alias))
        return this
    }

    fun properties(subProjection: EventPropertyResponseProjection): EventResponseProjection = properties(null, subProjection)

    fun properties(alias: String?, subProjection: EventPropertyResponseProjection): EventResponseProjection {
        `add$`(GraphQLResponseField("properties").alias(alias).projection(subProjection))
        return this
    }

    fun status(): EventResponseProjection = status(null)

    fun status(alias: String?): EventResponseProjection {
        `add$`(GraphQLResponseField("status").alias(alias))
        return this
    }

    fun createdBy(): EventResponseProjection = createdBy(null)

    fun createdBy(alias: String?): EventResponseProjection {
        `add$`(GraphQLResponseField("createdBy").alias(alias))
        return this
    }

    fun createdDateTime(): EventResponseProjection = createdDateTime(null)

    fun createdDateTime(alias: String?): EventResponseProjection {
        `add$`(GraphQLResponseField("createdDateTime").alias(alias))
        return this
    }

    fun active(): EventResponseProjection = active(null)

    fun active(alias: String?): EventResponseProjection {
        `add$`(GraphQLResponseField("active").alias(alias))
        return this
    }

    fun rating(): EventResponseProjection = rating(null)

    fun rating(alias: String?): EventResponseProjection {
        `add$`(GraphQLResponseField("rating").alias(alias))
        return this
    }

    fun typename(): EventResponseProjection = typename(null)

    fun typename(alias: String?): EventResponseProjection {
        `add$`(GraphQLResponseField("__typename").alias(alias))
        return this
    }

    override fun `deepCopy$`(): EventResponseProjection = EventResponseProjection(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val that = other as EventResponseProjection
        return Objects.equals(fields, that.fields)
    }

    override fun hashCode(): Int = Objects.hash(fields)

}
