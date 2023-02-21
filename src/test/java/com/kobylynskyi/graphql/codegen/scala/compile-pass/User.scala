import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequestSerializer
import scala.collection.JavaConverters._

/**
 * type with directive using enum value
 */
case class User(
    name: String,
    friends: scala.Seq[User]
) {

    override def toString(): String = {
        scala.Seq(
            if (name != null) "name: " + GraphQLRequestSerializer.getEntry(name) else "",
            if (friends != null) "friends: " + GraphQLRequestSerializer.getEntry(friends.asJava) else ""
        ).filter(_ != "").mkString("{", ",", "}")
    }
}

