package argos.api

/**
 * Classes which implements this interface can be used to represent the result of an assertion.
 */
interface IAssertionResult {
    /**
     * Returns the result message
     */
    fun getMessage(): String
}
