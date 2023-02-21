import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseField
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseProjection
import java.util.Objects
import scala.collection.mutable.HashMap
import scala.collection.JavaConverters._

/**
 * Response projection for User
 */
class UserResponseProjection() extends GraphQLResponseProjection() {

    def this(projection: UserResponseProjection) = {
        this()
        if (projection != null) {
            for (field <- projection.fields.values.asScala) {
                add$(field)
            }
        }
    }

    def this(projections: scala.Seq[UserResponseProjection]) = {
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

    def all$(): UserResponseProjection = all$(3)

    def all$(maxDepth: Int): UserResponseProjection = {
        this.name()
        if (projectionDepthOnFields.getOrElse("UserResponseProjection.UserResponseProjection.friends", 0) <= maxDepth) {
            projectionDepthOnFields.put("UserResponseProjection.UserResponseProjection.friends", projectionDepthOnFields.getOrElse("UserResponseProjection.UserResponseProjection.friends", 0) + 1)
            this.friends(new UserResponseProjection().all$(maxDepth - projectionDepthOnFields.getOrElse("UserResponseProjection.UserResponseProjection.friends", 0)))
        }
        this.typename()
        this
    }

    def name(): UserResponseProjection = {
        name(null.asInstanceOf[String])
    }

    def name(alias: String): UserResponseProjection = {
        add$(new GraphQLResponseField("name").alias(alias))
        this
    }

    def friends(subProjection: UserResponseProjection): UserResponseProjection = {
        friends(null.asInstanceOf[String], subProjection)
    }

    def friends(alias: String, subProjection: UserResponseProjection): UserResponseProjection = {
        add$(new GraphQLResponseField("friends").alias(alias).projection(subProjection))
        this
    }

    def typename(): UserResponseProjection = {
        typename(null.asInstanceOf[String])
    }

    def typename(alias: String): UserResponseProjection = {
        add$(new GraphQLResponseField("__typename").alias(alias))
        this
    }

    override def deepCopy$(): UserResponseProjection = new UserResponseProjection(this)

    override def equals(obj: Any): Boolean = {
        if (this == obj) {
            return true
        }
        if (obj == null || getClass != obj.getClass) {
            return false
        }
        val that = obj.asInstanceOf[UserResponseProjection]
        Objects.equals(fields, that.fields)
    }

    override def hashCode(): Int = Objects.hash(fields)

}
