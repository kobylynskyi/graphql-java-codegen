
/**
 * Version of the application.
 */
interface VersionQueryResolver {

    /**
     * Version of the application.
     */
    @Throws(Exception::class)
    fun version(): String

}
