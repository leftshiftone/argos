package argos.core.listener.support

import argos.api.*

/**
 * This class holds all the information about an assertion test
 *
 * @param assertion the assertion instance that executed the test
 * @param result the result of this assertion test
 * @param timeMillis the duration of this test in milliseconds
 * @param msg the log message
 */
data class AssertionTestResult(
    val assertion: IAssertion,
    val result: IAssertionResult,
    val timeMillis: Long,
    val msg: String)