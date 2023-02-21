import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseField
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseProjection
import java.util.Objects

/**
 * Response projection for User
 */
open class UserResponseProjection : GraphQLResponseProjection {

    constructor(): super()

    constructor(projection: UserResponseProjection): super(projection)

    constructor(projections: List<UserResponseProjection>): super(projections)

    private val projectionDepthOnFields: MutableMap<String, Int> by lazy { mutableMapOf<String, Int>() }

    fun `all$`(): UserResponseProjection = `all$`(3)

    fun `all$`(maxDepth: Int): UserResponseProjection {
        this.name()
        if (projectionDepthOnFields.getOrDefault("UserResponseProjection.UserResponseProjection.friends", 0) <= maxDepth) {
            projectionDepthOnFields["UserResponseProjection.UserResponseProjection.friends"] = projectionDepthOnFields.getOrDefault("UserResponseProjection.UserResponseProjection.friends", 0) + 1
            this.friends(UserResponseProjection().`all$`(maxDepth - projectionDepthOnFields.getOrDefault("UserResponseProjection.UserResponseProjection.friends", 0)))
        }
        this.typename()
        return this
    }

    fun name(): UserResponseProjection = name(null)

    fun name(alias: String?): UserResponseProjection {
        `add$`(GraphQLResponseField("name").alias(alias))
        return this
    }

    fun friends(subProjection: UserResponseProjection): UserResponseProjection = friends(null, subProjection)

    fun friends(alias: String?, subProjection: UserResponseProjection): UserResponseProjection {
        `add$`(GraphQLResponseField("friends").alias(alias).projection(subProjection))
        return this
    }

    fun typename(): UserResponseProjection = typename(null)

    fun typename(alias: String?): UserResponseProjection {
        `add$`(GraphQLResponseField("__typename").alias(alias))
        return this
    }

    override fun `deepCopy$`(): UserResponseProjection = UserResponseProjection(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val that = other as UserResponseProjection
        return Objects.equals(fields, that.fields)
    }

    override fun hashCode(): Int = Objects.hash(fields)

}
