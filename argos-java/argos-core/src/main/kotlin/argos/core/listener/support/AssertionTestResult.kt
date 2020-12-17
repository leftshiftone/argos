package argos.core.listener.support

import argos.api.*

data class AssertionTestResult(
    val assertion: IAssertion,
    val result: IAssertionResult,
    val timeMillis: Long,
    val msg: String)