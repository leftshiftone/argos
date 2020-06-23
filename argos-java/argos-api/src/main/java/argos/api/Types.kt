package argos.api

/**
 * Concrete assertion result implementation which represents a successful assertion.
 */
data class Success(private val message: String) : IAssertionResult {
    override fun getMessage() = message
}

/**
 * Concrete assertion result implementation which represents a failed assertion.
 */
data class Failure(private val message: String) : IAssertionResult {
    override fun getMessage() = message
}

/**
 * Concrete assertion result implementation which represents an assertion error.
 */
data class Error(val throwable: Throwable) : IAssertionResult {
    override fun getMessage() = throwable.message ?: "an error occurred"
}
