import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResult
import java.util.{ Map => JMap }

/**
 * Version of the application.
 */
class VersionQueryResponse extends GraphQLResult[JMap[String, String]] {

    /**
     * Version of the application.
     */
    def version(): String = {
        val data: JMap[String, String] = getData
        if (data != null) data.get(VersionQueryResponse.OPERATION_NAME) else null
    }

}

object VersionQueryResponse {

    private final val OPERATION_NAME: String = "version"

}
